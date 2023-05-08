package cMerge;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
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

	private static boolean merge(
			List<String> linesZ, List<String> linesConflict,
			List<String> linesBase,
			List<DiffLine> listDiffX, List<DiffLine> listDiffY) {
		D.dprint_method_start();
		int indexX = 0;
		int indexY = 0;
		int indexB = 0;
		DiffLine diffX = listDiffX.get(indexX);
		DiffLine diffY = listDiffY.get(indexY);
		while (true) {
			if (diffX.getPosition() < diffY.getPosition()) {

			} else if (diffX.getPosition() > diffY.getPosition()) {

			} else {
				// 対応可能なパターンあり
			}
		}
		D.dprint_method_end();
		return true;
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
//        		for (int i=0; i<delta.getRevised().size(); i++ ) {
            		DiffLine diffLine = new DiffLine(
            				DiffLine.MODE_INSERT,
            				delta.getOriginal().getPosition(),
            				null,
            				delta.getRevised().getLines()
            				);
//            		D.dprint(diffLine);
            		listDiffLine.add(diffLine);
//        		}
        	} else if (delta.getRevised().size() == 0) {
        		// 削除
//        		D.dprint(delta.getOriginal().size());
//        		for (int i=0; i<delta.getOriginal().size(); i++ ) {
            		DiffLine diffLine = new DiffLine(
            				DiffLine.MODE_DELETE,
            				delta.getOriginal().getPosition(),
            				delta.getOriginal().getLines(),
            				null
            				);
            		listDiffLine.add(diffLine);
//            		D.dprint(diffLine);
//        		}
        	} else {
        		// 置換
        		createDiffListChange(listDiffLine, delta);

        	}
        }
        D.dprint(listDiffLine);
        D.dprint_method_end();
        return listDiffLine;
	}


	private static void createDiffListChange(
			List<DiffLine> listDiffLine, Delta delta ) {
		D.dprint_method_start();
		D.dprint(delta.getOriginal().size());
		D.dprint(delta.getRevised().size());
		if (delta.getOriginal().size()
				== delta.getRevised().size()) {
			for (int i = 0; i < delta.getOriginal().size(); i++) {
				List<String> listOriginal = new ArrayList<String>();
				listOriginal.add((String)delta.getOriginal().getLines().get(i));
				List<String> listRevised = new ArrayList<String>();
				listRevised.add((String)delta.getRevised().getLines().get(i));
				DiffLine diffLine = new DiffLine(
						DiffLine.MODE_CHANGE,
						delta.getOriginal().getPosition() + i,
						listOriginal, listRevised);
				String Src1 = listOriginal.get(0);
				String[] src = Src1.split("");
				String Dst1 = listRevised.get(0);
				String[] dst = Dst1.split("");
				Diff<String> diff = (Diff<String>) new Diff(src, dst);
				List lDiff = diff.execute();
				if (lDiff.size() != 0) {
					int index = 0;
					Iterator iter = lDiff.iterator();
					while (iter.hasNext()) {
						Difference o = (Difference) iter.next();
						DiffChar diffC;
						if (o.getDeletedEnd() == -1) {
							diffC = new DiffChar(
									DiffChar.MODE_INSERT,
									o.getAddedStart(),
									"",
									dst[o.getAddedStart()]);
						} else if (o.getAddedEnd() == -1) {
							diffC = new DiffChar(
									DiffChar.MODE_DELETE,
									o.getDeletedStart(),
									src[o.getDeletedStart()],
									"");
						} else {
							diffC = new DiffChar(
									DiffChar.MODE_CHANGE,
									o.getDeletedStart(),
									src[o.getDeletedStart()],
									dst[o.getAddedStart()]);
						}
						D.dprint(diffC);
						diffLine.addDiffChar(diffC);
					}
				}
				listDiffLine.add(diffLine);
			}
		} else {
			// TODO
			DiffLine diffLine = new DiffLine(
					DiffLine.MODE_CHANGE,
					delta.getOriginal().getPosition(),
					delta.getOriginal().getLines(),
					delta.getRevised().getLines());
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

//		String strFileBase = args[0];
		String strFileBase = "base.txt";
		D.dprint("ベースファイル:" + strFileBase);
//		String strFileX = args[1];
		String strFileX = "X.txt";
		D.dprint("ファイルX:" + strFileX);
//		String strFileY = args[2];
		String strFileY = "Y.txt";
		D.dprint("ファイルY:" + strFileY);

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

		List<DiffLine> listDiffX = createDiffList(
				linesB, linesX);
		List<DiffLine> listDiffY = createDiffList(
				linesB, linesY);
		return;

	}
}
