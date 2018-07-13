package ph.adamw.nnt3.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));

		Scene scene = new Scene(root, 900, 594);

		stage.setTitle("NNt3 Mazes");
		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();
	}
}
