import java.sql.*;
import java.util.Scanner;

public class Guest {
	private Connection conn;
	private int id = -1;
	private String firstName = "";
	private String lastName = "";
	
	public Guest(Connection conn) {
		this.conn = conn;
	}
	
	public void options() {
		
	}
	
	public String signUp() throws SQLException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Hotel RNTN Guest Sign Up");
		System.out.print("Please Enter Your Email: ");
		String email = scanner.nextLine();
		
		System.out.print("Please Enter Your First Name: ");
		String firstName = scanner.nextLine();
		
		System.out.print("Please Enter Your Last Name: ");
		String lastName = scanner.nextLine();
		
		System.out.print("Please Enter Your Password: ");
		String password = scanner.nextLine();
		

		String sql = "INSERT INTO ACCOUNT (email, first_name, last_name, password, is_admin) VALUES(?, ?, ?, ?, ?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, email);
		pstmt.setString(2, firstName);
		pstmt.setString(3, lastName);
		pstmt.setString(4, password);
		pstmt.setBoolean(5, false);
		int result = pstmt.executeUpdate();
		
//		System.out.println(result);
		
		return "You have successfully Signed Up";
	}
	
	public boolean login() throws SQLException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Hotel RNTN Guest Login");
		
		System.out.print("Email: ");
		String email = scanner.nextLine();
		
		System.out.print("Password: ");
		String password = scanner.nextLine();
		
		String sql = "SELECT id, first_name, last_name FROM ACCOUNT where email = ? AND password = ? AND is_admin = false";
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
	
	public String createOptionStr(String option, String str) {
		return "[" + option + "] " + str;
	}
}
