package application;
/**
 *
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author sue-t
 *
 */
public final class D {
	/** デバッグプリントの指示
	 * 0 プリントしない
	 * 1 コンソールへプリント
	 * 3 ログファイルへ書込み
	 */
	private static int c = 0;

	public static <T> void dprint(T value) {
		if (D.c == 1) {
			System.out.println(value);
		} else if (D.c == 3) {
			File file = new File("log.txt");
			FileWriter filewriter;
			try {
				filewriter = new FileWriter(file, true);
				String str = value.toString();
				filewriter.write(str);
				filewriter.write("\n");
				filewriter.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

	public static <T> void dprint_name( String str, T value) {
		if (D.c == 1) {
			System.out.print(str);
			System.out.print(" ");
			System.out.println(value);
		} else if (D.c == 3) {
			File file = new File("log.txt");
			FileWriter filewriter;
			try {
				filewriter = new FileWriter(file, true);
				filewriter.write(str);
				filewriter.write(" ");
				String strV = value.toString();
				filewriter.write(strV);
				filewriter.write("\n");
				filewriter.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

	public static void dprint_method_start() {
		if (D.c == 1) {
			System.out.print(Thread.currentThread().
					getStackTrace()[2].getClassName());
			System.out.print(" ");
			System.out.print(Thread.currentThread().
					getStackTrace()[2].getMethodName());
			System.out.println(" start");
		} else if (D.c == 3) {
			File file = new File("log.txt");
			FileWriter filewriter;
			try {
				filewriter = new FileWriter(file, true);
				filewriter.write(Thread.currentThread().
						getStackTrace()[2].getClassName());
				filewriter.write(" ");
				filewriter.write(Thread.currentThread().
						getStackTrace()[2].getMethodName());
				filewriter.write(" start");
				filewriter.write("\n");
				filewriter.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

	public static void dprint_method_end() {
		if (D.c == 1) {
			System.out.print(Thread.currentThread().
					getStackTrace()[2].getClassName());
			System.out.print(" ");
			System.out.print(Thread.currentThread().
					getStackTrace()[2].getMethodName());
			System.out.println(" end");
		} else if (D.c == 3) {
			File file = new File("log.txt");
			FileWriter filewriter;
			try {
				filewriter = new FileWriter(file, true);
				filewriter.write(Thread.currentThread().
						getStackTrace()[2].getClassName());
				filewriter.write(" ");
				filewriter.write(Thread.currentThread().
						getStackTrace()[2].getMethodName());
				filewriter.write(" end");
				filewriter.write("\n");
				filewriter.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

}
