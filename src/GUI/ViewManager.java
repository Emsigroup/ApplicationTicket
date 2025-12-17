package GUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class ViewManager {

	public showadmin() {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/admin.fxml"));

		
	}
public showt(echnicien() {
    Parent root = FXMLLoader.load(getClass().getResource("/fxml/tecnien.fxml"));

		
	}

public showclient() {
	
    Parent root = FXMLLoader.load(getClass().getResource("/fxml/client.fxml"));

}}
