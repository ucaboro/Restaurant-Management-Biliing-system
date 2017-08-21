/*
 *<h1>Total Orders class:</h1>
 * 
 *Creates a view to filter the total orders 
 *
 *<p>
 *Functions:
 *</p>
 *
 * <p>
 * Filter by all the fields / Time fields visibility on selection / Importing/Exporting to Excel
 * </p>
 * 
 * 
 * @author Anton Morozov
 * @since  18/12/2016
 */
package pkgOrders;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.ss.usermodel.Row;
import application.SqliteConnection;
import application.tablemodels.orders;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

/**
 * The Class OrdersController.
 */
public class OrdersController implements Initializable {

	/** The tbl orders table. */
	@FXML
	private TableView<orders> tblOrdersTable;

	/** The table data. */
	private ObservableList<orders> tableData;

	/** The Search txt. */
	@FXML
	private TextField SearchTxt;

	/** The Search drop. */
	@FXML
	private ComboBox<String> SearchDrop;

	/** The Update view. */
	@FXML
	private Button UpdateView;

	/** The Export button. */
	@FXML
	private ImageView ExportButton;

	/** The Import button. */
	@FXML
	private ImageView ImportButton;

	/** The lbl from. */
	@FXML
	private Label lblFrom;

	/** The lbl to. */
	@FXML
	private Label lblTo;

	/** The to search. */
	@FXML
	private DatePicker toSearch;

	/** The from search. */
	@FXML
	private DatePicker fromSearch;

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

	/** The connection. */
	Connection connection;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// populating comboboxes
		SearchDrop.setItems(
				FXCollections.observableArrayList("ordernum", "dish", "quantity", "comment", "tablenum", "time"));

		lblFrom.setVisible(false);
		lblTo.setVisible(false);
		toSearch.setVisible(false);
		fromSearch.setVisible(false);

		connection = SqliteConnection.Connector();

		populateTableView();

		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Populate table view with all orders before the filter applies.
	 */
	@FXML
	private void populateTableView() {

		try {
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			tableData = FXCollections.observableArrayList();
			String query = "select * from orders";

			preparedStatement = connection.prepareStatement(query);
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

		tblOrdersTable.setItems(tableData);

		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update button as per the user input and selection on what to search by.
	 * Adds Date picker if the user selects time from the dropdown box.
	 *
	 * @param event
	 *            the event
	 * @throws SQLException
	 *             the SQL exception
	 */
	@FXML
	private void UpdateButton(ActionEvent event) throws SQLException {

		connection = SqliteConnection.Connector();
		String search = SearchTxt.getText();
		tableData = FXCollections.observableArrayList();

		// if search by is selected
		if (SearchDrop.getValue() != null) {

			// if there is an input in the text field or the date picker
			if (SearchTxt.getText().isEmpty() == false
					|| toSearch.getValue() != null && fromSearch.getValue() != null) {

				// if the dropdown box selection is time
				if (SearchDrop.getSelectionModel().getSelectedItem().equals("time")) {

					// getting the required values from the datepicker
					int dayto = toSearch.getValue().getDayOfMonth();// 17
					int monthto = toSearch.getValue().getMonthValue();// 12
					int yearto = toSearch.getValue().getYear();// 2016

					String ToString = String.valueOf(yearto + "/" + monthto + "/" + dayto + "%");

					int dayfrom = fromSearch.getValue().getDayOfMonth();// 17
					int monthfrom = fromSearch.getValue().getMonthValue();// 12
					int yearfrom = fromSearch.getValue().getYear();// 2016
					String FromString = String.valueOf(yearfrom + "/" + monthfrom + "/" + dayfrom + "%");

					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;
					String upd = "SELECT * FROM orders WHERE time BETWEEN ? AND ?";
					preparedStatement = connection.prepareStatement(upd);
					preparedStatement.setString(1, FromString);
					preparedStatement.setString(2, ToString);
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

					orderCol.setCellValueFactory(new PropertyValueFactory<>("ordernum"));
					dishCol.setCellValueFactory(new PropertyValueFactory<>("dish"));
					quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
					commentCol.setCellValueFactory(new PropertyValueFactory<>("comment"));
					tablenumCol.setCellValueFactory(new PropertyValueFactory<>("tablenum"));
					priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
					timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));

					tblOrdersTable.setItems(tableData);

					connection.close();

					// if not time is selected
				} else {

					try {
						PreparedStatement preparedStatement = null;
						ResultSet resultSet = null;
						String upd = "select * from orders where " + SearchDrop.getValue() + "=?";
						preparedStatement = connection.prepareStatement(upd);
						preparedStatement.setString(1, search);

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

						orderCol.setCellValueFactory(new PropertyValueFactory<>("ordernum"));
						dishCol.setCellValueFactory(new PropertyValueFactory<>("dish"));
						quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
						commentCol.setCellValueFactory(new PropertyValueFactory<>("comment"));
						tablenumCol.setCellValueFactory(new PropertyValueFactory<>("tablenum"));
						priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
						timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));

						tblOrdersTable.setItems(tableData);

						connection.close();

					} catch (NullPointerException ex) {
						ex.printStackTrace();
					}
				}

			} else {
				Alert a = new Alert(AlertType.ERROR, "Please input some text or select the date", ButtonType.OK);
				a.show();

			}
			// if the dropdown is not selected
		} else {
			Alert a = new Alert(AlertType.ERROR, "Please select the dropdown", ButtonType.OK);
			a.show();
		}

	}

	/**
	 * Handle. If the user selects time / other itmes change the visisbility of
	 * several actionable elements for a better user interface.
	 *
	 * @param event
	 *            the event
	 */
	@FXML
	public void handle(ActionEvent event) {

		if (SearchDrop.getSelectionModel().getSelectedItem().equals("time")) {
			try {
				SearchTxt.setVisible(false);
				lblFrom.setVisible(true);
				lblTo.setVisible(true);
				toSearch.setVisible(true);
				fromSearch.setVisible(true);

			} catch (Exception ex) {

				ex.printStackTrace();

			}

		} else {
			SearchTxt.setVisible(true);
			lblFrom.setVisible(false);
			lblTo.setVisible(false);
			toSearch.setVisible(false);
			fromSearch.setVisible(false);
		}

	}

	/**
	 * Export button. Exports to excel file.
	 *
	 * @param event
	 *            the event
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	// exporting function
	@FXML
	private void ExportButton(MouseEvent event) throws IOException {
		FileChooser choose = new FileChooser();

		// creating extension for excel
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Excel File (*.xls)", "*.xls");
		choose.getExtensionFilters().add(filter);

		// save
		File file = choose.showSaveDialog(ExportButton.getScene().getWindow());
		if (file != null) {
			try {
				writeExcel(file);
			} catch (Exception e) {

				e.printStackTrace();
			}
			Alert a = new Alert(AlertType.INFORMATION, "Saved to .xls!", ButtonType.OK);
			a.show();
		}
	}

	/**
	 * Write excel. Used for exporting functionality. Places inputs in different
	 * cells to make it more natural and usable afterwards.
	 *
	 * @param file
	 *            the file
	 * @throws Exception
	 *             the exception
	 */
	// write to .csv function
	public void writeExcel(File file) throws Exception {
		Writer writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));

			String head = "OrderNumber \t" + "Dish \t" + "Quantity \t" + "Commentary \t" + "Table number \t"
					+ "Price \t" + "Time Stamp \n";
			writer.write(head);
			for (orders ord : tableData) {

				String text = ord.getOrdernum() + "\t" + ord.getDish() + "\t" + ord.getQuantity() + "\t"
						+ ord.getComment() + "\t" + ord.getTablenum() + "\t" + ord.getPrice() + "\t" + ord.getTime()
						+ "\n";

				writer.write(text);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			writer.flush();
			writer.close();
		}
	}

	/**
	 * Import button. Imports a file to the database.
	 * 
	 * <p>
	 * NOTE: same style have to be used. Excel format 97-03. If wrong the system
	 * will show the alert advising to change the style / format.
	 * </p>
	 *
	 * @param event
	 *            the event
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws SQLException
	 *             the SQL exception
	 */
	@FXML
	private void ImportButton(MouseEvent event) throws IOException, SQLException {
		connection = SqliteConnection.Connector();

		try {
			PreparedStatement preparedStatement = null;

			String query = "INSERT INTO orders (ordernum,dish,quantity,comment,tablenum,bill,time) values(?,?,?,?,?,?,?)";
			preparedStatement = connection.prepareStatement(query);

			FileChooser choose = new FileChooser();
			choose.setTitle("Open File for Import");
			File file = choose.showOpenDialog(ImportButton.getScene().getWindow());

			if (file != null) {
				FileInputStream fileIn = new FileInputStream(file);
				HSSFWorkbook wb = new HSSFWorkbook(fileIn);
				HSSFSheet sheet = wb.getSheetAt(0);

				Row row;

				for (int i = 1; i <= sheet.getLastRowNum(); i++) {
					row = sheet.getRow(i);

					preparedStatement.setInt(1, (int) row.getCell(0).getNumericCellValue());
					preparedStatement.setString(2, row.getCell(1).getStringCellValue());
					preparedStatement.setInt(3, (int) row.getCell(2).getNumericCellValue());
					preparedStatement.setString(4, row.getCell(3).getStringCellValue());
					preparedStatement.setInt(5, (int) row.getCell(4).getNumericCellValue());
					preparedStatement.setDouble(6, (double) row.getCell(5).getNumericCellValue());
					preparedStatement.setInt(7, (int) row.getCell(5).getNumericCellValue());

					try {
						preparedStatement.execute();
					} catch (SQLException e) {

						e.printStackTrace();
					}
				}

				// popup confirmation to the user
				Alert a = new Alert(AlertType.INFORMATION, "Success! the data has been imported into database!",
						ButtonType.OK);
				a.show();
				preparedStatement.close();
				wb.close();
				fileIn.close();

				populateTableView();

				connection.close();
			} else {
				Alert a = new Alert(AlertType.ERROR, "You need to select a file", ButtonType.OK);
				a.show();
			}

		} catch (NotOLE2FileException ex) {
			Alert a = new Alert(AlertType.ERROR, "Didn't work.. Ensure proper layout and .xls format is 97-03",
					ButtonType.OK);
			a.show();
			ex.printStackTrace();

		}

	}

}
