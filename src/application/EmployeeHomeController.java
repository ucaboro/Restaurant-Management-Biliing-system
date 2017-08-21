/*
 *<h1>Employee Home Page Controller</h1>
 *
 *.
 * Controls the functionality of the FXML file for the Employee
 * 
 * 
 * @author Anton Morozov
 * @since  18/12/2016
 */
package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.ManagerHomeController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pkgEditOrder.EditOrderController;
import pkgOrderPopup.OrderController;

/**
 * The Class EmployeeHomeController.
 */
public class EmployeeHomeController extends ManagerHomeController implements Initializable {

	/** The lbl name. */
	@FXML
	private Label lblName;

	/** The btn log out. */
	@FXML
	private Button btnLogOut;

	/** The Highlights for the most popular dish. */
	@FXML
	private Label Hdish;

	/** The Highlight for the overall number of orders. */
	@FXML
	private Label Hnum;

	/** The Highlight for the earnings this month */
	@FXML
	private Label Hday;

	/** The btn table 1. */
	@FXML
	private Button btnTable1;

	/** The btn table 2. */
	@FXML
	private Button btnTable2;

	/** The btn table 3. */
	@FXML
	private Button btnTable3;

	/** The btn table 4. */
	@FXML
	private Button btnTable4;

	/** The btn table 5. */
	@FXML
	private Button btnTable5;

	/** The btn table 6. */
	@FXML
	private Button btnTable6;

	/** The update button. */
	@FXML
	private ImageView updateButton;

	/** The Constant EMPLOYEE URL link. */
	public static final String EMPLOYEE = "/application/EmployeeHome.fxml";

	/** The connection. */
	Connection connection;

	/*
	 * @see application.ManagerHomeController#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		connection = SqliteConnection.Connector();

		/**
		 * try to colorize buttons that have active orders and set up the
		 * Highlights (Analytics)
		 */
		try {

			Hdish.setText(HighlightDish());

			Hday.setText("£" + HighlightSum());

			Hnum.setText(HighlightOrders());
			colorize();

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			connection.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Gets the name of the user from the login screen and uses it in the
	 * welcome message
	 *
	 * @param user
	 *            the user
	 */
	public void GetName(String user) {
		lblName.setText(user);
	}

	/*
	 * @see
	 * application.ManagerHomeController#Update(javafx.scene.input.MouseEvent)
	 */
	@Override
	/**
	 * Update. Creating an update/refresh (hide current frame and jump to the
	 * same page) button with minimal pressing animation
	 */
	@FXML
	protected void Update(MouseEvent event) throws Exception {

		updateButton.setStyle("-fx-scale-x:1.1; -fx-scale-y:1.1;");

		connection = SqliteConnection.Connector();

		jump.nextStage(EMPLOYEE, "EMPLOYEE PANEL", false);

		((Node) (event.getSource())).getScene().getWindow().hide();

		connection.close();
	}

	/*
	 * Re-used again for the Employee screen as the sqlite connection won't go
	 * through when using 'Extends'
	 * 
	 * @see application.ManagerHomeController#activeTables()
	 * 
	 * @return all the active tables (the ones with the order)
	 */
	protected ArrayList<String> activeTables() throws SQLException {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<String> activeTables = new ArrayList<String>();

		String query = "select tablenum from tables where active =1";

		preparedStatement = connection.prepareStatement(query);
		resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {

			activeTables.add(resultSet.getString(1));

		}

		return activeTables;
	}

	/*
	 * Re-used again for the Employee screen as the sqlite connection won't go
	 * through when using 'Extends'
	 * 
	 * @see application.ManagerHomeController#TableLastOrder(java.lang.String)
	 * 
	 * @return the last order of the table
	 */

	protected String TableLastOrder(String num) throws SQLException {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			String query = "select ordernum from orders where tablenum=? ORDER BY ordernum DESC";

			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, num);
			resultSet = preparedStatement.executeQuery();
			resultSet.getString(1);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resultSet.getString(1);

	}

	/*
	 * Re-used again for the Employee screen as the sqlite connection won't go
	 * through when using 'Extends'
	 * 
	 * @see
	 * application.ManagerHomeController#changeTableColor(javafx.scene.control.
	 * Button)
	 */
	protected void changeTableColor(Button name) {
		name.setStyle("-fx-base: green; -fx-background-radius: 50; -fx-font-weight:bold");

	}

	/*
	 * Re-used again for the Employee screen as the sqlite connection won't go
	 * through when using 'Extends'
	 * 
	 * @see application.ManagerHomeController#colorize()
	 * 
	 * If the table is "Active" change the color to green
	 */
	protected void colorize() throws Exception {
		ActiveTables.addAll(activeTables());

		for (int i = 0; i < ActiveTables.size(); i++) {
			String number = ActiveTables.get(i);

			if (btnTable1.getText().equals(number.toString())) {
				changeTableColor(btnTable1);
			}
			if (btnTable2.getText().equals(number.toString())) {
				changeTableColor(btnTable2);
			}
			if (btnTable3.getText().equals(number.toString())) {
				changeTableColor(btnTable3);
			}
			if (btnTable4.getText().equals(number.toString())) {
				changeTableColor(btnTable4);
			}
			if (btnTable5.getText().equals(number.toString())) {
				changeTableColor(btnTable5);
			}
			if (btnTable6.getText().equals(number.toString())) {
				changeTableColor(btnTable6);
			}

		}
	}

	/*
	 * Re-used again for the Employee screen as the sqlite connection won't go
	 * through when using 'Extends'
	 * 
	 * @see application.ManagerHomeController#HighlightDish()
	 * 
	 * @return the top most popular dish (the one that appears most often in the
	 * database)
	 */
	@FXML
	protected String HighlightDish() throws SQLException {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			String query = "select dish from (select dish, count(1) from orders group by dish order by count(1) desc) Limit 1";

			preparedStatement = connection.prepareStatement(query);

			resultSet = preparedStatement.executeQuery();
			resultSet.getString(1);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resultSet.getString(1);

	}

	/*
	 * Re-used again for the Employee screen as the sqlite connection won't go
	 * through when using 'Extends'
	 * 
	 * @see application.ManagerHomeController#HighlightOrders()
	 * 
	 * @return the last number of order, being the current number of orders
	 */
	@FXML
	protected String HighlightOrders() throws SQLException {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			String query = "select ordernum from orders ORDER BY ordernum DESC";

			preparedStatement = connection.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();
			resultSet.getString(1);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resultSet.getString(1);

	}

	/*
	 * Re-used again for the Employee screen as the sqlite connection won't go
	 * through when using 'Extends'
	 * 
	 * @see application.ManagerHomeController#HighlightSum()
	 */
	@FXML
	protected String HighlightSum() throws SQLException {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		String todaysmonth = ("%/"
				+ String.valueOf(time.currentDate().charAt(5) + String.valueOf(time.currentDate().charAt(6))) + "/%");

		try {
			String query = "SELECT SUM (bill) FROM orders WHERE time LIKE ?";

			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, todaysmonth);
			resultSet = preparedStatement.executeQuery();
			resultSet.getString(query);

		} catch (Exception ex) {

		}
		return resultSet.getString(1);

	}

	/*
	 * Re-used again for the Employee screen as the sqlite connection won't go
	 * through when using 'Extends'
	 * 
	 * @see application.ManagerHomeController#getName(javafx.event.ActionEvent)
	 */

	@FXML
	protected void getName(ActionEvent event) throws IOException, SQLException {
		connection = SqliteConnection.Connector();

		Object source = event.getSource();
		String sourceString = source.toString();

		/** get the number of the table as a character */
		char num = sourceString.charAt(sourceString.length() - 2);
		SelectedTable = (String.valueOf(num));

		/** validation whether the table has the order already (if its green) */
		if ((ActiveTables.contains(SelectedTable)) == true) {

			/**
			 * If there is currently an order at the table Give the Edit
			 * existing Order popup
			 * 
			 */
			Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			Parent root = loader.load(getClass().getResource(EDIT).openStream());

			/** set the table number and the last order number for the popup */
			EditOrderController orderController = (EditOrderController) loader.getController();
			orderController.GetTableNum(SelectedTable);

			orderController.GetOrderNum(TableLastOrder(SelectedTable));

			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);

			/**
			 * Remove the title bar, so that the user cannot add/edit data and
			 * close the popups this does skew the data, so the custom close
			 * button is created, which limits the user to exit after making
			 * changes and asks to confirm the changes first
			 * 
			 */
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.show();
			stage.setResizable(false);

		} else {

			/**
			 * If there is currently no order at the table Give the Create new
			 * Order popup
			 * 
			 */
			Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			Parent root = loader.load(getClass().getResource(ORDER).openStream());

			OrderController orderController = (OrderController) loader.getController();
			orderController.GetTableNum(SelectedTable);

			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
			stage.setResizable(false);
		}
		connection.close();
	}

}
