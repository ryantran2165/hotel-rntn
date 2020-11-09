import java.sql.Connection;

public class Guest {
	private Connection conn;
	
	public Guest(Connection conn) {
		this.conn = conn;
	}
}
