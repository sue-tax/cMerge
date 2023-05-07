package cMerge;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
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

		System.out.println(String.format("CMerge version %.2f",
				VERSION));

//		String strFileBase = args[0];
		String strFileBase = "base.txt";
		System.out.println("ベースファイル:" + strFileBase);
//		String strFileX = args[1];
		String strFileX = "X.txt";
		System.out.println("ファイルX:" + strFileX);
//		String strFileY = args[2];
//		System.out.println("ファイルY:" + strFileY);

		// https://hito4-t.hatenablog.com/entry/2015/02/09/223006

        List<String> oldLines = null;
		List<String> newLines = null;
		try {
			oldLines = Files.readAllLines(
					FileSystems.getDefault().getPath(strFileBase), Charset.defaultCharset());
			newLines = Files.readAllLines(
					FileSystems.getDefault().getPath(strFileX), Charset.defaultCharset());
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

        Patch<String> patch = DiffUtils.diff(oldLines, newLines);
        for (Delta delta : patch.getDeltas()) {
        	System.out.println(delta);
        	System.out.println(delta.getOriginal());
        	System.out.println(delta.getRevised());

            System.out.println(String.format("[変更前(%d)行目]",
            		delta.getOriginal().getPosition() + 1));
            for (Object line : delta.getOriginal().getLines()) {
                System.out.println(line);
            }

            System.out.println("　↓");

            System.out.println(String.format("[変更後(%d)行目]",
            		delta.getRevised().getPosition() + 1));
            for (Object line : delta.getRevised().getLines()) {
                System.out.println(line);
            }
            System.out.println("　↓");

//            if ((delta.getOriginal().size() == 1)
//            		&& (delta.getRevised().size() == 1)) {
            if (delta.getOriginal().size() ==
            		delta.getRevised().size()) {
            	for (int i = 0; i < delta.getOriginal().size(); i++) {

            		//            	System.out.println(delta.getOriginal().getLines().get(0));
	            	String Src1 = (String) delta.getOriginal().
	            			getLines().get(i);
	    	        String[] src = Src1.split("");
	//    	        List<String> listSrc = Arrays.asList(src);
	            	String Dst1 = (String) delta.getRevised().
	            			getLines().get(i);
	                System.out.println(Src1);
	                System.out.println(Dst1);
	    	        String[] dst = Dst1.split("");
	//    	        List<String> listDst = Arrays.asList(dst);
	//                System.out.println(listSrc);
	//                System.out.println(listDst);
	//    	        System.out.println(src);
	//    	        System.out.println(dst);

	        		Diff<String> diff = (Diff<String>)new Diff(src, dst);
	        		List lDiff = diff.execute();
	//    			System.out.println(lDiff.size());
	        		if (lDiff.size() != 0) {
	        			int index = 0;
	            		Iterator iter = lDiff.iterator();
	            		while (iter.hasNext()) {
	            			Difference o = (Difference)iter.next();
	            			System.out.println(o.getDeletedStart());
	            			System.out.println(o.getDeletedEnd());
	            			System.out.println(o.getAddedStart());
	            			System.out.println(o.getAddedEnd());

	            			DiffChar diffC;
	            			if (o.getDeletedEnd() == -1) {
	            				diffC = new DiffChar(
	            						DiffChar.MODE_INSERT,
	            						o.getAddedStart(),
	            						"",
	            						dst[o.getAddedStart()]
	            						);
	            			} else if (o.getAddedEnd() == -1) {
	            				diffC = new DiffChar(
	            						DiffChar.MODE_DELETE,
	            						o.getDeletedStart(),
	            						src[o.getDeletedStart()],
	    	            				""
	            						);
	            			} else {
	            				diffC = new DiffChar(
	            						DiffChar.MODE_CHANGE,
	            						o.getDeletedStart(),
	            						src[o.getDeletedStart()],
	            						dst[o.getAddedStart()]
	            						);
	            			}
	            			System.out.println(diffC);
	            		}
	            	}
        		}

//    	        Patch<String> patchChar = DiffUtils.diff(listSrc, listDst);
//                System.out.println(patchChar);
//    	        for (Delta deltaChar : patchChar.getDeltas()) {
//    	        	System.out.println(deltaChar);
//    	        	System.out.println(deltaChar.getOriginal());
//    	        	System.out.println(deltaChar.getRevised());
//    	        }
//    	        [DeleteDelta, position: 3, lines: [E]]
//    	        		[position: 3, size: 1, lines: [E]]
//    	        		[position: 3, size: 0, lines: []]
//    	        		[InsertDelta, position: 5, lines: [F]]
//    	        		[position: 5, size: 0, lines: []]
//    	        		[position: 4, size: 1, lines: [F]]

            }


            System.out.println();

        }

	}

}
