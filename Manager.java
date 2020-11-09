import java.sql.Connection;

public class Manager {
	private Connection conn;
	
	public Manager(Connection conn) {
		this.conn = conn;
	}
}
