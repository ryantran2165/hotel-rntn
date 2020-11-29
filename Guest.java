import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Guest extends User {
	public Guest(Connection conn) {
		super(conn);
	}

	public void start() {
		String[] options = { "[1] Sign Up", "[2] Sign In", "[3] Back" };
		String choice = "";

		while (!choice.equals("3")) {
			HotelRNTN.printDivider();
			System.out.println("Hotel RNTN Guest Portal");
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
				System.out.println("Invalid choice, please try again!");
			}
		}
	}

	private void viewRoomsPrice() {
		System.out.println("Hotel RNTN Guest View Rooms by Price");

		System.out.print("Please enter minimum price: ");
		String minPrice = scanner.nextLine();
		BigDecimal minPriceDec;

		try {
			minPriceDec = new BigDecimal(minPrice);
		} catch (NumberFormatException e) {
			System.out.println("Invalid minimum price, please try again!");
			return;
		}

		System.out.print("Please enter maximum price: ");
		String maxPrice = scanner.nextLine();
		BigDecimal maxPriceDec;

		try {
			maxPriceDec = new BigDecimal(maxPrice);
		} catch (NumberFormatException e) {
			System.out.println("Invalid maximum price, please try again!");
			return;
		}

		String sql = "SELECT id, room_num, room_floor, sqft, price FROM room WHERE price >= ? AND price <= ?";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setBigDecimal(1, minPriceDec);
			pstmt.setBigDecimal(2, maxPriceDec);
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
		System.out.println("Hotel RNTN Guest View Rooms by Sqft");

		System.out.print("Please enter minimum sqft: ");
		String minSqft = scanner.nextLine();
		int minSqftInt;

		try {
			minSqftInt = Integer.parseInt(minSqft);
		} catch (NumberFormatException e) {
			System.out.println("Invalid minimum sqft, please try again!");
			return;
		}

		System.out.print("Please enter maximum sqft: ");
		String maxSqft = scanner.nextLine();
		int maxSqftInt;

		try {
			maxSqftInt = Integer.parseInt(maxSqft);
		} catch (NumberFormatException e) {
			System.out.println("Invalid maximum sqft, please try again!");
			return;
		}

		String sql = "SELECT id, room_num, room_floor, sqft, price FROM room WHERE sqft >= ? AND sqft <= ?";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, minSqftInt);
			pstmt.setInt(2, maxSqftInt);
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
		System.out.println("Hotel RNTN Guest View Rooms by Floor");

		System.out.print("Please enter minimum floor: ");
		String minFloor = scanner.nextLine();
		int minFloorInt;

		try {
			minFloorInt = Integer.parseInt(minFloor);
		} catch (NumberFormatException e) {
			System.out.println("Invalid minimum floor, please try again!");
			return;
		}

		System.out.print("Please enter maximum floor: ");
		String maxFloor = scanner.nextLine();
		int maxFloorInt;

		try {
			maxFloorInt = Integer.parseInt(maxFloor);
		} catch (NumberFormatException e) {
			System.out.println("Invalid maximum floor, please try again!");
			return;
		}

		String sql = "SELECT id, room_num, room_floor, sqft, price FROM room WHERE room_floor >= ? AND room_floor <= ?";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, minFloorInt);
			pstmt.setInt(2, maxFloorInt);
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
		System.out.println("Hotel RNTN View Reservations");

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
		System.out.println("Hotel RNTN Create Reservation");

		System.out.print("Please enter room id: ");
		String roomId = scanner.nextLine();
		int roomIdInt;

		try {
			roomIdInt = Integer.parseInt(roomId);
		} catch (NumberFormatException e) {
			System.out.println("Invalid room id, please try again!");
			return;
		}

		System.out.print("Please enter reservation date (mm-dd-yyyy): ");
		String date = scanner.nextLine();
		SimpleDateFormat f = new SimpleDateFormat("MM-dd-yyyy");
		Date reserveDate;

		try {
			long ms = f.parse(date).getTime();
			reserveDate = new Date(ms);
		} catch (ParseException e) {
			System.out.println("Invalid date, please try again!");
			return;
		}

		String sql = "INSERT INTO reservation (account_id, room_id, reserve_date) VALUES (?, ?, ?)";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.setInt(2, roomIdInt);
			pstmt.setDate(3, reserveDate);
			pstmt.executeUpdate();

			System.out.println("You have successfully created the reservation!");
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while creating the reservation!");
			}
		}
	}

	private void cancelReservation() {
		System.out.println("Hotel RNTN Cancel Reservation");

		System.out.print("Please enter room id: ");
		String roomId = scanner.nextLine();
		int roomIdInt;

		try {
			roomIdInt = Integer.parseInt(roomId);
		} catch (NumberFormatException e) {
			System.out.println("Invalid room id, please try again!");
			return;
		}

		System.out.print("Please enter reservation date (mm-dd-yyyy): ");
		String date = scanner.nextLine();
		SimpleDateFormat f = new SimpleDateFormat("MM-dd-yyyy");
		Date reserveDate;

		try {
			long ms = f.parse(date).getTime();
			reserveDate = new Date(ms);
		} catch (ParseException e) {
			System.out.println("Invalid date, please try again!");
			return;
		}

		String sql = "DELETE FROM reservation WHERE account_id = ? AND room_id = ? AND reserve_date = ?";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.setInt(2, roomIdInt);
			pstmt.setDate(3, reserveDate);
			pstmt.executeUpdate();

			System.out.println("You have successfully canceled the reservation!");
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while canceling the reservation!");
			}
		}
	}

	private void updateReservation() {
		System.out.println("Hotel RNTN Update Reservation");

		System.out.print("Please enter room id: ");
		String roomId = scanner.nextLine();
		int roomIdInt;

		try {
			roomIdInt = Integer.parseInt(roomId);
		} catch (NumberFormatException e) {
			System.out.println("Invalid room id, please try again!");
			return;
		}

		System.out.print("Please enter reservation date (mm-dd-yyyy): ");
		String date = scanner.nextLine();
		SimpleDateFormat f = new SimpleDateFormat("MM-dd-yyyy");
		Date reserveDate;

		try {
			long ms = f.parse(date).getTime();
			reserveDate = new Date(ms);
		} catch (ParseException e) {
			System.out.println("Invalid date, please try again!");
			return;
		}

		System.out.print("Please enter new room id: ");
		String newRoomId = scanner.nextLine();
		int newRoomIdInt;

		try {
			newRoomIdInt = Integer.parseInt(newRoomId);
		} catch (NumberFormatException e) {
			System.out.println("Invalid room id, please try again!");
			return;
		}

		System.out.print("Please enter reservation date (mm-dd-yyyy): ");
		String newDate = scanner.nextLine();
		Date newReserveDate;

		try {
			long ms = f.parse(newDate).getTime();
			newReserveDate = new Date(ms);
		} catch (ParseException e) {
			System.out.println("Invalid date, please try again!");
			return;
		}

		String sql = "UPDATE reservation SET room_id = ?, reserve_date = ? WHERE account_id = ? AND room_id = ? AND reserve_date = ?";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, newRoomIdInt);
			pstmt.setDate(2, newReserveDate);
			pstmt.setInt(3, id);
			pstmt.setInt(4, roomIdInt);
			pstmt.setDate(5, reserveDate);
			pstmt.executeUpdate();

			System.out.println("You have successfully updated the reservation!");
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while updating the reservation!");
			}
		}
	}

	private void viewReservationRequests() {
		System.out.println("Hotel RNTN View Reservation Requests");

		String sql = "SELECT room_id, reserve_date, request FROM reservation_request WHERE account_id = ?";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.executeQuery();

			ResultSet rs = pstmt.getResultSet();

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
		System.out.println("Hotel RNTN Create Reservation Request");

		System.out.print("Please enter room id: ");
		String roomId = scanner.nextLine();
		int roomIdInt;

		try {
			roomIdInt = Integer.parseInt(roomId);
		} catch (NumberFormatException e) {
			System.out.println("Invalid room id, please try again!");
			return;
		}

		System.out.print("Please enter reservation date (mm-dd-yyyy): ");
		String date = scanner.nextLine();
		SimpleDateFormat f = new SimpleDateFormat("MM-dd-yyyy");
		Date reserveDate;

		try {
			long ms = f.parse(date).getTime();
			reserveDate = new Date(ms);
		} catch (ParseException e) {
			System.out.println("Invalid date, please try again!");
			return;
		}

		System.out.print("Please enter reservation request: ");
		String request = scanner.nextLine();

		String sql = "INSERT INTO reservation_request (account_id, room_id, reserve_date, request) VALUES (?, ?, ?, ?)";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.setInt(2, roomIdInt);
			pstmt.setDate(3, reserveDate);
			pstmt.setString(4, request);
			pstmt.executeUpdate();

			System.out.println("You have successfully created the reservation request!");
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while creating the reservation request!");
			}
		}
	}

	private void cancelReservationRequest() {
		System.out.println("Hotel RNTN Cancel Reservation Request");

		System.out.print("Please enter room id: ");
		String roomId = scanner.nextLine();
		int roomIdInt;

		try {
			roomIdInt = Integer.parseInt(roomId);
		} catch (NumberFormatException e) {
			System.out.println("Invalid room id, please try again!");
			return;
		}

		System.out.print("Please enter reservation date (mm-dd-yyyy): ");
		String date = scanner.nextLine();
		SimpleDateFormat f = new SimpleDateFormat("MM-dd-yyyy");
		Date reserveDate;

		try {
			long ms = f.parse(date).getTime();
			reserveDate = new Date(ms);
		} catch (ParseException e) {
			System.out.println("Invalid date, please try again!");
			return;
		}

		System.out.print("Please enter reservation request: ");
		String request = scanner.nextLine();

		String sql = "DELETE FROM reservation_request WHERE account_id = ? AND room_id = ? AND reserve_date = ? AND request = ?";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.setInt(2, roomIdInt);
			pstmt.setDate(3, reserveDate);
			pstmt.setString(4, request);
			pstmt.executeUpdate();

			System.out.println("You have successfully canceled the reservation request!");
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while canceling the reservation request!");
			}
		}
	}

}
