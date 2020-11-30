import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Guest extends User {
	public Guest(Connection conn) {
		super(conn);
	}

	public void start() {
		String[] options = { "[1] Sign Up", "[2] Sign In", "[3] Back" };
		String choice = "";

		while (!choice.equals("3")) {
			HotelRNTN.printDivider();
			System.out.println("Hotel RNTN - Guest Portal");
			HotelRNTN.printOptions(options);
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
				System.out.println("Invalid choice!");
			}
		}
	}

	private void signUp() {
		System.out.println("Hotel RNTN Guest - Sign Up");

		String firstName = promptInput("first name", String.class);
		String lastName = promptInput("last name", String.class);
		String email = promptInput("email", String.class);
		String password = promptInput("password", String.class);

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
				System.out.println("That email is already used!");
				break;
			case 3819:
				System.out.println("Fields cannot be empty!");
				break;
			default:
				System.out.println("An error has occurred while signing up!");
			}
		}
	}

	private void signIn() {
		System.out.println("Hotel RNTN Guest - Sign In");

		String email = promptInput("email", String.class);
		String password = promptInput("password", String.class);

		String sql = "SELECT id, first_name, last_name FROM account where email = ? AND password = ? AND is_admin = FALSE";

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
				System.out.println("Invalid credentials!");
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while signing in!");
			}
		}
	}

	private void startSignedIn() {
		String[] options = { "[1] View All Rooms", "[2] View Rooms by Price", "[3] View Rooms by Sqft",
				"[4] View Rooms by Floor", "[5] View Reservations", "[6] Create Reservation", "[7] Cancel Reservation",
				"[8] Update Reservation", "[9] View Reservation Requests", "[10] Create Reservation Request",
				"[11] Cancel Reservation Request", "[12] Sign Out" };
		String choice = "";

		while (!choice.equals("12")) {
			HotelRNTN.printDivider();
			System.out.printf("Signed in as guest %s %s%n", firstName, lastName);
			HotelRNTN.printOptions(options);
			choice = scanner.nextLine();

			switch (choice) {
			case "1":
				viewRoomsAll();
				break;
			case "2":
				viewRoomsPrice();
				break;
			case "3":
				viewRoomsSqft();
				break;
			case "4":
				viewRoomsFloor();
				break;
			case "5":
				viewReservations();
				break;
			case "6":
				createReservation();
				break;
			case "7":
				cancelReservation();
				break;
			case "8":
				updateReservation();
				break;
			case "9":
				viewReservationRequests();
				break;
			case "10":
				createReservationRequest();
				break;
			case "11":
				cancelReservationRequest();
				break;
			case "12":
				System.out.println("Signed out!");
				break;
			default:
				System.out.println("Invalid choice!");
			}
		}
	}

	private void viewRoomsPrice() {
		System.out.println("Hotel RNTN Guest - View Rooms by Price");

		BigDecimal minPrice = promptInput("minimum price", BigDecimal.class);
		if (minPrice == null) {
			return;
		}

		BigDecimal maxPrice = promptInput("maximum price", BigDecimal.class);
		if (maxPrice == null) {
			return;
		}

		String sql = "SELECT id, room_num, room_floor, sqft, price FROM room WHERE price >= ? AND price <= ?";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setBigDecimal(1, minPrice);
			pstmt.setBigDecimal(2, maxPrice);
			pstmt.executeQuery();

			ResultSet rs = pstmt.getResultSet();

			if (!rs.isBeforeFirst()) {
				System.out.println("There are no rooms for that price range!");
			} else {
				printRooms(rs);
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing rooms by price!");
			}
		}
	}

	private void viewRoomsSqft() {
		System.out.println("Hotel RNTN Guest - View Rooms by Sqft");

		Integer minSqft = promptInput("minimum sqft", Integer.class);
		if (minSqft == null) {
			return;
		}

		Integer maxSqft = promptInput("maximum sqft", Integer.class);
		if (maxSqft == null) {
			return;
		}

		String sql = "SELECT id, room_num, room_floor, sqft, price FROM room WHERE sqft >= ? AND sqft <= ?";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, minSqft);
			pstmt.setInt(2, maxSqft);
			pstmt.executeQuery();

			ResultSet rs = pstmt.getResultSet();

			if (!rs.isBeforeFirst()) {
				System.out.println("There are no rooms for that sqft range!");
			} else {
				printRooms(rs);
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing rooms by sqft!");
			}
		}
	}

	private void viewRoomsFloor() {
		System.out.println("Hotel RNTN Guest - View Rooms by Floor");

		Integer minFloor = promptInput("minimum floor", Integer.class);
		if (minFloor == null) {
			return;
		}

		Integer maxFloor = promptInput("max floor", Integer.class);
		if (maxFloor == null) {
			return;
		}

		String sql = "SELECT id, room_num, room_floor, sqft, price FROM room WHERE room_floor >= ? AND room_floor <= ?";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, minFloor);
			pstmt.setInt(2, maxFloor);
			pstmt.executeQuery();

			ResultSet rs = pstmt.getResultSet();

			if (!rs.isBeforeFirst()) {
				System.out.println("There are no rooms for that floor range!");
			} else {
				printRooms(rs);
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing rooms by floor!");
			}
		}
	}

	private void viewReservations() {
		System.out.println("Hotel RNTN Guest - View Reservations");

		String sql = "SELECT room_id, reserve_date FROM reservation WHERE account_id = ?";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.executeQuery();

			ResultSet rs = pstmt.getResultSet();

			if (!rs.isBeforeFirst()) {
				System.out.println("You have no reservations!");
			} else {
				printReservations(rs);
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing reservations!");
			}
		}
	}

	private void createReservation() {
		System.out.println("Hotel RNTN Guest - Create Reservation");

		Integer roomId = promptInput("room id", Integer.class);
		if (roomId == null) {
			return;
		}

		Date reserveDate = promptInput("reserve date (mm-dd-yyyy)", Date.class);
		if (reserveDate == null) {
			return;
		}

		String sql = "INSERT INTO reservation (account_id, room_id, reserve_date) VALUES (?, ?, ?)";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.setInt(2, roomId);
			pstmt.setDate(3, reserveDate);
			pstmt.executeUpdate();

			System.out.println("You have successfully created the reservation!");
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			case 1062:
				System.out.println("This reservation already exists!");
				break;
			case 1452:
				System.out.println("That room does not exist!");
				break;
			case 1644:
				System.out.println("That date already passed!");
				break;
			default:
				System.out.println("An error has occurred while creating the reservation!");
			}
		}
	}

	private void cancelReservation() {
		System.out.println("Hotel RNTN Guest - Cancel Reservation");

		Integer roomId = promptInput("room id", Integer.class);
		if (roomId == null) {
			return;
		}

		Date reserveDate = promptInput("reserve date (mm-dd-yyyy)", Date.class);
		if (reserveDate == null) {
			return;
		}

		String sql = "DELETE FROM reservation WHERE account_id = ? AND room_id = ? AND reserve_date = ?";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.setInt(2, roomId);
			pstmt.setDate(3, reserveDate);
			int deleted = pstmt.executeUpdate();

			if (deleted > 0) {
				System.out.println("You have successfully canceled the reservation!");
			} else {
				System.out.println("That reservation does not exist!");
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while canceling the reservation!");
			}
		}
	}

	private void updateReservation() {
		System.out.println("Hotel RNTN Guest - Update Reservation");

		Integer roomId = promptInput("room id", Integer.class);
		if (roomId == null) {
			return;
		}

		Date reserveDate = promptInput("reserve date (mm-dd-yyyy)", Date.class);
		if (reserveDate == null) {
			return;
		}

		Integer newRoomId = promptInput("new room id", Integer.class);
		if (newRoomId == null) {
			return;
		}

		Date newReserveDate = promptInput("new reserve date (mm-dd-yyyy)", Date.class);
		if (newReserveDate == null) {
			return;
		}

		String sql = "UPDATE reservation SET room_id = ?, reserve_date = ? WHERE account_id = ? AND room_id = ? AND reserve_date = ?";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, newRoomId);
			pstmt.setDate(2, newReserveDate);
			pstmt.setInt(3, id);
			pstmt.setInt(4, roomId);
			pstmt.setDate(5, reserveDate);
			pstmt.executeUpdate();

			System.out.println("You have successfully updated the reservation!");
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			case 1062:
				System.out.println("This reservation already exists!");
				break;
			case 1452:
				System.out.println("That room does not exist!");
				break;
			case 1644:
				System.out.println("That date already passed!");
				break;
			default:
				System.out.println("An error has occurred while updating the reservation!");
			}
		}
	}

	private void viewReservationRequests() {
		System.out.println("Hotel RNTN Guest - View Reservation Requests");

		String sql = "SELECT room_id, reserve_date, request FROM reservation_request NATURAL JOIN reservation WHERE account_id = ?";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();

			if (!rs.isBeforeFirst()) {
				System.out.println("You have no reservation requests!");
			} else {
				printReservationRequests(rs);
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing reservation requests!");
			}
		}
	}

	private void createReservationRequest() {
		System.out.println("Hotel RNTN Guest - Create Reservation Request");

		Integer roomId = promptInput("room id", Integer.class);
		if (roomId == null) {
			return;
		}

		Date reserveDate = promptInput("reserve date (mm-dd-yyyy)", Date.class);
		if (reserveDate == null) {
			return;
		}

		String request = promptInput("reservation request", String.class);

		String sql1 = "SELECT * FROM reservation WHERE account_id = ? AND room_id = ? AND reserve_date = ?";

		// Only create reservation request if requested by same guest as reservation
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql1);
			pstmt.setInt(1, id);
			pstmt.setInt(2, roomId);
			pstmt.setDate(3, reserveDate);
			ResultSet rs = pstmt.executeQuery();

			if (!rs.isBeforeFirst()) {
				System.out.println("That reservation does not exist!");
			} else {
				String sql2 = "INSERT INTO reservation_request (room_id, reserve_date, request) VALUES (?, ?, ?)";

				try {
					pstmt = conn.prepareStatement(sql2);
					pstmt.setInt(1, roomId);
					pstmt.setDate(2, reserveDate);
					pstmt.setString(3, request);
					pstmt.executeUpdate();

					System.out.println("You have successfully created the reservation request!");
				} catch (SQLException e) {
					switch (e.getErrorCode()) {
					case 1062:
						System.out.println("This reservation request already exists!");
						break;
					default:
						System.out.println("An error has occurred while creating the reservation request!");
					}
				}
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while creating the reservation request!");
			}
		}
	}

	private void cancelReservationRequest() {
		System.out.println("Hotel RNTN Guest - Cancel Reservation Request");

		Integer roomId = promptInput("room id", Integer.class);
		if (roomId == null) {
			return;
		}

		Date reserveDate = promptInput("reserve date (mm-dd-yyyy)", Date.class);
		if (reserveDate == null) {
			return;
		}

		String request = promptInput("reservation request", String.class);

		String sql1 = "SELECT * FROM reservation WHERE account_id = ? AND room_id = ? AND reserve_date = ?";

		// Only delete reservation request if requested by same guest as reservation
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql1);
			pstmt.setInt(1, id);
			pstmt.setInt(2, roomId);
			pstmt.setDate(3, reserveDate);
			ResultSet rs = pstmt.executeQuery();

			if (!rs.isBeforeFirst()) {
				System.out.println("That reservation does not exist!");
			} else {
				String sql2 = "DELETE FROM reservation_request WHERE room_id = ? AND reserve_date = ? AND request = ?";

				try {
					pstmt = conn.prepareStatement(sql2);
					pstmt.setInt(1, roomId);
					pstmt.setDate(2, reserveDate);
					pstmt.setString(3, request);
					int deleted = pstmt.executeUpdate();

					if (deleted > 0) {
						System.out.println("You have successfully canceled the reservation request!");
					} else {
						System.out.println("That reservation request does not exist!");
					}
				} catch (SQLException e) {
					switch (e.getErrorCode()) {
					default:
						System.out.println("An error has occurred while canceling the reservation request!");
					}
				}
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while canceling the reservation request!");
			}
		}
	}
}
