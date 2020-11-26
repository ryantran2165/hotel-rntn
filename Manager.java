import java.sql.*;
import java.util.Scanner;

public class Manager {
	private Connection conn;
	private int id = -1;
	private String firstName = "";
	private String lastName = "";
	
	public Manager(Connection conn) {
		this.conn = conn;
	}
	
	public boolean login() throws SQLException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Hotel RNTN Admin Login");
		
		System.out.print("Email: ");
		String email = scanner.nextLine();
		
		System.out.print("Password: ");
		String password = scanner.nextLine();
		
		String sql = "SELECT id, first_name, last_name FROM ACCOUNT where email = ? AND password = ? AND is_admin = true";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, email);
		pstmt.setString(2, password);
		pstmt.executeQuery();
		
		ResultSet rs = pstmt.getResultSet();
		if(rs.next()) {
			id = rs.getInt("id");
			firstName = rs.getString("first_name");
			lastName = rs.getString("last_name");
			
			System.out.println(id);
			System.out.println(firstName);
			System.out.println(lastName);
			return true;
		}
		return false;
	}
}
