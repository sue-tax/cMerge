module cMergeWin {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.web;
	requires diffj;
	requires diffutils;
	
	opens application to javafx.graphics, javafx.fxml;
}
