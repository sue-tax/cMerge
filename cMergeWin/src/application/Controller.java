package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * "cMerge.fxml"コントローラ・クラスのサンプル・スケルトン
 */

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;

public class Controller {

//	String strFileBase = null;
//	String strFileX = null;
//	String strFileY = null;
//	String strFileMerge = null;

	List<String> linesMerge = null;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="buttonBase"
    private Button buttonBase; // Value injected by FXMLLoader

    @FXML // fx:id="buttonX"
    private Button buttonX; // Value injected by FXMLLoader

    @FXML // fx:id="buttonY"
    private Button buttonY; // Value injected by FXMLLoader

    @FXML // fx:id="buttonZ"
    private Button buttonZ; // Value injected by FXMLLoader

    @FXML // fx:id="buttonSave"
    private Button buttonSave; // Value injected by FXMLLoader

    @FXML // fx:id="buttonMerge"
    private Button buttonMerge; // Value injected by FXMLLoader

    @FXML // fx:id="textAreaConflict"
    private TextArea textAreaConflict; // Value injected by FXMLLoader

    @FXML // fx:id="tfBase"
    private TextField tfBase; // Value injected by FXMLLoader

    @FXML // fx:id="tfMerge"
    private TextField tfMerge; // Value injected by FXMLLoader

    @FXML // fx:id="tfMsg"
    private TextField tfMsg; // Value injected by FXMLLoader

    @FXML // fx:id="tfX"
    private TextField tfX; // Value injected by FXMLLoader

    @FXML // fx:id="tfY"
    private TextField tfY; // Value injected by FXMLLoader

    @FXML // fx:id="wvMerge"
    private WebView wvMerge; // Value injected by FXMLLoader

    @FXML
    void onBase(ActionEvent event) {
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.setTitle("ベース・ファイルの選択");
	    String strBase = tfBase.getText();
	    if ((strBase != null) && (! strBase.equals(""))) {
	    	File file1 = new File(strBase);
	    	File fileDir = new File(file1.getParent());
	    	fileChooser.setInitialDirectory(fileDir);
	    } else {
	    	String userDir = System.getProperty("user.dir");
	    	D.dprint(userDir);
	    	File fileDir = new File(userDir);
	    	fileChooser.setInitialDirectory(fileDir);
	    }
	    File file = null;
		try {
			file = fileChooser.showOpenDialog(Main.stage);
		} catch (Exception e1) {
			printMsg("ファイルの選択ができませんでした。");
			return;
		}
		if (file == null) {
			printMsg("ファイルの選択ができませんでした。");
			return;
		}
		String strFileBase = file.toString();
		tfBase.setText(strFileBase);
    }

    @FXML
    void onX(ActionEvent event) {
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.setTitle("ファイルＸの選択");
	    String strX = tfX.getText();
	    if ((strX != null) && (! strX.equals(""))) {
	    	File file1 = new File(strX);
	    	File fileDir = new File(file1.getParent());
	    	fileChooser.setInitialDirectory(fileDir);
	    } else {
		    String strBase = tfBase.getText();
		    if ((strBase != null) && (! strBase.equals(""))) {
		    	File file1 = new File(strBase);
		    	File fileDir = new File(file1.getParent());
		    	fileChooser.setInitialDirectory(fileDir);
		    } else {
		    	String userDir = System.getProperty("user.dir");
		    	D.dprint(userDir);
		    	File fileDir = new File(userDir);
		    	fileChooser.setInitialDirectory(fileDir);
		    }
	    }
	    File file = null;
		try {
			file = fileChooser.showOpenDialog(Main.stage);
		} catch (Exception e1) {
			printMsg("ファイルの選択ができませんでした。");
			return;
		}
		if (file == null) {
			printMsg("ファイルの選択ができませんでした。");
			return;
		}
		String strFileX = file.toString();
		tfX.setText(strFileX);
    }

    @FXML
    void onY(ActionEvent event) {
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.setTitle("ファイルYの選択");
	    String strY = tfY.getText();
	    if ((strY != null) && (! strY.equals(""))) {
	    	File file1 = new File(strY);
	    	File fileDir = new File(file1.getParent());
	    	fileChooser.setInitialDirectory(fileDir);
	    } else {
		    String strBase = tfBase.getText();
		    if ((strBase != null) && (! strBase.equals(""))) {
		    	File file1 = new File(strBase);
		    	File fileDir = new File(file1.getParent());
		    	fileChooser.setInitialDirectory(fileDir);
		    } else {
		    	String userDir = System.getProperty("user.dir");
		    	D.dprint(userDir);
		    	File fileDir = new File(userDir);
		    	fileChooser.setInitialDirectory(fileDir);
		    }
	    }
	    File file = null;
		try {
			file = fileChooser.showOpenDialog(Main.stage);
		} catch (Exception e1) {
			printMsg("ファイルの選択ができませんでした。");
			return;
		}
		if (file == null) {
			printMsg("ファイルの選択ができませんでした。");
			return;
		}
		String strFileY = file.toString();
		tfY.setText(strFileY);
    }

    @FXML
    void onZ(ActionEvent event) {
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.setTitle("マージ・ファイルの選択");
	    String strMerge = tfMerge.getText();
	    if ((strMerge != null) && (! strMerge.equals(""))) {
	    	File file1 = new File(strMerge);
	    	File fileDir = new File(file1.getParent());
	    	fileChooser.setInitialDirectory(fileDir);
	    } else {
		    String strBase = tfBase.getText();
		    if ((strBase != null) && (! strBase.equals(""))) {
		    	File file1 = new File(strBase);
		    	File fileDir = new File(file1.getParent());
		    	fileChooser.setInitialDirectory(fileDir);
		    } else {
		    	String userDir = System.getProperty("user.dir");
		    	D.dprint(userDir);
		    	File fileDir = new File(userDir);
		    	fileChooser.setInitialDirectory(fileDir);
		    }
	    }
	    File file = null;
		try {
			file = fileChooser.showOpenDialog(Main.stage);
		} catch (Exception e1) {
			printMsg("ファイルの選択ができませんでした。");
			return;
		}
		if (file == null) {
			printMsg("ファイルの選択ができませんでした。");
			return;
		}
		String strFileMerge = file.toString();
		tfMerge.setText(strFileMerge);
    }

    @FXML
    void onMerge(ActionEvent event) {
    	String strFileBase = tfBase.getText();
    	D.dprint(strFileBase);
        List<String> linesB = null;
		try {
			linesB = Files.readAllLines(
					FileSystems.getDefault().getPath(strFileBase),
					Charset.defaultCharset());
		} catch (IOException e) {
			printMsg("ベース・ファイル" + strFileBase + "が読めません。");
			return;
		}
    	String strFileX = tfX.getText();
    	D.dprint(strFileX);
        List<String> linesX = null;
		try {
			linesX = Files.readAllLines(
					FileSystems.getDefault().getPath(strFileX),
					Charset.defaultCharset());
		} catch (IOException e) {
			printMsg("Ｘファイル" + strFileX + "が読めません。");
			return;
		}
    	String strFileY = tfY.getText();
    	D.dprint(strFileY);
		List<String> linesY = null;
		try {
			linesY = Files.readAllLines(
					FileSystems.getDefault().getPath(strFileY),
					Charset.defaultCharset());
		} catch (IOException e) {
			printMsg("Ｙファイル" + strFileY + "が読めません。");
			return;
		}

		List<DiffLine> listDiffX = CMerge.createDiffList(
				linesB, linesX);
		List<DiffLine> listDiffY = CMerge.createDiffList(
				linesB, linesY);

		List<String> linesZ = new ArrayList<String>();
		List<String> linesConflict = new ArrayList<String>();
		List<String> linesColor = new ArrayList<String>();

		boolean flag = CMerge.merge(
				linesZ, linesConflict,
				linesColor,
				linesB, listDiffX, listDiffY);
		D.dprint(flag);
		if (flag) {
			printMsg("コンフリクトなしでマージしました。");
		} else {
			printMsg("コンフリクトが発生しました。");
		}
		linesMerge = linesZ;
		D.dprint(linesZ);
		D.dprint(linesColor);
		String strColored = String.join("<br>", linesColor);
		D.dprint(strColored);
    	wvMerge.getEngine().loadContent(strColored);
    }

    @FXML
    void onSaveAction(ActionEvent event) {
    	if (linesMerge == null) {
    		printMsg("マージされていません。");
    		return;
    	}
    	String strFileZ = tfMerge.getText();
    	if (strFileZ.equals("")) {
    	    FileChooser fileChooser = new FileChooser();
		    String strBase = tfBase.getText();
		    if ((strBase != null) && (! strBase.equals(""))) {
		    	File file1 = new File(strBase);
		    	File fileDir = new File(file1.getParent());
		    	fileChooser.setInitialDirectory(fileDir);
    	    }
    	    fileChooser.setTitle("マージ・ファイルの選択");
    	    File file = null;
    	    try {
    			file = fileChooser.showSaveDialog(Main.stage);
    		} catch (Exception e1) {
    			printMsg("ファイルの選択ができませんでした。");
    			return;
    		}
        	if (file == null) {
    			printMsg("ファイルが選択されませんでした。");
    			return;
        	}
        	strFileZ = file.toString();
        	tfMerge.setText(strFileZ);
    	}
    	Path path1 = Paths.get(strFileZ); //パス
    	try (BufferedWriter bw = Files.newBufferedWriter(
    			path1, StandardCharsets.UTF_8);
              PrintWriter pw = new PrintWriter(bw, true)) {
    		Iterator<String> iter = linesMerge.iterator();
    		while (iter.hasNext()) {
    			pw.println(iter.next());
    		}
    	} catch (IOException e) {
    		printMsg("保存できませんでした。");
    		return;
    	}
    	printMsg("マージ・ファイルに保存しました。");
    }


    @FXML
    void onDragDroppedBase(DragEvent event) {
        Dragboard board = event.getDragboard();
        if (board.hasFiles()) {
        	File file = board.getFiles().get(0);
            String strFileBase = file.getAbsolutePath();
            D.dprint(strFileBase);
//    		tfBase.setEditable(true);
    		tfBase.setText(strFileBase);
    		tfBase.end();
//    		tfBase.setEditable(false);
    		printMsg("ベース・ファイル名を設定しました。");
            event.setDropCompleted(true);
        } else {
            event.setDropCompleted(false);
        }
    }

    @FXML
    void onDragDroppedMerge(DragEvent event) {
        Dragboard board = event.getDragboard();
        if (board.hasFiles()) {
        	File file = board.getFiles().get(0);
            String strFileMerge = file.getAbsolutePath();
            D.dprint(strFileMerge);
//    		tfMerge.setEditable(true);
            tfMerge.setText(strFileMerge);
            tfMerge.end();
//    		tfMerge.setEditable(false);
    		printMsg("マージ・ファイル名を設定しました。");
            event.setDropCompleted(true);
        } else {
            event.setDropCompleted(false);
        }

    }

    @FXML
    void onDragDroppedX(DragEvent event) {
        Dragboard board = event.getDragboard();
        if (board.hasFiles()) {
        	File file = board.getFiles().get(0);
            String strFileX = file.getAbsolutePath();
            D.dprint(strFileX);
//    		tfX.setEditable(true);
    		tfX.setText(strFileX);
    		tfX.end();
//    		tfX.setEditable(false);
    		printMsg("Ｘファイル名を設定しました。");
            event.setDropCompleted(true);
        } else {
            event.setDropCompleted(false);
        }

    }

    @FXML
    void onDragDroppedY(DragEvent event) {
        Dragboard board = event.getDragboard();
        if (board.hasFiles()) {
        	File file = board.getFiles().get(0);
            String strFileBase = file.getAbsolutePath();
            D.dprint(strFileBase);
//    		tfY.setEditable(true);
            tfY.setText(strFileBase);
            tfY.end();
//    		tfY.setEditable(false);
    		printMsg("Ｙファイル名を設定しました。");
            event.setDropCompleted(true);
        } else {
            event.setDropCompleted(false);
        }

    }

    @FXML
    void onDragOver(DragEvent event) {
        Dragboard board = event.getDragboard();
        if (board.hasFiles()) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
    }


    void printMsg( String strMsg ) {
		tfMsg.setEditable(true);
		tfMsg.setText(strMsg);
		tfMsg.end();
		tfMsg.setEditable(false);
		return;
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert buttonBase != null : "fx:id=\"buttonBase\" was not injected: check your FXML file 'cMerge.fxml'.";
        assert buttonX != null : "fx:id=\"buttonX\" was not injected: check your FXML file 'cMerge.fxml'.";
        assert buttonY != null : "fx:id=\"buttonY\" was not injected: check your FXML file 'cMerge.fxml'.";
        assert buttonZ != null : "fx:id=\"buttonZ\" was not injected: check your FXML file 'cMerge.fxml'.";
        assert buttonSave != null : "fx:id=\"buttonSave\" was not injected: check your FXML file 'cMerge.fxml'.";
        assert buttonMerge != null : "fx:id=\"buttonMerge\" was not injected: check your FXML file 'cMerge.fxml'.";
        assert textAreaConflict != null : "fx:id=\"textAreaConflict\" was not injected: check your FXML file 'cMerge.fxml'.";
        assert tfBase != null : "fx:id=\"tfBase\" was not injected: check your FXML file 'cMerge.fxml'.";
        assert tfMerge != null : "fx:id=\"tfMerge\" was not injected: check your FXML file 'cMerge.fxml'.";
        assert tfMsg != null : "fx:id=\"tfMsg\" was not injected: check your FXML file 'cMerge.fxml'.";
        assert tfX != null : "fx:id=\"tfX\" was not injected: check your FXML file 'cMerge.fxml'.";
        assert tfY != null : "fx:id=\"tfY\" was not injected: check your FXML file 'cMerge.fxml'.";
        assert wvMerge != null : "fx:id=\"wvMerge\" was not injected: check your FXML file 'cMerge.fxml'.";

    }

}
