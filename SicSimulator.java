package SP20_simulator;

import java.io.File;

/**
 * 시뮬레이터로서의 작업을 담당한다. VisualSimulator에서 사용자의 요청을 받으면 이에 따라 ResourceManager에 접근하여
 * 작업을 수행한다.
 * 
 * 작성중의 유의사항 : <br>
 * 1) 새로운 클래스, 새로운 변수, 새로운 함수 선언은 얼마든지 허용됨. 단, 기존의 변수와 함수들을 삭제하거나 완전히 대체하는 것은
 * 지양할 것.<br>
 * 2) 필요에 따라 예외처리, 인터페이스 또는 상속 사용 또한 허용됨.<br>
 * 3) 모든 void 타입의 리턴값은 유저의 필요에 따라 다른 리턴 타입으로 변경 가능.<br>
 * 4) 파일, 또는 콘솔창에 한글을 출력시키지 말 것. (채점상의 이유. 주석에 포함된 한글은 상관 없음)<br>
 * 
 * <br>
 * <br>
 * + 제공하는 프로그램 구조의 개선방법을 제안하고 싶은 분들은 보고서의 결론 뒷부분에 첨부 바랍니다. 내용에 따라 가산점이 있을 수
 * 있습니다.
 */
public class SicSimulator {
	ResourceManager rMgr;
	InstLuncher inst;

	public SicSimulator(ResourceManager resourceManager) {
		// 필요하다면 초기화 과정 추가
		this.rMgr = resourceManager;
	}

	/**
	 * 레지스터, 메모리 초기화 등 프로그램 load와 관련된 작업 수행. 단, object code의 메모리 적재 및 해석은
	 * SicLoader에서 수행하도록 한다.
	 */
	public void load(File program) {
		/* 메모리 초기화, 레지스터 초기화 등 */
		this.rMgr.initializeResource();
		inst = new InstLuncher(rMgr);
	}

	/**
	 * 1개의 instruction이 수행된 모습을 보인다.
	 */
	public void oneStep() {
		boolean immediate = false;
		boolean indirect = false;
		boolean pc = false;
		boolean ext = false;
		char[] bts = this.rMgr.getMemory(rMgr.register[rMgr.PC_REG], 2);
		int op = (bts[0] >>> 4) + (bts[0] & 15);
		if ((op & 2) == 2) {
			op -= 2;
			indirect = true;
			// indirect
		}
		if ((op & 1) == 1) {
			op -= 1;
			immediate = true;
			// immediate
		}
		int format = (bts[1] >>>8);
		if ((format & 1) == 1) {
			ext = true;
			// 4형식
		}
		if ((format & 2) == 2) {
			pc = true;
			// pc relative
		}
		if (op == 0x14) addLog(inst.stl(pc, ext, immediate, indirect));
		else if(op == 0x48) addLog(inst.jsub(pc, ext, immediate, indirect)); 
		else if(op == 0) addLog(inst.lda(pc, ext, immediate, indirect)); 
		else if(op == 0x28) addLog(inst.comp(pc, ext, immediate, indirect)); 
		else if(op == 0x50) addLog(inst.ldch(pc, ext, immediate, indirect)); 
		else if(op == 0xdc) addLog(inst.wd(pc, ext, immediate, indirect)); 
		else if(op == 0x3c) addLog(inst.jump(pc, ext, immediate, indirect)); 
		else if(op == 0x0c) addLog(inst.sta(pc, ext, immediate, indirect)); 
		else if(op == 0x74) addLog(inst.ldt(pc, ext, immediate, indirect)); 
		else if(op == 0xe0) addLog(inst.td(pc, ext, immediate, indirect)); 
		else if(op == 0xd8) addLog(inst.rd(pc, ext, immediate, indirect)); 
		else if(op == 0x54) addLog(inst.stch(pc, ext, immediate, indirect));
		else if(op == 0x38) addLog(inst.jlt(pc, ext, immediate, indirect)); 
		else if(op == 0x10) addLog(inst.stx(pc, ext, immediate, indirect)); 
		else if(op == 0x30) addLog(inst.jeq(pc, ext, immediate, indirect)); 
		else if(op == 0x4c) addLog(inst.rsub());
		else if(op == 0xb4) addLog(inst.clear()); 
		else if(op == 0xa0) addLog(inst.compr()); 
		else if(op == 0xb8) addLog(inst.tixr());
	}

	/**
	 * 남은 모든 instruction이 수행된 모습을 보인다.
	 */
	public void allStep() {
		while (true) {
			
			oneStep();
			
			if (rMgr.getRegister(ResourceManager.PC_REG) == 0x0000)
				break;
		}
	}

	/**
	 * 각 단계를 수행할 때 마다 관련된 기록을 남기도록 한다.
	 */
	public void addLog(String log) {
		rMgr.logList.add(log);
	}
}
