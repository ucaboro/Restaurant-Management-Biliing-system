/*
 *<h1>Order Popup class:</h1>
 * 
 *Creates a popup to register a new order
 *
 *<p>
 *Functions:
 *</p>
 *
 * <p>
 * Add new item / Select item / Select quantity / See the overall price of the order / Price of the last dish
 * </p>
 * 
 * 
 * @author Anton Morozov
 * @since  18/12/2016
 */
package pkgOrderPopup;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import application.Functionality;
import application.SqliteConnection;
import application.tablemodels.totalorder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * The Class OrderController.
 */
public class OrderController implements Initializable {

	/** The data. */
	private ObservableList<String> data;

	/** The table data. */
	private ObservableList<totalorder> tableData;

	/** The Table num. */
	@FXML
	private Label TableNum;

	/** The total price. */
	@FXML
	private Label totalPrice;

	/** The sub price. */
	@FXML
	private Label subPrice;

	/** The order ID. */
	@FXML
	private Label orderID;

	/** The Add button. */
	@FXML
	private Button AddButton;

	/** The Confirm button. */
	@FXML
	private Button ConfirmButton;

	/** The Comment txt. */
	@FXML
	private TextField CommentTxt;

	/** The Qt drop. */
	@FXML
	private ComboBox<String> QtDrop;

	/** The Dish drop. */
	@FXML
	private ComboBox<String> DishDrop;

	/** The tbl total order. */
	@FXML
	private TableView<totalorder> tblTotalOrder;

	/** The id col. */
	@FXML
	private TableColumn<totalorder, String> idCol;

	/** The dish col. */
	@FXML
	private TableColumn<totalorder, String> dishCol;

	/** The quantity col. */
	@FXML
	private TableColumn<totalorder, String> quantityCol;

	/** The price col. */
	@FXML
	private TableColumn<String, String> priceCol;

	/** The comment col. */
	@FXML
	private TableColumn<totalorder, String> commentCol;

	/** The total. */
	private double total = 0;

	/** The ordernum. */
	private int ordernum = 1;

	/** The Add counter. */
	private int AddCounter = 0;

	/** The connection. */
	Connection connection;

	/** The link. */
	Functionality link = new Functionality();

	/** The log. */
	Functionality log = new Functionality();

	/** The time. */
	Functionality time = new Functionality();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// clear table if there is smth from the previous session
		ClearTableView();

		connection = SqliteConnection.Connector();
		// instantiating orderID from 1
		orderID.setText(String.valueOf(ordernum));

		// set order ID after comapring against existing ones in the db
		CreateOrderId();

		// setting the total and sub price calc
		totalPrice.setText(String.valueOf(total));
		subPrice.setText(String.valueOf(total));

		// populating comboboxes
		QtDrop.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5"));
		// populate dish combobox
		link.linkDbToComboBox(DishDrop, data, connection);

		try {
			connection.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	/**
	 * Populate table view. Used to populate when the user inputs data and click
	 * the button.
	 */
	private void populateTableView() {

		try {
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			tableData = FXCollections.observableArrayList();
			String query = "select * from totalordercalc";

			preparedStatement = connection.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				int id = resultSet.getInt(1);
				String dish = resultSet.getString(2);
				int qt = resultSet.getInt(3);
				String comment = resultSet.getString(4);

				tableData.add(new totalorder(id, dish, qt, comment));
			}
			preparedStatement.close();

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());

		}

		idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
		dishCol.setCellValueFactory(new PropertyValueFactory<>("dish"));
		quantityCol.setCellValueFactory(new PropertyValueFactory<>("qt"));
		commentCol.setCellValueFactory(new PropertyValueFactory<>("comment"));

		tblTotalOrder.setItems(tableData);

	}

	/**
	 * Add button. Add a new item to the database and refreshes the table view.
	 *
	 * @param event
	 *            the event
	 * @throws SQLException
	 *             the SQL exception
	 */
	@FXML
	private void AddButton(ActionEvent event) throws SQLException {

		connection = SqliteConnection.Connector();

		if (DishDrop.getValue() != null && QtDrop.getValue() != null) {

			// set overall price
			double sum = calculatePrice();
			total = total + sum;

			totalPrice.setText(String.valueOf(total));

			// set sub price
			subPrice.setText(String.valueOf(sum));

			// send the selected order to db
			sendToDb();

			// DishDrop QtDrop CommentTxt retrieval
			String dish = DishDrop.getValue();
			String qt = QtDrop.getValue();
			String comment = CommentTxt.getText();

			PreparedStatement preparedStatement = null;

			String query = "INSERT INTO totalordercalc(\"dish\",qt,comment) values(?,?,?)";

			try {
				// to use for Confirm button
				AddCounter++;

				preparedStatement = connection.prepareStatement(query);

				preparedStatement.setString(1, dish);
				preparedStatement.setString(2, qt);
				preparedStatement.setString(3, comment);

				preparedStatement.execute();
				clearFields();

				// refresh with the update
				populateTableView();

				connection.close();

			} catch (NullPointerException ex) {
				ex.printStackTrace();

			}
		} else {
			Alert a = new Alert(AlertType.ERROR, "Please select the dish and the quantity", ButtonType.OK);
			a.show();
		}

	}

	/**
	 * Clear fields. Clearing input for a new potential order
	 */

	private void clearFields() {

		QtDrop.setValue(null);
		DishDrop.setValue(null);

		CommentTxt.setText(null);

	}

	/**
	 * Gets the table number.
	 *
	 * @param number
	 *            of the table.
	 */
	public void GetTableNum(String number) {
		TableNum.setText(number);
	}

	/**
	 * Calculate the total price.
	 *
	 * @return price as double
	 */
	public double calculatePrice() {

		String qt;
		double price;
		double sum = 0;

		// get the qt value of the dropdown box
		qt = QtDrop.getValue();

		// get the price of the dish from the db
		price = retrievePrice();

		int qtint = Integer.parseInt(qt);

		sum = (qtint * price);
		return sum;

	}

	/**
	 * Retrieve price.
	 *
	 * @return the double price of the selected dish
	 */

	public double retrievePrice() {

		double price = 0;
		String dish = DishDrop.getValue();
		PreparedStatement preparedStatement = null;

		String query = "select price from foodmenu where dish = ?";

		try {
			preparedStatement = connection.prepareStatement(query);

			preparedStatement.setString(1, dish);

			preparedStatement.execute();
			price = Double.parseDouble(preparedStatement.getResultSet().getString(1));

			preparedStatement.close();

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return price;

	}

	/**
	 * Clear table view. Clear the table view on openning the scene and on
	 * hitting confirm order
	 */

	private void ClearTableView() {

		connection = SqliteConnection.Connector();

		PreparedStatement preparedStatement = null;
		try {
			// deleting highlighted row from the menu
			String del = "delete from totalordercalc";
			preparedStatement = connection.prepareStatement(del);
			preparedStatement.execute();

			populateTableView();

			preparedStatement.close();
			connection.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Send to database. Sends the order of a specific item to a db prior to
	 * confirmation
	 */

	@FXML
	private void sendToDb() {

		String ordernum = orderID.getText();
		String dish = DishDrop.getValue();
		String quantity = QtDrop.getValue();
		String comment = CommentTxt.getText();
		String tablenum = TableNum.getText();
		String bill = subPrice.getText();

		PreparedStatement preparedStatement = null;
		try {

			String send = "INSERT INTO orders (\"ordernum\",dish,quantity,comment,tablenum,bill) values(?,?,?,?,?,?)";
			preparedStatement = connection.prepareStatement(send);
			preparedStatement.setString(1, ordernum);
			preparedStatement.setString(2, dish);
			preparedStatement.setString(3, quantity);
			preparedStatement.setString(4, comment);
			preparedStatement.setString(5, tablenum);
			preparedStatement.setString(6, bill);

			preparedStatement.execute();

			preparedStatement.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Creates the order id. Creating order id and checking against a database
	 * if it exists if it does, increment by 1 and use the new order
	 * 
	 * @return the int
	 */

	private int CreateOrderId() {

		int ordernum = Integer.parseInt(orderID.getText());

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {

			// retrieveing data from db to see if the ordernum exist
			String query = "SELECT ordernum from orders ORDER BY ordernum DESC";
			preparedStatement = connection.prepareStatement(query);

			resultSet = preparedStatement.executeQuery();

			String number = resultSet.getString(1);
			while (Integer.parseInt(number) >= ordernum) {
				ordernum++;
			}

			resultSet.close();
			preparedStatement.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		orderID.setText(String.valueOf(ordernum));
		return ordernum;

	}

	/**
	 * Confirm button. sending the date to the db of the order placed changing
	 * the activity filter on the table to true
	 *
	 * @param event
	 *            the event
	 */
	// close the screen
	@FXML
	public void ConfirmButton(ActionEvent event) {
		// confirm if something is entered in the field
		if (AddCounter != 0) {

			// logging
			log.writeToLog("Created an order " + " at " + time.currentDate());

			connection = SqliteConnection.Connector();
			PreparedStatement preparedStatement = null;
			String date = currentDate();
			String tablenum = TableNum.getText();

			try {
				String upd = "update orders set time=? where tablenum=?";
				preparedStatement = connection.prepareStatement(upd);
				preparedStatement.setString(1, date);
				preparedStatement.setString(2, tablenum);

				preparedStatement.execute();

				// updating the table to active to show that the order was
				// placed
				swtchTable();

				// popup confirmation to the user
				Alert a = new Alert(AlertType.INFORMATION, "Success! The order has been placed", ButtonType.OK);
				a.show();

				// hide current screen
				((Node) (event.getSource())).getScene().getWindow().hide();

				connection.close();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			Alert a = new Alert(AlertType.ERROR, "To Confirm please enter at least 1 item to order", ButtonType.OK);
			a.show();
		}

	}

	/**
	 * Current date.
	 *
	 * @return the string
	 */
	public String currentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String currentDate = dateFormat.format(date);
		return currentDate;
	}

	/**
	 * Switch table.
	 */
	public void swtchTable() {

		PreparedStatement preparedStatement = null;
		String tablenum = TableNum.getText();
		boolean switcher = true;

		try {
			String upd = "update tables set active=? where tablenum=?";
			preparedStatement = connection.prepareStatement(upd);
			preparedStatement.setBoolean(1, switcher);
			preparedStatement.setString(2, tablenum);

			preparedStatement.execute();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
