import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class HotelRNTN {
	private static final String DATABASE_NAME = "hotel_rntn";
	private static final String DATABASE_USERNAME = "root";
	private static final String DATABASE_PASSWORD = "toor";

	public static void main(String[] args) {
		try {
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?serverTimezone=UTC", DATABASE_USERNAME,
					DATABASE_PASSWORD);

			try (Scanner scanner = new Scanner(System.in)) {
				System.out.println("Welcome to Hotel RNTN!");

				String choice = "";
				String[] options = { "1", "2", "3" };

				while (true) {
					System.out.printf("Please choose an option:%5s%s%5s%s%5s%s%n", "",
							createOptionStr(options[0], "User"), "", createOptionStr(options[1], "Admin"), "",
							createOptionStr(options[2], "Quit"));
					choice = scanner.nextLine();

					if (choice.equals(options[0])) {
						new Guest(conn);
					} else if (choice.equals(options[1])) {
						new Manager(conn);
					} else if (choice.equals(options[2])) {
						System.out.println("Thank you for visiting Hotel RNTN!");
						System.exit(0);
					} else {
						System.out.println("Invalid choice, please try again!");
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("Connection to database failed!");
		}
	}

	public static String createOptionStr(String option, String str) {
		return "[" + option + "] " + str;
	}
}
