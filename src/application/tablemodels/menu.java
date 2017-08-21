/*
 *<h1>Menu data model:</h1>
 * 
 * Setters and Getters for Food Menu Table View extraction
 * 
 * 
 * @author Anton Morozov
 * @since  18/12/2016
 */
package application.tablemodels;

public class menu {
	private int id;
	private String group;
	private String dish;
	private double kcal;
	private double price;

	public menu(int id, String group, String dish, double kcal, double price) {
		this.id = id;
		this.group = group;
		this.dish = dish;
		this.kcal = kcal;
		this.price = price;
	}

	public menu() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getDish() {
		return dish;
	}

	public void setDish(String dish) {
		this.dish = dish;
	}

	public double getKcal() {
		return kcal;
	}

	public void setKcal(double kcal) {
		this.kcal = kcal;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}
