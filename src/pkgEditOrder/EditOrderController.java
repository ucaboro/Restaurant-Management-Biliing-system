/*
 *<h1>Edit Order screen:</h1>
 * 
 *Creates a customised popup with the table number and the order number
 *to review and edit the table's current order
 *
 *<p>
 *Functions:
 *</p>
 *
 * <p>
 * Add new dish / Edit current dish / Delete dish / Delete whole Order / Confirm any changes / Close Order
 * </p>
 * 
 * 
 * @author Anton Morozov
 * @since  18/12/2016
 */
package pkgEditOrder;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Functionality;
import application.SqliteConnection;
import application.tablemodels.orders;
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
import javafx.scene.input.MouseEvent;

/**
 * The Class EditOrderController.
 */
public class EditOrderController implements Initializable {

	/** The Table num. */
	@FXML
	private Label TableNum;

	/** The Order num. */
	@FXML
	private Label OrderNum;

	/** The table data. */
	private ObservableList<orders> tableData;

	/** The data. To link data to the Dish Combobox from the Food Menu */
	private ObservableList<String> data;

	/** The Comment txt. */
	@FXML
	private TextField CommentTxt;

	/** The Quantity drop. */
	@FXML
	private ComboBox<String> QtDrop;

	/** The Dish drop. */
	@FXML
	private ComboBox<String> DishDrop;

	/** The Add button. */
	@FXML
	private Button AddButton;

	/** The Modify button. */
	@FXML
	private Button ModifyButton;

	/** The Delete button. */
	@FXML
	private Button DeleteButton;

	/** The Order served. */
	@FXML
	private Button OrderServed;

	/** The Confirm changes. */
	@FXML
	private Button ConfirmChanges;

	/** The Load button. */
	@FXML
	private Button LoadButton;

	/** The Close button. */
	@FXML
	private Button CloseButton;

	/** The Delete order. */
	@FXML
	private Button DeleteOrder;

	/** The tbl current order. */
	@FXML
	private TableView<orders> tblCurrentOrder;

	/** The order col. */
	@FXML
	private TableColumn<orders, String> orderCol;

	/** The dish col. */
	@FXML
	private TableColumn<orders, String> dishCol;

	/** The quantity col. */
	@FXML
	private TableColumn<orders, String> quantityCol;

	/** The comment col. */
	@FXML
	private TableColumn<orders, String> commentCol;

	/** The tablenum col. */
	@FXML
	private TableColumn<orders, String> tablenumCol;

	/** The price col. */
	@FXML
	private TableColumn<orders, String> priceCol;

	/** The time col. */
	@FXML
	private TableColumn<orders, String> timeCol;

	/** The Order number. */
	String OrderNumber;

	/** The Button counter. To use for validation and checks */
	int ButtonCounter = 0;

	/** The Load button counter. Avoid changes unless this button pressed */
	int LoadButtonCounter = 0;

	/** The connection. */
	Connection connection;

	/** The link. To connect data to the dropdown box */
	Functionality link = new Functionality();

	/** The price. To calculate total price when the quantity is changed */
	Functionality price = new Functionality();

	/** The today. To get today's data */
	Functionality today = new Functionality();

	/** The log. To log the action into a file */
	Functionality log = new Functionality();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		connection = SqliteConnection.Connector();
		/** setting buttons invisible at first */
		AddButton.setVisible(false);
		ModifyButton.setVisible(false);
		DeleteButton.setVisible(false);

		/** populating comboboxes */
		QtDrop.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5"));
		/* populate dish combobox */
		link.linkDbToComboBox(DishDrop, data, connection);

		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Populate table view.
	 *
	 * @param event
	 *            the event
	 */
	@FXML
	private void populateTableView(ActionEvent event) {
		connection = SqliteConnection.Connector();

		try {
			LoadButtonCounter++;
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			tableData = FXCollections.observableArrayList();
			String query = "select * from orders where tablenum=? and ordernum=?";

			preparedStatement = connection.prepareStatement(query);
			int tablenumber = Integer.parseInt(TableNum.getText());
			int ordernumber = Integer.parseInt(OrderNum.getText());

			preparedStatement.setInt(1, tablenumber);
			preparedStatement.setInt(2, ordernumber);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				int ordernum = resultSet.getInt(2);
				String dish = resultSet.getString(3);
				int quantity = resultSet.getInt(4);
				String comment = resultSet.getString(5);
				int tablenum = resultSet.getInt(6);
				double price = resultSet.getDouble(7);
				String time = resultSet.getString(8);

				tableData.add(new orders(ordernum, dish, quantity, comment, tablenum, price, time));
			}

			preparedStatement.close();
			resultSet.close();

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());

		}

		orderCol.setCellValueFactory(new PropertyValueFactory<>("ordernum"));
		dishCol.setCellValueFactory(new PropertyValueFactory<>("dish"));
		quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		commentCol.setCellValueFactory(new PropertyValueFactory<>("comment"));
		tablenumCol.setCellValueFactory(new PropertyValueFactory<>("tablenum"));
		priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
		timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));

		tblCurrentOrder.setItems(tableData);

		/** Set buttons visisble for actions when data is loaded */
		AddButton.setVisible(true);
		ModifyButton.setVisible(true);
		DeleteButton.setVisible(true);

		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the table number.
	 *
	 * @param number
	 *            of the table
	 */
	public void GetTableNum(String number) {
		TableNum.setText(number);
	}

	/**
	 * Gets the order number.
	 *
	 * @param number
	 *            of the order
	 */
	public void GetOrderNum(String number) {
		OrderNum.setText(number);
	}

	/**
	 * Action for the Add button. If table is loaded, sends the input to the
	 * database, updates the input fields and refreshes the table
	 *
	 * @param event
	 *            for the button
	 * @throws SQLException
	 *             the SQL exception
	 */
	@FXML
	private void AddButton(ActionEvent event) throws SQLException {

		connection = SqliteConnection.Connector();

		if (DishDrop.getValue() != null && QtDrop.getValue() != null) {
			try {
				ButtonCounter++;
				sendToDb();
				clearFields();

				populateTableView(event);

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
	 * Delete button. Deletes the highlighted item
	 *
	 * @param event
	 *            the event
	 */
	@FXML
	private void DeleteButton(ActionEvent event) {
		int row = tblCurrentOrder.getSelectionModel().getSelectedIndex() + 1;
		int rowforcell = tblCurrentOrder.getSelectionModel().getSelectedIndex();

		String dish = dishCol.getCellData(rowforcell);
		String qt = String.valueOf(quantityCol.getCellData(rowforcell));
		String ordernum = OrderNum.getText();

		connection = SqliteConnection.Connector();

		PreparedStatement preparedStatement = null;

		try {

			if (row != 0) {
				ButtonCounter++;
				String del = "delete from orders where dish=? and quantity=? and ordernum=?";
				preparedStatement = connection.prepareStatement(del);
				preparedStatement.setString(1, dish);
				preparedStatement.setString(2, qt);
				preparedStatement.setString(3, ordernum);
				preparedStatement.execute();

				populateTableView(event);

				Alert a = new Alert(AlertType.INFORMATION, "The Row was deleted!", ButtonType.OK);
				a.show();
			} else {
				Alert a = new Alert(AlertType.ERROR, "You need to select the row!", ButtonType.OK);
				a.show();
			}

			preparedStatement.close();
			connection.close();

		} catch (Exception ex) {

		}

	}

	/**
	 * Edit button. Updates the database with the user input. Also calculate the
	 * new total price if the quantity was changed.
	 *
	 * @param event
	 *            the event
	 */
	@FXML
	private void EditButton(ActionEvent event) {

		connection = SqliteConnection.Connector();
		int row = tblCurrentOrder.getSelectionModel().getSelectedIndex();

		// update with this values
		String dish = DishDrop.getValue();
		String qt = QtDrop.getValue();
		String comment = CommentTxt.getText();
		double sum = calculatePrice();
		// where the following values are met
		String ordernum = OrderNum.getText();
		String search = dishCol.getCellData(row);

		PreparedStatement preparedStatement = null;

		String query = "update orders SET dish=?, quantity=?, comment=?, bill=? where ordernum=? and dish=?";

		try {
			preparedStatement = connection.prepareStatement(query);

			preparedStatement.setString(1, dish);
			preparedStatement.setString(2, qt);
			preparedStatement.setString(3, comment);
			preparedStatement.setDouble(4, sum);

			preparedStatement.setString(5, ordernum);
			preparedStatement.setString(6, search);

			/** manual validation that the fields are not empty */
			if ((DishDrop.getValue().length() != 0) && (QtDrop.getValue().length() != 0)) {
				ButtonCounter++;
				preparedStatement.execute();

				clearFields();
				populateTableView(event);
				Alert a = new Alert(AlertType.INFORMATION, "Success! The dish has been edited", ButtonType.OK);
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
	 * Calculate price. Used for the changed total price calculation
	 *
	 * @return the double
	 */
	public double calculatePrice() {

		String qt;
		double price;
		double sum = 0;

		qt = QtDrop.getValue();

		price = retrievePrice();

		int qtint = Integer.parseInt(qt);

		sum = (qtint * price);
		return sum;

	}

	/**
	 * Retrieve price.
	 *
	 * @return the price as double for a selected dish
	 */
	// returns price of the selected dish
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
	 * Used to update the fields on the row selection
	 *
	 * @param event
	 *            the event
	 */
	@FXML
	public void handle(MouseEvent event) {
		PrepopulateOnClick();
	}

	/**
	 * Prepopulate on click. Prepopulates the fields as per the row that is
	 * selected.
	 */
	private void PrepopulateOnClick() {
		int row = tblCurrentOrder.getSelectionModel().getSelectedIndex();

		try {

			DishDrop.setValue(dishCol.getCellData(row));

			QtDrop.setValue(String.valueOf(quantityCol.getCellData(row)));

			CommentTxt.setText(commentCol.getCellData(row));

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Send to the selected fields (user input) to the database
	 */
	private void sendToDb() {

		String ordernum = OrderNum.getText();
		String dish = DishDrop.getValue();
		String quantity = QtDrop.getValue();
		String comment = CommentTxt.getText();
		String tablenum = TableNum.getText();
		double bill = price.calculatePrice(QtDrop, DishDrop, connection);

		PreparedStatement preparedStatement = null;
		try {

			String send = "INSERT INTO orders (\"ordernum\",dish,quantity,comment,tablenum,bill) values(?,?,?,?,?,?)";
			preparedStatement = connection.prepareStatement(send);
			preparedStatement.setString(1, ordernum);
			preparedStatement.setString(2, dish);
			preparedStatement.setString(3, quantity);
			preparedStatement.setString(4, comment);
			preparedStatement.setString(5, tablenum);
			preparedStatement.setDouble(6, bill);

			preparedStatement.execute();

			preparedStatement.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Clear fields.
	 */
	private void clearFields() {

		QtDrop.setValue(null);
		DishDrop.setValue(null);
		CommentTxt.setText(null);

	}

	/**
	 * Confirm button. Confirms the changes, hide the screen, updates the
	 * database and logs action to a .txt file.
	 *
	 * @param event
	 *            the event
	 * @throws SQLException
	 *             the SQL exception
	 */
	@FXML
	private void ConfirmButton(ActionEvent event) throws SQLException {

		connection = SqliteConnection.Connector();

		if (ButtonCounter != 0) {

			log.writeToLog("Edited an existing order " + " at " + today.currentDate());

			try {
				PreparedStatement preparedStatement = null;
				String date = today.currentDate();
				String tablenum = TableNum.getText();
				String ordernum = OrderNum.getText();

				String upd = "update orders set time=? where tablenum=? and ordernum=?";
				preparedStatement = connection.prepareStatement(upd);
				preparedStatement.setString(1, date);
				preparedStatement.setString(2, tablenum);
				preparedStatement.setString(3, ordernum);

				preparedStatement.execute();

				Alert a = new Alert(AlertType.INFORMATION, "Success! The order has been updated", ButtonType.OK);
				a.show();

				((Node) (event.getSource())).getScene().getWindow().hide();

				connection.close();

			} catch (NullPointerException ex) {
				ex.printStackTrace();

			}
		} else {
			Alert a = new Alert(AlertType.ERROR, "Please make some changes before saving it", ButtonType.OK);
			a.show();
		}

	}

	/**
	 * Served button. Closes the order and updates the table activity in the
	 * database. Also logs action to a file
	 *
	 * @param event
	 *            the event
	 * @throws SQLException
	 *             the SQL exception
	 */
	@FXML
	private void ServedButton(ActionEvent event) throws SQLException {

		connection = SqliteConnection.Connector();

		if (LoadButtonCounter > 0) {
			if (ButtonCounter == 0) {
				try {

					log.writeToLog("Closed an existing order " + " at " + today.currentDate());
					PreparedStatement preparedStatement = null;

					String tablenum = TableNum.getText();
					String close = "false";

					String upd = "update tables set active=? where tablenum=?";
					preparedStatement = connection.prepareStatement(upd);
					preparedStatement.setString(1, close);
					preparedStatement.setString(2, tablenum);

					preparedStatement.execute();

					Alert a = new Alert(AlertType.INFORMATION, "The order has been closed. The table is free now!",
							ButtonType.OK);
					a.show();

					((Node) (event.getSource())).getScene().getWindow().hide();
					preparedStatement.close();
					connection.close();

				} catch (NullPointerException ex) {
					ex.printStackTrace();

				}
			} else {
				Alert a = new Alert(AlertType.ERROR, "You made changes. Please confirm them first.", ButtonType.OK);
				a.show();
			}
		} else {
			Alert a = new Alert(AlertType.ERROR, "Load the table first", ButtonType.OK);
			a.show();
		}
	}

	/**
	 * Deletes order. Deletes the whole order from the system after asking for
	 * confirmation.
	 *
	 * @param event
	 *            the event
	 * @throws SQLException
	 *             the SQL exception
	 */
	@FXML
	private void DeleteOrder(ActionEvent event) throws SQLException {

		String ordernum = OrderNum.getText();

		if (LoadButtonCounter > 0) {
			try {
				connection = SqliteConnection.Connector();

				PreparedStatement preparedStatement = null;

				Alert a = new Alert(AlertType.CONFIRMATION,
						"Do you confirm you want to delete the whole order from the system?", ButtonType.NO,
						ButtonType.YES);

				Optional<ButtonType> result = a.showAndWait();
				if (result.get() == ButtonType.YES) {
					// if user chose YES
					// delete the whole order
					String del = "delete from orders where ordernum=?";
					preparedStatement = connection.prepareStatement(del);
					preparedStatement.setString(1, ordernum);
					preparedStatement.execute();

					populateTableView(event);
					preparedStatement.close();
					connection.close();

					// close the frame and update the activity of the table
					if (LoadButtonCounter > 0) {
						try {
							connection = SqliteConnection.Connector();
							PreparedStatement preparedStatement1 = null;

							String tablenum = TableNum.getText();
							String close = "false";

							String upd = "update tables set active=? where tablenum=?";
							preparedStatement1 = connection.prepareStatement(upd);
							preparedStatement1.setString(1, close);
							preparedStatement1.setString(2, tablenum);

							preparedStatement1.execute();

							// hide current screen
							((Node) (event.getSource())).getScene().getWindow().hide();
							preparedStatement1.close();
							connection.close();

						} catch (NullPointerException ex) {
							ex.printStackTrace();

						}

						Alert b = new Alert(AlertType.INFORMATION, "The whole order was deleted!", ButtonType.OK);
						b.show();

					} else {
						Alert b = new Alert(AlertType.INFORMATION, "Load the order first", ButtonType.OK);
						b.show();
					}

				} else {
					Alert b = new Alert(AlertType.INFORMATION, "The order is still in place", ButtonType.OK);
					b.show();
				}
			}

			catch (Exception ex) {
				ex.printStackTrace();
			}

		} else {
			Alert b = new Alert(AlertType.ERROR, "Load the order first", ButtonType.OK);
			b.show();
		}

	}

	/**
	 * Close button. Close the frame if no changes were made.
	 *
	 * @param event
	 *            the event
	 * @throws SQLException
	 *             the SQL exception
	 */
	@FXML
	private void CloseButton(ActionEvent event) throws SQLException {

		// if the user has made any changes - cannot just close the window
		if (ButtonCounter == 0) {

			((Node) (event.getSource())).getScene().getWindow().hide();

		} else {
			Alert a = new Alert(AlertType.ERROR, "You made changes. You need to confirm them!", ButtonType.OK);
			a.show();
		}

	}

}
