import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class HotelRNTN {
	private static final String DATABASE_NAME = "hotel_rntn";
	private static final String DATABASE_USERNAME = "root";
	private static final String DATABASE_PASSWORD = "ss3goku90";

	public static void main(String[] args) {
		try {
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?serverTimezone=UTC", DATABASE_USERNAME,
					DATABASE_PASSWORD);

			try (Scanner scanner = new Scanner(System.in)) {
				System.out.println("Welcome to Hotel RNTN!");

				String choice = "";
				String[] options = { "1", "2", "3" };
				String status = "main";

				while (status != "exit") {
					//Main Menu
					if(status == "main") {
						System.out.printf("Please choose an option:%5s%s%5s%s%5s%s%n", "",
								createOptionStr(options[0], "User"), "", createOptionStr(options[1], "Admin"), "",
								createOptionStr(options[2], "Quit"));
						choice = scanner.nextLine();

						if (choice.equals(options[0])) {
							status = "guest";
						} else if (choice.equals(options[1])) {
							status = "admin";

						} else if (choice.equals(options[2])) {
							System.out.println("Thank you for visiting Hotel RNTN!");
							System.exit(0);
						} else {
							System.out.println("Invalid choice, please try again!");

						}
					}

					//User / Guest menu
					else if(status == "guest") {
						Guest g = new Guest(conn);
						System.out.println("Guest Portal");
						System.out.printf("User Options:%5s%s%5s%s%5s%s%n", "",
								createOptionStr(options[0], "Sign Up"), "", createOptionStr(options[1], "Login"), "",
								createOptionStr(options[2], "Back"));
						choice = scanner.nextLine();

						//Sign up
						if(choice.contentEquals(options[0])) {
							System.out.println(g.signUp());
						}

						//Login
						else if(choice.contentEquals(options[1])) {
							//Success = true if all the credentials match
							boolean success = g.login();

							if(success) {
								System.out.println("You have logged in as a Guest!\n");
							}
							else {
								System.out.println("Incorrect email or password\n");
							}
						}
						else if(choice.contentEquals(options[2])) {
							status = "main";
						}

					}

					//Manager Menu
					else if(status == "admin") {
						Manager m = new Manager(conn);
						Guest g = new Guest(conn);
						System.out.println("Manager Portal");
						System.out.printf("User Options:%5s%s%5s%s%n", "",
								createOptionStr(options[0], "Login"), "",
								createOptionStr(options[1], "Back"));
						choice = scanner.nextLine();
						if(choice.contentEquals(options[0])) {
							boolean success = m.login();
							if(success) {
								System.out.println("You have logged in as a Manager!\n");
							}
							else {
								System.out.println("Incorrect email or password\n");
							}
						}
						else if(choice.contentEquals(options[1])) {
							status = "main";					
						}
					}
				}
			}
		} catch (SQLException e) {

			System.out.println(e);
			System.out.println("Connection to database failed!");
		}
	}

	public static String createOptionStr(String option, String str) {
		return "[" + option + "] " + str;
	}
}
