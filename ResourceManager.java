package SP20_simulator;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.text.html.HTMLDocument.Iterator;

/**
 * ResourceManager�� ��ǻ���� ���� ���ҽ����� �����ϰ� �����ϴ� Ŭ�����̴�. ũ�� �װ����� ���� �ڿ� ������ �����ϰ�, �̸�
 * ������ �� �ִ� �Լ����� �����Ѵ�.<br>
 * <br>
 * 
 * 1) ������� ���� �ܺ� ��ġ �Ǵ� device<br>
 * 2) ���α׷� �ε� �� ������ ���� �޸� ����. ���⼭�� 64KB�� �ִ밪���� ��´�.<br>
 * 3) ������ �����ϴµ� ����ϴ� �������� ����.<br>
 * 4) SYMTAB �� simulator�� ���� �������� ���Ǵ� �����͵��� ���� ������. <br>
 * <br>
 * 2���� simulator������ ����Ǵ� ���α׷��� ���� �޸𸮰����� �ݸ�, 4���� simulator�� ������ ���� �޸� �����̶�� ������
 * ���̰� �ִ�.
 */
public class ResourceManager {
	/**
	 * ����̽��� ���� ����� ��ġ���� �ǹ� ������ ���⼭�� ���Ϸ� ����̽��� ��ü�Ѵ�.<br>
	 * ��, 'F1'�̶�� ����̽��� 'F1'�̶�� �̸��� ������ �ǹ��Ѵ�. <br>
	 * deviceManager�� ����̽��� �̸��� �Է¹޾��� �� �ش� �̸��� ���� ����� ���� Ŭ������ �����ϴ� ������ �Ѵ�. ���� ���,
	 * 'A1'�̶�� ����̽����� ������ read���� ������ ���, hashMap�� <"A1", scanner(A1)> ���� �������μ� �̸�
	 * ������ �� �ִ�. <br>
	 * <br>
	 * ������ ���·� ����ϴ� �� ���� ����Ѵ�.<br>
	 * ���� ��� key������ String��� Integer�� ����� �� �ִ�. ���� ������� ���� ����ϴ� stream ���� �������� ����,
	 * �����Ѵ�. <br>
	 * <br>
	 * �̰͵� �����ϸ� �˾Ƽ� �����ؼ� ����ص� �������ϴ�.
	 */
	HashMap<String, Object> deviceManager = new HashMap<String, Object>();
	char[] memory = new char[65536]; // String���� �����ؼ� ����Ͽ��� ������.
	int[] register = new int[10];
	double register_F;

	SymbolTable symtabList;
	// �̿ܿ��� �ʿ��� ���� �����ؼ� ����� ��.
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
	 * �޸�, �������͵� ���� ���ҽ����� �ʱ�ȭ�Ѵ�.
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
	 * deviceManager�� �����ϰ� �ִ� ���� ����� stream���� ���� �����Ű�� ����. ���α׷��� �����ϰų� ������ ���� ��
	 * ȣ���Ѵ�.
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
	 * ����̽��� ����� �� �ִ� ��Ȳ���� üũ. TD��ɾ ������� �� ȣ��Ǵ� �Լ�. ����� stream�� ���� deviceManager��
	 * ���� ������Ų��.
	 * 
	 * @param devName Ȯ���ϰ��� �ϴ� ����̽��� ��ȣ,�Ǵ� �̸�
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
	 * ����̽��κ��� ���ϴ� ������ŭ�� ���ڸ� �о���δ�. RD��ɾ ������� �� ȣ��Ǵ� �Լ�.
	 * 
	 * @param devName ����̽��� �̸�
	 * @param num     �������� ������ ����
	 * @return ������ ������
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
	 * ����̽��� ���ϴ� ���� ��ŭ�� ���ڸ� ����Ѵ�. WD��ɾ ������� �� ȣ��Ǵ� �Լ�.
	 * 
	 * @param devName ����̽��� �̸�
	 * @param data    ������ ������
	 * @param num     ������ ������ ����
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
			// ������ ã�� ������ ���� ���� �ڵ鸵
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
	}

	/**
	 * �޸��� Ư�� ��ġ���� ���ϴ� ������ŭ�� ���ڸ� �����´�.
	 * 
	 * @param location �޸� ���� ��ġ �ε���
	 * @param num      ������ ����
	 * @return �������� ������
	 */
	public char[] getMemory(int location, int num) {
		char[] re = new char[num];
		for (int i = 0; i < num; i++)
			re[i] = this.memory[i + location];
		return re;
	}

	/**
	 * �޸��� Ư�� ��ġ�� ���ϴ� ������ŭ�� �����͸� �����Ѵ�.
	 * 
	 * @param locate ���� ��ġ �ε���
	 * @param data   �����Ϸ��� ������
	 * @param num    �����ϴ� �������� ����
	 */
	public void setMemory(int locate, char[] data, int num) {
		for (int i = 0; i < num; i++)
			this.memory[locate + i] = data[i];
	}

	/**
	 * ��ȣ�� �ش��ϴ� �������Ͱ� ���� ��� �ִ� ���� �����Ѵ�. �������Ͱ� ��� �ִ� ���� ���ڿ��� �ƴԿ� �����Ѵ�.
	 * 
	 * @param regNum �������� �з���ȣ
	 * @return �������Ͱ� ������ ��
	 */
	public int getRegister(int regNum) {
		return this.register[regNum];
	}

	/**
	 * ��ȣ�� �ش��ϴ� �������Ϳ� ���ο� ���� �Է��Ѵ�. �������Ͱ� ��� �ִ� ���� ���ڿ��� �ƴԿ� �����Ѵ�.
	 * 
	 * @param regNum ���������� �з���ȣ
	 * @param value  �������Ϳ� ����ִ� ��
	 */
	public void setRegister(int regNum, int value) {
		this.register[regNum] = value;
	}

	/**
	 * �ַ� �������Ϳ� �޸𸮰��� ������ ��ȯ���� ���ȴ�. int���� char[]���·� �����Ѵ�.
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
	 * �ַ� �������Ϳ� �޸𸮰��� ������ ��ȯ���� ���ȴ�. char[]���� int���·� �����Ѵ�.
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