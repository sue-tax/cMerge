package application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.incava.diff.Diff;
import org.incava.diff.Difference;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;


public class CMerge {
//	public static final float VERSION = 0.13f;
	// フォルダ指定を可能にする
	public static final float VERSION = 0.20f;

	static final String X_COL = "<font color=\"blue\">";
	static final String Y_COL = "<font color=\"green\">";
	static final String M_COL = "<font color=\"magenta\">";
	static final String C_COL = "<font color=\"red\">";
	static final String COL_END = "</font>";

	static final String DEL_START = "<del>";
	static final String DEL_END = "</del>";


	public static void compare(
			List<String> linesColor,
			List<String> linesBase,
			List<DiffLine> listDiff,
			String strCol) {
		D.dprint_method_start();
		int positionB = 0;
		Iterator<DiffLine> iterDiff = listDiff.iterator();
		DiffLine diffLine;
		// mergeから派生させたため、ループ条件が変
//		if (iterDiff.hasNext()) {
//			diffLine = iterDiff.next();
//		} else {
//			diffLine = null;
//		}
//		while (diffLine != null) {
		while (iterDiff.hasNext()) {
			D.dprint("compare while Loop");
			diffLine = iterDiff.next();
			int mode = diffLine.getMode();
			if (positionB < diffLine.getPosition()) {
				for (int i = positionB; i < Integer.min(
						diffLine.getPosition(),
						linesBase.size()); i++) {
					//				linesZ.add(linesBase.get(i));
					linesColor.add(linesBase.get(i));
				}
				positionB = diffLine.getPosition();
			}
			if (mode == DiffLine.MODE_INSERT) {
//				linesZ.add(diffX.getRevised());
				linesColor.add(strCol +
						diffLine.getRevised()
						+ COL_END);
			} else if (mode == DiffLine.MODE_DELETE) {
				linesColor.add(strCol +
						DEL_START +
						diffLine.getOriginal()
						+ DEL_END
						+ COL_END);
				positionB += 1;
			} else {
				assert(mode==DiffLine.MODE_CHANGE);
				D.dprint(positionB);
				D.dprint(diffLine);
				compareChangeChar(
					strCol, linesColor,
					linesBase.get(positionB),
					diffLine.getListDiffChar());

				positionB += 1;
			}
//			if (iterDiff.hasNext()) {
//				diffLine = iterDiff.next();
//			}
			D.dprint(positionB);
		}
		for (int i=positionB; i<linesBase.size(); i++) {
//			linesZ.add(linesBase.get(i));
			linesColor.add(linesBase.get(i));
		}
		D.dprint_method_end();
		return;
	}

	// 行の単独CHANGEがあったときの各文字の処理
	private static void compareChangeChar(
			String strColor, List<String> linesColor,
			String strBase,
			List<DiffChar> listDiff) {
		D.dprint_method_start();
		D.dprint(strBase);
		D.dprint(listDiff);
//		StringBuffer sb = new StringBuffer();
		StringBuffer sbC = new StringBuffer();
		int positionB = 0;
		Iterator<DiffChar> iter = listDiff.iterator();
		while (iter.hasNext()) {
			DiffChar diff = iter.next();
			D.dprint(diff);
			D.dprint(positionB);
			D.dprint(diff.getPosition());
			if (positionB < diff.getPosition()) {
				for (int i=positionB;
						i<diff.getPosition(); i++) {
//					sb.append(strBase.charAt(i));
					sbC.append(strBase.charAt(i));
				}
				positionB = diff.getPosition() ;
//				positionB = diff.getPosition() - 1;
			}
			int mode = diff.getMode();
			if (mode == DiffChar.MODE_INSERT) {
//				sb.append(diff.getRevised());
				sbC.append(strColor);
				sbC.append(diff.getRevised());
				sbC.append(COL_END);
			} else if (mode == DiffChar.MODE_DELETE) {
				sbC.append(strColor);
				sbC.append(DEL_START);
				sbC.append(diff.getOriginal());
				sbC.append(DEL_END);
				sbC.append(COL_END);
				positionB += 1;
			} else {
//				sb.append(diff.getRevised());
				sbC.append(strColor);
				sbC.append(DEL_START);
				sbC.append(diff.getOriginal());
				sbC.append(DEL_END);
				sbC.append(diff.getRevised());
				sbC.append(COL_END);
				positionB += 1;
			}
			D.dprint(positionB);
		}
		for (int i=positionB; i<strBase.length(); i++) {
//			sb.append(strBase.charAt(i));
			sbC.append(strBase.charAt(i));
		}
//		String strZ = sb.toString();
//		linesZ.add(strZ);
		String strC = sbC.toString();
		linesColor.add(strC);
		D.dprint_method_end();
		return;
	}


	// 文字でのコンフリクトの表示文字列
	private static String displayConflictChar(
			DiffChar diffX, DiffChar diffY) {
		StringBuffer sb = new StringBuffer();
		int xMode = diffX.getMode();
		int yMode = diffY.getMode();
		sb.append("<<<");
		if (xMode == DiffLine.MODE_INSERT) {
			sb.append("+");
			sb.append(diffX.getRevised());
		} else if (xMode == DiffLine.MODE_DELETE) {
			sb.append("-");
			sb.append(diffX.getOriginal());
		} else {
			sb.append("-");
			sb.append(diffX.getOriginal());
			sb.append("+");
			sb.append(diffX.getRevised());
		}
		sb.append("===");
		if (yMode == DiffLine.MODE_INSERT) {
			sb.append("+");
			sb.append(diffY.getRevised());
		} else if (yMode == DiffLine.MODE_DELETE) {
			sb.append("-");
			sb.append(diffY.getOriginal());
		} else {
			sb.append("-");
			sb.append(diffY.getOriginal());
			sb.append("+");
			sb.append(diffY.getRevised());
		}
		sb.append(">>>");
		String str = sb.toString();
		return str;
	}

	// 行でのコンフリクトの表示文字列
	private static List<String> displayConflict(
			DiffLine diffX, DiffLine diffY) {
		List<String> lines = new ArrayList<String>();
		int xMode = diffX.getMode();
		int yMode = diffY.getMode();
		lines.add("<<<<<<");
		if (xMode == DiffLine.MODE_INSERT) {
			lines.add("+ " + diffX.getRevised());
		} else if (xMode == DiffLine.MODE_DELETE) {
			lines.add("- " + diffX.getOriginal());
		} else {
			lines.add("- " + diffX.getOriginal());
			lines.add("+ " + diffX.getRevised());
		}
		lines.add("======");
		if (yMode == DiffLine.MODE_INSERT) {
			lines.add("+ " + diffY.getRevised());
		} else if (yMode == DiffLine.MODE_DELETE) {
			lines.add("- " + diffY.getOriginal());
		} else {
			lines.add("- " + diffY.getOriginal());
			lines.add("+ " + diffY.getRevised());
		}
		lines.add(">>>>>>");
		return lines;
	}

	// 行の単独CHANGEがあったときの各文字の処理
	private static boolean mergeChangeChar(
			List<String> linesZ, List<String> linesConflict,
			String strColor, List<String> linesColor,
			String strBase,
			List<DiffChar> listDiff) {
		D.dprint_method_start();
		D.dprint(strBase);
		StringBuffer sb = new StringBuffer();
		StringBuffer sbC = new StringBuffer();
		int positionB = 0;
		Iterator<DiffChar> iter = listDiff.iterator();
		while (iter.hasNext()) {
			DiffChar diff = iter.next();
			D.dprint(diff);
			D.dprint(positionB);
			if (positionB < diff.getPosition()) {
				for (int i=positionB;
						i<diff.getPosition(); i++) {
					sb.append(strBase.charAt(i));
					sbC.append(strBase.charAt(i));
				}
				positionB = diff.getPosition() ;
//				positionB = diff.getPosition() - 1;
			}
			int mode = diff.getMode();
			if (mode == DiffChar.MODE_INSERT) {
				sb.append(diff.getRevised());
				sbC.append(strColor);
				sbC.append(diff.getRevised());
				sbC.append(COL_END);
			} else if (mode == DiffChar.MODE_DELETE) {
				sbC.append(strColor);
				sbC.append(DEL_START);
				sbC.append(diff.getOriginal());
				sbC.append(DEL_END);
				sbC.append(COL_END);
				positionB += 1;
			} else {
				sb.append(diff.getRevised());
				sbC.append(strColor);
				sbC.append(DEL_START);
				sbC.append(diff.getOriginal());
				sbC.append(DEL_END);
				sbC.append(diff.getRevised());
				sbC.append(COL_END);
				positionB += 1;
			}
			D.dprint(positionB);
		}
		for (int i=positionB; i<strBase.length(); i++) {
			sb.append(strBase.charAt(i));
			sbC.append(strBase.charAt(i));
		}
		String strZ = sb.toString();
		linesZ.add(strZ);
		String strC = sbC.toString();
		linesColor.add(strC);
		D.dprint_method_end();
		return true;
	}

	// X,Yとも行のINSERTだったときの各文字の処理
	private static boolean mergeInsert(
			List<String> linesZ, List<String> linesConflict,
			List<String> linesColor,
			String strX,
			String strY ) {
		D.dprint_method_start();
		boolean flag =true;
		DiffLine diffLine = createDiffChar(
				0, strX, strY);
		List<DiffChar> listDiff = diffLine.getListDiffChar();
		StringBuffer sb = new StringBuffer();
		StringBuffer sbC = new StringBuffer();
		int positionB = 0;
		Iterator<DiffChar> iter = listDiff.iterator();
		while (iter.hasNext()) {
			DiffChar diff = iter.next();
			D.dprint(diff);
			if (positionB < diff.getPosition()) {
				sbC.append(M_COL);
				for (int i=positionB;
						i<Integer.min(
								diff.getPosition(),
								strX.length());
						i++) {
					sb.append(strX.charAt(i));
					sbC.append(strX.charAt(i));
				}
				sbC.append(COL_END);
				positionB = diff.getPosition();
			}
			int mode = diff.getMode();
			char cOriginal = diff.getOriginal().charAt(0);
			char cRevised = diff.getRevised().charAt(0);
			if (mode == DiffChar.MODE_INSERT) {
				sb.append(cRevised);
				sbC.append(Y_COL);
				sbC.append(cRevised);
				sbC.append(COL_END);
			} else if (mode == DiffChar.MODE_DELETE) {
				sb.append(cOriginal);
				sbC.append(X_COL);
				sbC.append(cOriginal);
				sbC.append(COL_END);
				positionB += 1;
			} else {
				sbC.append(C_COL);
				sbC.append("<<<+");
				sbC.append(cOriginal);
				sbC.append("===+");
				sbC.append(cRevised);
				sbC.append(">>>");
				sbC.append(COL_END);
				flag = false;
			}
		}
		sbC.append(M_COL);
		for (int i=positionB; i<strX.length(); i++) {
			sb.append(strX.charAt(i));
			sbC.append(strX.charAt(i));
		}
		sbC.append(COL_END);
		String strZ = sb.toString();
		linesZ.add(strZ);
		String strC = sbC.toString();
		linesColor.add(strC);
		D.dprint_method_end();
		return flag;
	}


	// X,Yいずれかの行のCHANGEだったときの各文字の処理
	private static boolean mergeChange(
			List<String> linesZ, List<String> linesConflict,
			List<String> linesColor,
			String strBase,
			List<DiffChar> listDiffX,
			List<DiffChar> listDiffY) {
		D.dprint_method_start();
		D.dprint(strBase);
		StringBuffer sb = new StringBuffer();
		StringBuffer sbC = new StringBuffer();
		int positionB = 0;
		DiffChar diffX = null;
		DiffChar diffY = null;
		Iterator<DiffChar> iterX = null;
		Iterator<DiffChar> iterY = null;
		if (listDiffX != null) {
			iterX = listDiffX.iterator();
			diffX = iterX.next();
		}
		if (listDiffY != null) {
			iterY = listDiffY.iterator();
			diffY = iterY.next();
		}
		while ((diffX != null) || (diffY != null)) {
			D.dprint(diffX);
			D.dprint(diffY);
			if ((diffY == null) ||
					((diffX != null) &&
					(diffX.getPosition()
							< diffY.getPosition()))) {
				if (positionB < diffX.getPosition()) {
					for (int i=positionB;
							i<Integer.min(
									diffX.getPosition(),
									strBase.length());
							i++) {
						sb.append(strBase.charAt(i));
						sbC.append(strBase.charAt(i));
					}
					positionB = diffX.getPosition();
				}
				int xMode = diffX.getMode();
				char cOriginal = diffX.getOriginal().
						charAt(0);
				char cRevised = diffX.getRevised().
						charAt(0);
				if (xMode == DiffLine.MODE_INSERT) {
					sb.append(cRevised);
					sbC.append(X_COL);
					sbC.append(cRevised);
					sbC.append(COL_END);
				} else if (xMode == DiffLine.MODE_DELETE) {
					sbC.append(X_COL);
					sbC.append(DEL_START);
					sbC.append(cOriginal);
					sbC.append(DEL_END);
					sbC.append(COL_END);
					positionB += 1;
				} else {
					sb.append(cRevised);
					sbC.append(X_COL);
					sbC.append(DEL_START);
					sbC.append(cOriginal);
					sbC.append(DEL_END);
					sbC.append(cRevised);
					sbC.append(COL_END);
					positionB += 1;
				}
				if ((diffX != null) && (iterX.hasNext())) {
					diffX = iterX.next();
				} else {
					diffX = null;
				}
			} else if ((diffX == null) ||
					((diffY != null) &&
					(diffX.getPosition()
							> diffY.getPosition()))) {
				if (positionB < diffY.getPosition()) {
					for (int i=positionB;
							i<Integer.min(
									diffY.getPosition(),
									strBase.length());
							i++) {
						sb.append(strBase.charAt(i));
						sbC.append(strBase.charAt(i));
					}
					positionB = diffY.getPosition();
				}
				int yMode = diffY.getMode();
				char cOriginal = diffY.getOriginal().
						charAt(0);
				char cRevised = diffY.getRevised().
						charAt(0);
				if (yMode == DiffLine.MODE_INSERT) {
					sb.append(cRevised);
					sbC.append(Y_COL);
					sbC.append(cRevised);
					sbC.append(COL_END);
				} else if (yMode == DiffLine.MODE_DELETE) {
					sbC.append(Y_COL);
					sbC.append(DEL_START);
					sbC.append(cOriginal);
					sbC.append(DEL_END);
					sbC.append(COL_END);
					positionB += 1;
				} else {
					sb.append(cRevised);
					sbC.append(Y_COL);
					sbC.append(DEL_START);
					sbC.append(cOriginal);
					sbC.append(DEL_END);
					sbC.append(cRevised);
					sbC.append(COL_END);
					positionB += 1;
				}
				if ((diffY != null) && (iterY.hasNext())) {
					diffY = iterY.next();
				} else {
					diffY = null;
				}
			} else {
				if (positionB < diffX.getPosition()) {
					for (int i=positionB;
							i<diffX.getPosition(); i++) {
						sb.append(strBase.charAt(i));
						sbC.append(strBase.charAt(i));
					}
					positionB = diffX.getPosition();
				}
				int xMode = diffX.getMode();
				int yMode = diffY.getMode();
				if ((xMode == DiffChar.MODE_INSERT) &&
						(yMode == DiffChar.MODE_DELETE)) {
					sb.append(diffX.getRevised());
					sbC.append(Y_COL);
					sbC.append(DEL_START);
					sbC.append(diffY.getOriginal().charAt(0));
					sbC.append(DEL_END);
					sbC.append(COL_END);
					sbC.append(X_COL);
					sbC.append(diffX.getRevised().charAt(0));
					sbC.append(COL_END);
					positionB += 1;
				} else if ((yMode == DiffChar.MODE_INSERT) &&
						(xMode == DiffChar.MODE_DELETE)) {
					sb.append(diffY.getRevised());
					sbC.append(X_COL);
					sbC.append(DEL_START);
					sbC.append(diffX.getOriginal().charAt(0));
					sbC.append(DEL_END);
					sbC.append(COL_END);
					sbC.append(Y_COL);
					sbC.append(diffY.getRevised().charAt(0));
					sbC.append(COL_END);
					positionB += 1;
				} else if ((xMode == DiffChar.MODE_CHANGE) &&
						(yMode == DiffChar.MODE_DELETE)) {
					sb.append(diffX.getRevised());
					sbC.append(Y_COL);
					sbC.append(DEL_START);
					sbC.append(diffY.getOriginal().charAt(0));
					sbC.append(DEL_END);
					sbC.append(COL_END);
					sbC.append(X_COL);
					sbC.append(diffX.getRevised().charAt(0));
					sbC.append(COL_END);
					positionB += 1;
				} else if ((yMode == DiffChar.MODE_CHANGE) &&
						(xMode == DiffChar.MODE_DELETE)) {
					sb.append(diffY.getRevised());
					sbC.append(X_COL);
					sbC.append(DEL_START);
					sbC.append(diffX.getOriginal().charAt(0));
					sbC.append(DEL_END);
					sbC.append(COL_END);
					sbC.append(Y_COL);
					sbC.append(diffY.getRevised().charAt(0));
					sbC.append(COL_END);
					positionB += 1;
				} else if ((xMode == DiffChar.MODE_INSERT)
						&& (yMode == DiffChar.MODE_CHANGE)) {
					if (diffX.getRevised().charAt(0)
							== diffY.getRevised().charAt(0)) {
						sb.append(diffX.getRevised());
						sbC.append(Y_COL);
						sbC.append(DEL_START);
						sbC.append(diffY.getOriginal().
								charAt(0));
						sbC.append(DEL_END);
						sbC.append(COL_END);
						sbC.append(M_COL);
						sbC.append(diffX.getRevised().
								charAt(0));
						sbC.append(COL_END);
						positionB += 1;
					} else {
						String str = displayConflictChar(diffX, diffY);
						linesConflict.add(str);
						linesZ.add(str);
						linesColor.add(C_COL + str + COL_END);
						D.dprint("コンフリクトmergeChange4");
						D.dprint_method_end();
						return false;
					}
				} else if ((yMode == DiffChar.MODE_INSERT)
						&& (xMode == DiffChar.MODE_CHANGE)) {
					if (diffY.getRevised().charAt(0)
							== diffX.getRevised().charAt(0)) {
						sb.append(diffY.getRevised());
						sbC.append(X_COL);
						sbC.append(DEL_START);
						sbC.append(diffX.getOriginal().
								charAt(0));
						sbC.append(DEL_END);
						sbC.append(COL_END);
						sbC.append(M_COL);
						sbC.append(diffY.getRevised().
								charAt(0));
						sbC.append(COL_END);
						positionB += 1;
					} else {
						String str = displayConflictChar(
								diffX, diffY);
						linesConflict.add(str);
						linesZ.add(str);
						linesColor.add(C_COL + str + COL_END);
						D.dprint("コンフリクトmergeChange4");
						D.dprint_method_end();
						return false;
					}
				} else {
					// xMode == yMode
					assert(xMode == yMode);
					char cOriginal = diffX.getOriginal().
							charAt(0);
					char cRevised = diffX.getRevised().
							charAt(0);
					if (xMode == DiffLine.MODE_INSERT) {
						if (! diffX.getRevised().equals(
								diffY.getRevised())) {
							String str = displayConflictChar(
									diffX, diffY);
							linesConflict.add(str);
							linesZ.add(str);
							linesColor.add(C_COL
									+ str + COL_END);
							D.dprint("コンフリクトmergeChange2");
							D.dprint_method_end();
							return false;
						}
						sb.append(cRevised);
						sbC.append(M_COL);
						sbC.append(cRevised);
						sbC.append(COL_END);
					} else if (xMode == DiffLine.MODE_DELETE) {
						sbC.append(M_COL);
						sbC.append(DEL_START);
						sbC.append(cOriginal);
						sbC.append(DEL_END);
						sbC.append(COL_END);
						positionB += 1;
					} else {
						// CHANGE
						if (! diffX.getRevised().equals(
								diffY.getRevised())) {
							String str = displayConflictChar(
									diffX, diffY);
							linesConflict.add(str);
							linesZ.add(str);
							linesColor.add(C_COL
									+ str + COL_END);
							D.dprint("コンフリクトmergeChange3");
							D.dprint_method_end();
							return false;
						}
						sb.append(cRevised);
						sbC.append(M_COL);
						sbC.append(DEL_START);
						sbC.append(cOriginal);
						sbC.append(DEL_END);
						sbC.append(cRevised);
						sbC.append(COL_END);
						positionB += 1;
					}
				}
				if (iterX.hasNext()) {
					diffX = iterX.next();
				} else {
					diffX = null;
				}
				if (iterY.hasNext()) {
					diffY = iterY.next();
				} else {
					diffY = null;
				}
			}
		}
		for (int i=positionB; i<strBase.length(); i++) {
			sb.append(strBase.charAt(i));
			sbC.append(strBase.charAt(i));
		}
		String strZ = sb.toString();
		linesZ.add(strZ);
		String strC = sbC.toString();
		linesColor.add(strC);
		D.dprint_method_end();
		return true;
	}


	public static boolean merge(
			List<String> linesZ, List<String> linesConflict,
			List<String> linesColor,
			List<String> linesBase,
			List<DiffLine> listDiffX,
			List<DiffLine> listDiffY) {
		D.dprint_method_start();
		boolean flagConflict = true;
		int positionB = 0;
		Iterator<DiffLine> iterX = listDiffX.iterator();
		Iterator<DiffLine> iterY = listDiffY.iterator();
		DiffLine diffX;
		DiffLine diffY;
		if (iterX.hasNext()) {
			diffX = iterX.next();
		} else {
			diffX = null;
		}
		if (iterY.hasNext()) {
			diffY = iterY.next();
		} else {
			diffY = null;
		}
		while ((diffX != null) || (diffY != null)) {
			D.dprint("merge while Loop");
			int xMode = -1;
			if (diffX != null) {
				xMode = diffX.getMode();
			}
			int yMode = -1;
			if (diffY != null) {
				yMode = diffY.getMode();
			}
			if ((diffY == null) ||
					((diffX != null) &&
					(diffX.getPosition()
							< diffY.getPosition()))) {
				if (positionB < diffX.getPosition()) {
					for (int i=positionB;
							i<Integer.min(
									diffX.getPosition(),
									linesBase.size());
							i++) {
						linesZ.add(linesBase.get(i));
						linesColor.add(linesBase.get(i));
					}
					positionB = diffX.getPosition();
				}
				if (xMode == DiffLine.MODE_INSERT) {
					linesZ.add(diffX.getRevised());
					linesColor.add(X_COL +
							diffX.getRevised()
							+ COL_END);
				} else if (xMode == DiffLine.MODE_DELETE) {
					linesColor.add(X_COL +
							DEL_START +
							diffX.getOriginal()
							+ DEL_END
							+ COL_END);
					positionB += 1;
				} else {
					boolean flag = mergeChangeChar(
						linesZ, linesConflict,
						X_COL, linesColor,
						linesBase.get(positionB),
						diffX.getListDiffChar());
					if (! flag) {
						flagConflict = false;
					}
					positionB += 1;
				}
				if (iterX.hasNext()) {
					diffX = iterX.next();
				} else {
					diffX = null;
				}
			} else if ((diffX == null) ||
					((diffY != null) &&
					(diffX.getPosition() > diffY.getPosition()))) {
				if (positionB < diffY.getPosition()) {
					for (int i=positionB;
							i<Integer.min(
									diffY.getPosition(),
									linesBase.size());
							i++) {
						linesZ.add(linesBase.get(i));
						linesColor.add(linesBase.get(i));
					}
					positionB = diffY.getPosition();
				}
				if (yMode == DiffLine.MODE_INSERT) {
					linesZ.add(diffY.getRevised());
					linesColor.add(Y_COL +
							diffY.getRevised()
							+ COL_END);
				} else if (yMode == DiffLine.MODE_DELETE) {
					linesColor.add(Y_COL +
							DEL_START +
							diffY.getOriginal()
							+ DEL_END
							+ COL_END);
					positionB += 1;
				} else {
					boolean flag = mergeChangeChar(
						linesZ, linesConflict,
						Y_COL, linesColor,
						linesBase.get(positionB),
						diffY.getListDiffChar());
					if (! flag) {
						flagConflict =false;
					}
					positionB += 1;
				}
				if (iterY.hasNext()) {
					diffY = iterY.next();
				} else {
					diffY = null;
				}
			} else {
				// Xの差異とYの差異が同じ位置
				if (positionB < diffX.getPosition()) {
					for (int i=positionB;
							i<diffX.getPosition(); i++) {
						linesZ.add(linesBase.get(i));
						linesColor.add(linesBase.get(i));
					}
					positionB = diffX.getPosition();
				}
				if (xMode != yMode) {
					if ((xMode == DiffLine.MODE_INSERT)
							&& (yMode == DiffLine.MODE_DELETE)) {
						linesZ.add(diffX.getRevised());
						linesColor.add(Y_COL +
								DEL_START +
								diffY.getOriginal()
								+ DEL_END
								+ COL_END);
						linesColor.add(X_COL +
								diffX.getRevised()
								+ COL_END);
						positionB += 1;
					} else if ((yMode == DiffLine.MODE_INSERT)
							&& (xMode == DiffLine.MODE_DELETE)) {
						linesZ.add(diffY.getRevised());
						linesColor.add(X_COL +
								DEL_START +
								diffX.getOriginal()
								+ DEL_END
								+ COL_END);
						linesColor.add(Y_COL +
								diffY.getRevised()
								+ COL_END);
						positionB += 1;
					} else if ((xMode == DiffLine.MODE_CHANGE)
							&& (yMode == DiffLine.MODE_DELETE)) {
						linesZ.add(diffX.getRevised());
						linesColor.add(M_COL +
								DEL_START +
								diffY.getOriginal()
								+ DEL_END
								+ COL_END);
						linesColor.add(X_COL +
								diffX.getRevised()
								+ COL_END);
						positionB += 1;
					} else if ((yMode == DiffLine.MODE_CHANGE)
							&& (xMode == DiffLine.MODE_DELETE)) {
						linesZ.add(diffY.getRevised());
						linesColor.add(M_COL +
								DEL_START +
								diffX.getOriginal()
								+ DEL_END
								+ COL_END);
						linesColor.add(Y_COL +
								diffY.getRevised()
								+ COL_END);
						positionB += 1;
					} else if ((xMode == DiffLine.MODE_CHANGE)
							&& (yMode == DiffLine.MODE_INSERT)) {
						if (diffX.getRevised().equals(
								diffY.getRevised())) {
							linesZ.add(diffX.getRevised());
							linesColor.add(X_COL +
									DEL_START +
									diffX.getOriginal()
									+ DEL_END
									+ COL_END);
							linesColor.add(M_COL +
									diffX.getRevised()
									+ COL_END);
							positionB += 1;
						} else {
							flagConflict = false;
							List<String> lines
									= displayConflict(
									diffX, diffY);
							linesConflict.addAll(lines);
							D.dprint("コンフリクトmerge5");
//							D.dprint_method_end();
//							return false;
//							linesZ.add("コンフリクトmerge5");
							linesZ.addAll(lines);
							linesColor.add(C_COL);
							linesColor.addAll(lines);
							linesColor.add(COL_END);
							positionB += 1;
						}
					} else if ((yMode == DiffLine.MODE_CHANGE)
							&& (xMode == DiffLine.MODE_INSERT)) {
						if (diffY.getRevised().equals(
								diffX.getRevised())) {
							linesZ.add(diffY.getRevised());
							linesColor.add(Y_COL +
									DEL_START +
									diffY.getOriginal()
									+ DEL_END
									+ COL_END);
							linesColor.add(M_COL +
									diffY.getRevised()
									+ COL_END);
							positionB += 1;
						} else {
							flagConflict = false;
							List<String> lines
									= displayConflict(
									diffX, diffY);
							linesConflict.addAll(lines);
							D.dprint("コンフリクトmerge5");
//							D.dprint_method_end();
//							return false;
//							linesZ.add("コンフリクトmerge5");
							linesZ.addAll(lines);
							linesColor.add(C_COL);
							linesColor.addAll(lines);
							linesColor.add(COL_END);
							positionB += 1;
						}
					}
				} else {
					assert(xMode == yMode);
					if (xMode == DiffLine.MODE_INSERT) {
						if (! diffX.getRevised().equals(
								diffY.getRevised())) {
							boolean flag = mergeInsert(
									linesZ, linesConflict,
									linesColor,
									diffX.getRevised(),
									diffY.getRevised());
							if (! flag) {
								flagConflict = false;
								List<String> lines
										= displayConflict(
										diffX, diffY);
								linesConflict.addAll(lines);
								D.dprint("コンフリクトmerge2");
		//						D.dprint_method_end();
		//						return false;
//								linesZ.add("コンフリクトmerge2");
								linesZ.addAll(lines);
								linesColor.add(C_COL); //改行多い
								linesColor.addAll(lines);
								linesColor.add(COL_END);
							}
						} else {
							linesZ.add(diffX.getRevised());
							linesColor.add(M_COL +
									diffX.getRevised()
									+ COL_END);
						}
					} else if (xMode == DiffLine.MODE_DELETE) {
						linesColor.add(M_COL +
								DEL_START +
								diffY.getOriginal()
								+ DEL_END
								+ COL_END);
						positionB += 1;
					} else {
						// CHANGE
						if (! diffX.getRevised().equals(
								diffY.getRevised())) {
							boolean flag = mergeChange(
									linesZ, linesConflict,
									linesColor,
									linesBase.get(positionB),
									diffX.getListDiffChar(),
									diffY.getListDiffChar());
							if (! flag) {
								flagConflict = false;
								List<String> lines
										= displayConflict(
										diffX, diffY);
								linesConflict.addAll(lines);
								D.dprint("コンフリクトmerge3");
		//						D.dprint_method_end();
		//						return false;
								linesZ.add("コンフリクトmerge3");
								linesZ.addAll(lines);
								linesColor.add(C_COL);
								linesColor.addAll(lines);
								linesColor.add(COL_END);
							}
							positionB += 1;
						} else {
							linesZ.add(diffX.getRevised());
							linesColor.add(M_COL +
									DEL_START +
									diffX.getOriginal()
									+ DEL_END
									+ COL_END);
							linesColor.add(M_COL +
									diffX.getRevised()
									+ COL_END);
							positionB += 1;
						}
					}
				}
				if (iterX.hasNext()) {
					diffX = iterX.next();
				} else {
					diffX = null;
				}
				if (iterY.hasNext()) {
					diffY = iterY.next();
				} else {
					diffY = null;
				}
			}
			D.dprint(positionB);
		}
		for (int i=positionB; i<linesBase.size(); i++) {
			linesZ.add(linesBase.get(i));
			linesColor.add(linesBase.get(i));
		}
		D.dprint_method_end();
		return flagConflict;
	}

	public static List<DiffLine> createDiffList(
			List<String> linesA, List<String> linesB ) {
        D.dprint_method_start();
        D.dprint(linesA);
        D.dprint(linesB);
		List<DiffLine> listDiffLine
				= new ArrayList<DiffLine>();
		Patch<String> patch = DiffUtils.diff(linesA, linesB);
        for (Delta<String> delta : patch.getDeltas()) {
        	if (delta.getOriginal().size() == 0) {
        		// 追加
        		for (int i=0; i<delta.getRevised().size();
        				i++ ) {
        			D.dprint(delta.getOriginal().getPosition());
            		DiffLine diffLine = new DiffLine(
            				DiffLine.MODE_INSERT,
            				delta.getOriginal().getPosition(),
            				null,
            				(String) delta.getRevised().
            						getLines().get(i)
            				);
            		listDiffLine.add(diffLine);
        		}
        	} else if (delta.getRevised().size() == 0) {
        		// 削除
        		for (int i=0; i<delta.getOriginal().size();
        				i++ ) {
            		DiffLine diffLine = new DiffLine(
            				DiffLine.MODE_DELETE,
            				delta.getOriginal().getPosition(),
            				(String)delta.getOriginal().
            						getLines().get(i),
            				null
            				);
            		listDiffLine.add(diffLine);
        		}
        	} else {
        		// 置換
        		createDiffListChange(listDiffLine, delta);
        	}
        }
//        D.dprint(listDiffLine);
        Iterator<DiffLine> iter = listDiffLine.iterator();
        while (iter.hasNext()) {
        	D.dprint(iter.next());
        }
        D.dprint_method_end();
        return listDiffLine;
	}


	private static void createDiffListChange(
			List<DiffLine> listDiffLine, Delta<String> delta ) {
		D.dprint_method_start();
		if (delta.getOriginal().size()
				== delta.getRevised().size()) {
			createDiffListChangeSame(listDiffLine, delta);
		} else {
			int i;
    		for (i=0;
    				i<Integer.min(delta.getOriginal().size(),
    						delta.getRevised().size());
    				i++ ) {
    			DiffLine diffLine = createDiffCharChange(delta, i);
    			listDiffLine.add(diffLine);
    		}
    		int ii = i;
    		for (i=ii; i<delta.getOriginal().size(); i++ ) {
	    		DiffLine diffLine = new DiffLine(
	    				DiffLine.MODE_DELETE,
	    				delta.getOriginal().getPosition()+i,
	    				(String) delta.getOriginal().
	    						getLines().get(i),
	    				null
	    				);
	    		listDiffLine.add(diffLine);
//    			D.dprint(diffLine);
    		}
    		for (i=ii; i<delta.getRevised().size(); i++ ) {
	    		DiffLine diffLine = new DiffLine(
	    				DiffLine.MODE_INSERT,
	    				delta.getOriginal().getPosition()+i,
	    				null,
	    				(String) delta.getRevised().
	    						getLines().get(i)
	    				);
	    		listDiffLine.add(diffLine);
//    			D.dprint(diffLine);
    		}

		}
		D.dprint_method_end();
	}

	private static void createDiffListChangeSame(
			List<DiffLine> listDiffLine, Delta<String> delta) {
		D.dprint_method_start();
		for (int i = 0; i < delta.getOriginal().size();
				i++) {
			DiffLine diffLine
					= createDiffCharChange(delta, i);
			listDiffLine.add(diffLine);
		}
		D.dprint_method_end();
	}

	private static DiffLine createDiffCharChange(
			Delta<String> delta, int i) {
		D.dprint_method_start();
		String Src1 = (String)delta.getOriginal().
				getLines().get(i);
		String Dst1 = (String)delta.getRevised().
				getLines().get(i);
		DiffLine diffLine = createDiffChar(
//				delta,
				delta.getOriginal().getPosition() + i,
				Src1, Dst1);
		D.dprint_method_end();
		return diffLine;
	}


	private static DiffLine createDiffChar(
			int pos,
			String strA, String strB ) {
		D.dprint_method_start();
		DiffLine diffLine = new DiffLine(
				DiffLine.MODE_CHANGE,
				pos,
				strA, strB);
//		char[] src = strA.toCharArray();
//		char[] dst = strB.toCharArray();
		String[] src = strA.split("");
		String[] dst = strB.split("");
		Diff<String> diff = (Diff<String>)
				new Diff<String>(src, dst);
//		Diff<char[]> diff = (Diff<char[]>)
//				new Diff(src, dst);
		List<Difference> lDiff = diff.execute();
		if (lDiff.size() != 0) {
//			int index = 0;
			Iterator<Difference> iter = lDiff.iterator();
			while (iter.hasNext()) {
				D.dprint("while loop");
				Difference o = iter.next();
				DiffChar diffC;
				if (o.getDeletedEnd() == -1) {
					D.dprint("INSERT");
					for (int j=o.getAddedStart();
							j<o.getAddedEnd()+1; j++) {
						diffC = new DiffChar(
								DiffChar.MODE_INSERT,
								o.getDeletedStart(),
								null,
								dst[j]);
						diffLine.addDiffChar(diffC);
					}
				} else if (o.getAddedEnd() == -1) {
					D.dprint("DELETE");
					for (int j=o.getDeletedStart();
							j<o.getDeletedEnd()+1; j++) {
						diffC = new DiffChar(
								DiffChar.MODE_DELETE,
								j,
								src[j],
								null);
						diffLine.addDiffChar(diffC);
					}
				} else {
					D.dprint("OTHER");
					if ((o.getDeletedEnd()-o.getDeletedStart())
							== (o.getAddedEnd()-o.getAddedStart())) {
						D.dprint("SAME");
						for (int j=o.getDeletedStart();
								j<o.getDeletedEnd()+1; j++) {
							diffC = new DiffChar(
									DiffChar.MODE_CHANGE,
									j,
									src[j],
									dst[o.getAddedStart()
									    +j-o.getDeletedStart()]);
							diffLine.addDiffChar(diffC);
						}
					} else {
						for (int j=o.getDeletedStart();
								j<o.getDeletedEnd()+1; j++) {
							diffC = new DiffChar(
									DiffChar.MODE_DELETE,
									j,
									src[j],
									null);
							diffLine.addDiffChar(diffC);
						}
						if (o.getAddedStart() != o.getAddedEnd()) {
							// 経験則
							for (int j = o.getAddedStart(); j < o.getAddedEnd() + 1; j++) {
								diffC = new DiffChar(
										DiffChar.MODE_INSERT,
										o.getDeletedEnd() + 1,
										null,
										dst[j]);
								diffLine.addDiffChar(diffC);
							}
						}
					}
				}
			}
		}
		D.dprint_method_end();
		return diffLine;
	}

}
