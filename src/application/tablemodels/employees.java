/*
 *<h1>Employees data model:</h1>
 * 
 * Setters and Getters for Manage Employee Table View extraction
 * 
 * 
 * @author Anton Morozov
 * @since  18/12/2016
 */
package application.tablemodels;

public class employees {
	private String name;
	private String surname;
	private String manager;
	private String username;
	private String password;

	public employees(String name, String surname, String manager, String username, String password) {

		this.name = name;
		this.surname = surname;
		this.manager = manager;
		this.username = username;
		this.password = password;
	}

	public employees() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
