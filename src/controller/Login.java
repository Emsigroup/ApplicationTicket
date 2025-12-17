package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.shape.Circle;

public class Login {
	@FXML
	private Circle rond;
	private double x;
	private double y;
	

	public  void enrgeister(ActionEvent e) {
		rond.setCenterY(y-=20);
		
	}
	public  void Droit(ActionEvent e) {
			rond.setCenterX(y+=20);
			
		}
}
