/*
 *<h1>Analytics screen:</h1>
 * 
 * Provides a simple, but detailed view on some of the data stored in the database
 * Gives the breakdown of the most popular dishes and most profitable table
 * 
 * <p>
 * Provides additional information on mouse click
 * </p>
 * 
 * 
 * @author Anton Morozov
 * @since  18/12/2016
 */
package pkgAnalytics;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.SqliteConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert.AlertType;

/**
 * The Class AnalyticsController. Assigns 2 pie charts to actions and update
 * labels on the mouse click with extra info
 */
public class AnalyticsController implements Initializable {

	/** The PieChart 1. */
	@FXML
	private PieChart pchrt1;

	/** The PieChart 2. */
	@FXML
	private PieChart pchrt2;

	/** The Review button. To update the view. */
	@FXML
	private Button ReviewButton;

	/** The labels to output extra info. */
	@FXML
	private Label lblExtra, lblExtra1;;

	/** The popular food ArrayList. */
	ArrayList<String> popularFood = new ArrayList<String>();

	/** The table profit Array List. */
	ArrayList<String> tableProfit = new ArrayList<String>();

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

		connection = SqliteConnection.Connector();

		try {
			connection.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Review action. Updates the view of the Pie CHarts. Outputs the
	 * information to the user if its not enough data in the database to perform
	 * the analysis
	 *
	 * @param event
	 *            on the button click
	 */
	@FXML
	private void ReviewAction(ActionEvent event) {
		connection = SqliteConnection.Connector();
		try {
			FoodAnalysis();
			TableProfit();
			connection.close();
		} catch (Exception e1) {

			Alert a = new Alert(AlertType.INFORMATION,
					"Not enough data to provide analysis. Come back when all tables have orders", ButtonType.OK);
			a.show();

		}
	}

	/**
	 * Food analysis. Creates a PieChart and links it to the database values
	 * with clickable fields for extra info.
	 */
	private void FoodAnalysis() {

		try {
			popularFood.addAll(popularFood());
		} catch (SQLException e1) {

			e1.printStackTrace();
		}

		ObservableList<Data> list = FXCollections.observableArrayList(

				new PieChart.Data(popularFood.get(0), Integer.parseInt(popularFood.get(1))),
				new PieChart.Data(popularFood.get(2), Integer.parseInt(popularFood.get(3))),
				new PieChart.Data(popularFood.get(4), Integer.parseInt(popularFood.get(5))),
				new PieChart.Data(popularFood.get(6), Integer.parseInt(popularFood.get(7))),
				new PieChart.Data(popularFood.get(8), Integer.parseInt(popularFood.get(9))));

		pchrt1.setData(list);

		for (final PieChart.Data data : pchrt1.getData()) {
			data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new javafx.event.EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					lblExtra.setText(String.valueOf(data.getPieValue()) + " times ordered");

				}
			});
		}

	}

	/**
	 * Table profit. Creates a PieChart and links it to the database values with
	 * clickable fields for extra info.
	 */
	private void TableProfit() {

		try {
			tableProfit.addAll(tableProfit());
		} catch (SQLException e1) {

			e1.printStackTrace();
		}

		ObservableList<Data> list2 = FXCollections.observableArrayList(

				new PieChart.Data(tableProfit.get(0), Double.parseDouble(tableProfit.get(1))),
				new PieChart.Data(tableProfit.get(2), Double.parseDouble(tableProfit.get(3))),
				new PieChart.Data(tableProfit.get(4), Double.parseDouble(tableProfit.get(5))),
				new PieChart.Data(tableProfit.get(6), Double.parseDouble(tableProfit.get(7))),
				new PieChart.Data(tableProfit.get(8), Double.parseDouble(tableProfit.get(9))),
				new PieChart.Data(tableProfit.get(10), Double.parseDouble(tableProfit.get(9)))

		);

		pchrt2.setData(list2);

		for (final PieChart.Data data1 : pchrt2.getData()) {
			data1.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new javafx.event.EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {

					lblExtra1.setText("£ " + String.valueOf(data1.getPieValue()));

				}
			});
		}
	}

	/**
	 * Popular food.
	 *
	 * @return the array list of the most popular food in the form [dish1,
	 *         count1, dish2, count2..].
	 * @throws SQLException
	 *             the SQL exception
	 */
	private ArrayList<String> popularFood() throws SQLException {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<String> popularFood = new ArrayList<String>();

		String query = "select dish, count(*) from orders group by dish order by count(*) desc Limit 5";

		preparedStatement = connection.prepareStatement(query);
		resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			popularFood.add(resultSet.getString(1));
			popularFood.add(resultSet.getString(2));

		}
		preparedStatement.close();
		resultSet.close();
		return popularFood;
	}

	/**
	 * Table profit.
	 *
	 * @return the array list of the profit per table in the form [table number,
	 *         sum of the bill,..].
	 * @throws SQLException
	 *             the SQL exception
	 */
	private ArrayList<String> tableProfit() throws SQLException {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<String> tableProfit = new ArrayList<String>();

		String query = "Select tablenum, sum (bill)  from orders group by tablenum";

		preparedStatement = connection.prepareStatement(query);
		resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			tableProfit.add(resultSet.getString(1));
			tableProfit.add(resultSet.getString(2));

		}
		preparedStatement.close();
		resultSet.close();
		return tableProfit;
	}

}
