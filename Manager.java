import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
				"[8] High-Activity Months", "[9] Recurring Guests", "[10] Unpopular Rooms", "[11] Sign Out" };
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
				numberReservationsDate();
				break;
			case "6":
				numberReservationsRoom();
				break;
			case "7":
				popularMonths();
				break;
			case "8":
				highActivityMonths();
				break;
			case "9":
				recurringGuests();
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
		System.out.println("Hotel RNTN Manager View Reserved Rooms");

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
		System.out.println("Hotel RNTN Manager Number of Reservations by Date");

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

		String sql = "SELECT COUNT(*) FROM reservation WHERE reserve_date = ?";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setDate(1, reserveDate);
			pstmt.executeQuery();

			ResultSet rs = pstmt.getResultSet();
			rs.next();
			int count = rs.getInt("COUNT(*)");
			date = f.format(reserveDate);

			System.out.printf("Reservations on %s: %d%n", date, count);
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing number of reservations by date!");
			}
		}
	}

	private void numberReservationsRoom() {
		System.out.println("Hotel RNTN Manager Number of Reservations by Room");

		System.out.print("Please enter room id: ");
		String roomId = scanner.nextLine();
		int roomIdInt;

		try {
			roomIdInt = Integer.parseInt(roomId);
		} catch (NumberFormatException e) {
			System.out.println("Invalid room id, please try again!");
			return;
		}

		String sql = "SELECT COUNT(*) FROM reservation WHERE room_id = ?";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, roomIdInt);
			pstmt.executeQuery();

			ResultSet rs = pstmt.getResultSet();
			rs.next();
			int count = rs.getInt("COUNT(*)");

			System.out.printf("Reservations for room id %d: %d%n", roomIdInt, count);
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing number of reservations by room!");
			}
		}
	}

	private void popularMonths() {
		System.out.println("Hotel RNTN Manager Popular Months");

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

	private void highActivityMonths() {
		System.out.println("Hotel RNTN Manager High-Activity Months");

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

	private void recurringGuests() {
		System.out.println("Hotel RNTN Manager Recurring Guests");

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

	private void unpopularRooms() {
		System.out.println("Hotel RNTN Manager Unpopular Rooms");

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
