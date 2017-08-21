/*
 *<h1>Manage Employees Controller class:</h1>
 * 
 *Screen to manage current employees
 *
 *<p>
 *Functions:
 *</p>
 *
 * <p>
 * Add new employee / Edit current employee / Delete employee </p>
 * 
 * 
 * @author Anton Morozov
 * @since  18/12/2016
 */
package pkgEmployees;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Scanner;

import application.SqliteConnection;
import application.tablemodels.employees;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class EmployeesController.
 */
public class EmployeesController implements Initializable {

	/** The data. */
	private ObservableList<employees> data;

	/** The tbl employees. */
	@FXML
	private TableView<employees> tblEmployees;

	/** The id col. */
	@FXML
	private TableColumn<employees, Integer> idCol;

	/** The name col. */
	@FXML
	private TableColumn<employees, String> nameCol;

	/** The surname col. */
	@FXML
	private TableColumn<employees, String> surnameCol;

	/** The manager col. */
	@FXML
	private TableColumn<employees, String> managerCol;

	/** The username col. */
	@FXML
	private TableColumn<employees, String> usernameCol;

	/** The password col. */
	@FXML
	private TableColumn<employees, String> passwordCol;

	/** The Add button. */
	@FXML
	private Button AddButton;

	/** The txt area. */
	@FXML
	private TextArea txtArea;

	/** The Modify button. */
	@FXML
	private Button ModifyButton;

	/** The Delete button. */
	@FXML
	private Button DeleteButton;

	/** The name txt. */
	@FXML
	private TextField nameTxt;

	/** The surname txt. */
	@FXML
	private TextField surnameTxt;

	/** The username txt. */
	@FXML
	private TextField usernameTxt;

	/** The password txt. */
	@FXML
	private TextField passwordTxt;

	/** The Manager drop. */
	@FXML
	private ComboBox<String> ManagerDrop;

	/** The connection. */
	Connection connection;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		try {
			Scanner sc = new Scanner(new File("LogActivities.txt"));
			sc.useDelimiter(" ");
			while (sc.hasNext()) {

				txtArea.appendText(sc.next() + " ");

			}
			sc.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}

		// populating comboboxes
		ManagerDrop.setItems(FXCollections.observableArrayList("true", "false"));

		connection = SqliteConnection.Connector();
		populateTableView();

		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Populate table view with the data from the username database.
	 */
	private void populateTableView() {
		try {
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			data = FXCollections.observableArrayList();
			String query = "select * from username";

			preparedStatement = connection.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				String name = resultSet.getString(2);
				String surname = resultSet.getString(3);
				String manager = resultSet.getString(4);
				String username = resultSet.getString(5);
				String password = resultSet.getString(6);
				data.add(new employees(name, surname, manager, username, password));
			}

			preparedStatement.close();
			resultSet.close();

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());

		}

		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
		managerCol.setCellValueFactory(new PropertyValueFactory<>("manager"));
		usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
		passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));

		tblEmployees.setItems(data);

	}

	/**
	 * Handle. Used to prepopulate fields on click.
	 *
	 * @param event
	 *            the event
	 */
	@FXML
	public void handle(MouseEvent event) {
		PrepopulateOnClick();
	}

	/**
	 * Prepopulate fields on click.
	 */
	private void PrepopulateOnClick() {
		int row = tblEmployees.getSelectionModel().getSelectedIndex();

		connection = SqliteConnection.Connector();

		PreparedStatement preparedStatement = null;
		try {

			// retrieveing data from the highlighted row from the menu
			String query = "SELECT * from username where rowid=?";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, row);
			preparedStatement.execute();

			nameTxt.setText(nameCol.getCellData(row));
			surnameTxt.setText(surnameCol.getCellData(row));
			ManagerDrop.setValue(managerCol.getCellData(row));
			usernameTxt.setText(usernameCol.getCellData(row));
			passwordTxt.setText(passwordCol.getCellData(row));

			preparedStatement.close();

		} catch (Exception ex) {

		}

	}

	/**
	 * Add button. To add a new user of the system. Both manager and employee
	 *
	 * @param event
	 *            the event
	 */
	@FXML
	private void AddButton(ActionEvent event) {
		connection = SqliteConnection.Connector();

		String name = nameTxt.getText();
		String surname = surnameTxt.getText();
		String manager = ManagerDrop.getValue();
		String username = usernameTxt.getText();
		String pass = passwordTxt.getText();

		PreparedStatement preparedStatement = null;

		String query = "INSERT INTO username(\"name\",surname,manager,user,pass) values(?,?,?,?,?)";

		try {
			preparedStatement = connection.prepareStatement(query);

			preparedStatement.setString(1, name);
			preparedStatement.setString(2, surname);
			preparedStatement.setString(3, manager);
			preparedStatement.setString(4, username);
			preparedStatement.setString(5, pass);

			// manual validation
			if ((nameTxt.getText().length() != 0) && (surnameTxt.getText().length() != 0)
					&& (usernameTxt.getText().length() != 0) && (passwordTxt.getText().length() != 0)
					&& (ManagerDrop.getValue() != null)) {

				preparedStatement.execute();

				clearFields();

				Alert a = new Alert(AlertType.INFORMATION, "Success! The new user has been added", ButtonType.OK);
				a.show();

				// refresh with the update
				populateTableView();

			} else {
				Alert b = new Alert(AlertType.ERROR, "You need to fill in all the fields to proceed", ButtonType.OK);
				b.show();
			}

			connection.close();

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	/**
	 * Clear fields.
	 */
	private void clearFields() {

		ManagerDrop.setValue(null);
		nameTxt.setText(null);
		surnameTxt.setText(null);
		usernameTxt.setText(null);
		passwordTxt.setText(null);
	}

	/**
	 * Modify button. To edit information of the current employee
	 *
	 * @param event
	 *            the event
	 */
	@FXML
	private void ModifyButton(ActionEvent event) {
		int row = tblEmployees.getSelectionModel().getSelectedIndex() + 1;

		String name = nameTxt.getText();
		String surname = surnameTxt.getText();
		String manager = ManagerDrop.getValue();
		String user = usernameTxt.getText();
		String pass = passwordTxt.getText();

		PreparedStatement preparedStatement = null;

		String query = "update username SET \"name\"=?, surname=?, manager=?, user=?, pass=? where id=?";

		try {
			preparedStatement = connection.prepareStatement(query);

			preparedStatement.setString(1, name);
			preparedStatement.setString(2, surname);
			preparedStatement.setString(3, manager);
			preparedStatement.setString(4, user);
			preparedStatement.setString(5, pass);

			preparedStatement.setInt(6, row);
			// manual validation
			if ((nameTxt.getText().length() != 0) && (surnameTxt.getText().length() != 0)
					&& (usernameTxt.getText().length() != 0) && (passwordTxt.getText().length() != 0)
					&& (ManagerDrop.getValue() != null)) {

				preparedStatement.execute();

				clearFields();
				populateTableView();
				Alert a = new Alert(AlertType.INFORMATION, "Success! The user has been edited", ButtonType.OK);
				a.show();

			} else {
				Alert b = new Alert(AlertType.ERROR, "Please try again", ButtonType.OK);
				b.show();
			}
			preparedStatement.close();
			connection.close();

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	/**
	 * Delete button. To delete the user from the database.
	 * 
	 * <p>
	 * Assuming no more than 50 employees in the current system
	 * </p>
	 *
	 * @param event
	 *            the event
	 */
	@FXML
	private void DeleteButton(ActionEvent event) {
		int row = tblEmployees.getSelectionModel().getSelectedIndex() + 1;

		connection = SqliteConnection.Connector();

		PreparedStatement preparedStatement = null;

		try {

			if (row != 0) {
				// deleting highlighted row from the menu
				String del = "delete from username where id=?";
				preparedStatement = connection.prepareStatement(del);
				preparedStatement.setInt(1, row);
				preparedStatement.execute();

				int refresh;
				// prepopulating/refreshing row ids again as one was deleted and
				// made it wrong (e.g. 1,2,3,5,6)
				for (int i = 0; i < 50; ++i) {
					refresh = row + i;

					updateRowID(refresh);
				}

				populateTableView();

				Alert a = new Alert(AlertType.INFORMATION, "The Row was deleted!", ButtonType.OK);
				a.show();
			} else {
				Alert a = new Alert(AlertType.ERROR, "You need to select the row!", ButtonType.OK);
				a.show();
			}

			preparedStatement.close();
			connection.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Update row ID. Update row ids within the range to use when the row id is
	 * deleted
	 *
	 * @param rowID
	 *            the row ID
	 */
	private void updateRowID(int rowID) {

		PreparedStatement preparedStatement = null;
		try {

			String upd = "UPDATE username SET id=? where rowid=?";

			preparedStatement = connection.prepareStatement(upd);

			preparedStatement.setInt(1, rowID);
			preparedStatement.setInt(2, rowID + 1);
			preparedStatement.execute();

			preparedStatement.close();

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

}
