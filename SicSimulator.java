package SP20_simulator;

import java.io.File;

/**
 * �ùķ����ͷμ��� �۾��� ����Ѵ�. VisualSimulator���� ������� ��û�� ������ �̿� ���� ResourceManager�� �����Ͽ�
 * �۾��� �����Ѵ�.
 * 
 * �ۼ����� ���ǻ��� : <br>
 * 1) ���ο� Ŭ����, ���ο� ����, ���ο� �Լ� ������ �󸶵��� ����. ��, ������ ������ �Լ����� �����ϰų� ������ ��ü�ϴ� ����
 * ������ ��.<br>
 * 2) �ʿ信 ���� ����ó��, �������̽� �Ǵ� ��� ��� ���� ����.<br>
 * 3) ��� void Ÿ���� ���ϰ��� ������ �ʿ信 ���� �ٸ� ���� Ÿ������ ���� ����.<br>
 * 4) ����, �Ǵ� �ܼ�â�� �ѱ��� ��½�Ű�� �� ��. (ä������ ����. �ּ��� ���Ե� �ѱ��� ��� ����)<br>
 * 
 * <br>
 * <br>
 * + �����ϴ� ���α׷� ������ ��������� �����ϰ� ���� �е��� ������ ��� �޺κп� ÷�� �ٶ��ϴ�. ���뿡 ���� �������� ���� ��
 * �ֽ��ϴ�.
 */
public class SicSimulator {
	ResourceManager rMgr;
	InstLuncher inst;

	public SicSimulator(ResourceManager resourceManager) {
		// �ʿ��ϴٸ� �ʱ�ȭ ���� �߰�
		this.rMgr = resourceManager;
	}

	/**
	 * ��������, �޸� �ʱ�ȭ �� ���α׷� load�� ���õ� �۾� ����. ��, object code�� �޸� ���� �� �ؼ���
	 * SicLoader���� �����ϵ��� �Ѵ�.
	 */
	public void load(File program) {
		/* �޸� �ʱ�ȭ, �������� �ʱ�ȭ �� */
		this.rMgr.initializeResource();
		inst = new InstLuncher(rMgr);
	}

	/**
	 * 1���� instruction�� ����� ����� ���δ�.
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
			// 4����
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
	 * ���� ��� instruction�� ����� ����� ���δ�.
	 */
	public void allStep() {
		while (true) {
			
			oneStep();
			
			if (rMgr.getRegister(ResourceManager.PC_REG) == 0x0000)
				break;
		}
	}

	/**
	 * �� �ܰ踦 ������ �� ���� ���õ� ����� ���⵵�� �Ѵ�.
	 */
	public void addLog(String log) {
		rMgr.logList.add(log);
	}
}
