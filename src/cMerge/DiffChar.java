package cMerge;

public class DiffChar {
	public final static int MODE_DELETE = 1;
	public final static int MODE_INSERT = 2;
	public final static int MODE_CHANGE = 3;

	private int mode;
	private int position;
	private String strOriginal;
	private String strRevised;

	public DiffChar( int mode, int position,
			String strOriginal, String strRevised ) {
		this.mode = mode;
		this.position = position;
		this.strOriginal = strOriginal;
		this.strRevised = strRevised;
		if (strRevised != null && strRevised.equals("")) {
			D.dprint("===========================================");
		}
		return;
	}

	public int getMode() {
		return this.mode;
	}

	public int getPosition() {
		return this.position;
	}

	public String getOriginal() {
		return this.strOriginal;
	}

	public String getRevised() {
		return this.strRevised;
	}

	public String toString() {
		String str = "DELETE";
		if (mode == MODE_INSERT) {
			str = "INSERT";
		} else if (mode == MODE_CHANGE) {
			str = "CHANGE";
		}
		return str + " :" + Integer.toString(position)
				+ " [" + strOriginal + "," + strRevised + "]";
	}
}
