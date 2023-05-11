package application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DiffLine {
	public final static int MODE_DELETE = 1;
	public final static int MODE_INSERT = 2;
	public final static int MODE_CHANGE = 3;

	private int mode;
	private int position;
	private String strOriginal;
	private String strRevised;
//	private List<String> strOriginal;
//	private List<String> strRevised;
	private List<DiffChar> listDiffChar;

	public DiffLine( int mode, int position,
			String strOriginal, String strRevised ) {
//			List<String> strOriginal, List<String> strRevised ) {
		this.mode = mode;
		this.position = position;
		this.strOriginal = strOriginal;
		this.strRevised = strRevised;
		this.listDiffChar = null;
		if (mode == MODE_CHANGE) {
			this.listDiffChar = new ArrayList<DiffChar>();
		}
		return;
	}

	public void addDiffChar( DiffChar diffChar ) {
		this.listDiffChar.add(diffChar);
		return;
	}

	public int getMode() {
		return this.mode;
	}

	public int getPosition() {
		return this.position;
	}

//	public String getOriginal() {
//		return this.strOriginal;
//	}
//
//	public String getRevised() {
//		return this.strRevised;
//	}

	public String getOriginal() {
//	public List<String> getOriginal() {
		return this.strOriginal;
	}

	public String getRevised() {
//	public List<String> getRevised() {
		return this.strRevised;
	}

	public List<DiffChar> getListDiffChar() {
		return this.listDiffChar;
	}

	public String toString() {
		String str = "DELETE";
		String str2 = "";
		if (mode == MODE_INSERT) {
			str = "INSERT";
		} else if (mode == MODE_CHANGE) {
			str = "CHANGE";
			str2 = "{\n";
			Iterator<DiffChar> iter = listDiffChar.iterator();
			while (iter.hasNext()) {
				DiffChar diffChar = iter.next();
				str2 += diffChar.toString() + "\n ";
			}
			str2 += "}\n";
		}

		return str + " :" + Integer.toString(position)
				+ " [" + strOriginal + "," + strRevised + "]"
				+ str2;
	}

}
