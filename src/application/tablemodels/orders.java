/*
 *<h1>Orders data model:</h1>
 * 
 * Setters and Getters for Total Orders and Search Table View extraction
 * 
 * 
 * @author Anton Morozov
 * @since  18/12/2016
 */
package application.tablemodels;

public class orders {
	private int ordernum;
	private String dish;
	private int quantity;
	private String comment;
	private int tablenum;
	private double price;
	private String time;

	public orders(int ordernum, String dish, int quantity, String comment, int tablenum, double price, String time) {
		this.ordernum = ordernum;
		this.dish = dish;
		this.quantity = quantity;
		this.comment = comment;
		this.tablenum = tablenum;
		this.price = price;
		this.time = time;
	}

	public orders() {

	}

	public int getOrdernum() {
		return ordernum;
	}

	public void setOrdernum(int ordernum) {
		this.ordernum = ordernum;
	}

	public String getDish() {
		return dish;
	}

	public void setDish(String dish) {
		this.dish = dish;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;

	}

	public int getTablenum() {
		return tablenum;
	}

	public void setTablenum(int tablenum) {
		this.tablenum = tablenum;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
