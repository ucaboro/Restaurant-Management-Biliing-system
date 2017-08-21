/*
 *<h1>SQLITE Connection class</h1>
 *
 *.
 * Class to ensure the sqlite driver is in place
 * 
 * 
 * @author Anton Morozov
 * @since  18/12/2016
 */
package application;

import java.sql.*;
import java.util.logging.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class SqliteConnection.
 */
public class SqliteConnection {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	/**
	 * Connector.
	 *
	 * @return the connection
	 */
	
	public static Connection Connector() {

		try {
			Class.forName("org.sqlite.JDBC");
			//create db if not exist (but blank)
			Connection conn = DriverManager.getConnection("jdbc:sqlite:/usernameDB.sqlite");
			LOGGER.info("db connected");
			return conn;
		} catch (Exception ex) {
			LOGGER.severe("db couldn't connect");
			ex.printStackTrace();
			return null;
		}

	}

}
