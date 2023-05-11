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
	static final float VERSION = 0.11f;

	private static String displayConflictChar(
			DiffChar diffX, DiffChar diffY) {
		StringBuffer sb = new StringBuffer();
		sb.append("<<<");
		if (diffX.getMode() == DiffLine.MODE_INSERT) {
			sb.append("+");
			sb.append(diffX.getRevised());
		} else if (diffX.getMode() == DiffLine.MODE_DELETE) {
			sb.append("-");
			sb.append(diffX.getOriginal());
		} else {
			sb.append("-");
			sb.append(diffX.getOriginal());
			sb.append("+");
			sb.append(diffX.getRevised());
		}
		sb.append("===");
		if (diffY.getMode() == DiffLine.MODE_INSERT) {
			sb.append("+");
			sb.append(diffY.getRevised());
		} else if (diffY.getMode() == DiffLine.MODE_DELETE) {
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

	private static List<String> displayConflict(
//			List<String> linesConflict,
			DiffLine diffX, DiffLine diffY) {
		List<String> lines = new ArrayList<String>();
		lines.add("<<<<<<<");
		if (diffX.getMode() == DiffLine.MODE_INSERT) {
			lines.add("+ " + diffX.getRevised());
		} else if (diffX.getMode() == DiffLine.MODE_DELETE) {
			lines.add("- " + diffX.getOriginal());
		} else {
			lines.add("- " + diffX.getOriginal());
			lines.add("+ " + diffX.getRevised());
		}
		lines.add("++====");
		if (diffY.getMode() == DiffLine.MODE_INSERT) {
			lines.add("+ " + diffY.getRevised());
		} else if (diffY.getMode() == DiffLine.MODE_DELETE) {
			lines.add("- " + diffY.getOriginal());
		} else {
			lines.add("- " + diffY.getOriginal());
			lines.add("+ " + diffY.getRevised());
		}
		lines.add(">>>>>>>");
		return lines;
	}

	// 行のCHANGEがあったときの各文字の処理
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
			if (positionB < diff.getPosition()) {
				for (int i=positionB; i<diff.getPosition(); i++) {
					sb.append(strBase.charAt(i));
					sbC.append(strBase.charAt(i));
				}
				positionB = diff.getPosition() ;
//				positionB = diff.getPosition() - 1;
			}
			if (diff.getMode() == DiffChar.MODE_INSERT) {
				sb.append(diff.getRevised());
				sbC.append("<font color=\"");
				sbC.append(strColor);
				sbC.append("\">");
				sbC.append(diff.getRevised());
				sbC.append("</font>");
			} else if (diff.getMode() == DiffChar.MODE_DELETE) {
				sbC.append("<font color=\"");
				sbC.append(strColor);
				sbC.append("\">");
				sbC.append("<del>");
				sbC.append(diff.getOriginal());
				sbC.append("</del>");
				sbC.append("</font>");
				positionB += 1;
			} else {
				sb.append(diff.getRevised());
				sbC.append("<font color=\"");
				sbC.append(strColor);
				sbC.append("\">");
				sbC.append("<del>");
				sbC.append(diff.getRevised());
				sbC.append("</del>");
				sbC.append("</font>");
				positionB += 1;
			}
		}
		for (int i=positionB; i<strBase.length(); i++) {
			sb.append(strBase.charAt(i));
			sbC.append(strBase.charAt(i));
		}
		String strZ = sb.toString();
		D.dprint(strZ);
		linesZ.add(strZ);
		String strC = sbC.toString();
		linesColor.add(strC);
		D.dprint_method_end();
		return true;
	}

	// X,Yとも行のCHANGEだったときの各文字の処理
	private static boolean mergeChange(
			List<String> linesZ, List<String> linesConflict,
			List<String> linesColor,
			String strBase,
			List<DiffChar> listDiffX, List<DiffChar> listDiffY) {
		D.dprint_method_start();
		D.dprint(strBase);
		StringBuffer sb = new StringBuffer();
		StringBuffer sbC = new StringBuffer();
		int positionB = 0;
		Iterator<DiffChar> iterX = listDiffX.iterator();
		Iterator<DiffChar> iterY = listDiffY.iterator();
		DiffChar diffX = iterX.next();
		DiffChar diffY = iterY.next();
		while ((diffX != null) || (diffY != null)) {
//			D.dprint("mergeChange while Loop");
			if ((diffY == null) ||
					((diffX != null) &&
					(diffX.getPosition() < diffY.getPosition()))) {
				if (positionB < diffX.getPosition()) {
					for (int i=positionB;
							i<Integer.min(diffX.getPosition(),strBase.length());
							i++) {
						sb.append(strBase.charAt(i));
						sbC.append(strBase.charAt(i));
					}
					positionB = diffX.getPosition();
				}
				if (diffX.getMode() == DiffLine.MODE_INSERT) {
					sb.append(diffX.getRevised().charAt(0));
					sbC.append("<font color=\"blue\">");
					sbC.append(diffX.getRevised().charAt(0));
					sbC.append("</font>");
				} else if (diffX.getMode() == DiffLine.MODE_DELETE) {
					sbC.append("<del>");
					sbC.append(diffX.getOriginal().charAt(0));
					sbC.append("</del>");
					positionB += 1;
				} else {
					sb.append(diffX.getRevised().charAt(0));
					sbC.append("<del>");
					sbC.append(diffX.getOriginal().charAt(0));
					sbC.append("</del>");
					sbC.append("<font color=\"blue\">");
					sbC.append(diffX.getRevised().charAt(0));
					sbC.append("</font>");
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
							i<Integer.min(diffY.getPosition(),strBase.length());
							i++) {
						sb.append(strBase.charAt(i));
						sbC.append(strBase.charAt(i));
					}
					positionB = diffY.getPosition();
				}
				if (diffY.getMode() == DiffLine.MODE_INSERT) {
					sb.append(diffY.getRevised().charAt(0));
					sbC.append("<font color=\"green\">");
					sbC.append(diffY.getRevised().charAt(0));
					sbC.append("</font>");
				} else if (diffY.getMode() == DiffLine.MODE_DELETE) {
					sbC.append("<del>");
					sbC.append(diffY.getOriginal().charAt(0));
					sbC.append("</del>");
					positionB += 1;
				} else {
					sb.append(diffY.getRevised().charAt(0));
					sbC.append("<font color=\"green\">");
					sbC.append("<del>");
					sbC.append(diffY.getOriginal().charAt(0));
					sbC.append("</del>");
					sbC.append(diffY.getRevised().charAt(0));
					sbC.append("</font>");
					positionB += 1;
				}
				if (iterY.hasNext()) {
					diffY = iterY.next();
				} else {
					diffY = null;
				}
			} else {
//				D.dprint(diffX.getPosition());
//				D.dprint(diffY.getPosition());
				if (positionB < diffX.getPosition()) {
					for (int i=positionB; i<diffX.getPosition(); i++) {
						sb.append(strBase.charAt(i));
						sbC.append(strBase.charAt(i));
					}
					positionB = diffX.getPosition();
				}
				// TODO 要検討
				if ((diffX.getMode() == DiffChar.MODE_INSERT) &&
						(diffY.getMode() == DiffChar.MODE_DELETE)) {
					sb.append(diffX.getRevised());
					sbC.append("<font color=\"green\">");
					sbC.append("<del>");
					sbC.append(diffY.getOriginal().charAt(0));
					sbC.append("</del>");
					sbC.append("</font>");
					sbC.append("<font color=\"blue\">");
					sbC.append(diffX.getRevised().charAt(0));
					sbC.append("</font>");
					positionB += 1;
				} else if ((diffY.getMode() == DiffChar.MODE_INSERT) &&
						(diffX.getMode() == DiffChar.MODE_DELETE)) {
					sb.append(diffY.getRevised());
					sbC.append("<font color=\"blue\">");
					sbC.append("<del>");
					sbC.append(diffX.getOriginal().charAt(0));
					sbC.append("</del>");
					sbC.append("</font>");
					sbC.append("<font color=\"green\">");
					sbC.append(diffY.getRevised().charAt(0));
					sbC.append("</font>");
					positionB += 1;
				} else if (diffX.getMode() != diffY.getMode()) {
					// 対応可能なパターンあるかも
					String str = displayConflictChar(diffX, diffY);
					linesConflict.add(str);
					linesZ.add(str);
					linesColor.add("<font color=\"red\">");
					linesColor.add(str);
					linesColor.add("</font>");
					D.dprint("コンフリクトmergeChange1");
					D.dprint_method_end();
					return false;
				} else {
					if (diffX.getMode() == DiffLine.MODE_INSERT) {
						if (! diffX.getRevised().equals(
								diffY.getRevised())) {
							String str = displayConflictChar(diffX, diffY);
							linesConflict.add(str);
							linesZ.add(str);
							linesColor.add("<font color=\"red\">");
							linesColor.add(str);
							linesColor.add("</font>");
							D.dprint("コンフリクトmergeChange2");
							D.dprint_method_end();
							return false;
						}
						sb.append(diffX.getRevised().charAt(0));
					} else if (diffX.getMode() == DiffLine.MODE_DELETE) {
						positionB += 1;
					} else {
						// CHANGE
						if (! diffX.getRevised().equals(
								diffY.getRevised())) {
							String str = displayConflictChar(diffX, diffY);
							linesConflict.add(str);
							linesZ.add(str);
							linesColor.add("<font color=\"red\">");
							linesColor.add(str);
							linesColor.add("</font>");
							D.dprint("コンフリクトmergeChange3");
							D.dprint_method_end();
							return false;
						}
						sb.append(diffX.getRevised().charAt(0));
//						sbC.append("<font color=\"green\">");
						sbC.append("<del>");
						sbC.append(diffY.getOriginal().charAt(0));
						sbC.append("</del>");
						sbC.append(diffY.getRevised().charAt(0));
//						sbC.append("</font>");
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
		D.dprint(strZ);
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
			List<DiffLine> listDiffX, List<DiffLine> listDiffY) {
		D.dprint_method_start();
		boolean flagConflict = true;
		int indexX = 0;
		int indexY = 0;
		int positionB = 0;
		DiffLine diffX = null;
		if (listDiffX.size() != 0) {
			diffX = listDiffX.get(indexX);
		}
		DiffLine diffY = null;
		if (listDiffY.size() != 0) {
			diffY = listDiffY.get(indexY);
		}
		while ((diffX != null) || (diffY != null)) {
//			D.dprint("merge while Loop");
			if ((diffY == null) ||
					((diffX != null) &&
					(diffX.getPosition() < diffY.getPosition()))) {
				if (positionB < diffX.getPosition()) {
					for (int i=positionB;
							i<Integer.min(diffX.getPosition(),linesBase.size());
							i++) {
						linesZ.add(linesBase.get(i));
						linesColor.add(linesBase.get(i));
					}
					positionB = diffX.getPosition();
				}
				if (diffX.getMode() == DiffLine.MODE_INSERT) {
					linesZ.add(diffX.getRevised());
					linesColor.add("<font color=\"blue\">" +
							diffX.getRevised()
							+ "</font>");
				} else if (diffX.getMode() == DiffLine.MODE_DELETE) {
					linesColor.add("<font color=\"blue\">" +
							"<del>" +
							diffX.getOriginal()
							+ "</del>"
							+ "</font>");
					positionB = diffX.getPosition() + 1;
				} else {
					boolean flag = mergeChangeChar(
						linesZ, linesConflict,
						"blue", linesColor,
						linesBase.get(positionB),
						diffX.getListDiffChar());
					if (! flag) {
						flagConflict = false;
					}
					positionB = diffX.getPosition() + 1;
				}
				indexX ++;
				if (indexX < listDiffX.size()) {
					diffX = listDiffX.get(indexX);
				} else {
					diffX = null;
				}
			} else if ((diffX == null) ||
					((diffY != null) &&
					(diffX.getPosition() > diffY.getPosition()))) {
				if (positionB < diffY.getPosition()) {
					for (int i=positionB;
							i<Integer.min(diffY.getPosition(),linesBase.size());
							i++) {
						linesZ.add(linesBase.get(i));
						linesColor.add(linesBase.get(i));
					}
					positionB = diffY.getPosition();
				}
				if (diffY.getMode() == DiffLine.MODE_INSERT) {
					linesZ.add(diffY.getRevised());
					linesColor.add("<font color=\"green\">" +
							diffY.getRevised()
							+ "</font>");
				} else if (diffY.getMode() == DiffLine.MODE_DELETE) {
					linesColor.add("<font color=\"green\">" +
							"<del>" +
							diffY.getOriginal()
							+ "</del>"
							+ "</font>");
					positionB = diffY.getPosition() + 1;
				} else {
					boolean flag = mergeChangeChar(
						linesZ, linesConflict,
						"green", linesColor,
						linesBase.get(positionB),
						diffY.getListDiffChar());
					positionB = diffY.getPosition() + 1;
				}
				indexY ++;
				if (indexY < listDiffY.size()) {
					diffY = listDiffY.get(indexY);
				} else {
					diffY = null;
				}
			} else {
				if (positionB < diffX.getPosition()) {
					for (int i=positionB; i<diffX.getPosition(); i++) {
						linesZ.add(linesBase.get(i));
						linesColor.add(linesBase.get(i));
					}
					positionB = diffX.getPosition();
				}
				if (diffX.getMode() != diffY.getMode()) {
					// 対応可能なパターンあるかも
					flagConflict = false;
					List<String> lines = displayConflict(
							diffX, diffY);
					linesConflict.addAll(lines);
					D.dprint("コンフリクトmerge1");
//					D.dprint_method_end();
//					return false;
					linesZ.add("コンフリクトmerge1");
					linesZ.addAll(lines);
					linesColor.add("<font color=\"red\">");
					linesColor.addAll(lines);
					linesColor.add("</font>");
				} else {
					if (diffX.getMode() == DiffLine.MODE_INSERT) {
						D.dprint(diffX.getRevised());
						D.dprint(diffY.getRevised());
						if (! diffX.getRevised().equals(
								diffY.getRevised())) {
							flagConflict = false;
							List<String> lines = displayConflict(
									diffX, diffY);
							linesConflict.addAll(lines);
							D.dprint("コンフリクトmerge2");
	//						D.dprint_method_end();
	//						return false;
							linesZ.add("コンフリクトmerge2");
							linesZ.addAll(lines);
							linesColor.add("<font color=\"red\">");
							linesColor.addAll(lines);
							linesColor.add("</font>");
						} else {
							linesZ.add(diffX.getRevised());
							linesColor.add("<font color=\"blue\">" +
									diffX.getRevised()
									+ "</font>");
						}
					} else if (diffX.getMode() ==
							DiffLine.MODE_DELETE) {
						linesColor.add(
								"<del>" +
								diffY.getOriginal()
								+ "</del>"
								); //+ "</font>");
						positionB += 1;
					} else {
						// CHANGE
						boolean flag = mergeChange(
								linesZ, linesConflict,
								linesColor,
								linesBase.get(positionB),
								diffX.getListDiffChar(),
								diffY.getListDiffChar());
						if (! flag) {
							flagConflict = false;
							List<String> lines = displayConflict(
									diffX, diffY);
							linesConflict.addAll(lines);
							D.dprint("コンフリクトmerge3");
	//						D.dprint_method_end();
	//						return false;
							linesZ.add("コンフリクトmerge3");
							linesZ.addAll(lines);
							linesColor.add("<font color=\"red\">");
							linesColor.addAll(lines);
							linesColor.add("</font>");
						}
						positionB += 1;
					}
				}
				indexX ++;
				if (indexX < listDiffX.size()) {
					diffX = listDiffX.get(indexX);
				} else {
					diffX = null;
				}
				indexY ++;
				if (indexY < listDiffY.size()) {
					diffY = listDiffY.get(indexY);
				} else {
					diffY = null;
				}
			}
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
		List<DiffLine> listDiffLine = new ArrayList<DiffLine>();
		Patch<String> patch = DiffUtils.diff(linesA, linesB);
        for (Delta delta : patch.getDeltas()) {
        	if (delta.getOriginal().size() == 0) {
        		// 追加
        		for (int i=0; i<delta.getRevised().size(); i++ ) {
            		DiffLine diffLine = new DiffLine(
            				DiffLine.MODE_INSERT,
            				delta.getOriginal().getPosition(),
            				null,
            				(String) delta.getRevised().getLines().get(i)
            				);
            		listDiffLine.add(diffLine);
        		}
        	} else if (delta.getRevised().size() == 0) {
        		// 削除
        		for (int i=0; i<delta.getOriginal().size(); i++ ) {
            		DiffLine diffLine = new DiffLine(
            				DiffLine.MODE_DELETE,
            				delta.getOriginal().getPosition(),
            				(String)delta.getOriginal().getLines().get(i),
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
			List<DiffLine> listDiffLine, Delta delta ) {
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
	    		// TODO 暫定　
    			// もし、各行が似ているならば、CHANGEも
    			// 考えられる。
    			DiffLine diffLine = createDiffCharChange(delta, i);
    			listDiffLine.add(diffLine);
//    			DiffLine diffLine = new DiffLine(
//	    				DiffLine.MODE_DELETE,
//	    				delta.getOriginal().getPosition()+i,
//	    				(String) delta.getOriginal().getLines().get(i),
//	    				null
//	    				);
//	    		listDiffLine.add(diffLine);
//	    		diffLine = new DiffLine(
//	    				DiffLine.MODE_INSERT,
//	    				delta.getOriginal().getPosition()+i,
//	    				null,
//	    				(String) delta.getRevised().getLines().get(i)
//	    				);
//	    		listDiffLine.add(diffLine);
//    			D.dprint(diffLine);
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
			List<DiffLine> listDiffLine, Delta delta) {
		D.dprint_method_start();
		for (int i = 0; i < delta.getOriginal().size(); i++) {
			DiffLine diffLine = createDiffCharChange(delta, i);
			listDiffLine.add(diffLine);
		}
		D.dprint_method_end();
	}

	private static DiffLine createDiffCharChange(Delta delta, int i) {
		D.dprint_method_start();
		String Src1 = (String)delta.getOriginal().getLines().get(i);
		String Dst1 = (String)delta.getRevised().getLines().get(i);
		DiffLine diffLine = new DiffLine(
				DiffLine.MODE_CHANGE,
				delta.getOriginal().getPosition() + i,
				Src1, Dst1);
		String[] src = Src1.split("");
		String[] dst = Dst1.split("");
		Diff<String> diff = (Diff<String>)
				new Diff(src, dst);
		List lDiff = diff.execute();
		if (lDiff.size() != 0) {
			int index = 0;
			Iterator iter = lDiff.iterator();
			while (iter.hasNext()) {
				D.dprint("while loop");
				Difference o = (Difference)iter.next();
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
