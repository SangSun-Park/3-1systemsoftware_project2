package SP20_simulator;

import java.io.*;

/**
 * SicLoader는 프로그램을 해석해서 메모리에 올리는 역할을 수행한다. 이 과정에서 linker의 역할 또한 수행한다. <br>
 * <br>
 * SicLoader가 수행하는 일을 예를 들면 다음과 같다.<br>
 * - program code를 메모리에 적재시키기<br>
 * - 주어진 공간만큼 메모리에 빈 공간 할당하기<br>
 * - 과정에서 발생하는 symbol, 프로그램 시작주소, control section 등 실행을 위한 정보 생성 및 관리
 */
public class SicLoader {
	ResourceManager rMgr;
	int ctrsec;

	public SicLoader(ResourceManager resourceManager) {
		// 필요하다면 초기화
		setResourceManager(resourceManager);
	}

	/**
	 * Loader와 프로그램을 적재할 메모리를 연결시킨다.
	 * 
	 * @param rMgr
	 */
	public void setResourceManager(ResourceManager resourceManager) {
		this.rMgr = resourceManager;
		ctrsec = 0;
	}

	/**
	 * object code를 읽어서 load과정을 수행한다. load한 데이터는 resourceManager가 관리하는 메모리에 올라가도록
	 * 한다. load과정에서 만들어진 symbol table 등 자료구조 역시 resourceManager에 전달한다.
	 * 
	 * @param objectCode 읽어들인 파일
	 */
	public void load(File objectCode) {
		try {
			FileReader fr = new FileReader(objectCode);
			BufferedReader bfr = new BufferedReader(fr);
			String line = "";

			while ((line = bfr.readLine()) != null) {
				if (line.length() == 0)
					continue;

				if (line.charAt(0) == 'H') {
					String name = line.substring(1, line.length() - 13);
					String start = line.substring(7, line.length() - 7);
					String hleng = line.substring(13, line.length());
					this.rMgr.setProgram(name, start, hleng, ctrsec);
					this.rMgr.symtabList.putSymbol(name, this.rMgr.getstart(ctrsec));
				} else if (line.charAt(0) == 'D') {
					String str1 = line.substring(1, line.length());
					String symbol = "";
					int address = 0;
					for (int i = 0; i < str1.length(); i += 12) {
						symbol = str1.substring(i, i + 6);
						address = Integer.parseInt(str1.substring(i + 6, i + 12), 16) + this.rMgr.getstart(ctrsec);
						this.rMgr.symtabList.putSymbol(symbol, address);
					}
				} else if (line.charAt(0) == 'R') {
					continue;
				} else if (line.charAt(0) == 'T') {
					String str1 = line.substring(1, line.length());
					int tstart = Integer.parseInt(str1.substring(0, 6), 16) + this.rMgr.getstart(ctrsec);
					int tleng = Integer.parseInt(str1.substring(6, 8), 16);
					char[] tdata = packing(str1.substring(8, str1.length()));
					this.rMgr.setMemory(tstart, tdata, tleng);
				} else if (line.charAt(0) == 'M') {
					String str1 = line.substring(1, line.length());
					int modifLocation = Integer.parseInt(str1.substring(0, 6), 16) + +rMgr.getstart(ctrsec);
					int modifSize = Integer.parseInt(str1.substring(6, 8), 16);
					char modifMode = str1.charAt(8);
					String symbol = str1.substring(9, str1.length());

					rMgr.extabList.putExSymbol(symbol, modifLocation, modifSize, modifMode, ctrsec);
				} else if (line.charAt(0) == 'E') {
					ctrsec++;
				}

			}

			for (int i = 0; i < rMgr.extabList.symbolList.size(); i++) {
				String symbol = rMgr.extabList.getSymbol(i);
				int modifSize = rMgr.extabList.getModifSize(i);
				char modifMode = rMgr.extabList.getModifMode(i);

				String modifAddr = "000000";
				if(modifSize == 5)
					modifAddr = String.format("%05X", rMgr.symtabList.search(symbol));
				else if(modifSize == 6)
					modifAddr = String.format("%06X", rMgr.symtabList.search(symbol));
				char[] packedAddr = packing(modifAddr);

				rMgr.modifme(rMgr.extabList.getaddress(i), packedAddr, packedAddr.length, modifMode);
			}

			bfr.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
		}

	}

	public char[] packing(String str1) {
		char[] data = str1.toCharArray();
		int length = (data.length / 2) + (data.length % 2);
		char[] outputData = new char[length];

		int upByte = 0;
		int downByte = 0;

		if (data.length % 2 == 0)
		{

			for (int i = 0; i < length; i++)
			{
				upByte = data[i * 2] - '0';
				downByte = data[i * 2 + 1] - '0';
				if (upByte >= 10)
					upByte -= 7;
				if (downByte >= 10)
					downByte -= 7;

				outputData[i] = (char) ((upByte << 8) + downByte);
			}
		}
		else
		{
			downByte = (data[0] - '0');
			if(downByte >= 10)
				downByte -= 7;
			outputData[0] = (char) downByte;
			
			for (int i = 1; i < length; i++)
			{
				upByte = data[i * 2 - 1] - '0';
				downByte = data[i * 2] - '0';
				if (upByte >= 10)
					upByte -= 7;
				if (downByte >= 10)
					downByte -= 7;

				outputData[i] = (char) ((upByte << 8) + downByte);
			}
		}
		return outputData;
	}
}
