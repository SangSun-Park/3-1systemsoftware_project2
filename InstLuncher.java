package SP20_simulator;

// instruction에 따라 동작을 수행하는 메소드를 정의하는 클래스

public class InstLuncher {
	ResourceManager rMgr;
	int reg_num;
	char[] data;
	int num;
	char[] instruction;
	int difference = 0;
	String currentDevice;
	int registerNum = 0;

	public InstLuncher(ResourceManager resourceManager) {
		this.rMgr = resourceManager;
		instruction = new char[1];
	}

	public String stl(boolean pc, boolean ext, boolean im, boolean in) {
		// STL 명령어: L 레지스터 값을 해당 주소에 저장하는 명령어
		if (ext) // 4형식 명령어인 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 4);
			rMgr.locate = ((instruction[1] & 15) << 16) + ((instruction[2] >>> 8) << 12) + ((instruction[2] & 15) << 8)
					+ ((instruction[3] >>> 8) << 4) + (instruction[3] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 4);

			char[] data = new char[3];
			rMgr.setMemory(rMgr.locate, data, 3);
			data = rMgr.intToChar(rMgr.getRegister(rMgr.L_REG));
			rMgr.modifme(rMgr.locate + (3 - data.length), data, data.length, '+');
		} else // 3형식 명령어인 경우
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 3);
			rMgr.locate = ((instruction[1] & 15) << 8) + ((instruction[2] >>> 8) << 4) + (instruction[2] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 3);

			if (pc)
				rMgr.locate += rMgr.getRegister(rMgr.PC_REG);
			char[] data = new char[3];
			rMgr.setMemory(rMgr.locate, data, 3);
			data = rMgr.intToChar(rMgr.getRegister(rMgr.L_REG));
			rMgr.modifme(rMgr.locate + (3 - data.length), data, data.length, '+');
		}
		addinst();
		if(ext)
			return "+STL";
		else 
			return "STL";
	}

	public String jsub(boolean pc, boolean ext, boolean im, boolean in) {// JSUB 명령어: 주소값으로 들어온 곳으로 이동
		if (ext) // 4형식 명령어인 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 4);
			rMgr.locate = ((instruction[1] & 15) << 16) + ((instruction[2] >> 8) << 12) + ((instruction[2] & 15) << 8)
					+ ((instruction[3] >> 8) << 4) + (instruction[3] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 4);

			rMgr.setRegister(rMgr.L_REG, rMgr.getRegister(rMgr.PC_REG));
			rMgr.setRegister(rMgr.PC_REG, rMgr.locate);
			rMgr.setCurrentSection();
		} else // 3형식 명령어인 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 3);
			rMgr.locate = ((instruction[1] & 15) << 8) + ((instruction[2] >> 8) << 4) + (instruction[2] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 3);

			rMgr.setRegister(rMgr.L_REG, rMgr.getRegister(rMgr.PC_REG));

			if (pc)
				rMgr.locate += rMgr.getRegister(rMgr.PC_REG);
			rMgr.setRegister(rMgr.PC_REG, rMgr.locate);
			rMgr.setCurrentSection();
		}
		addinst();
		if(ext)
			return "+JSUB";
		else
			return "JSUB";
	}

	public String lda(boolean pc, boolean ext, boolean im, boolean in) { // LDA 명령어: 해당 피연산자 주소에 저장된 값을 A 레지스터로 가져옴
		if (ext) // 4형식인 명령어인 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 4);
			rMgr.locate = ((instruction[1] & 15) << 16) + ((instruction[2] >> 8) << 12) + ((instruction[2] & 15) << 8)
					+ ((instruction[3] >> 8) << 4) + (instruction[3] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 4);

			char[] data = rMgr.getMemory(rMgr.locate, 3);
			rMgr.setRegister(rMgr.A_REG, rMgr.byteToInt(data));
		} else // 3형식 명령어인 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 3);
			rMgr.locate = ((instruction[1] & 15) << 8) + ((instruction[2] >> 8) << 4) + (instruction[2] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 3);

			if (pc) // PC relative를 사용하는 경우,
			{
				rMgr.locate += rMgr.getRegister(rMgr.PC_REG);

				char[] data = rMgr.getMemory(rMgr.locate, 3);
				rMgr.setRegister(rMgr.A_REG, rMgr.byteToInt(data));
			} else if (im) // im를 사용하는 경우,
			{
				rMgr.setRegister(rMgr.A_REG, rMgr.locate);
			}
		}
		addinst();
		if(ext)
			return "+LDA";
		else
			return "LDA";
	}

	public String comp(boolean pc, boolean ext, boolean im, boolean in) { // COMP 명령어: A레지스터 값과 명령어에 주어진 값과 비교한다.
		if (ext) // 4형식 명령어를 사용하는 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 4);
			rMgr.locate = ((instruction[1] & 15) << 16) + ((instruction[2] >>> 8) << 12) + ((instruction[2] & 15) << 8)
					+ ((instruction[3] >>> 8) << 4) + (instruction[3] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 4);

			if (im) {
				difference = rMgr.getRegister(rMgr.A_REG) - rMgr.locate;
				rMgr.setRegister(rMgr.SW_REG, difference);
				System.out.println("SW: " + rMgr.getRegister(rMgr.SW_REG));
			}
		} else // 3형식 명령어를 사용하는 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 3);
			rMgr.locate = ((instruction[1] & 15) << 8) + ((instruction[2] >>> 8) << 4) + (instruction[2] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 3);

			if (im) {
				difference = rMgr.getRegister(rMgr.A_REG) - rMgr.locate;
				rMgr.setRegister(rMgr.SW_REG, difference);
				System.out.println("SW: " + rMgr.getRegister(rMgr.SW_REG));
			}
		}
		addinst();
		if(ext)
			return "+COMP";
		else
			return "COMP";
	}

	public String rsub() { // RSUB 명령어: L 레지스터에 저장되어있는 주소로 이동(호출 시점 다음 명령어로 돌아감)
		instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 3);
		rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.L_REG));

		currentDevice = "";
		rMgr.setCurrentSection();
		addinst();
		return "RSUB";
	}

	public String ldch(boolean pc, boolean ext, boolean im, boolean in) {// LDCH 명령어: 해당 주소의 값을 A레지스터 하위 1바이트에 불러온다.
		if (ext) // 4형식 명령어를 사용하는 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 4);
			rMgr.locate = ((instruction[1] & 15) << 16) + ((instruction[2] >>> 8) << 12) + ((instruction[2] & 15) << 8)
					+ ((instruction[3] >>> 8) << 4) + (instruction[3] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 4);

			char[] data = rMgr.getMemory(rMgr.locate + rMgr.getRegister(rMgr.X_REG), 1);
			rMgr.setRegister(rMgr.A_REG, rMgr.byteToInt(data));
			System.out.println((char) rMgr.getRegister(rMgr.A_REG));

		} else // 3형식 명령어를 사용하는 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 3);
			rMgr.locate = ((instruction[1] & 15) << 8) + ((instruction[2] >>> 8) << 4) + (instruction[2] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 3);

			if (pc) // PC relative를 사용하는 경우,
				rMgr.locate += rMgr.getRegister(rMgr.PC_REG);
			char[] data = rMgr.getMemory(rMgr.locate + rMgr.getRegister(rMgr.X_REG), 1);
			rMgr.setRegister(rMgr.A_REG, rMgr.byteToInt(data));
			System.out.println((char) rMgr.getRegister(rMgr.A_REG));
		}
		addinst();
		if(ext)
			return "+LDCH";
		else
			return "LDCH";
	}

	public String jump(boolean pc, boolean ext, boolean im, boolean in) { // 피연산자로 들어온 주소로 프로그램 흐름을 이동한다.
		if (ext) // 4형식인 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 4);
			rMgr.locate = ((instruction[1] & 15) << 16) + ((instruction[2] >> 8) << 12) + ((instruction[2] & 15) << 8)
					+ ((instruction[3] >> 8) << 4) + (instruction[3] & 15);
			if ((instruction[1] & 15) == 15) // 음수인 경우(상위 8비트가 F인 경우)
				rMgr.locate += (0xFFF << 20); // 상위 나머지 비트들도 F로 채워 음수값을 만든다.
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 4);

			rMgr.setRegister(rMgr.PC_REG, rMgr.locate);
			rMgr.setCurrentSection();
		} else // 3형식인 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 3);
			rMgr.locate = ((instruction[1] & 15) << 8) + ((instruction[2] >> 8) << 4) + (instruction[2] & 15);
			if ((instruction[1] & 15) == 15) // 음수인 경우(상위 8비트가 F인 경우)
				rMgr.locate += (0xFFFFF << 12); // 상위 나머지 비트들도 F로 채워 음수값을 만든다.
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 3);

			if (pc) {
				rMgr.locate += rMgr.getRegister(rMgr.PC_REG);

				if (in & !im) {
					rMgr.locate = rMgr.byteToInt(rMgr.getMemory(rMgr.locate, 3));
				}

				rMgr.setRegister(rMgr.PC_REG, rMgr.locate);
			}
			rMgr.setCurrentSection();
		}
		addinst();
		if(ext)
			return "+J";
		else
			return "J";
	}

	public String sta(boolean pc, boolean ext, boolean im, boolean in) {
		// STA 명령어: A 레지스터에 저장된 값을 지정된 주소로 저장한다.
		if(ext)
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 4);
			rMgr.locate = ((instruction[1] & 15) << 16) + ((instruction[2] >> 8) << 12) + ((instruction[2] & 15) << 8) + ((instruction[3] >> 8) << 4) + (instruction[3] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 4);
			
			char[] data = new char[3];
			rMgr.setMemory(rMgr.locate, data, 3);
			data = rMgr.intToChar(rMgr.getRegister(rMgr.A_REG));
			rMgr.modifme(rMgr.locate + (3 - data.length), data, data.length, '+');
		}
		else  // 3형식 명령어인 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 3);
			rMgr.locate = ((instruction[1] & 15) << 8) + ((instruction[2] >> 8) << 4) + (instruction[2] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 3);
			
			if(pc)
				rMgr.locate += rMgr.getRegister(rMgr.PC_REG);
			char[] data = new char[3];
			rMgr.setMemory(rMgr.locate, data, 3);
			data = rMgr.intToChar(rMgr.getRegister(rMgr.A_REG));
			rMgr.modifme(rMgr.locate + (3 - data.length), data, data.length, '+');
		}
		addinst();
		if(ext)
			return "+STA";
		else
			return "STA";
	}

	public String clear() { // CLEAR 명령어: 해당 레지스터의 값을 0으로 초기화 시키는 명령어
		instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 2);
		rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 2);

		registerNum = instruction[1] >>> 8;
		rMgr.setRegister(registerNum, 0);
		addinst();
		return "CLEAR";
	}

	public String ldt(boolean pc, boolean ext, boolean im, boolean in) { // LDT 명령어: 해당 피연산자의 값을 T 레지스터에 저장한다.
		if (ext) {
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 4);
			rMgr.locate = ((instruction[1] & 15) << 16) + ((instruction[2] >> 8) << 12) + ((instruction[2] & 15) << 8)
					+ ((instruction[3] >> 8) << 4) + (instruction[3] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 4);

			char[] data = rMgr.getMemory(rMgr.locate, 3);
			rMgr.setRegister(rMgr.T_REG, rMgr.byteToInt(data));
		} else {
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 3);
			rMgr.locate = ((instruction[1] & 15) << 8) + ((instruction[2] >> 8) << 4) + (instruction[2] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 3);

			if (pc)
				rMgr.locate += rMgr.getRegister(rMgr.PC_REG);
			char[] data = rMgr.getMemory(rMgr.locate, 3);
			rMgr.setRegister(rMgr.T_REG, rMgr.byteToInt(data));
		}
		addinst();
		if(ext)
			return "+LDT";
		else
			return "LDT";
	}

	public String td(boolean pc, boolean ext, boolean im, boolean in) {// TD 명령어: 해당 이름의 기기(또는 파일)의 입출력 스트림을 확인한다.
		if (ext) // 4형식 명령어인 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 4);
			rMgr.locate = ((instruction[1] & 15) << 16) + ((instruction[2] >> 8) << 12) + ((instruction[2] & 15) << 8)
					+ ((instruction[3] >> 8) << 4) + (instruction[3] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 4);

			char[] deviceInfo = rMgr.getMemory(rMgr.locate, 1);
			String deviceName = String.format("%X%X", deviceInfo[0] >> 8, deviceInfo[0] & 15);
			currentDevice = deviceName;
			rMgr.testDevice(deviceName);
		} else // 3형식 명령어인 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 3);
			rMgr.locate = ((instruction[1] & 15) << 8) + ((instruction[2] >>> 8) << 4) + (instruction[2] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 3);

			if (pc)
				rMgr.locate += rMgr.getRegister(rMgr.PC_REG);
			char[] deviceInfo = rMgr.getMemory(rMgr.locate, 1);
			String deviceName = String.format("%X%X", deviceInfo[0] >> 8, deviceInfo[0] & 15);
			currentDevice = deviceName;
			rMgr.testDevice(deviceName);
		}
		addinst();
		if(ext)
			return "+TD";
		else
			return "TD";
	}

	public String compr() {// COMPR 명령어: 두 레지스터 값을 비교한다.
		instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 2);
		rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 2);

		registerNum = instruction[1] >>> 8;
		int compareRegister = instruction[1] & 15;
		difference = rMgr.getRegister(registerNum) - rMgr.getRegister(compareRegister);
		rMgr.setRegister(rMgr.SW_REG, difference);
		addinst();
		return "COMPR";
	}

	public String stch(boolean pc, boolean ext, boolean im, boolean in) { // STCH 명령어: A레지스터 하위 1바이트에 저장된 문자를 지정된 주소에
																		// 저장한다.
		if (ext) // 4형식 명령어인 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 4);
			rMgr.locate = ((instruction[1] & 15) << 16) + ((instruction[2] >> 8) << 12) + ((instruction[2] & 15) << 8)
					+ ((instruction[3] >> 8) << 4) + (instruction[3] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 4);

			char[] data = rMgr.intToChar(rMgr.getRegister(rMgr.A_REG) & 255);
			rMgr.setMemory(rMgr.locate + rMgr.getRegister(rMgr.X_REG), data, 1);
		} else // 3형식 명령어인 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 3);
			rMgr.locate = ((instruction[1] & 15) << 8) + ((instruction[2] >> 8) << 4) + (instruction[2] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 3);

			if (pc)
				rMgr.locate += rMgr.getRegister(rMgr.PC_REG);
			char[] data = rMgr.intToChar(rMgr.getRegister(rMgr.A_REG) & 255);
			rMgr.setMemory(rMgr.locate + rMgr.getRegister(rMgr.X_REG), data, 1);
		}
		addinst();
		if(ext)
			return "+STCH";
		else
			return "STCH";
	}

	public String tixr() { // TIXR 명령어: X 레지스터 값을 1 올리고 피연산자로 들어온 레지스터의 값과 비교한다.

		instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 2);
		rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 2);

		registerNum = instruction[1] >>> 8;
		rMgr.setRegister(rMgr.X_REG, rMgr.getRegister(rMgr.X_REG) + 1);
		difference = rMgr.getRegister(rMgr.X_REG) - rMgr.getRegister(registerNum);
		rMgr.setRegister(rMgr.SW_REG, difference);
		addinst();
		return "TIXR";
	}

	public String jlt(boolean pc, boolean ext, boolean im, boolean in) { // JLT 명령어: 비교 후 작다면 명시된 주소로 이동한다.
		if (ext) // 4형식 명령어인 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 4);
			rMgr.locate = ((instruction[1] & 15) << 16) + ((instruction[2] >>> 8) << 12) + ((instruction[2] & 15) << 8)
					+ ((instruction[3] >>> 8) << 4) + (instruction[3] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 4);

			if ((instruction[1] & 15) == 15)
				rMgr.locate += (0xFFF << 20);

			if (rMgr.getRegister(rMgr.SW_REG) < 0) {
				rMgr.setRegister(rMgr.PC_REG, rMgr.locate);
			}
			rMgr.setCurrentSection();
		} else // 3형식 명령어인 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 3);
			rMgr.locate = ((instruction[1] & 15) << 8) + ((instruction[2] >>> 8) << 4) + (instruction[2] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 3);

			if ((instruction[1] & 15) == 15)
				rMgr.locate += (0xFFFFF << 12);

			if (pc)
				rMgr.locate += rMgr.getRegister(rMgr.PC_REG);

			if (rMgr.getRegister(rMgr.SW_REG) < 0) {
				rMgr.setRegister(rMgr.PC_REG, rMgr.locate);
			}
			rMgr.setCurrentSection();
		}
		addinst();
		if(ext)
			return "+JLT";
		else
			return "JLT";
	}

	public String stx(boolean pc, boolean ext, boolean im, boolean in) { // STX 명령어: X레지스터의 값을 지정된 주소에 저장한다.
		if (ext) // 4형식 명령어인 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 4);
			rMgr.locate = ((instruction[1] & 15) << 16) + ((instruction[2] >>> 8) << 12) + ((instruction[2] & 15) << 8)
					+ ((instruction[3] >>> 8) << 4) + (instruction[3] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 4);

			char[] data = new char[3];
			rMgr.setMemory(rMgr.locate, data, 3);
			data = rMgr.intToChar(rMgr.getRegister(rMgr.X_REG));
			rMgr.modifme(rMgr.locate + (3 - data.length), data, data.length, '+');
		} else // 3형식 명령어인 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 3);
			rMgr.locate = ((instruction[1] & 15) << 8) + ((instruction[2] >>> 8) << 4) + (instruction[2] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 3);

			if (pc)
				rMgr.locate += rMgr.getRegister(rMgr.PC_REG);
			char[] data = new char[3];
			rMgr.setMemory(rMgr.locate, data, 3);
			data = rMgr.intToChar(rMgr.getRegister(rMgr.X_REG));
			rMgr.modifme(rMgr.locate + (3 - data.length), data, data.length, '+');
		}
		addinst();
		if(ext)
			return "+STX";
		else
			return "STX";
	}

	public String jeq(boolean pc, boolean ext, boolean im, boolean in) { // JEQ 명령어: 비교한 두 값이 같은 경우 지정된 주소로 이동한다.
		if (ext) // 4형식 명령어인 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 4);
			rMgr.locate = ((instruction[1] & 15) << 16) + ((instruction[2] >>> 8) << 12) + ((instruction[2] & 15) << 8)
					+ ((instruction[3] >>> 8) << 4) + (instruction[3] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 4);

			if ((instruction[1] & 15) == 15)
				rMgr.locate += (0xFFF << 20);

			if (rMgr.getRegister(rMgr.SW_REG) == 0) {
				rMgr.setRegister(rMgr.PC_REG, rMgr.locate);
			}
			rMgr.setCurrentSection();
		} else // 3형식 명령어인 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 3);
			rMgr.locate = ((instruction[1] & 15) << 8) + ((instruction[2] >>> 8) << 4) + (instruction[2] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 3);

			if ((instruction[1] & 15) == 15)
				rMgr.locate += (0xFFFFF << 12);
			if (pc)
				rMgr.locate += rMgr.getRegister(rMgr.PC_REG);
			if (rMgr.getRegister(rMgr.SW_REG) == 0) {
				rMgr.setRegister(rMgr.PC_REG, rMgr.locate);
			}
			rMgr.setCurrentSection();
		}
		addinst();
		if(ext)
			return "+JEQ";
		else
			return "JEQ";
	}
	
	public String wd(boolean pc, boolean ext, boolean im, boolean in) { // WD 명령어: 지정된 기기(또는 파일)에 A 레지스터 하위 1바이트의 값을 출력한다.
		int num = 10;
		if (ext) // 4형식 명령어를 사용하는 경우,
		{
			rMgr.logList.set(rMgr.logList.size() - 1, "+" + rMgr.logList.get(rMgr.logList.size() - 1));
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 4);
			rMgr.locate = ((instruction[1] & 15) << 16) + ((instruction[2] >>> 8) << 12) + ((instruction[2] & 15) << 8)
					+ ((instruction[3] >>> 8) << 4) + (instruction[3] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 4);

			char[] deviceInfo = rMgr.getMemory(rMgr.locate, 1);
			String deviceName = String.format("%X%X", deviceInfo[0] >> 8, deviceInfo[0] & 15);
			System.out.println(deviceName);
			data = new char[1];
			data[0] = (char)(rMgr.register[rMgr.A_REG] & 255);
			rMgr.writeDevice(deviceName,  data, 1);
			System.out.print(Integer.toBinaryString(rMgr.getRegister(rMgr.A_REG)));
		} else // 3형식 명령어를 사용하는 경우,
		{
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 3);
			rMgr.locate = ((instruction[1] & 15) << 8) + ((instruction[2] >>> 8) << 4) + (instruction[2] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 3);

			if (pc)
				rMgr.locate += rMgr.getRegister(rMgr.PC_REG);
			char[] deviceInfo = rMgr.getMemory(rMgr.locate, 1);
			String deviceName = String.format("%X%X", deviceInfo[0] >> 8, deviceInfo[0] & 15);
			System.out.println(deviceName);
			data = new char[1];
			data[0] = (char)(rMgr.register[rMgr.A_REG] & 255);
			rMgr.writeDevice(deviceName, data, 1);
			System.out.print(Integer.toBinaryString(rMgr.getRegister(rMgr.A_REG)));
		}
		addinst();
		if(ext)
			return "+WD";
		else
			return "WD";
	}
	
	public String rd(boolean pc, boolean ext, boolean im, boolean in) { // RD 명령어: 해당 기기(또는 파일)에서 문자 하나를 읽어 A레지스터에 저장한다.
		if (ext) {
			rMgr.logList.set(rMgr.logList.size() - 1, "+" + rMgr.logList.get(rMgr.logList.size() - 1));
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 4);
			rMgr.locate = ((instruction[1] & 15) << 16) + ((instruction[2] >> 8) << 12) + ((instruction[2] & 15) << 8)
					+ ((instruction[3] >> 8) << 4) + (instruction[3] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 4);

			char[] deviceInfo = rMgr.getMemory(rMgr.locate, 1);
			String deviceName = String.format("%X%X", deviceInfo[0] >> 8, deviceInfo[0] & 15);
			rMgr.setRegister(rMgr.A_REG, rMgr.readDevice(deviceName, 1));
		} else {
			instruction = rMgr.getMemory(rMgr.getRegister(rMgr.PC_REG), 3);
			rMgr.locate = ((instruction[1] & 15) << 8) + ((instruction[2] >>> 8) << 4) + (instruction[2] & 15);
			rMgr.setRegister(rMgr.PC_REG, rMgr.getRegister(rMgr.PC_REG) + 3);

			if (pc)
				rMgr.locate += rMgr.getRegister(rMgr.PC_REG);
			char[] deviceInfo = rMgr.getMemory(rMgr.locate, 1);
			String deviceName = String.format("%X%X", deviceInfo[0] >> 8, deviceInfo[0] & 15);
			rMgr.setRegister(rMgr.A_REG, rMgr.readDevice(deviceName, 1));
		}
		addinst();
		if(ext)
			return "+RD";
		else
			return "RD";
	}
	
	public void addinst() {
		char[] outputInst = new char[instruction.length*2];
		for(int  i = 0; i < instruction.length; i++)
		{
			outputInst[i * 2] = (char)((instruction[i] >> 8) + '0');
			outputInst[i * 2 + 1] = (char)((instruction[i] & 255) + '0');
			
			if((instruction[i] >> 8) >= 10)
				outputInst[i * 2] += 7;
			
			if((instruction[i] & 255) >= 10)
				outputInst[i * 2 + 1] += 7;
		}
		rMgr.InstList.add(new String(outputInst, 0, outputInst.length));
	}

}