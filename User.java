import java.sql.Connection;
import java.util.Scanner;

public abstract class User {
	protected Connection conn;
	protected Scanner scanner;
	protected int id;
	protected String firstName;
	protected String lastName;

	public User(Connection conn) {
		this.conn = conn;
		scanner = HotelRNTN.SCANNER;
	}
}
