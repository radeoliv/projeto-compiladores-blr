package encoder;

public class IdentifierLocation {
	private int level;
	private int offset;
	private String spelling;
	
	public IdentifierLocation(int level, int offset, String spelling){
		this.level = level;
		this.offset = offset;
		this.spelling = spelling;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public String getSpelling() {
		return spelling;
	}

	public void setSpelling(String spelling) {
		this.spelling = spelling;
	}
	
	
	
}
