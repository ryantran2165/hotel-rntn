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
	protected int id;
	protected String firstName;
	protected String lastName;

	public User(Connection conn) {
		this.conn = conn;
		scanner = HotelRNTN.SCANNER;
	}

	protected void viewRoomsAll() {
		System.out.println("Hotel RNTN View All Rooms");

		String sql = "SELECT id, room_num, room_floor, sqft, price FROM room";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.executeQuery();

			ResultSet rs = pstmt.getResultSet();

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

	protected void printReservations(ResultSet rs) throws SQLException {
		System.out.printf("%-20s%-20s%n", "room id", "reserve date");
		SimpleDateFormat f = new SimpleDateFormat("MM-dd-yyyy");

		while (rs.next()) {
			int roomId = rs.getInt("room_id");
			Date reserveDate = rs.getDate("reserve_date");
			String date = f.format(reserveDate);

			System.out.printf("%-20d%-20s%n", roomId, date);
		}
	}

	protected void printReservationRequests(ResultSet rs) throws SQLException {
		System.out.printf("%-20s%-20s%-20s%n", "room id", "reserve date", "request");
		SimpleDateFormat f = new SimpleDateFormat("MM-dd-yyyy");

		while (rs.next()) {
			int roomId = rs.getInt("room_id");
			Date reserveDate = rs.getDate("reserve_date");
			String request = rs.getString("request");
			String date = f.format(reserveDate);

			System.out.printf("%-20d%-20s%-20s%n", roomId, date, request);
		}
	}
}
