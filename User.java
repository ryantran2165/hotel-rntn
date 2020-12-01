import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public abstract class User {
	protected Connection conn;
	protected Scanner scanner;
	protected SimpleDateFormat dateFormat;
	protected int id;
	protected String firstName;
	protected String lastName;

	protected User(Connection conn, Scanner scanner) {
		this.conn = conn;
		this.scanner = scanner;
		dateFormat = new SimpleDateFormat("MM-dd-yyyy");
	}

	@SuppressWarnings("unchecked")
	protected <T> T promptInput(String label, Class<T> type) {
		System.out.print("Please enter " + label + ": ");
		String value = scanner.nextLine();

		try {
			if (type == String.class) {
				return (T) value;
			} else if (type == BigDecimal.class) {
				return (T) new BigDecimal(value);
			} else if (type == Integer.class) {
				return (T) new Integer(value);
			} else if (type == Date.class) {
				long ms = dateFormat.parse(value).getTime();
				return (T) new Date(ms);
			}
			return null;
		} catch (Exception e) {
			System.out.println("Invalid " + label + " format!");
			return null;
		}
	}

	protected void viewAllRooms() {
		System.out.println("Hotel RNTN - View All Rooms");

		String sql = "SELECT id, room_num, room_floor, sqft, price FROM room";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			if (!rs.isBeforeFirst()) {
				System.out.println("There are no rooms!");
			} else {
				printRooms(rs);
			}
		} catch (SQLException e) {
			switch (e.getErrorCode()) {
			default:
				System.out.println("An error has occurred while viewing all rooms!");
			}
		} finally {
			HotelRNTN.closeQuietly(rs);
			HotelRNTN.closeQuietly(pstmt);
		}
	}

	protected void printRooms(ResultSet rs) throws SQLException {
		System.out.printf("%-20s%-20s%-20s%-20s%-20s%n", "id", "room number", "room floor", "sqft", "price");

		while (rs.next()) {
			int id = rs.getInt("id");
			String roomNum = rs.getString("room_num");
			int roomFloor = rs.getInt("room_floor");
			int sqft = rs.getInt("sqft");
			BigDecimal price = rs.getBigDecimal("price");

			System.out.printf("%-20d%-20s%-20d%-20d%-20.2f%n", id, roomNum, roomFloor, sqft, price);
		}
	}

	protected void printReservationRequests(ResultSet rs) throws SQLException {
		System.out.printf("%-20s%-20s%-20s%n", "room id", "reserve date", "request");

		while (rs.next()) {
			int roomId = rs.getInt("room_id");
			Date reserveDate = rs.getDate("reserve_date");
			String request = rs.getString("request");
			
			String date = dateFormat.format(reserveDate);

			System.out.printf("%-20d%-20s%-20s%n", roomId, date, request);
		}
	}
}
