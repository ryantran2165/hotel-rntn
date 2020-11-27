import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Guest extends User {
	public Guest(Connection conn) {
		super(conn);
	}

	public void start() {
		String choice = "";

		while (!choice.equals("3")) {
			System.out.println("Hotel RNTN Guest Portal");
			System.out.printf("Please choose an option:%5s%s%5s%s%5s%s%n", "", "[1] Sign Up", "", "[2] Sign In", "",
					"[3] Back");
			choice = scanner.nextLine();

			switch (choice) {
			case "1":
				signUp();
				break;
			case "2":
				signIn();
				break;
			case "3":
				System.out.println("Thank you for using the Guest Portal!");
				break;
			default:
				System.out.println("Invalid choice, please try again!");
			}
		}
	}

	private void signUp() {
		System.out.println("Hotel RNTN Guest Sign Up");

		System.out.print("Please enter first name: ");
		String firstName = scanner.nextLine();

		System.out.print("Please enter last name: ");
		String lastName = scanner.nextLine();

		System.out.print("Please enter email: ");
		String email = scanner.nextLine();

		System.out.print("Please enter password: ");
		String password = scanner.nextLine();

		String sql = "INSERT INTO account (email, password, first_name, last_name, is_admin) VALUES(?, ?, ?, ?, ?)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, email);
			pstmt.setString(2, password);
			pstmt.setString(3, firstName);
			pstmt.setString(4, lastName);
			pstmt.setBoolean(5, false);
			pstmt.executeUpdate();

			System.out.println("You have successfully signed up!");
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			case 1062:
				System.out.println("That email is already used, please use another one!");
				break;
			default:
				System.out.println("An error has occurred while signing up!");
			}
		}
	}

	private void signIn() {
		System.out.println("Hotel RNTN Guest Sign In");

		System.out.print("Please enter email: ");
		String email = scanner.nextLine();

		System.out.print("Please enter password: ");
		String password = scanner.nextLine();

		String sql = "SELECT id, first_name, last_name FROM ACCOUNT where email = ? AND password = ? AND is_admin = FALSE";
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
			System.out.printf("Signed in as guest %s %s%n", firstName, lastName);
			System.out.printf("Please choose an option:%5s%s%5s%s%5s%s%5s%s%5s%s%5s%s%5s%s%n", "",
					"[1] Create Reservation", "", "[2] Cancel Reservation", "", "[3] Update Reservation", "",
					"[4] View Rooms (Price)", "", "[5] View Rooms (Sqft)", "", "[6] View Rooms (Floor)", "",
					"[7] Sign Out");
			choice = scanner.nextLine();

			switch (choice) {
			case "1":
				createReservation();
				break;
			case "2":
				cancelReservation();
				break;
			case "3":
				updateReservation();
				break;
			case "4":
				viewRoomsPrice();
				break;
			case "5":
				viewRoomsSqft();
				break;
			case "6":
				viewRoomsFloor();
				break;
			case "7":
				System.out.println("Signed out!");
				break;
			default:
				System.out.println("Invalid choice, please try again!");
			}
		}
	}

	private void createReservation() {

	}

	private void cancelReservation() {

	}

	private void updateReservation() {

	}

	private void viewRoomsPrice() {

	}

	private void viewRoomsSqft() {

	}

	private void viewRoomsFloor() {

	}
}
