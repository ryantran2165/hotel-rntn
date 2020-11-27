import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Manager extends User {
	public Manager(Connection conn) {
		super(conn);
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
				firstName = rs.getString("first_name");
				lastName = rs.getString("last_name");
				startSignedIn();
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

	private void startSignedIn() {
		String choice = "";

		while (!choice.equals("7")) {
			System.out.printf("Signed in as manager %s %s%n", firstName, lastName);
			System.out.printf("Please choose an option:%5s%s%5s%s%5s%s%5s%s%5s%s%5s%s%5s%s%n", "",
					"[1] Number of Reservations by Date", "", "[2] Number of Reservations by Room", "",
					"[3] Popular Months", "", "[4] Recurring Guests", "", "[5] High-Activity Months", "",
					"[6] Unpopular Rooms", "", "[7] Sign Out");
			choice = scanner.nextLine();

			switch (choice) {
			case "1":
				numberReservationsDate();
				break;
			case "2":
				numberReservationsRoom();
				break;
			case "3":
				popularMonths();
				break;
			case "4":
				recurringGuests();
				break;
			case "5":
				highActivityMonths();
				break;
			case "6":
				unpopularRooms();
				break;
			case "7":
				System.out.println("Signed out!");
				break;
			default:
				System.out.println("Invalid choice, please try again!");
			}
		}
	}

	private void numberReservationsDate() {

	}

	private void numberReservationsRoom() {

	}

	private void popularMonths() {

	}

	private void recurringGuests() {

	}

	private void highActivityMonths() {

	}

	private void unpopularRooms() {

	}
}
