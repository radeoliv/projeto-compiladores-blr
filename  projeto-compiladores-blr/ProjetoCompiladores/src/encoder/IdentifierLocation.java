package encoder;

public class IdentifierLocation {
	private int level;
	private int offset;
	
	public IdentifierLocation(int level, int offset){
		this.level = level;
		this.offset = offset;
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
}
