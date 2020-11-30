import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;

public class Manager extends User {
	public Manager(Connection conn) {
		super(conn);
	}

	public void start() {
		String[] options = { "[1] Sign In", "[2] Back" };
		String choice = "";

		while (!choice.equals("2")) {
			HotelRNTN.printDivider();
			System.out.println("Hotel RNTN - Manager Portal");
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
				System.out.println("Invalid choice!");
			}
		}
	}

	private void signIn() {
		System.out.println("Hotel RNTN Manager - Sign In");

		String email = promptInput("email", String.class);
		String password = promptInput("password", String.class);

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
		String[] options = { "[1] View All Rooms", "[2] View Reserved Rooms", "[3] Create Room", "[4] Delete Room",
				"[5] View Number of Reservations by Date", "[6] View Number of Reservations by Room",
				"[7] View Popular Months", "[8] View High-Activity Months", "[9] View Recurring Guests",
				"[10] View Unpopular Rooms", "[11] Sign Out" };
		String choice = "";

		while (!choice.equals("11")) {
			HotelRNTN.printDivider();
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
				viewNumberReservationsDate();
				break;
			case "6":
				viewNumberReservationsRoom();
				break;
			case "7":
				viewPopularMonths();
				break;
			case "8":
				viewHighActivityMonths();
				break;
			case "9":
				viewRecurringGuests();
				break;
			case "10":
				viewUnpopularRooms();
				break;
			case "11":
				System.out.println("Signed out!");
				break;
			default:
				System.out.println("Invalid choice!");
			}
		}
	}

	private void viewReservedRooms() {
		System.out.println("Hotel RNTN Manager - View Reserved Rooms");

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
		System.out.println("Hotel RNTN Manager - Create Room");

		String roomNumber = promptInput("room number", String.class);

		Integer roomFloor = promptInput("room floor", Integer.class);
		if (roomFloor == null) {
			return;
		}

		Integer sqft = promptInput("sqft", Integer.class);
		if (sqft == null) {
			return;
		}

		BigDecimal price = promptInput("price", BigDecimal.class);
		if (price == null) {
			return;
		}

		String sql = "INSERT INTO room (room_num, room_floor, sqft, price) VALUES (?, ?, ?, ?)";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, roomNumber);
			pstmt.setInt(2, roomFloor);
			pstmt.setInt(3, sqft);
			pstmt.setBigDecimal(4, price);
			pstmt.executeUpdate();

			System.out.println("You have successfully created the room!");
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			case 1062:
				System.out.println("That room number is already used, please use another one!");
				break;
			default:
				System.out.println("An error has occurred while creating the room!");
			}
		}
	}

	private void deleteRoom() {
		System.out.println("Hotel RNTN Manager - Delete Room");

		Integer id = promptInput("room id", Integer.class);
		if (id == null) {
			return;
		}

		String sql = "DELETE FROM room WHERE id = ?";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			int deleted = pstmt.executeUpdate();

			if (deleted > 0) {
				System.out.println("You have successfully deleted the room!");
			} else {
				System.out.println("That room does not exist!");
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while deleting the room!");
			}
		}
	}

	private void viewNumberReservationsDate() {
		System.out.println("Hotel RNTN Manager - View Number of Reservations by Date");

		Date reserveDate = promptInput("reserve date (mm-dd-yyyy)", Date.class);
		if (reserveDate == null) {
			return;
		}

		String sql = "SELECT COUNT(*) FROM reservation WHERE reserve_date = ?";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setDate(1, reserveDate);
			pstmt.executeQuery();

			ResultSet rs = pstmt.getResultSet();
			rs.next();

			String date = dateFormat.format(reserveDate);
			int count = rs.getInt("COUNT(*)");

			System.out.printf("Reservations on %s: %d%n", date, count);
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing number of reservations by date!");
			}
		}
	}

	private void viewNumberReservationsRoom() {
		System.out.println("Hotel RNTN Manager - View Number of Reservations by Room");

		Integer roomId = promptInput("room id", Integer.class);
		if (roomId == null) {
			return;
		}

		String sql = "SELECT COUNT(*) FROM reservation WHERE room_id = ?";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, roomId);
			pstmt.executeQuery();

			ResultSet rs = pstmt.getResultSet();
			rs.next();
			int count = rs.getInt("COUNT(*)");

			System.out.printf("Reservations for room id %d: %d%n", roomId, count);
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing number of reservations by room!");
			}
		}
	}

	private void viewPopularMonths() {
		System.out.println("Hotel RNTN Manager - View Popular Months");

		String sql = "SELECT MONTH(reserve_date) FROM reservation GROUP BY MONTH(reserve_date) HAVING COUNT(*) >= 5";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.executeQuery();

			ResultSet rs = pstmt.getResultSet();

			if (!rs.isBeforeFirst()) {
				System.out.println("There are no popular months!");
			} else {
				printMonths(rs);
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing popular months!");
			}
		}
	}

	private void viewHighActivityMonths() {
		System.out.println("Hotel RNTN Manager - View High-Activity Months");

		String sql = "(SELECT MONTH(reserve_date) FROM canceled_reservation GROUP BY MONTH(reserve_date) HAVING COUNT(*) >= 3) UNION (SELECT MONTH(reserve_date) FROM reservation GROUP BY MONTH(reserve_date) HAVING COUNT(*) >= 3)";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.executeQuery();

			ResultSet rs = pstmt.getResultSet();

			if (!rs.isBeforeFirst()) {
				System.out.println("There are no high-activity months!");
			} else {
				printMonths(rs);
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing high-activity months!");
			}
		}
	}

	private void viewRecurringGuests() {
		System.out.println("Hotel RNTN Manager - View Recurring Guests");

		String sql = "SELECT id, first_name, last_name FROM account WHERE (SELECT COUNT(*) FROM reservation WHERE account_id = id) >= 2";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.executeQuery();

			ResultSet rs = pstmt.getResultSet();

			if (!rs.isBeforeFirst()) {
				System.out.println("There are no recurring guests!");
			} else {
				System.out.printf("%-20s%-20s%-20s%n", "id", "first name", "last name");

				while (rs.next()) {
					int id = rs.getInt("id");
					String firstName = rs.getString("first_name");
					String lastName = rs.getString("last_name");

					System.out.printf("%-20d%-20s%-20s%n", id, firstName, lastName);
				}
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing recurring guests!");
			}
		}
	}

	private void viewUnpopularRooms() {
		System.out.println("Hotel RNTN Manager - View Unpopular Rooms");

		String sql = "SELECT id, room_num, room_floor, sqft, price FROM room LEFT OUTER JOIN reservation ON id = room_id WHERE reserve_date IS NULL";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.executeQuery();

			ResultSet rs = pstmt.getResultSet();

			if (!rs.isBeforeFirst()) {
				System.out.println("There are no unpopular rooms!");
			} else {
				printRooms(rs);
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing unpopular rooms!");
			}
		}
	}

	private void printMonths(ResultSet rs) throws SQLException {
		while (rs.next()) {
			int monthInt = rs.getInt("MONTH(reserve_date)");
			String month = Month.of(monthInt).name();
			System.out.println(month);
		}
	}
}
