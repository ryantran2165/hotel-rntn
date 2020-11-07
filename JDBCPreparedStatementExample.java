
//STEP 1. Import required packages
import java.sql.*;

public class JDBCPreparedStatementExample 
{
   // JDBC driver name and database URL
	static final String DB_URL = "jdbc:mysql://localhost/";

   //  Database credentials
   static final String USER = "root";
   static final String PASS = "default$";
   private static Connection conn = null;
   private static PreparedStatement preparedStatement = null;
   
   public static void main(String[] args) throws SQLException
   {
   try
   {

      //Class.forName(JDBC_DRIVER); //Register JDBC Driver
      createDatabase(); 
      createTable();
      //loadDataIntoTable();
      manipulateData();
      batchUpdate();
   }
   catch(SQLException se){se.printStackTrace(); }
   catch(Exception e){ e.printStackTrace(); }
   finally
   {
      try{ if(preparedStatement!=null) preparedStatement.close(); }
      catch(SQLException se2){ }// nothing we can do
     
      try{ if(conn!=null) conn.close(); }
      catch(SQLException se){ se.printStackTrace(); }
   }
   System.out.println("Goodbye!");
}//end main

private static void createDatabase() throws SQLException
{

      // Open a connection
      System.out.println("Connecting to database...");
      conn = DriverManager.getConnection(DB_URL+"?serverTimezone=UTC", USER, PASS);

 
    String queryDrop = "DROP DATABASE IF EXISTS cs";
    Statement stmtDrop = conn.createStatement();
    stmtDrop.execute(queryDrop);

      // Create a database named CS
      System.out.println("Creating database...");
      
      String sql = "CREATE DATABASE CS";
      preparedStatement = conn.prepareStatement(sql);
      preparedStatement.executeUpdate();
      System.out.println("Database created successfully...");
}

private static void createTable() throws SQLException
{
      // Open a connection and select the database named CS
      
      System.out.println("Connecting to database...");
      conn = DriverManager.getConnection(DB_URL+"CS?serverTimezone=UTC", USER, PASS);

 String queryDrop = "DROP TABLE IF EXISTS Faculty";
    Statement stmtDrop = conn.createStatement();
    stmtDrop.execute(queryDrop);

      String createTableSQL = "CREATE TABLE FACULTY("
				+ "id INTEGER NOT NULL, "
				+ "name VARCHAR(20), "
				+ "age INTEGER, " 
				+ "PRIMARY KEY (ID))";
      preparedStatement = conn.prepareStatement(createTableSQL);
      preparedStatement.executeUpdate(); 
      System.out.println("Table called FACULTY created successfully...");

}

private static void loadDataIntoTable() throws SQLException
{
      System.out.println("Load data from a file faculty.txt");
      String loadDataSQL = "LOAD DATA LOCAL INFILE 'C:/JDBC/faculty.txt' INTO TABLE Faculty";

      preparedStatement = conn.prepareStatement(loadDataSQL);
      preparedStatement.execute(); 
}

private static void manipulateData() throws SQLException
{    String sql = null;
     ResultSet rs = null;

     sql =  "INSERT INTO Faculty " 
		 + "(id, name, age) VALUES" + "(?, ?, ?)";
     preparedStatement= conn.prepareStatement(sql);

     preparedStatement.setInt(1,848);
     preparedStatement.setString(2, "Dennis Weldon");
     preparedStatement.setInt(3,52);
     preparedStatement.executeUpdate();

     preparedStatement.setInt(1,254);
     preparedStatement.setString(2, "Suan Koening");
     preparedStatement.setInt(3,72);
     preparedStatement.executeUpdate();

     System.out.println("Faculty younger than 50");
     sql = "SELECT * from  Faculty where age  < ?";
     preparedStatement= conn.prepareStatement(sql);
     preparedStatement.setInt(1,50);
     rs = preparedStatement.executeQuery();
     printResultSetfromFaculty(rs);

     sql = "DELETE FROM Faculty WHERE age > ?";
     preparedStatement= conn.prepareStatement(sql);
     preparedStatement.setInt(1,70);
     preparedStatement.executeUpdate();
    

     System.out.println("After deleting faculty older than 70");
     sql = "SELECT * from  Faculty";
     preparedStatement= conn.prepareStatement(sql);
     rs = preparedStatement.executeQuery(sql);
     printResultSetfromFaculty(rs);
     
     sql = "UPDATE FACULTY SET name = ?" + "WHERE id = ?";
     preparedStatement= conn.prepareStatement(sql);
     preparedStatement.setString(1,"Dennis Chien");
     preparedStatement.setInt(2,848);
     preparedStatement.executeUpdate();
     
     System.out.println("After updating Dennis' lastname with Chien");
     sql = "SELECT * from  Faculty";
     preparedStatement= conn.prepareStatement(sql);
     rs = preparedStatement.executeQuery();
     printResultSetfromFaculty(rs);

}

private static void batchUpdate() throws SQLException
{
  conn.setAutoCommit(false);
    
    String sql =  "INSERT INTO Faculty (id, name, age) VALUES (?, ?, ?)";
    preparedStatement= conn.prepareStatement(sql);

    preparedStatement.setInt(1,404);
    preparedStatement.setString(2, "Margaret A. Steele");
    preparedStatement.setInt(3,47);
    preparedStatement.addBatch();

    preparedStatement.setInt(1,798);
    preparedStatement.setString(2, "Lorenzo G. Kirkham");
    preparedStatement.setInt(3,55);
    preparedStatement.addBatch();

    System.out.println("After processing a batch of faculty");
    preparedStatement.executeBatch();
    Statement statement = conn.createStatement();    
    ResultSet rs = statement.executeQuery("SELECT * from faculty");
    printResultSetfromFaculty(rs);
 
    conn.commit();


}


private static void printResultSetfromFaculty(ResultSet rs) throws SQLException
{
   while(rs.next())
   {
      int id = rs.getInt("id"); 
      String name = rs.getString("name"); 
      int age = rs.getInt("age");
      System.out.println("ID:" + id + " Name:" + name + " Age:" + age); 
   }
}
}//end JDBCExample
