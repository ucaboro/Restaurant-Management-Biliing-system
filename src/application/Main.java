/*
 *<h1>Add Main class:</h1>
 * Main class that creates a frame and launches it.
 * The whole program starts from here.
 * 
 * 
 * @author Anton Morozov
 * @since  18/12/2016
 */
package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

// TODO: Auto-generated Javadoc
/**
 * The Class Main. Extends Application to benefit from java fx functionality
 */
public class Main extends Application {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	/*
	 * Start method. Redirects to the first screen (Login Page)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage stage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/application/Login.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toString());
			stage.setScene(scene);
			stage.show();
			stage.setResizable(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The main method. launches the frame
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
