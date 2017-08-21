/*
 *<h1>Login Model class:</h1>
 * 
 * The model that is utilised for the Login Functionality
 * 
 * 
 * @author Anton Morozov
 * @since  18/12/2016
 */
package application;

import java.sql.*;

/**
 * The Class LoginModel.
 */
public class LoginModel {

	/**
	 * The connection. creates the connection and the main SQL query for the
	 * username selection
	 * 
	 */
	Connection connection;

	/**
	 * Instantiates a new login model.
	 */
	public LoginModel() {
		connection = SqliteConnection.Connector();
		if (connection == null) {
			System.exit(1);
		}

	}

	/**
	 * Checks if the db connected.
	 *
	 * @return true, if it is connected
	 */
	public boolean isDbConnected() {
		try {
			return !connection.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Checks if the login details meets the database fileds. check if username
	 * and password are recognised as per database.
	 * 
	 * @param user
	 *            the user
	 * @param pass
	 *            the password
	 * @return true, if login credentials are correct
	 * @throws SQLException
	 *             the SQL exception
	 */

	public boolean isLogin(String user, String pass) throws SQLException {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String query = "select * from username where user =? and pass =?";
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, pass);

			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return true;
			} else {
				return false;

			}

		} catch (SQLException ex) {
			return false;

		} finally {
			preparedStatement.close();
			resultSet.close();

		}
	}

	/**
	 * Checks if the user is a manager.
	 *
	 * @param user
	 *            the user
	 * @return true, if he/she is a manager
	 * @throws SQLException
	 *             the SQL exception
	 */

	public boolean isManager(String user) throws SQLException {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String query = "select * from username where user = ? and manager = ?";
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, "true");

			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return true;

			} else {
				return false;

			}

		} catch (SQLException ex) {
			return false;

		} finally {
			preparedStatement.close();
			resultSet.close();
		}
	}
	

}
