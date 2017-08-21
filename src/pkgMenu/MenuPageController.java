/*
 *<h1>Manage Food Menu Controller class:</h1>
 * 
 *Screen to manage Food items
 *
 *<p>
 *Functions:
 *</p>
 *
 * <p>
 * Add new item / Edit current item / Delete item </p>
 * 
 * 
 * @author Anton Morozov
 * @since  18/12/2016
 */
package pkgMenu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

//excel export functionality
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import application.Functionality;
import application.SqliteConnection;
import application.tablemodels.menu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

/**
 * The Class MenuPageController.
 */
public class MenuPageController implements Initializable {

	/** The data. */
	private ObservableList<menu> data;

	/** The tbl menu. */
	@FXML
	private TableView<menu> tblMenu;

	/** The txt id. */
	@FXML
	private TableColumn<menu, String> txtId;

	/** The txt group. */
	@FXML
	private TableColumn<menu, String> txtGroup;

	/** The txt dish. */
	@FXML
	private TableColumn<menu, String> txtDish;

	/** The txt kcal. */
	@FXML
	private TableColumn<menu, Double> txtKcal;

	/** The txt price. */
	@FXML
	private TableColumn<menu, Double> txtPrice;

	/** The Home button. */
	@FXML
	private Button HomeButton;

	/** The Add button. */
	@FXML
	private Button AddButton;

	/** The Export button. */
	@FXML
	private Button ExportButton;

	/** The Save button. */
	@FXML
	private Button SaveButton;

	/** The Group text. */
	@FXML
	private TextField GroupText;

	/** The Dish text. */
	@FXML
	private TextField DishText;

	/** The Kcal text. */
	@FXML
	private TextField KcalText;

	/** The Price text. */
	@FXML
	private TextField PriceText;

	/** The Constant MANAGER. */
	public static final String MANAGER = "/application/ManagerHome.fxml";

	/** The check. */
	Functionality check;

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
		// TODO

		connection = SqliteConnection.Connector();
		populateTableView();

		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Populate table view from the foodmenu table in the database.
	 */
	private void populateTableView() {
		try {
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			data = FXCollections.observableArrayList();
			String query = "select * from foodmenu";

			preparedStatement = connection.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				int id = resultSet.getInt(1);
				String group = resultSet.getString(2);
				String dish = resultSet.getString(3);
				double kcal = resultSet.getDouble(4);
				double price = resultSet.getDouble(5);

				data.add(new menu(id, group, dish, kcal, price));
			}

			preparedStatement.close();
			resultSet.close();

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());

		}

		txtId.setCellValueFactory(new PropertyValueFactory<>("id"));
		txtGroup.setCellValueFactory(new PropertyValueFactory<>("group"));
		txtDish.setCellValueFactory(new PropertyValueFactory<>("dish"));
		txtKcal.setCellValueFactory(new PropertyValueFactory<>("kcal"));
		txtPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

		tblMenu.setItems(data);

	}

	/**
	 * Addbutton. Used to add a new item.
	 *
	 * @param event
	 *            the event
	 */
	@FXML
	private void AddButton(ActionEvent event) {
		connection = SqliteConnection.Connector();

		String group = GroupText.getText();
		String dish = DishText.getText();
		String kcal = KcalText.getText();
		String price = PriceText.getText();

		PreparedStatement preparedStatement = null;

		String query = "INSERT INTO foodmenu(\"group\",dish,kcal,price) values(?,?,?,?)";

		try {
			preparedStatement = connection.prepareStatement(query);

			preparedStatement.setString(1, group);
			preparedStatement.setString(2, dish);
			preparedStatement.setString(3, kcal);
			preparedStatement.setString(4, price);

			// manual validation
			if ((kcal.length() != 0) && (price.length() != 0) && (dish.length() != 0) && (group.length() != 0)) {

				preparedStatement.execute();

				clearTxtFields();

				Alert a = new Alert(AlertType.INFORMATION, "Success! The new dish has been added", ButtonType.OK);
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
	 * Delete button. To Delete one of the items that is selected on a mouse
	 * click or manual user input.
	 * 
	 * Assuming no more than 50 dishes in the current menu
	 *
	 * @param event
	 *            the event
	 */
	@FXML
	private void DeleteButton(ActionEvent event) {
		int row = tblMenu.getSelectionModel().getSelectedIndex() + 1;

		connection = SqliteConnection.Connector();

		PreparedStatement preparedStatement = null;
		try {

			if (row != 0) {
				// deleting highlighted row from the menu
				String del = "delete from foodmenu where rowid=?";
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

		} catch (Exception ex) {

		}

	}

	/**
	 * Update row ID within the range to use when the row id is deleted
	 *
	 * @param rowID
	 *            the row ID
	 */
	private void updateRowID(int rowID) {

		PreparedStatement preparedStatement = null;
		try {

			String upd = "UPDATE foodmenu SET id=? where rowid=?";

			preparedStatement = connection.prepareStatement(upd);

			preparedStatement.setInt(1, rowID);
			preparedStatement.setInt(2, rowID + 1);
			preparedStatement.execute();

			populateTableView();
			preparedStatement.close();

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	/**
	 * Handle.
	 *
	 * @param event
	 *            the event
	 */
	@FXML
	public void handle(MouseEvent event) {
		PrepopulateOnClick();
	}

	/**
	 * Prepopulate input fields on click.
	 */
	private void PrepopulateOnClick() {
		int row = tblMenu.getSelectionModel().getSelectedIndex();

		connection = SqliteConnection.Connector();

		PreparedStatement preparedStatement = null;
		try {

			// retrieveing data from the highlighted row from the menu
			String query = "SELECT * from foodmenu where rowid=?";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, row);
			preparedStatement.execute();

			GroupText.setText(txtGroup.getCellData(row));

			DishText.setText(txtDish.getCellData(row));

			KcalText.setText(Double.toString(txtKcal.getCellData(row)));

			PriceText.setText(Double.toString(txtPrice.getCellData(row)));

			preparedStatement.close();

		} catch (Exception ex) {

		}

	}

	/**
	 * Save edit button. To update the database.
	 *
	 * @param event
	 *            the event
	 */
	@FXML
	private void SaveEditButton(ActionEvent event) {
		int row = tblMenu.getSelectionModel().getSelectedIndex() + 1;

		String group = GroupText.getText();
		String dish = DishText.getText();
		String kcal = KcalText.getText();
		String price = PriceText.getText();

		PreparedStatement preparedStatement = null;

		String query = "update foodmenu SET \"group\"=?, dish=?, kcal=?, price=? where id=?";

		try {
			preparedStatement = connection.prepareStatement(query);

			preparedStatement.setString(1, group);
			preparedStatement.setString(2, dish);
			preparedStatement.setString(3, kcal);
			preparedStatement.setString(4, price);

			preparedStatement.setInt(5, row);
			// manual validation
			if ((kcal.length() != 0) && (price.length() != 0) && (dish.length() != 0) && (group.length() != 0)) {

				preparedStatement.execute();

				clearTxtFields();
				populateTableView();
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
	 * Clear txt fields.
	 */
	// claring txt input
	private void clearTxtFields() {

		GroupText.setText(null);
		DishText.setText(null);

		KcalText.setText(null);

		PriceText.setText(null);

	}

	/**
	 * Export button. To save current edition of the menu as a excel file.
	 *
	 * @param event
	 *            the event
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */

	@FXML
	private void ExportButton(ActionEvent event) throws IOException {
		FileChooser choose = new FileChooser();

		// creating extension for excel
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Excel File (*.xls)", "*.xls");
		choose.getExtensionFilters().add(filter);

		// save
		File file = choose.showSaveDialog(ExportButton.getScene().getWindow());
		if (file != null) {
			saveToExcel(file);
			Alert a = new Alert(AlertType.INFORMATION, "Saved to .xls!", ButtonType.OK);
			a.show();
		}
	}

	/**
	 * Save to excel. implementing functionality to save to excel external
	 * APACHE POI used to implement this functionality
	 * <p>
	 * link
	 * http://mirror.ox.ac.uk/sites/rsync.apache.org/poi/release/bin/poi-bin-3.15-20160924.zip
	 * </p>
	 * 
	 * @param file
	 *            used
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */

	private void saveToExcel(File file) throws IOException {
		try {
			connection = SqliteConnection.Connector();

			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			String query = "select * from foodmenu";

			preparedStatement = connection.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();

			FileOutputStream out;
			out = new FileOutputStream(file);
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet worksheet = workbook.createSheet("Food Menu");
			HSSFRow header = worksheet.createRow(0);
			header.createCell(0).setCellValue("id");
			header.createCell(1).setCellValue("group");
			header.createCell(2).setCellValue("dish");
			header.createCell(3).setCellValue("kcal");
			header.createCell(4).setCellValue("price");
			int index = 1;
			while (resultSet.next()) {

				HSSFRow row = worksheet.createRow(index);

				row.createCell(0).setCellValue(resultSet.getString("id"));
				row.createCell(1).setCellValue(resultSet.getString("group"));
				row.createCell(2).setCellValue(resultSet.getString("dish"));
				row.createCell(3).setCellValue(resultSet.getString("kcal"));
				row.createCell(4).setCellValue(resultSet.getString("price"));
				index++;
			}
			workbook.write(out);
			out.flush();
			out.close();
			workbook.close();
			resultSet.close();

			preparedStatement.close();
			connection.close();

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

	}

}
