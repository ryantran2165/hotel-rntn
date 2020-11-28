import java.math.BigDecimal;
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

		while (!choice.equals("9")) {
			System.out.printf("Signed in as manager %s %s%n", firstName, lastName);
			System.out.printf("Please choose an option:%5s%s%5s%s%5s%s%5s%s%5s%s%5s%s%5s%s%5s%s%5s%s%n", "",
					"[1] Create Room", "", "[2] Delete Room", "", "[3] Number of Reservations by Date", "",
					"[4] Number of Reservations by Room", "", "[5] Popular Months", "", "[6] Recurring Guests", "",
					"[7] High-Activity Months", "", "[8] Unpopular Rooms", "", "[9] Sign Out");
			choice = scanner.nextLine();

			switch (choice) {
			case "1":
				createRoom();
				break;
			case "2":
				deleteRoom();
				break;
			case "3":
				numberReservationsDate();
				break;
			case "4":
				numberReservationsRoom();
				break;
			case "5":
				popularMonths();
				break;
			case "6":
				recurringGuests();
				break;
			case "7":
				highActivityMonths();
				break;
			case "8":
				unpopularRooms();
				break;
			case "9":
				System.out.println("Signed out!");
				break;
			default:
				System.out.println("Invalid choice, please try again!");
			}
		}
	}

	private void createRoom() {
		System.out.println("Hotel RNTN Manager Create Room");

		System.out.print("Please enter room number: ");
		String roomNumber = scanner.nextLine();

		System.out.print("Please enter room floor: ");
		String roomFloor = scanner.nextLine();
		int roomFloorInt;
		try {
			roomFloorInt = Integer.parseInt(roomFloor);
		} catch (NumberFormatException e) {
			System.out.println("Invalid room floor, please try again!");
			return;
		}

		System.out.print("Please enter room sqft: ");
		String sqft = scanner.nextLine();
		int sqftInt;
		try {
			sqftInt = Integer.parseInt(sqft);
		} catch (NumberFormatException e) {
			System.out.println("Invalid sqft, please try again!");
			return;
		}

		System.out.print("Please enter room price: ");
		String price = scanner.nextLine();
		BigDecimal priceDec;
		try {
			priceDec = new BigDecimal(price);
		} catch (NumberFormatException e) {
			System.out.println("Invalid price, please try again!");
			return;
		}

		String sql = "INSERT INTO room (room_num, room_floor, sqft, price) VALUES (?, ?, ?, ?)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, roomNumber);
			pstmt.setInt(2, roomFloorInt);
			pstmt.setInt(3, sqftInt);
			pstmt.setBigDecimal(4, priceDec);
			pstmt.executeUpdate();

			System.out.println("You have successfully created the room!");
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				e.printStackTrace();
				System.out.println("An error has occurred while creating the room!");
			}
		}
	}

	private void deleteRoom() {

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
