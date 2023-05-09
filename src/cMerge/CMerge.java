package cMerge;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.incava.diff.Diff;
import org.incava.diff.Difference;

//import com.florianingerl.util.regex.Matcher;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;



public class CMerge {
	static final float VERSION = 0.10f;

	// https://ja.stackoverflow.com/questions/52019/git-%E3%81%AE-3-way-merge-%E3%81%A8%E3%81%AF%E5%85%B7%E4%BD%93%E7%9A%84%E3%81%AB%E3%81%A9%E3%81%AE%E3%82%88%E3%81%86%E3%81%AA%E3%82%A2%E3%83%AB%E3%82%B4%E3%83%AA%E3%82%BA%E3%83%A0%E3%81%A7%E3%81%99%E3%81%8B

	private static void displayConflict(
			List<String> linesConflict,
			DiffLine diffX, DiffLine diffY) {
		linesConflict.add("<<<<<<<");
		if (diffX.getMode() == DiffLine.MODE_INSERT) {
			linesConflict.add("+ " + diffX.getRevised());
		} else if (diffX.getMode() == DiffLine.MODE_DELETE) {
			linesConflict.add("- " + diffX.getOriginal());
		} else {
			linesConflict.add("- " + diffX.getOriginal());
			linesConflict.add("+ " + diffX.getRevised());
		}
		linesConflict.add("++====");
		if (diffY.getMode() == DiffLine.MODE_INSERT) {
			linesConflict.add("+ " + diffY.getRevised());
		} else if (diffY.getMode() == DiffLine.MODE_DELETE) {
			linesConflict.add("- " + diffY.getOriginal());
		} else {
			linesConflict.add("- " + diffY.getOriginal());
			linesConflict.add("+ " + diffY.getRevised());
		}
		linesConflict.add(">>>>>>>");
		return;
	}

	// 行のCHANGEがあったときの各文字の処理
	private static boolean mergeChangeChar(
			List<String> linesZ, List<String> linesConflict,
			String strBase,
			List<DiffChar> listDiff) {
		D.dprint_method_start();
		D.dprint(strBase);
		StringBuffer sb = new StringBuffer();
		int positionB = 0;
		Iterator<DiffChar> iter = listDiff.iterator();
		while (iter.hasNext()) {
			DiffChar diff = iter.next();
			if (positionB < diff.getPosition()) {
//				D.dprint(diff.getPosition());
				for (int i=positionB; i<diff.getPosition(); i++) {
//					D.dprint(i);
					sb.append(strBase.charAt(i));
				}
				positionB = diff.getPosition() ;
//				positionB = diff.getPosition() - 1;
			}
			if (diff.getMode() == DiffChar.MODE_INSERT) {
				sb.append(diff.getRevised());
			} else if (diff.getMode() == DiffChar.MODE_DELETE) {
				positionB += 1;
			} else {
				sb.append(diff.getRevised());
				positionB += 1;
			}
		}
		for (int i=positionB; i<strBase.length(); i++) {
			sb.append(strBase.charAt(i));
		}
		String strZ = sb.toString();
		D.dprint(strZ);
		linesZ.add(strZ);
		D.dprint_method_end();
		return true;
	}

	// X,Yとも行のCHANGEだったときの各文字の処理
	private static boolean mergeChange(
			List<String> linesZ, List<String> linesConflict,
			String strBase,
			List<DiffChar> listDiffX, List<DiffChar> listDiffY) {
		D.dprint_method_start();
		D.dprint(strBase);
		StringBuffer sb = new StringBuffer();
		int positionB = 0;
		Iterator<DiffChar> iterX = listDiffX.iterator();
		Iterator<DiffChar> iterY = listDiffY.iterator();
		DiffChar diffX = iterX.next();
		DiffChar diffY = iterY.next();
		while ((diffX != null) || (diffY != null)) {
			D.dprint("mergeChange while Loop");
			D.dprint(diffX);
			D.dprint(diffY);
			if ((diffY == null) ||
					((diffX != null) &&
					(diffX.getPosition() < diffY.getPosition()))) {
				if (positionB < diffX.getPosition()) {
					for (int i=positionB;
							i<Integer.min(diffX.getPosition(),strBase.length());
							i++) {
						sb.append(strBase.charAt(i));
					}
					positionB = diffX.getPosition();
				}
				if (diffX.getMode() == DiffLine.MODE_INSERT) {
					sb.append(diffX.getRevised().charAt(0));
				} else if (diffX.getMode() == DiffLine.MODE_DELETE) {
					positionB += 1;
				} else {
					sb.append(diffX.getRevised().charAt(0));
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
					}
					positionB = diffY.getPosition();
				}
				if (diffY.getMode() == DiffLine.MODE_INSERT) {
					sb.append(diffY.getRevised().charAt(0));
				} else if (diffY.getMode() == DiffLine.MODE_DELETE) {
					positionB += 1;
				} else {
					sb.append(diffY.getRevised().charAt(0));
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
					}
					positionB = diffX.getPosition();
				}
				if (diffX.getMode() != diffY.getMode()) {
					// 対応可能なパターンあるかも
//					linesConflict.add("<<<<<<<");
					D.dprint("コンフリクトmergeChange1");
					D.dprint_method_end();
					return false;
				}
				if (diffX.getMode() == DiffLine.MODE_INSERT) {
					if (! diffX.getRevised().equals(
							diffY.getRevised())) {
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
						D.dprint("コンフリクト");
						D.dprint_method_end();
						return false;
					}
					sb.append(diffX.getRevised().charAt(0));
					positionB += 1;
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
		}
		String strZ = sb.toString();
		D.dprint(strZ);
		linesZ.add(strZ);
		D.dprint_method_end();
		return true;
	}


	private static boolean merge(
			List<String> linesZ, List<String> linesConflict,
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
			D.dprint("merge while Loop");
			D.dprint(diffX);
			D.dprint(diffY);
			if ((diffY == null) ||
					((diffX != null) &&
					(diffX.getPosition() < diffY.getPosition()))) {
				if (positionB < diffX.getPosition()) {
					for (int i=positionB;
							i<Integer.min(diffX.getPosition(),linesBase.size());
							i++) {
						linesZ.add(linesBase.get(i));
					}
					positionB = diffX.getPosition();
				}
				if (diffX.getMode() == DiffLine.MODE_INSERT) {
					linesZ.add(diffX.getRevised());
				} else if (diffX.getMode() == DiffLine.MODE_DELETE) {
					positionB = diffX.getPosition() + 1;
				} else {
					boolean flag = mergeChangeChar(
						linesZ, linesConflict,
						linesBase.get(positionB),
						diffX.getListDiffChar());
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
					}
					positionB = diffY.getPosition();
				}
				if (diffY.getMode() == DiffLine.MODE_INSERT) {
					linesZ.add(diffY.getRevised());
				} else if (diffY.getMode() == DiffLine.MODE_DELETE) {
					positionB = diffY.getPosition() + 1;
				} else {
					boolean flag = mergeChangeChar(
						linesZ, linesConflict,
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
					}
					positionB = diffX.getPosition();
				}
				if (diffX.getMode() != diffY.getMode()) {
					// 対応可能なパターンあるかも
					flagConflict = false;
					displayConflict(linesConflict,
							diffX, diffY);
					D.dprint("コンフリクトmerge1");
//					D.dprint_method_end();
//					return false;
					linesZ.add("コンフリクトmerge1");
				} else {
					if (diffX.getMode() == DiffLine.MODE_INSERT) {
						D.dprint(diffX.getRevised());
						D.dprint(diffY.getRevised());
						if (! diffX.getRevised().equals(
								diffY.getRevised())) {
							flagConflict = false;
							displayConflict(linesConflict,
									diffX, diffY);
							D.dprint("コンフリクトmerge2");
	//						D.dprint_method_end();
	//						return false;
							linesZ.add("コンフリクトmerge2");
						} else {
							linesZ.add(diffX.getRevised());
						}
					} else if (diffX.getMode() == DiffLine.MODE_DELETE) {
						positionB += 1;
					} else {
						// CHANGE
						boolean flag = mergeChange(
								linesZ, linesConflict,
								linesBase.get(positionB),
								diffX.getListDiffChar(),
								diffY.getListDiffChar());
						if (! flag) {
							flagConflict = false;
							displayConflict(linesConflict,
									diffX, diffY);
							D.dprint("コンフリクトmerge3");
	//						D.dprint_method_end();
	//						return false;
							linesZ.add("コンフリクトmerge3");
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
		}
		D.dprint_method_end();
		return flagConflict;
	}

	private static List<DiffLine> createDiffList(
			List<String> linesA, List<String> linesB ) {
        D.dprint_method_start();
        D.dprint(linesA);
        D.dprint(linesB);
		List<DiffLine> listDiffLine = new ArrayList<DiffLine>();
		Patch<String> patch = DiffUtils.diff(linesA, linesB);
        for (Delta delta : patch.getDeltas()) {
//        	D.dprint(delta);
//        	D.dprint(delta.getOriginal());
//        	D.dprint(delta.getRevised());

        	if (delta.getOriginal().size() == 0) {
        		// 追加
//        		D.dprint(delta.getRevised().size());
        		for (int i=0; i<delta.getRevised().size(); i++ ) {
            		DiffLine diffLine = new DiffLine(
            				DiffLine.MODE_INSERT,
            				delta.getOriginal().getPosition(),
            				null,
            				(String) delta.getRevised().getLines().get(i)
//            				delta.getRevised().getLines()
            				);
//            		D.dprint(diffLine);
            		listDiffLine.add(diffLine);
        		}
        	} else if (delta.getRevised().size() == 0) {
        		// 削除
//        		D.dprint(delta.getOriginal().size());
        		for (int i=0; i<delta.getOriginal().size(); i++ ) {
            		DiffLine diffLine = new DiffLine(
            				DiffLine.MODE_DELETE,
            				delta.getOriginal().getPosition(),
            				(String)delta.getOriginal().getLines().get(i),
            				null
            				);
            		listDiffLine.add(diffLine);
//            		D.dprint(diffLine);
        		}
        	} else {
        		// 置換
        		createDiffListChange(listDiffLine, delta);
        	}
        }
        D.dprint(listDiffLine);
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
//		D.dprint(delta.getOriginal().size());
//		D.dprint(delta.getRevised().size());
		if (delta.getOriginal().size()
				== delta.getRevised().size()) {
			for (int i = 0; i < delta.getOriginal().size(); i++) {
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
						Difference o = (Difference) iter.next();

//						String str = "[" +
//								o.getDeletedStart() + "," +
//								o.getDeletedEnd() + "," +
//								o.getAddedStart() + "," +
//								o.getAddedEnd() + "]";
//						D.dprint(str);

						DiffChar diffC;
						if (o.getDeletedEnd() == -1) {
//							D.dprint(o.getAddedStart());
//							D.dprint(o.getAddedEnd());
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
							if ((o.getDeletedEnd()-o.getDeletedStart())
									== (o.getAddedEnd()-o.getAddedStart())) {
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
								for (int j=o.getAddedStart();
										j<o.getAddedEnd()+1; j++) {
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
				listDiffLine.add(diffLine);
			}
		} else {
			int i;
    		for (i=0;
    				i<Integer.min(delta.getOriginal().size(),
    						delta.getRevised().size());
    				i++ ) {
	    		// TODO 暫定　
    			DiffLine diffLine = new DiffLine(
	    				DiffLine.MODE_DELETE,
	    				delta.getOriginal().getPosition()+i,
	    				(String) delta.getOriginal().getLines().get(i),
	    				null
	    				);
	    		listDiffLine.add(diffLine);
	    		diffLine = new DiffLine(
	    				DiffLine.MODE_INSERT,
	    				delta.getOriginal().getPosition()+i,
	    				null,
	    				(String) delta.getRevised().getLines().get(i)
	    				);
	    		listDiffLine.add(diffLine);
//    			D.dprint(diffLine);
    		}
    		int ii = i;
    		for (i=ii; i<delta.getOriginal().size(); i++ ) {
	    		DiffLine diffLine = new DiffLine(
	    				DiffLine.MODE_DELETE,
	    				delta.getOriginal().getPosition()+i,
	    				(String) delta.getOriginal().getLines().get(i),
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
	    				(String) delta.getRevised().getLines().get(i)
	    				);
	    		listDiffLine.add(diffLine);
//    			D.dprint(diffLine);
    		}

		}
		D.dprint_method_end();
	}

	public static void main( String[] args ) {

		// http://dalmore.blog7.fc2.com/blog-entry-191.html

		// 右クリックで、
		// args[1] にファイル名 0からかも
		// 複数ファイルも可

		// Windows + R
		// shell:sendto
		// ファイルコピー
		// 設定ファイルも？
		// バッチファイルを作って、設定ファイルを切替える？

		//java -jar C:\Users\xxxxx\AppData\Roaming\Microsoft\Windows\SendTo\nanishi.jar %1

		D.dprint(String.format("CMerge version %.2f",
				VERSION));
		mainCmerge();
	}

	private static void mainCmerge() {

		String strFileBase, strFileX, strFileY;
//		String strFileBase = args[0];
//		String strFileBase = "base.txt";
		strFileBase = "改正前.txt";
		strFileBase = "旧所法8_4.txt";
		D.dprint("ベースファイル:" + strFileBase);

//		String strFileX = args[1];
//		String strFileX = "X.txt";
		strFileX = "コメント入り.txt";
		strFileX = "コメント所法8_4.txt";
		D.dprint("ファイルX:" + strFileX);
//		String strFileY = args[2];
//		String strFileY = "Y.txt";
		strFileY = "改正後.txt";
		strFileY = "新所法8_4.txt";
		D.dprint("ファイルY:" + strFileY);
		String strFileZ = "マージ.txt";

		// https://hito4-t.hatenablog.com/entry/2015/02/09/223006

        List<String> linesB = null;
        List<String> linesX = null;
		List<String> linesY = null;
		try {
			linesB = Files.readAllLines(
					FileSystems.getDefault().getPath(strFileBase),
					Charset.defaultCharset());
			linesX = Files.readAllLines(
					FileSystems.getDefault().getPath(strFileX),
					Charset.defaultCharset());
			linesY = Files.readAllLines(
					FileSystems.getDefault().getPath(strFileY),
					Charset.defaultCharset());
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

//		linesB = new ArrayList<String>();
//		linesB.add("AABB");
//		linesX = new ArrayList<String>();
//		linesX.add("AACCBBDD");
//		linesY = new ArrayList<String>();
//		linesY.add("AACCBB");

		List<DiffLine> listDiffX = createDiffList(
				linesB, linesX);
		List<DiffLine> listDiffY = createDiffList(
				linesB, linesY);

		List<String> linesZ = new ArrayList<String>();
		List<String> linesConflict = new ArrayList<String>();

		boolean flag = merge(
				linesZ, linesConflict,
				linesB, listDiffX, listDiffY);
		D.dprint(flag);
//		flag = true;
    	Path path1 = Paths.get(strFileZ); //パス
        try (BufferedWriter bw = Files.newBufferedWriter(
        		path1, StandardCharsets.UTF_8);
                PrintWriter pw = new PrintWriter(bw, true)) {
            Iterator<String> iter = linesZ.iterator();
            while (iter.hasNext()) {
            	pw.println(iter.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		if (flag) {
//	    	Path path1 = Paths.get(strFileZ); //パス
//	        try (BufferedWriter bw = Files.newBufferedWriter(
//	        		path1, StandardCharsets.UTF_8);
//	                PrintWriter pw = new PrintWriter(bw, true)) {
//	            Iterator<String> iter = linesZ.iterator();
//	            while (iter.hasNext()) {
//	            	pw.println(iter.next());
//	            }
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
		} else {
            Iterator<String> iter = linesConflict.iterator();
            while (iter.hasNext()) {
            	D.dprint(iter.next());
            }

		}

//		D.dprint(listDiffX);
//		D.dprint(listDiffY);

		return;

	}
}
