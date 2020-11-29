import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class HotelRNTN {
	private static final String DATABASE_NAME = "hotel_rntn";
	private static final String DATABASE_USERNAME = "root";
	private static final String DATABASE_PASSWORD = "toor";
	public static final Scanner SCANNER = new Scanner(System.in);
	public static final int NEW_LINE_INTERVAL = 5;
	public static final int SPACE_SIZE = 5;
	public static final int DIVIDER_LENGTH = 100;
	public static final String DIVIDER_STRING = new String(new char[DIVIDER_LENGTH]).replace("\0", "-");

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
		String[] options = { "[1] Guest", "[2] Manager", "[3] Quit" };
		String choice = "";

		while (!choice.equals("3")) {
			printDivider();
			System.out.println("Hotel RNTN Main Menu");
			printOptions(options);
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

	public static void printOptions(String[] options) {
		StringBuilder format = new StringBuilder();
		Object[] values = new Object[2 * options.length - (int) Math.ceil(options.length / NEW_LINE_INTERVAL)];
		int valueIndex = 0;

		for (int i = 0; i < options.length; i++) {
			if (i != 0 && i % NEW_LINE_INTERVAL == 0) {
				format.append("%n");
			}

			if (i % NEW_LINE_INTERVAL != 0) {
				format.append("%" + SPACE_SIZE + "s");
				values[valueIndex] = "";
				valueIndex++;
			}

			format.append("%s");
			values[valueIndex] = options[i];
			valueIndex++;
		}
		format.append("%n");

		System.out.printf(format.toString(), values);
	}

	public static void printDivider() {
		System.out.println(DIVIDER_STRING);
	}
}
