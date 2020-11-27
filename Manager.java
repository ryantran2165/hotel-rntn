import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Manager {
	private Connection conn;
	private Scanner scanner;
	private int id;

	public Manager(Connection conn) {
		this.conn = conn;
		this.scanner = HotelRNTN.SCANNER;
	}

	public void start() {
		String choice = "";

		while (!choice.equals("2")) {
			System.out.println("Hotel RNTN Manager Portal");
			System.out.printf("Please choose an option:%5s%s%5s%s%n", "", "[1] Sign In", "", "[2] Back");
			choice = scanner.nextLine();

			switch (choice) {
			case "1":
				signIn();
				break;
			case "2":
				System.out.println("Thank you for using the Manager Portal!");
				break;
			default:
				System.out.println("Invalid choice, please try again!");
			}
		}
	}

	private void signIn() {
		System.out.println("Hotel RNTN Manager Sign In");

		System.out.print("Please enter email: ");
		String email = scanner.nextLine();

		System.out.print("Please enter password: ");
		String password = scanner.nextLine();

		String sql = "SELECT id, first_name, last_name FROM ACCOUNT where email = ? AND password = ? AND is_admin = TRUE";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, email);
			pstmt.setString(2, password);
			pstmt.executeQuery();

			ResultSet rs = pstmt.getResultSet();
			if (rs.next()) {
				id = rs.getInt("id");
				System.out.printf("You have successfully signed in as %s %s!%n", rs.getString("first_name"),
						rs.getString("last_name"));
			} else {
				System.out.println("Invalid credentials, please try again!");
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while signing in!");
			}
		}
	}
}
