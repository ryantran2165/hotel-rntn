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
		String[] options = { "[1] Sign In", "[2] Back" };
		String choice = "";

		while (!choice.equals("2")) {
			System.out.println("Hotel RNTN Manager Portal");
			HotelRNTN.printOptions(options);
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

		String sql = "SELECT id, first_name, last_name FROM account where email = ? AND password = ? AND is_admin = TRUE";
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
		String[] options = { "[1] View All Rooms", "[2] View Reserved Rooms", "[3] Create Room", "[4] Delete Room",
				"[5] Number of Reservations by Date", "[6] Number of Reservations by Room", "[7] Popular Months",
				"[8] Recurring Guests", "[9] High-Activity Months", "[10] Unpopular Rooms", "[11] Sign Out" };
		String choice = "";

		while (!choice.equals("11")) {
			System.out.printf("Signed in as manager %s %s%n", firstName, lastName);
			HotelRNTN.printOptions(options);
			choice = scanner.nextLine();

			switch (choice) {
			case "1":
				viewRoomsAll();
				break;
			case "2":
				viewReservedRooms();
				break;
			case "3":
				createRoom();
				break;
			case "4":
				deleteRoom();
				break;
			case "5":
				numberReservationsDate();
				break;
			case "6":
				numberReservationsRoom();
				break;
			case "7":
				popularMonths();
				break;
			case "8":
				recurringGuests();
				break;
			case "9":
				highActivityMonths();
				break;
			case "10":
				unpopularRooms();
				break;
			case "11":
				System.out.println("Signed out!");
				break;
			default:
				System.out.println("Invalid choice, please try again!");
			}
		}
	}

	private void viewReservedRooms() {
		String sql = "SELECT id, room_num, room_floor, sqft, price FROM room WHERE id IN (SELECT room_id FROM reservation)";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.executeQuery();

			ResultSet rs = pstmt.getResultSet();

			if (!rs.isBeforeFirst()) {
				System.out.println("There are no reserved rooms!");
			} else {
				printRooms(rs);
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing reserved rooms!");
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
		System.out.println("Hotel RNTN Manager Delete Room");

		System.out.print("Please enter room id: ");
		String id = scanner.nextLine();
		int idInt;

		try {
			idInt = Integer.parseInt(id);
		} catch (NumberFormatException e) {
			System.out.println("Invalid id, please try again!");
			return;
		}

		String sql = "DELETE FROM room WHERE id = ?";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, idInt);
			pstmt.executeUpdate();

			System.out.println("You have successfully deleted the room!");
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				e.printStackTrace();
				System.out.println("An error has occurred while deleting the room!");
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
