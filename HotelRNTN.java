import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class HotelRNTN {
	private static final String DATABASE_NAME = "hotel_rntn";
	private static final String DATABASE_USERNAME = "root";
	private static final String DATABASE_PASSWORD = "toor";

	private static final int NEW_LINE_INTERVAL = 3;
	private static final int PADDING = 5;
	private static final int DIVIDER_LENGTH = 150;
	private static final String DIVIDER_STRING = new String(new char[DIVIDER_LENGTH]).replace("\0", "-");

	public static void main(String[] args) {
		Connection conn = null;
		Scanner scanner = new Scanner(System.in);

		try {
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?serverTimezone=America/Los_Angeles",
					DATABASE_USERNAME, DATABASE_PASSWORD);
			start(conn, scanner);
		} catch (SQLException e) {
			System.out.println("Connection to database failed!");
		} finally {
			scanner.close();
			closeQuietly(conn);
		}
	}

	public static void start(Connection conn, Scanner scanner) {
		String[] options = { "[1] Guest", "[2] Manager", "[3] Quit" };
		String choice = "";

		while (!choice.equals("3")) {
			printDivider();
			System.out.println("Hotel RNTN - Main Menu");
			printOptions(options);
			choice = scanner.nextLine();

			switch (choice) {
			case "1":
				Guest guest = new Guest(conn, scanner);
				guest.start();
				break;
			case "2":
				Manager manager = new Manager(conn, scanner);
				manager.start();
				break;
			case "3":
				System.out.println("Thank you for visiting Hotel RNTN!");
				break;
			default:
				System.out.println("Invalid choice!");
			}
		}
	}

	public static void printOptions(String[] options) {
		StringBuilder format = new StringBuilder();
		int longestLength = 0;

		// Get longest length option
		for (String option : options) {
			if (option.length() > longestLength) {
				longestLength = option.length();
			}
		}

		for (int i = 0; i < options.length; i++) {
			if (i != 0 && i % NEW_LINE_INTERVAL == 0) {
				format.append("%n");
			}

			format.append("%-" + (longestLength + PADDING) + "s");
		}
		format.append("%n");

		System.out.printf(format.toString(), (Object[]) options);
	}

	public static void printDivider() {
		System.out.println(DIVIDER_STRING);
	}

	public static void closeQuietly(AutoCloseable autoCloseable) {
		try {
			autoCloseable.close();
		} catch (Exception e) {
			// Nothing
		}
	}
}
