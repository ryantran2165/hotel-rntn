import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Scanner;

public class Manager extends User {
	private SimpleDateFormat timestampFormat;

	public Manager(Connection conn, Scanner scanner) {
		super(conn, scanner);
		timestampFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
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
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, email);
			pstmt.setString(2, password);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				id = rs.getInt("id");
				firstName = rs.getString("first_name");
				lastName = rs.getString("last_name");

				System.out.println("You have successfully signed in!");
				startSignedIn();
			} else {
				System.out.println("Invalid credentials!");
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while signing in!");
			}
		} finally {
			HotelRNTN.closeQuietly(rs);
			HotelRNTN.closeQuietly(pstmt);
		}
	}

	private void startSignedIn() {
		String[] options = { "[1] View All Guests", "[2] View Recurring Guests", "[3] Delete Guest",
				"[4] View All Rooms", "[5] View Reserved Rooms", "[6] View Unpopular Rooms", "[7] Create Room",
				"[8] Delete Room", "[9] View All Reservations", "[10] View Number of Reservations by Date",
				"[11] View Number of Reservations by Room", "[12] View Canceled Reservations",
				"[13] Cancel Reservation", "[14] View Popular Months", "[15] View High-Activity Months",
				"[16] Archive Reservations", "[17] View Reservation Archive", "[18] Sign Out" };
		String choice = "";

		while (!choice.equals("18")) {
			HotelRNTN.printDivider();
			System.out.printf("Hotel RNTN Manager - %s %s%n", firstName, lastName);
			HotelRNTN.printOptions(options);
			choice = scanner.nextLine();

			switch (choice) {
			case "1":
				viewAllGuests();
				break;
			case "2":
				viewRecurringGuests();
				break;
			case "3":
				deleteGuest();
				break;
			case "4":
				viewAllRooms();
				break;
			case "5":
				viewReservedRooms();
				break;
			case "6":
				viewUnpopularRooms();
				break;
			case "7":
				createRoom();
				break;
			case "8":
				deleteRoom();
				break;
			case "9":
				viewAllReservations();
				break;
			case "10":
				viewNumberReservationsDate();
				break;
			case "11":
				viewNumberReservationsRoom();
				break;
			case "12":
				viewCanceledReservations();
				break;
			case "13":
				cancelReservation();
				break;
			case "14":
				viewPopularMonths();
				break;
			case "15":
				viewHighActivityMonths();
				break;
			case "16":
				archiveReservations();
				break;
			case "17":
				viewReservationArchive();
				break;
			case "18":
				System.out.println("Signed out!");
				break;
			default:
				System.out.println("Invalid choice!");
			}
		}
	}

	private void viewAllGuests() {
		System.out.println("Hotel RNTN Manager - View All Guests");

		String sql = "SELECT id, first_name, last_name FROM account WHERE is_admin = FALSE";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			if (!rs.isBeforeFirst()) {
				System.out.println("There are no guests!");
			} else {
				printGuests(rs);
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing all guests!");
			}
		} finally {
			HotelRNTN.closeQuietly(rs);
			HotelRNTN.closeQuietly(pstmt);
		}
	}

	private void viewRecurringGuests() {
		System.out.println("Hotel RNTN Manager - View Recurring Guests");

		String sql = "SELECT id, first_name, last_name FROM account WHERE (SELECT COUNT(*) FROM reservation WHERE account_id = id) >= 2";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			if (!rs.isBeforeFirst()) {
				System.out.println("There are no recurring guests!");
			} else {
				printGuests(rs);
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing recurring guests!");
			}
		} finally {
			HotelRNTN.closeQuietly(rs);
			HotelRNTN.closeQuietly(pstmt);
		}
	}

	private void deleteGuest() {
		System.out.println("Hotel RNTN Manager - Delete Guest");

		Integer id = promptInput("guest id", Integer.class);
		if (id == null) {
			return;
		}

		String sql = "DELETE FROM account WHERE id = ? AND is_admin = FALSE";
		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			int deleted = pstmt.executeUpdate();

			if (deleted > 0) {
				System.out.println("You have successfully deleted the guest!");
			} else {
				System.out.println("That guest does not exist!");
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while deleting the guest!");
			}
		} finally {
			HotelRNTN.closeQuietly(pstmt);
		}
	}

	private void viewReservedRooms() {
		System.out.println("Hotel RNTN Manager - View Reserved Rooms");

		String sql = "SELECT id, room_num, room_floor, sqft, price FROM room WHERE id IN (SELECT room_id FROM reservation)";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

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
		} finally {
			HotelRNTN.closeQuietly(rs);
			HotelRNTN.closeQuietly(pstmt);
		}
	}

	private void viewUnpopularRooms() {
		System.out.println("Hotel RNTN Manager - View Unpopular Rooms");

		String sql = "SELECT id, room_num, room_floor, sqft, price FROM room LEFT OUTER JOIN reservation ON id = room_id WHERE reserve_date IS NULL";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

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
		} finally {
			HotelRNTN.closeQuietly(rs);
			HotelRNTN.closeQuietly(pstmt);
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
		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, roomNumber);
			pstmt.setInt(2, roomFloor);
			pstmt.setInt(3, sqft);
			pstmt.setBigDecimal(4, price);
			pstmt.executeUpdate();

			System.out.println("You have successfully created the room!");
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			case 1062:
				System.out.println("That room number is already used!");
				break;
			default:
				System.out.println("An error has occurred while creating the room!");
			}
		} finally {
			HotelRNTN.closeQuietly(pstmt);
		}
	}

	private void deleteRoom() {
		System.out.println("Hotel RNTN Manager - Delete Room");

		Integer id = promptInput("room id", Integer.class);
		if (id == null) {
			return;
		}

		String sql = "DELETE FROM room WHERE id = ?";
		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement(sql);
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
		} finally {
			HotelRNTN.closeQuietly(pstmt);
		}
	}

	private void viewAllReservations() {
		System.out.println("Hotel RNTN Manager - View All Reservations");

		String sql = "SELECT account_id, first_name, last_name, room_id, room_num, reserve_date, updated_at FROM reservation INNER JOIN account ON account_id = account.id INNER JOIN room ON room_id = room.id";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			if (!rs.isBeforeFirst()) {
				System.out.println("There are no reservations!");
			} else {
				printReservations(rs);
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing all reservations!");
			}
		} finally {
			HotelRNTN.closeQuietly(rs);
			HotelRNTN.closeQuietly(pstmt);
		}
	}

	private void viewNumberReservationsDate() {
		System.out.println("Hotel RNTN Manager - View Number of Reservations by Date");

		Date reserveDate = promptInput("reserve date (mm-dd-yyyy)", Date.class);
		if (reserveDate == null) {
			return;
		}

		String sql = "SELECT COUNT(*) FROM reservation WHERE reserve_date = ?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setDate(1, reserveDate);
			rs = pstmt.executeQuery();
			rs.next();

			String date = dateFormat.format(reserveDate);
			int count = rs.getInt("COUNT(*)");

			System.out.printf("Reservations on %s: %d%n", date, count);
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing number of reservations by date!");
			}
		} finally {
			HotelRNTN.closeQuietly(rs);
			HotelRNTN.closeQuietly(pstmt);
		}
	}

	private void viewNumberReservationsRoom() {
		System.out.println("Hotel RNTN Manager - View Number of Reservations by Room");

		Integer roomId = promptInput("room id", Integer.class);
		if (roomId == null) {
			return;
		}

		String sql = "SELECT COUNT(*) FROM reservation WHERE room_id = ?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, roomId);
			rs = pstmt.executeQuery();
			rs.next();

			int count = rs.getInt("COUNT(*)");

			System.out.printf("Reservations for room id %d: %d%n", roomId, count);
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing number of reservations by room!");
			}
		} finally {
			HotelRNTN.closeQuietly(rs);
			HotelRNTN.closeQuietly(pstmt);
		}
	}

	private void viewCanceledReservations() {
		System.out.println("Hotel RNTN Manager - View Canceled Reservations");

		String sql = "SELECT account_id, first_name, last_name, room_id, room_num, reserve_date, cancel_date FROM canceled_reservation INNER JOIN account ON account_id = account.id INNER JOIN room ON room_id = room.id WHERE (room_id, reserve_date) NOT IN (SELECT room_id, reserve_date FROM reservation_archive)";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			if (!rs.isBeforeFirst()) {
				System.out.println("There are no canceled reservations!");
			} else {
				printCanceledReservations(rs);
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing canceled reservations!");
			}
		} finally {
			HotelRNTN.closeQuietly(rs);
			HotelRNTN.closeQuietly(pstmt);
		}
	}

	private void cancelReservation() {
		System.out.println("Hotel RNTN Manager - Cancel Reservation");

		Integer roomId = promptInput("room id", Integer.class);
		if (roomId == null) {
			return;
		}

		Date reserveDate = promptInput("reserve date (mm-dd-yyyy)", Date.class);
		if (reserveDate == null) {
			return;
		}

		String sql = "DELETE FROM reservation WHERE room_id = ? AND reserve_date = ?";
		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, roomId);
			pstmt.setDate(2, reserveDate);
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
		} finally {
			HotelRNTN.closeQuietly(pstmt);
		}
	}

	private void viewPopularMonths() {
		System.out.println("Hotel RNTN Manager - View Popular Months");

		String sql = "SELECT MONTH(reserve_date) FROM reservation GROUP BY MONTH(reserve_date) HAVING COUNT(*) >= 5";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

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
		} finally {
			HotelRNTN.closeQuietly(rs);
			HotelRNTN.closeQuietly(pstmt);
		}
	}

	private void viewHighActivityMonths() {
		System.out.println("Hotel RNTN Manager - View High-Activity Months");

		String sql = "(SELECT MONTH(reserve_date) FROM canceled_reservation GROUP BY MONTH(reserve_date) HAVING COUNT(*) >= 3) UNION (SELECT MONTH(reserve_date) FROM reservation GROUP BY MONTH(reserve_date) HAVING COUNT(*) >= 3)";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

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
		} finally {
			HotelRNTN.closeQuietly(rs);
			HotelRNTN.closeQuietly(pstmt);
		}
	}

	private void archiveReservations() {
		System.out.println("Hotel RNTN Manager - Archive Reservations");

		Date cutoffDate = promptInput("cutoff date (mm-dd-yyyy)", Date.class);
		if (cutoffDate == null) {
			return;
		}

		String sql = "{CALL proc_archive_reservation(?)}";
		CallableStatement cstmt = null;

		try {
			cstmt = conn.prepareCall(sql);
			cstmt.setDate(1, cutoffDate);
			cstmt.execute();

			int updated = cstmt.getUpdateCount();

			System.out.printf("Reservations archived: %d%n", updated);
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			case 1062:
				System.out.println("The archive contains a conflicting reservation (unique room_id and reserve_date)!");
				break;
			default:
				System.out.println("An error has occurred while archiving reservations!");
			}
		} finally {
			HotelRNTN.closeQuietly(cstmt);
		}
	}

	private void viewReservationArchive() {
		System.out.println("Hotel RNTN Manager - View Reservation Archive");

		String sql = "SELECT account_id, first_name, last_name, room_id, room_num, reserve_date, updated_at FROM reservation_archive INNER JOIN account ON account_id = account.id INNER JOIN room ON room_id = room.id";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			if (!rs.isBeforeFirst()) {
				System.out.println("The reservation archive is empty!");
			} else {
				printReservations(rs);
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing the reservation archive!");
			}
		} finally {
			HotelRNTN.closeQuietly(rs);
			HotelRNTN.closeQuietly(pstmt);
		}
	}

	private void printGuests(ResultSet rs) throws SQLException {
		System.out.printf("%-20s%-20s%-20s%n", "id", "first name", "last name");

		while (rs.next()) {
			int id = rs.getInt("id");
			String firstName = rs.getString("first_name");
			String lastName = rs.getString("last_name");

			System.out.printf("%-20d%-20s%-20s%n", id, firstName, lastName);
		}
	}

	private void printReservations(ResultSet rs) throws SQLException {
		System.out.printf("%-20s%-20s%-20s%-20s%-20s%-20s%-20s%n", "guest id", "first name", "last name", "room id",
				"room number", "reserve date", "updated at");

		while (rs.next()) {
			int guestId = rs.getInt("account_id");
			String firstName = rs.getString("first_name");
			String lastName = rs.getString("last_name");
			int roomId = rs.getInt("room_id");
			String roomNum = rs.getString("room_num");
			Date reserveDate = rs.getDate("reserve_date");
			Timestamp updatedAt = rs.getTimestamp("updated_at");

			String reserveDateStr = dateFormat.format(reserveDate);
			String updatedAtStr = timestampFormat.format(updatedAt);

			System.out.printf("%-20d%-20s%-20s%-20d%-20s%-20s%-20s%n", guestId, firstName, lastName, roomId, roomNum,
					reserveDateStr, updatedAtStr);
		}
	}

	private void printCanceledReservations(ResultSet rs) throws SQLException {
		System.out.printf("%-20s%-20s%-20s%-20s%-20s%-20s%-20s%n", "guest id", "first name", "last name", "room id",
				"room number", "reserve date", "cancel date");

		while (rs.next()) {
			int guestId = rs.getInt("account_id");
			String firstName = rs.getString("first_name");
			String lastName = rs.getString("last_name");
			int roomId = rs.getInt("room_id");
			String roomNum = rs.getString("room_num");
			Date reserveDate = rs.getDate("reserve_date");
			Date cancelDate = rs.getDate("cancel_date");

			String reserveDateStr = dateFormat.format(reserveDate);
			String cancelDateStr = dateFormat.format(cancelDate);

			System.out.printf("%-20d%-20s%-20s%-20d%-20s%-20s%-20s%n", guestId, firstName, lastName, roomId, roomNum,
					reserveDateStr, cancelDateStr);
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
