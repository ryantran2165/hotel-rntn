import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class HotelRNTN {
	private static final String DATABASE_NAME = "hotel_rntn";
	private static final String DATABASE_USERNAME = "root";
	private static final String DATABASE_PASSWORD = "toor";
	public static final Scanner SCANNER = new Scanner(System.in);

	public static void main(String[] args) {
		try {
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?serverTimezone=UTC", DATABASE_USERNAME,
					DATABASE_PASSWORD);
			start(conn);
		} catch (SQLException e) {
			System.out.println("Connection to database failed!");
		}
	}

	public static void start(Connection conn) {
		String choice = "";

		while (!choice.equals("3")) {
			System.out.println("Hotel RNTN Main Menu");
			System.out.printf("Please choose an option:%5s%s%5s%s%5s%s%n", "", "[1] Guest", "", "[2] Manager", "",
					"[3] Quit");
			choice = SCANNER.nextLine();

			switch (choice) {
			case "1":
				Guest guest = new Guest(conn);
				guest.start();
				break;
			case "2":
				Manager manager = new Manager(conn);
				manager.start();
				break;
			case "3":
				System.out.println("Thank you for visiting Hotel RNTN!");
				SCANNER.close();
				System.exit(0);
			default:
				System.out.println("Invalid choice, please try again!");
			}
		}
	}
}
