package SP20_simulator;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.text.html.HTMLDocument.Iterator;

/**
 * ResourceManager는 컴퓨터의 가상 리소스들을 선언하고 관리하는 클래스이다. 크게 네가지의 가상 자원 공간을 선언하고, 이를
 * 관리할 수 있는 함수들을 제공한다.<br>
 * <br>
 * 
 * 1) 입출력을 위한 외부 장치 또는 device<br>
 * 2) 프로그램 로드 및 실행을 위한 메모리 공간. 여기서는 64KB를 최대값으로 잡는다.<br>
 * 3) 연산을 수행하는데 사용하는 레지스터 공간.<br>
 * 4) SYMTAB 등 simulator의 실행 과정에서 사용되는 데이터들을 위한 변수들. <br>
 * <br>
 * 2번은 simulator위에서 실행되는 프로그램을 위한 메모리공간인 반면, 4번은 simulator의 실행을 위한 메모리 공간이라는 점에서
 * 차이가 있다.
 */
public class ResourceManager {
	/**
	 * 디바이스는 원래 입출력 장치들을 의미 하지만 여기서는 파일로 디바이스를 대체한다.<br>
	 * 즉, 'F1'이라는 디바이스는 'F1'이라는 이름의 파일을 의미한다. <br>
	 * deviceManager는 디바이스의 이름을 입력받았을 때 해당 이름의 파일 입출력 관리 클래스를 리턴하는 역할을 한다. 예를 들어,
	 * 'A1'이라는 디바이스에서 파일을 read모드로 열었을 경우, hashMap에 <"A1", scanner(A1)> 등을 넣음으로서 이를
	 * 관리할 수 있다. <br>
	 * <br>
	 * 변형된 형태로 사용하는 것 역시 허용한다.<br>
	 * 예를 들면 key값으로 String대신 Integer를 사용할 수 있다. 파일 입출력을 위해 사용하는 stream 역시 자유로이 선택,
	 * 구현한다. <br>
	 * <br>
	 * 이것도 복잡하면 알아서 구현해서 사용해도 괜찮습니다.
	 */
	HashMap<String, Object> deviceManager = new HashMap<String, Object>();
	char[] memory = new char[65536]; // String으로 수정해서 사용하여도 무방함.
	int[] register = new int[10];
	double register_F;

	SymbolTable symtabList;
	// 이외에도 필요한 변수 선언해서 사용할 것.
	SymbolTable extabList;

	public static final int A_REG = 0;
	public static final int X_REG = 1;
	public static final int L_REG = 2;
	public static final int B_REG = 3;
	public static final int S_REG = 4;
	public static final int T_REG = 5;
	public static final int F_REG = 6;
	public static final int PC_REG = 8;
	public static final int SW_REG = 9;
	
	int ctrsection;
	int locate;
	int readpointer = 0;
	int ad;
	String registerA;
	String registerX;
	String registerL;
	String registerB;
	String registerS;
	String registerT;
	String registerPc;
	String registerSw;

	ArrayList<String> InstList = new ArrayList<>();
	ArrayList<String> logList = new ArrayList<>();
	ArrayList<String> progName = new ArrayList<>();
	ArrayList<Integer> progStart = new ArrayList<>();
	ArrayList<Integer> progLeng = new ArrayList<>();

	/**
	 * 메모리, 레지스터등 가상 리소스들을 초기화한다.
	 */
	public void initializeResource() {
		for (int i = 0; i < this.register.length; i++)
			this.register[i] = 0;
		this.register_F = 0;
		register[L_REG] = 0x000;
		symtabList = new SymbolTable();
		extabList = new SymbolTable();
		this.ad = 0;
	}

	/**
	 * deviceManager가 관리하고 있는 파일 입출력 stream들을 전부 종료시키는 역할. 프로그램을 종료하거나 연결을 끊을 때
	 * 호출한다.
	 */
	public void closeDevice() {
		String[] it = deviceManager.keySet().toArray(new String[deviceManager.size()]);
		for (int i = 0; i < deviceManager.size(); i++)
		{
			String key = it[i];
			Object stream = deviceManager.get(key);

			try
			{
				if (stream instanceof FileReader)
				{
					((FileReader) stream).close();
				}
				else if (stream instanceof FileWriter)

				{
					((FileWriter) stream).close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 디바이스를 사용할 수 있는 상황인지 체크. TD명령어를 사용했을 때 호출되는 함수. 입출력 stream을 열고 deviceManager를
	 * 통해 관리시킨다.
	 * 
	 * @param devName 확인하고자 하는 디바이스의 번호,또는 이름
	 */
	public void testDevice(String devName) {
		try
		{
			File file = new File(devName);
			if (devName.equals("F1"))
			{
				FileReader fileReader = new FileReader(file);
				deviceManager.put(devName, fileReader);
				register[SW_REG] = 1;
			}
			else if (devName.equals("05"))
			{
				FileWriter fileWriter = new FileWriter(file, true);
				deviceManager.put(devName, fileWriter);
				register[SW_REG] = 1;
			}
		}
		catch (FileNotFoundException e)
		{
			register[SW_REG] =  0;
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 디바이스로부터 원하는 개수만큼의 글자를 읽어들인다. RD명령어를 사용했을 때 호출되는 함수.
	 * 
	 * @param devName 디바이스의 이름
	 * @param num     가져오는 글자의 개수
	 * @return 가져온 데이터
	 */
	public char readDevice(String devName, int num) {
		char input = ' ';
		try
		{
			FileReader fileReader = (FileReader) deviceManager.get(devName);
			int inputChar = 0;
			int index = 0;
			
			while(index <= readpointer)
			{
				inputChar = fileReader.read();
				index++;
			}
			
			if (inputChar != -1)
			{
				input = (char) inputChar;
			}
			else
				input = 0;
			readpointer ++;
		}
		catch (FileNotFoundException e)
		{
		}
		catch (IOException e)
		{
			System.out.println(e);
		}

		return input;

	}

	/**
	 * 디바이스로 원하는 개수 만큼의 글자를 출력한다. WD명령어를 사용했을 때 호출되는 함수.
	 * 
	 * @param devName 디바이스의 이름
	 * @param data    보내는 데이터
	 * @param num     보내는 글자의 개수
	 */
	public void writeDevice(String devName, char[] data, int num) {
		try
		{
			FileWriter fileWriter = (FileWriter) deviceManager.get(devName);

			fileWriter.write(data[0]);
			fileWriter.flush();

		}
		catch (FileNotFoundException e)
		{
			// 파일을 찾지 못했을 때에 대한 핸들링
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
	}

	/**
	 * 메모리의 특정 위치에서 원하는 개수만큼의 글자를 가져온다.
	 * 
	 * @param location 메모리 접근 위치 인덱스
	 * @param num      데이터 개수
	 * @return 가져오는 데이터
	 */
	public char[] getMemory(int location, int num) {
		char[] re = new char[num];
		for (int i = 0; i < num; i++)
			re[i] = this.memory[i + location];
		return re;
	}

	/**
	 * 메모리의 특정 위치에 원하는 개수만큼의 데이터를 저장한다.
	 * 
	 * @param locate 접근 위치 인덱스
	 * @param data   저장하려는 데이터
	 * @param num    저장하는 데이터의 개수
	 */
	public void setMemory(int locate, char[] data, int num) {
		for (int i = 0; i < num; i++)
			this.memory[locate + i] = data[i];
	}

	/**
	 * 번호에 해당하는 레지스터가 현재 들고 있는 값을 리턴한다. 레지스터가 들고 있는 값은 문자열이 아님에 주의한다.
	 * 
	 * @param regNum 레지스터 분류번호
	 * @return 레지스터가 소지한 값
	 */
	public int getRegister(int regNum) {
		return this.register[regNum];
	}

	/**
	 * 번호에 해당하는 레지스터에 새로운 값을 입력한다. 레지스터가 들고 있는 값은 문자열이 아님에 주의한다.
	 * 
	 * @param regNum 레지스터의 분류번호
	 * @param value  레지스터에 집어넣는 값
	 */
	public void setRegister(int regNum, int value) {
		this.register[regNum] = value;
	}

	/**
	 * 주로 레지스터와 메모리간의 데이터 교환에서 사용된다. int값을 char[]형태로 변경한다.
	 * 
	 * @param data
	 * @return
	 */
	public char[] intToChar(int data) {
		char[] inputData = String.format("%X", data).toCharArray();
		int length = (inputData.length / 2) + (inputData.length % 2);
		char[] outputData = new char[length];

		int upByte = 0;
		int downByte = 0;

		if (inputData.length % 2 == 0)
		{

			for (int i = 0; i < length; i++)
			{
				upByte = inputData[i * 2] - '0';
				downByte = inputData[i * 2 + 1] - '0';
				if (upByte >= 10)
					upByte -= 7;
				if (downByte >= 10)
					downByte -= 7;
				
				

				outputData[i] = (char) ((upByte << 8) + downByte);
			}
		}
		else
		{
			downByte = (inputData[0] - '0');
			if(downByte >= 10)
				downByte -= 7;
			outputData[0] = (char) downByte;
			
			for (int i = 1; i < length; i++)
			{
				upByte = inputData[i * 2 - 1] - '0';
				downByte = inputData[i * 2] - '0';
				if (upByte >= 10)
					upByte -= 7;
				if (downByte >= 10)
					downByte -= 7;

				outputData[i] = (char) ((upByte << 8) + downByte);
			}
		}
		return outputData;
	}

	/**
	 * 주로 레지스터와 메모리간의 데이터 교환에서 사용된다. char[]값을 int형태로 변경한다.
	 * 
	 * @param data
	 * @return
	 */
	public int byteToInt(char[] data) {
		int result = 0;
		for(int i = 0; i < data.length; i++)
		{
			result = result << 4;
			result += (data[i] >> 8);
			result = result << 4;
			result += (data[i] & 255);
		}
		return result;
	}

	public void setProgram(String pname, String pstart, String pleng, int section) {
		int addr = Integer.parseInt(pstart, 16);
		this.progName.add(section, pname);	
		this.progLeng.add(section, Integer.parseInt(pleng, 16));
		if (section > 0)
		{
			addr += progStart.get(section - 1) + progLeng.get(section - 1);
		}
		this.progStart.add(section, addr);
	}
	
	public String getname(int section) {
		return progName.get(section);
	}
	public int getstart(int section) {
		return progStart.get(section);
	}
	public int getleng(int section) {
		return progLeng.get(section);
	}
	
	public void modifme(int locate, char[] data, int num, char modifMode) {
		if (modifMode == '+')
		{
			for (int i = locate; i < locate + num; i++)
			{
				memory[i] += data[i - locate];
			}
		}
		else if (modifMode == '-')
		{
			for (int i = locate; i < locate + num; i++)
			{
				memory[i] -= data[i - locate];
			}
		}
	}


	public ArrayList<String> getInstList(){
		return InstList;
	}
	
	public double getFRegister() {
		return register_F;
	}
	
	public int getAddr() {
		return locate;
	}
	
	public ArrayList<String> getLogList() {
		return logList;
	}
	
	public String getDevice() {
		return null;
	};
	
	
	public int getCurrentSection() {
		return ctrsection;
	}
	
	public void setCurrentSection()
	{
		int  i = 0;
		for(i = 0; i < progName.size(); i++)
		{
			if(getstart(i) <= getRegister(PC_REG) && getRegister(PC_REG) < getstart(i) + getleng(i))
			{
				ctrsection = i;
				break;
			}
		}
		
		if(i == progName.size())
			ctrsection = 0;
	}

}