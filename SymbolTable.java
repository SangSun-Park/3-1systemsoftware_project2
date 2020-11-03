package SP20_simulator;

import java.util.ArrayList;

/**
 * symbol과 관련된 데이터와 연산을 소유한다. section 별로 하나씩 인스턴스를 할당한다.
 */
public class SymbolTable {
	ArrayList<String> symbolList;
	ArrayList<Integer> addressList;
	// 기타 literal, external 선언 및 처리방법을 구현한다.
	// 수정 사이즈를 담은 리스트
	ArrayList<Integer> modifSizeList;
	// 수정 모드, 즉 부호를 담은 리스트
	ArrayList<Character> modifModeList;
	// 수정할 섹션 프로그램 번호를 담은 리스트
	ArrayList<Integer> sectionList;

	/**
	 * 새로운 Symbol을 table에 추가한다.
	 * 
	 * @param symbol  : 새로 추가되는 symbol의 label
	 * @param address : 해당 symbol이 가지는 주소값 <br>
	 *                <br>
	 *                주의 : 만약 중복된 symbol이 putSymbol을 통해서 입력된다면 이는 프로그램 코드에 문제가 있음을
	 *                나타낸다. 매칭되는 주소값의 변경은 modifySymbol()을 통해서 이루어져야 한다.
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
	 * 기존에 존재하는 symbol 값에 대해서 가리키는 주소값을 변경한다.
	 * 
	 * @param symbol     : 변경을 원하는 symbol의 label
	 * @param newaddress : 새로 바꾸고자 하는 주소값
	 */
	public void modifySymbol(String symbol, int newaddress) {
		for (int i = 0; i < this.symbolList.size(); i++)
			if (this.symbolList.get(i).equals(symbol)) {
				this.addressList.set(i, newaddress);
				break;
			}
	}

	/**
	 * 인자로 전달된 symbol이 어떤 주소를 지칭하는지 알려준다.
	 * 
	 * @param symbol : 검색을 원하는 symbol의 label
	 * @return symbol이 가지고 있는 주소값. 해당 symbol이 없을 경우 -1 리턴
	 */
	public int search(String symbol) {
		int address = 0;
		for (int i = 0; i < this.symbolList.size(); i++)
			if (this.symbolList.get(i).equals(symbol)) {
				address = this.addressList.get(i);
			}
		return address;
	}
	
	public String getSymbol(int index)  // modify의 이름 반환
	{
		return symbolList.get(index);
	}
	
	public int getaddress(int index) // modify의 주소 반환
	{
		return addressList.get(index);
	}

	public int getModifSize(int index)  // modify의 크기 반환
	{
		return modifSizeList.get(index);
	}
	
	public char getModifMode(int index)  // modify의 부호 반환
	{
		return modifModeList.get(index);
	}
}
