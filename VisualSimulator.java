package SP20_simulator;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * VisualSimulator는 사용자와의 상호작용을 담당한다.<br>
 * 즉, 버튼 클릭등의 이벤트를 전달하고 그에 따른 결과값을 화면에 업데이트 하는 역할을 수행한다.<br>
 * 실제적인 작업은 SicSimulator에서 수행하도록 구현한다.
 */
public class VisualSimulator extends JFrame {
	ResourceManager resourceManager = new ResourceManager();
	SicLoader sicLoader = new SicLoader(resourceManager);
	SicSimulator sicSimulator = new SicSimulator(resourceManager);

	private JTextField textFileName;
	private JTextField textProgName;
	private JTextField textStartAddrObProg;
	private JTextField textLengthProg;
	private JTextField textFirstInstAddr;
	private JTextField textStartAddrMemory;
	private JTextField textADec;
	private JTextField textAHex;
	private JTextField textXDec;
	private JTextField textXHex;
	private JTextField textLDec;
	private JTextField textLHex;
	private JTextField textPCDec;
	private JTextField textPCHex;
	private JTextField textSW;
	private JTextField textTargetAddr;
	private JTextField textBDec;
	private JTextField textBHex;
	private JTextField textSDec;
	private JTextField textSHex;
	private JTextField textTDec;
	private JTextField textTHex;
	private JTextField textF;
	private JTextField textDevice;
	private JList<String> listInst;
	private JList<String> listLog;

	private String filePath;

	/**
	 * 프로그램 로드 명령을 전달한다.
	 */
	public void load(File program) {
		// ...
		sicSimulator.load(program);
		sicLoader.load(program);
	};

	public VisualSimulator() {
		super("SIC/XE Simulator");
		setBounds(100, 100, 720, 1000);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);

		JLabel labelFilename = new JLabel("FileName:");
		labelFilename.setBounds(22, 20, 78, 21);
		add(labelFilename);

		textFileName = new JTextField();
		textFileName.setBounds(105, 17, 156, 27);
		textFileName.setColumns(10);
		add(textFileName);

		JButton btnOpen = new JButton("open");
		btnOpen.setBounds(266, 16, 73, 29);
		btnOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileDialog fileD = new FileDialog(new JFrame(), "열기", FileDialog.LOAD);
				fileD.setVisible(true);

				filePath = fileD.getDirectory() + fileD.getFile();
				textFileName.setText(fileD.getFile());
				File file = new File(filePath);
				load(file);
				update();
			}
		});
		add(btnOpen);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(
				new TitledBorder(null, "H (Header Record)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(17, 60, 322, 180);
		add(panel_1);
		panel_1.setLayout(null);

		JLabel lblProgramName = new JLabel("Program name:");
		lblProgramName.setBounds(17, 33, 136, 21);
		panel_1.add(lblProgramName);

		JLabel lblStartAddressOf = new JLabel("Start Address of");
		lblStartAddressOf.setBounds(17, 69, 136, 21);
		panel_1.add(lblStartAddressOf);

		JLabel lblObjectProgram = new JLabel("Object Program:");
		lblObjectProgram.setBounds(17, 91, 136, 21);
		panel_1.add(lblObjectProgram);

		JLabel lblLengthOfProgram = new JLabel("Length of Program:");
		lblLengthOfProgram.setBounds(17, 141, 157, 21);
		panel_1.add(lblLengthOfProgram);

		textProgName = new JTextField();
		textProgName.setBounds(170, 30, 136, 27);
		panel_1.add(textProgName);
		textProgName.setColumns(10);

		textStartAddrObProg = new JTextField();
		textStartAddrObProg.setBounds(170, 85, 136, 27);
		panel_1.add(textStartAddrObProg);
		textStartAddrObProg.setColumns(10);

		textLengthProg = new JTextField();
		textLengthProg.setBounds(170, 138, 126, 27);
		panel_1.add(textLengthProg);
		textLengthProg.setColumns(10);

		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(new TitledBorder(null, "E (End Record)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(356, 60, 322, 104);
		add(panel);

		JLabel lblAddressOfFirst = new JLabel("Address of First instruction");
		lblAddressOfFirst.setBounds(17, 33, 218, 21);
		panel.add(lblAddressOfFirst);

		JLabel lblInIbjectProgram = new JLabel("in Object Program:");
		lblInIbjectProgram.setBounds(17, 65, 157, 21);
		panel.add(lblInIbjectProgram);

		textFirstInstAddr = new JTextField();
		textFirstInstAddr.setColumns(10);
		textFirstInstAddr.setBounds(180, 62, 126, 27);
		panel.add(textFirstInstAddr);

		JLabel lblStartAddressIn = new JLabel("Start Address In Memory");
		lblStartAddressIn.setBounds(356, 179, 229, 21);
		add(lblStartAddressIn);

		textStartAddrMemory = new JTextField();
		textStartAddrMemory.setBounds(512, 213, 166, 27);
		add(textStartAddrMemory);
		textStartAddrMemory.setColumns(10);

		JPanel panel_2 = new JPanel();
		panel_2.setLayout(null);
		panel_2.setBorder(new TitledBorder(null, "Register", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(17, 255, 322, 400);
		add(panel_2);

		JLabel lblA = new JLabel("A (#0)");
		lblA.setBounds(17, 54, 65, 21);
		panel_2.add(lblA);

		textADec = new JTextField();
		textADec.setColumns(10);
		textADec.setBounds(85, 51, 103, 27);
		panel_2.add(textADec);

		textAHex = new JTextField();
		textAHex.setColumns(10);
		textAHex.setBounds(203, 51, 103, 27);
		panel_2.add(textAHex);

		JLabel lblDec = new JLabel("Dec");
		lblDec.setBounds(121, 15, 43, 21);
		panel_2.add(lblDec);

		JLabel lblHex = new JLabel("Hex");
		lblHex.setBounds(238, 15, 43, 21);
		panel_2.add(lblHex);

		JLabel lblX = new JLabel("X (#1)");
		lblX.setBounds(17, 93, 65, 21);
		panel_2.add(lblX);

		textXDec = new JTextField();
		textXDec.setColumns(10);
		textXDec.setBounds(85, 90, 103, 27);
		panel_2.add(textXDec);

		textXHex = new JTextField();
		textXHex.setColumns(10);
		textXHex.setBounds(203, 90, 103, 27);
		panel_2.add(textXHex);

		JLabel lblL = new JLabel("L (#2)");
		lblL.setBounds(17, 132, 65, 21);
		panel_2.add(lblL);

		textLDec = new JTextField();
		textLDec.setColumns(10);
		textLDec.setBounds(85, 129, 103, 27);
		panel_2.add(textLDec);

		textLHex = new JTextField();
		textLHex.setColumns(10);
		textLHex.setBounds(203, 129, 103, 27);
		panel_2.add(textLHex);

		JLabel lblB = new JLabel("B (#3)");
		lblB.setBounds(17, 171, 65, 21);
		panel_2.add(lblB);

		textBDec = new JTextField();
		textBDec.setColumns(10);
		textBDec.setBounds(85, 168, 103, 27);
		panel_2.add(textBDec);

		textBHex = new JTextField();
		textBHex.setColumns(10);
		textBHex.setBounds(203, 168, 103, 27);
		panel_2.add(textBHex);

		JLabel lblS = new JLabel("S (#4)");
		lblS.setBounds(17, 210, 65, 21);
		panel_2.add(lblS);

		textSDec = new JTextField();
		textSDec.setColumns(10);
		textSDec.setBounds(85, 207, 103, 27);
		panel_2.add(textSDec);

		textSHex = new JTextField();
		textSHex.setColumns(10);
		textSHex.setBounds(203, 207, 103, 27);
		panel_2.add(textSHex);

		JLabel lblTargetAddress = new JLabel("Target Address:");
		lblTargetAddress.setBounds(361, 266, 136, 21);
		add(lblTargetAddress);

		textTargetAddr = new JTextField();
		textTargetAddr.setColumns(10);
		textTargetAddr.setBounds(512, 263, 166, 27);
		add(textTargetAddr);

		JLabel lblT = new JLabel("T (#5)");
		lblT.setBounds(17, 249, 65, 21);
		panel_2.add(lblT);

		textTDec = new JTextField();
		textTDec.setColumns(10);
		textTDec.setBounds(85, 245, 103, 27);
		panel_2.add(textTDec);

		textTHex = new JTextField();
		textTHex.setColumns(10);
		textTHex.setBounds(203, 245, 103, 27);
		panel_2.add(textTHex);

		JLabel lblF = new JLabel("F (#6)");
		lblF.setBounds(17, 288, 65, 21);
		panel_2.add(lblF);

		textF = new JTextField();
		textF.setColumns(10);
		textF.setBounds(85, 283, 221, 27);
		panel_2.add(textF);

		JLabel lblPc = new JLabel("PC(#8)");
		lblPc.setBounds(17, 322, 65, 21);
		panel_2.add(lblPc);

		textPCDec = new JTextField();
		textPCDec.setColumns(10);
		textPCDec.setBounds(85, 321, 103, 27);
		panel_2.add(textPCDec);

		textPCHex = new JTextField();
		textPCHex.setColumns(10);
		textPCHex.setBounds(203, 321, 103, 27);
		panel_2.add(textPCHex);

		JLabel lblSw = new JLabel("SW(#9)");
		lblSw.setBounds(17, 361, 65, 21);
		panel_2.add(lblSw);

		textSW = new JTextField();
		textSW.setColumns(10);
		textSW.setBounds(85, 359, 221, 27);
		panel_2.add(textSW);

		JLabel lblInstructions = new JLabel("Instructions:");
		lblInstructions.setBounds(361, 302, 112, 21);
		add(lblInstructions);

		JScrollPane instScroll = new JScrollPane();
		instScroll.setBounds(356, 338, 174, 315);
		add(instScroll);
		listInst = new JList<>();
		instScroll.setViewportView(listInst);

		JLabel label_1 = new JLabel("사용중인 장치");
		label_1.setBounds(547, 338, 131, 21);
		add(label_1);

		textDevice = new JTextField();
		textDevice.setBounds(571, 374, 107, 27);
		add(textDevice);
		textDevice.setColumns(10);

		JButton btnRunOneStep = new JButton("실행(1 Step)");
		btnRunOneStep.setBounds(542, 550, 136, 29);
		btnRunOneStep.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 1 step 실행 버튼을 누른 경우,
				// 하나의 명령어를 실행시키는 oneStep() 메소드 호출 후,
				oneStep();
			}
		});
		add(btnRunOneStep);

		JButton btnRunAll = new JButton("실행 (All)");
		btnRunAll.setBounds(542, 585, 136, 29);
		btnRunAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// all step 실행 버튼을 누른 경우,
				// 모든 명령어를 실행시키는 allStep() 메소드 호출 후,
				allStep();
			}
		});
		add(btnRunAll);

		JButton btnQuit = new JButton("종료");
		btnQuit.setBounds(542, 620, 136, 29);
		btnQuit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 종료 버튼을 누른 경우,
				// 시뮬레이터 프로그램을 종료시킨다.
				System.exit(1);
			}
		});
		add(btnQuit);

		JLabel lblLog = new JLabel("Log (명령어 수행 관련):");
		lblLog.setBounds(17, 670, 204, 21);
		add(lblLog);

		JScrollPane logScroll = new JScrollPane();
		logScroll.setBounds(17, 702, 664, 247);
		add(logScroll);
		listLog = new JList<>();
		logScroll.setViewportView(listLog);

		setVisible(true);
	}

	/**
	 * 하나의 명령어만 수행할 것을 SicSimulator에 요청한다.
	 */
	public void oneStep() {
		this.sicSimulator.oneStep();
		update();
	};

	/**
	 * 남아있는 모든 명령어를 수행할 것을 SicSimulator에 요청한다.
	 */
	public void allStep() {
		this.sicSimulator.allStep();
		update();
	};

	/**
	 * 화면을 최신값으로 갱신하는 역할을 수행한다.
	 */
	public void update() {
		// 실행한 명령어 리스트를 출력한다.
		String[] instStringList = resourceManager.getInstList()
				.toArray(new String[resourceManager.getInstList().size()]);
		listInst.setListData(instStringList);

		// 실행한 명령어 log를 출력한다.
		String[] logStringList = resourceManager.getLogList().toArray(new String[resourceManager.getLogList().size()]);
		listLog.setListData(logStringList);

		// 현재 실행중인 컨트롤 섹션 프로그램의 이름을 출력한다.
		textProgName.setText(resourceManager.getname(resourceManager.getCurrentSection()));
		
		// 현재 실행중인 컨트롤 섹션 프로그램의 실행 시작 주소를 출력한다.
		if (resourceManager.getCurrentSection() != 0) {
			textStartAddrObProg
					.setText(String.format("%06X", resourceManager.getstart(resourceManager.getCurrentSection())
							- resourceManager.getleng(resourceManager.getCurrentSection() - 1)));

			textFirstInstAddr
					.setText(String.format("%06X", resourceManager.getstart(resourceManager.getCurrentSection())
							- resourceManager.getleng(resourceManager.getCurrentSection() - 1)));
		} else {
			textStartAddrObProg.setText(
					String.format("%06X", resourceManager.getstart(resourceManager.getCurrentSection())));

			textFirstInstAddr.setText(
					String.format("%06X", resourceManager.getstart(resourceManager.getCurrentSection())));
		}

		// 해당 프로그램의 총 길이를 출력한다.
		int progLength = 0;
		for (int i = 0; i < resourceManager.progName.size(); i++)
			progLength += resourceManager.getleng(i);
		textLengthProg.setText(Integer.toHexString(progLength));

		// 해당 섹션 프로그램의 메모리상에서의 실행 시작 주소를 출력한다.
		textStartAddrMemory
				.setText(String.format("%06X", resourceManager.getstart(resourceManager.getCurrentSection())));

		// 각 레지스터의 값을 출력한다.
		textADec.setText(String.format("%d", resourceManager.getRegister(resourceManager.A_REG)));
		textAHex.setText(String.format("%06X", resourceManager.getRegister(resourceManager.A_REG)));

		textXDec.setText(String.format("%d", resourceManager.getRegister(resourceManager.X_REG)));
		textXHex.setText(String.format("%06X", resourceManager.getRegister(resourceManager.X_REG)));

		textLDec.setText(String.format("%d", resourceManager.getRegister(resourceManager.L_REG)));
		textLHex.setText(String.format("%06X", resourceManager.getRegister(resourceManager.L_REG)));

		textPCDec.setText(String.format("%d", resourceManager.getRegister(resourceManager.PC_REG)));
		textPCHex.setText(String.format("%06X", resourceManager.getRegister(resourceManager.PC_REG)));

		textSW.setText(String.format("%06X", resourceManager.getRegister(resourceManager.SW_REG)));

		textTargetAddr.setText(String.format("%06X", resourceManager.getAddr()));

		textBDec.setText(String.format("%d", resourceManager.getRegister(resourceManager.B_REG)));
		textBHex.setText(String.format("%06X", resourceManager.getRegister(resourceManager.B_REG)));

		textSDec.setText(String.format("%d", resourceManager.getRegister(resourceManager.S_REG)));
		textSHex.setText(String.format("%06X", resourceManager.getRegister(resourceManager.S_REG)));

		textTDec.setText(String.format("%d", resourceManager.getRegister(resourceManager.T_REG)));
		textTHex.setText(String.format("%06X", resourceManager.getRegister(resourceManager.T_REG)));

		textF.setText(String.format("%f", resourceManager.getFRegister()));

		// 입출력으로 장치를 사용할 경우, 사용중인 장치 정보를 출력한다.
		textDevice.setText(resourceManager.getDevice());
	};

	public static void main(String[] args) {
		new VisualSimulator();
	}
}
