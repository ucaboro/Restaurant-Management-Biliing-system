/*
 *<h1>Total order data model:</h1>
 * 
 * Setters and Getters for Create an Order Table View extraction
 * 
 * 
 * @author Anton Morozov
 * @since  18/12/2016
 */
package application.tablemodels;

public class totalorder {
	private int id;
	private String dish;
	private int qt;
	private String comment;

	public totalorder(int id, String dish, int qt, String comment) {
		this.id = id;
		this.dish = dish;
		this.qt = qt;
		this.comment = comment;
	}

	public totalorder() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDish() {
		return dish;
	}

	public void setDish(String dish) {
		this.dish = dish;
	}

	public double getQt() {
		return qt;
	}

	public void setQt(int qt) {
		this.qt = qt;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;

	}
}