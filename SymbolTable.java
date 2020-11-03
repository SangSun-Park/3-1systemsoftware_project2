package SP20_simulator;

import java.util.ArrayList;

/**
 * symbol�� ���õ� �����Ϳ� ������ �����Ѵ�. section ���� �ϳ��� �ν��Ͻ��� �Ҵ��Ѵ�.
 */
public class SymbolTable {
	ArrayList<String> symbolList;
	ArrayList<Integer> addressList;
	// ��Ÿ literal, external ���� �� ó������� �����Ѵ�.
	// ���� ����� ���� ����Ʈ
	ArrayList<Integer> modifSizeList;
	// ���� ���, �� ��ȣ�� ���� ����Ʈ
	ArrayList<Character> modifModeList;
	// ������ ���� ���α׷� ��ȣ�� ���� ����Ʈ
	ArrayList<Integer> sectionList;

	/**
	 * ���ο� Symbol�� table�� �߰��Ѵ�.
	 * 
	 * @param symbol  : ���� �߰��Ǵ� symbol�� label
	 * @param address : �ش� symbol�� ������ �ּҰ� <br>
	 *                <br>
	 *                ���� : ���� �ߺ��� symbol�� putSymbol�� ���ؼ� �Էµȴٸ� �̴� ���α׷� �ڵ忡 ������ ������
	 *                ��Ÿ����. ��Ī�Ǵ� �ּҰ��� ������ modifySymbol()�� ���ؼ� �̷������ �Ѵ�.
	 */
	public SymbolTable() {
		symbolList = new ArrayList<>();
		addressList = new ArrayList<>();
		modifSizeList = new ArrayList<>();
		modifModeList = new ArrayList<>();
		sectionList = new ArrayList<>();
	}

	public void putSymbol(String symbol, int address) {
		this.symbolList.add(symbol);
		this.addressList.add(address);
	}

	public void putExSymbol(String symbol, int address, int modifSize, char modifMode, int section)
	{
		symbolList.add(symbol);
		addressList.add(address);
		modifSizeList.add(modifSize);
		modifModeList.add(modifMode);
		sectionList.add(section);
	}
	
	/**
	 * ������ �����ϴ� symbol ���� ���ؼ� ����Ű�� �ּҰ��� �����Ѵ�.
	 * 
	 * @param symbol     : ������ ���ϴ� symbol�� label
	 * @param newaddress : ���� �ٲٰ��� �ϴ� �ּҰ�
	 */
	public void modifySymbol(String symbol, int newaddress) {
		for (int i = 0; i < this.symbolList.size(); i++)
			if (this.symbolList.get(i).equals(symbol)) {
				this.addressList.set(i, newaddress);
				break;
			}
	}

	/**
	 * ���ڷ� ���޵� symbol�� � �ּҸ� ��Ī�ϴ��� �˷��ش�.
	 * 
	 * @param symbol : �˻��� ���ϴ� symbol�� label
	 * @return symbol�� ������ �ִ� �ּҰ�. �ش� symbol�� ���� ��� -1 ����
	 */
	public int search(String symbol) {
		int address = 0;
		for (int i = 0; i < this.symbolList.size(); i++)
			if (this.symbolList.get(i).equals(symbol)) {
				address = this.addressList.get(i);
			}
		return address;
	}
	
	public String getSymbol(int index)  // modify�� �̸� ��ȯ
	{
		return symbolList.get(index);
	}
	
	public int getaddress(int index) // modify�� �ּ� ��ȯ
	{
		return addressList.get(index);
	}

	public int getModifSize(int index)  // modify�� ũ�� ��ȯ
	{
		return modifSizeList.get(index);
	}
	
	public char getModifMode(int index)  // modify�� ��ȣ ��ȯ
	{
		return modifModeList.get(index);
	}
}
