/*
 *<h1>Manger Home Page Controller</h1>
 *
 *.
 * The Main home screen for most of the functionality
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
 * The Class ManagerHomeController.
 */
public class ManagerHomeController implements Initializable {

	/**
	 * Accessing the Functionality class Controlling all the functionality on
	 * the HomePage
	 * 
	 * jump - switch to a new page
	 * 
	 * 
	 * 
	 */

	Functionality jump = new Functionality();

	/** log - send log data to the .txt file */
	Functionality log = new Functionality();

	/** time - get current time */
	Functionality time = new Functionality();

	/** The Constant FOOD MENU URL. */

	public static final String MENU = "/pkgMenu/MenuPage.fxml";

	/** The Constant ORDER POPUP URL. */
	public static final String ORDER = "/pkgOrderPopup/OrderPopup.fxml";

	/** The Constant EDIT ORDER URL. */
	public static final String EDIT = "/pkgEditOrder/EditOrder.fxml";

	/** The Constant MANAGER HOME SCREEN URL. */
	public static final String MANAGER = "/application/ManagerHome.fxml";

	/** The Constant ALL ORDERS URL. */
	public static final String ORDERS = "/pkgOrders/Orders.fxml";

	/** The Constant EMPLOYEES HOME PAGE URL. */
	public static final String EMPLOYEES = "/pkgEmployees/Employees.fxml";

	/** The Constant ANALYTICS URL. */
	public static final String ANALYTICS = "/pkgAnalytics/Analytics.fxml";

	/** The Constant LOGIN URL to logout. */
	public static final String LOGIN = "/application/Login.fxml";

	/** The button to log out. */
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

	/** The btn menu to jump to food menu screen. */
	@FXML
	private Button btnMenu;

	/** The btn orders to jump to all orders screen. */
	@FXML
	private Button btnOrders;

	/** The btn to jump to manage employees screen. */
	@FXML
	private Button btnEmployees;

	/** The btn to jump to analytics screen */
	@FXML
	private Button btnAnalytics;

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

	/** The update/refresh screen button. */
	@FXML
	private ImageView updateButton;

	/** The Selected table. To see which ones are selected */
	protected String SelectedTable;

	/**
	 * The Active tables.
	 * 
	 * String that retrieves from the database currently active (with order)
	 * tables
	 */
	protected ArrayList<String> ActiveTables = new ArrayList<String>();

	/** The connection to the database. */
	Connection connection;

	/*
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
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
	 * Menu action to jump to the food menu screen.
	 *
	 * @param event
	 *            the event
	 * @throws Exception
	 *             the exception
	 */
	public void MenuAction(ActionEvent event) throws Exception {
		jump.nextStage(MENU, "MENU", false);
	}

	/**
	 * Orders action to jump to the total orders screen.
	 *
	 * @param event
	 *            the event
	 * @throws Exception
	 *             the exception
	 */
	public void OrdersAction(ActionEvent event) throws Exception {
		jump.nextStage(ORDERS, "ORDERS", false);

	}

	/**
	 * Employees action to jump to the Manage employees screen.
	 *
	 * @param event
	 *            the event
	 * @throws Exception
	 *             the exception
	 */
	public void EmployeesAction(ActionEvent event) throws Exception {
		jump.nextStage(EMPLOYEES, "EMPLOYEES", false);

	}

	/**
	 * Analytics action to jump to the Analytics screen.
	 *
	 * @param event
	 *            the event
	 * @throws Exception
	 *             the exception
	 */
	public void AnalyticsAction(ActionEvent event) throws Exception {
		jump.nextStage(ANALYTICS, "ANALYTICS", false);

	}

	/**
	 * Logout action to jump to the logout screen.
	 *
	 * @param event
	 *            the event
	 * @throws Exception
	 *             the exception
	 */
	public void LogoutAction(ActionEvent event) throws Exception {
		// hide screen
		((Node) (event.getSource())).getScene().getWindow().hide();

		try {
			jump.nextStage(LOGIN, "LOGIN", false);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("sdf");
		}

	}

	/**
	 * Gets the name of the table (e.g. 1, 2, 3). Method to link with the
	 * OrderPopup view and create a popup, showing the name of the table
	 * selected.
	 * 
	 * Takes the source data as a String, by getting the right characters when
	 * pressed on the button
	 * <p>
	 * If there is an order at the table -> jump to Edit order screen and send
	 * order number / table number
	 * </p>
	 * <p>
	 * If no order -> jumps to Create an order screen and send order number /
	 * table number
	 * </p>
	 *
	 * @param event
	 *            the event
	 * @return the name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws SQLException
	 *             the SQL exception
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
			 * if there is currently an order at the table, then jump to Edit
			 * Order popup
			 */
			Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			Parent root = loader.load(getClass().getResource(EDIT).openStream());

			/**
			 * set the table number and the last order number for the next
			 * screen
			 */
			EditOrderController orderController = (EditOrderController) loader.getController();
			orderController.GetTableNum(SelectedTable);

			orderController.GetOrderNum(TableLastOrder(SelectedTable));

			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.show();
			stage.setResizable(false);

		} else {

			/** if there is currently no order at the table */
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

	/**
	 * Active tables
	 *
	 * @return the array list of the tables that has 1(true) in the database
	 *         (have orders)
	 * @throws SQLException
	 *             the SQL exception
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

	/**
	 * Table last order.
	 *
	 * @param num
	 *            the number of the table
	 * @return the string with the last order number
	 * @throws SQLException
	 *             the SQL exception
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

	/**
	 * Change table color.
	 *
	 * @param name
	 *            the name
	 */
	protected void changeTableColor(Button name) {
		name.setStyle("-fx-base: green; -fx-background-radius: 50; -fx-font-weight:bold");

	}

	/**
	 * Colorize if any of the tables has active field set to 1 (true) in the
	 * database (have orders)
	 *
	 * @throws Exception
	 *             the exception
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

	/**
	 * Update. Refresh the page (hide current and jump to the Manager Home)
	 * <p>
	 * Minimal animation sat tim imitate the button pressing
	 * </p>
	 * 
	 * @param event
	 *            on the mouse click
	 * @throws Exception
	 *             the exception
	 */
	// creating an update button with minimal pressing animation
	@FXML
	protected void Update(MouseEvent event) throws Exception {

		updateButton.setStyle("-fx-scale-x:1.1; -fx-scale-y:1.1;");

		connection = SqliteConnection.Connector();

		jump.nextStage(MANAGER, "MANAGER PANEL", true);

		((Node) (event.getSource())).getScene().getWindow().hide();

		connection.close();
	}

	/**
	 * Default size. Used for minimal animation on the Update button.
	 *
	 * @param event
	 *            on the mouse click
	 */
	@FXML
	protected void DefaultSize(MouseEvent event) {
		updateButton.setStyle("-fx-scale-x:0.9; -fx-scale-y:0.9;");
	}

	/**
	 * Highlight dish. Provide quick Analytics on the most popular food item
	 * 
	 * <p>
	 * Selects the dish that has the highest count in the database (appears most
	 * often)
	 * </p>
	 *
	 * @return the string (dish name)
	 * @throws SQLException
	 *             the SQL exception
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

	/**
	 * Highlight orders. Gets the number of the last order.
	 *
	 * @return the order number as a String
	 * @throws SQLException
	 *             the SQL exception
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

	/**
	 * Highlight sum. Gets the total money earned in this month.
	 * 
	 * <p>
	 * Takes the current date, get the month characters and filters the database
	 * query (total sum of bills in this month)
	 * </p>
	 *
	 * @return the sum of the bills as a String
	 * @throws SQLException
	 *             the SQL exception
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

}
