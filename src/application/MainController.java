/*
 *<h1>Main Controller class</h1>
 * 
 * Mostly used for the Login Screen functionality 
 * and jumping to the correct screen if the credentials are as per the database
 * 
 * 
 * @author Anton Morozov
 * @since  18/12/2016
 */
package application;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * The Class MainController.
 */
public class MainController implements Initializable {

	/**
	 * The Constant MANAGER. setting address for new pages
	 */
	public static final String MANAGER = "/application/ManagerHome.fxml";
	public static final String EMPLOYEE = "/application/EmployeeHome.fxml";

	/**
	 * The jump. functionality to jump to a new page
	 */

	Functionality jump = new Functionality();

	/** The time. To use for getting the current date */
	Functionality time = new Functionality();

	/** The is connected label for the login screen */
	@FXML
	private Label isConnected;

	/** The lbl status. */
	@FXML
	private Label lblStatus;

	/** The txt username. */
	@FXML
	private TextField txtUsername;

	/** The txt password. */
	@FXML
	private TextField txtPassword;

	/** The login model. */
	public LoginModel loginModel = new LoginModel();

	/** The send functionality to be used later */
	Functionality send = new Functionality();

	/**
	 * Login.
	 *
	 * Checks the txt fields with the database input, compares and logs it on
	 * success If the user is a manager jumps to the Manager Home screen
	 * ALternatively goes to the Employee home screen and sends employee's name
	 * for the welcome message
	 *
	 * Hides login screen on success
	 *
	 * If invalif input - erase the txt fields for another try
	 *
	 * @param event
	 *            the event
	 * @throws Exception
	 *             the exception
	 */
	public void Login(ActionEvent event) throws Exception {
		try {
			if (loginModel.isLogin(txtUsername.getText(), txtPassword.getText())) {
				send.writeToLog("User: " + txtUsername.getText() + " Loginned at " + time.currentDate());

				if (loginModel.isManager(txtUsername.getText())) {

					jump.nextStage(MANAGER, "MANAGER PANEL", true);

				} else {

					Stage stage = new Stage();
					FXMLLoader loader = new FXMLLoader();
					Parent root = loader.load(getClass().getResource("/application/EmployeeHome.fxml").openStream());
					EmployeeHomeController employeeController = (EmployeeHomeController) loader.getController();
					employeeController.GetName(txtUsername.getText());
					Scene scene = new Scene(root);
					scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
					stage.setScene(scene);
					stage.show();

				}

				((Node) (event.getSource())).getScene().getWindow().hide();

			} else {
				lblStatus.setText("invalid username or password");
				txtUsername.clear();
				txtPassword.clear();
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

	}

	/*
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		if (loginModel.isDbConnected()) {
			isConnected.setText("db connected");
		} else {
			isConnected.setText("db not connected");
		}
	}
}
