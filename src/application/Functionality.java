/*
 *<h1>Functionality class:</h1>
 *Makes use of repeated functions to implement on pages where the same outcome is expected
 * 
 * 
 * @author Anton Morozov
 * @since  18/12/2016
 */
package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * The Class Functionality.
 */
public class Functionality {
	/**
	 * Next stage.
	 *
	 * @param fxml
	 *            the fxml url for a jump
	 * @param title
	 *            the title of the page
	 * @param maximized
	 *            the maximized view
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */

	public void nextStage(String fxml, String title, boolean maximized) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(fxml));
		Stage stage = new Stage();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle(title);
		stage.setMaximized(maximized);
		stage.show();
	}

	/**
	 * Checks if the value from the user input is double.
	 *
	 * @param userInput
	 *            the user input
	 * @return true, if is double
	 */

	public boolean isDouble(TextField userInput) {
		boolean bool = false;
		if (!(userInput.getText() == null || userInput.getText().length() == 0)) {
			try {
				double num = Double.parseDouble(userInput.getText());
				System.out.println("num=" + num);
				if (num > 0) {
					bool = true;
				}

			} catch (NumberFormatException ex) {

			}
		}

		return bool;
	}

	/**
	 * Checks if the user input is not empty. Used to validate double input.
	 *
	 * @param userInput
	 *            the user input
	 * @return true, if it is not empty
	 */

	public boolean isNotEmpty(String userInput) {
		boolean bool = false;
		if (!(userInput == null || userInput.length() == 0)) {
			bool = true;

		}
		return bool;
	}

	/**
	 * Checks if the input is an integer.
	 *
	 * @param userInput
	 *            the user input
	 * @return true, if integer
	 */
	public boolean isNum(String userInput) {
		boolean b = false;
		try {
			Integer.parseInt(userInput);

			b = true;

		} catch (NumberFormatException ex) {

			b = false;

		}

		return b;

	}

	/**
	 * Link database to the combo box.
	 *
	 * @param DishDrop
	 *            the dish drop
	 * @param data
	 *            the data
	 * @param connection
	 *            the connection
	 */

	public void linkDbToComboBox(ComboBox<String> DishDrop, ObservableList<String> data, Connection connection) {

		try {
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			data = FXCollections.observableArrayList();

			String query = "select * from foodmenu";

			preparedStatement = connection.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				data.add(resultSet.getString(3));

			}
			DishDrop.setItems(data);
			preparedStatement.close();

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());

		}

	}

	/**
	 * Calculate the price, when linking different tables in the database
	 * together.
	 * 
	 * Used for the Create Order/Edit Order popup screens, when the user
	 * modifies the input
	 *
	 * @param QtDrop
	 *            the quanitity dropdown
	 * @param DishDrop
	 *            the dish dropdown
	 * @param connection
	 *            the connection
	 * @return the double sum value
	 */
	public double calculatePrice(ComboBox<String> QtDrop, ComboBox<String> DishDrop, Connection connection) {

		String qt;
		double price;
		double sum = 0;

		/** get the quanitity value of the dropdown box */
		qt = QtDrop.getValue();

		/** get the price of the dish from the database */
		price = retrievePrice(DishDrop, connection);

		int qtint = Integer.parseInt(qt);

		sum = (qtint * price);
		return sum;

	}

	/**
	 * Retrieve price from a different table in the database based on user input
	 *
	 * @param DishDrop
	 *            the dish dropdown
	 * @param connection
	 *            the connection
	 * @return the double price
	 */
	public double retrievePrice(ComboBox<String> DishDrop, Connection connection) {

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
	 * Current date.
	 *
	 * @return the string - today's date
	 */
	public String currentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String currentDate = dateFormat.format(date);
		return currentDate;
	}

	/**
	 * Write to log. method to write out to a file (used for embedding in
	 * actionable elements for logging activities)
	 *
	 * @param logtext
	 *            as per the log actitivies
	 * 
	 */
	public void writeToLog(String logtext) {
		try {
			File file = new File("LogActivities.txt");

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(logtext);
			bw.write("\r\n");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}