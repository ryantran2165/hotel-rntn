//STEP 1. Import required packages
import java.sql.*;

public class JDBCResultSet 
{
   // JDBC driver name and database URL
	static final String DB_URL = "jdbc:mysql://localhost/";

   //  Database credentials
   static final String USER = "root";
   static final String PASS = "default$";
   private static Connection conn = null;
   private static Statement statement = null;
   
   public static void main(String[] args) throws SQLException
   {
   try
   {

      //Class.forName(JDBC_DRIVER); //Register JDBC Driver
      createDatabase(); 
      createTable();
      
      manipulateData();
   }
   catch(SQLException se){se.printStackTrace(); }
   catch(Exception e){ e.printStackTrace(); }
   finally
   {
      try{ if(statement!=null) statement.close(); }
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

      // Create a database named CS
      System.out.println("Creating database...");
      statement = conn.createStatement();
      
      String queryDrop = "DROP DATABASE IF EXISTS cs";
      //Statement stmtDrop = conn.createStatement();
      statement.execute(queryDrop);
      String sql = "CREATE DATABASE CS";
      statement.executeUpdate(sql);
      System.out.println("Database created successfully...");
}
private static void createTable() throws SQLException
{
      // Open a connection and select the database named CS
      
      System.out.println("Connecting to database...");
      conn = DriverManager.getConnection(DB_URL+"CS?serverTimezone=UTC", USER, PASS);
      statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                   ResultSet.CONCUR_UPDATABLE);

      String queryDrop = "DROP TABLE IF EXISTS Faculty";
      statement.execute(queryDrop);
      String createTableSQL = "CREATE TABLE STUDENTS("
				+ "id INTEGER NOT NULL, "
				+ "name VARCHAR(20), "
				+ "age INTEGER, " 
				+ "PRIMARY KEY (ID))";
      statement.execute(createTableSQL); 
      System.out.println("Table called STUDENTS created successfully...");

}

private static void manipulateData() throws SQLException
{     statement.executeUpdate("INSERT INTO Students " + "VALUES (123, 'Ronald Kang', 18)");
      statement.executeUpdate("INSERT INTO Students " + "VALUES (456, 'Stephen Pchoa', 20)");
      statement.executeUpdate("INSERT INTO Students " + "VALUES (789, 'Megan Poulin', 19)");
      statement.executeUpdate("INSERT INTO Students " + "VALUES (012, 'Kenneth Martinez', 33)");
      ResultSet rs = statement.executeQuery("SELECT * FROM Students"); // The result goes to ResultSet
      System.out.println("Displaying record ...");
      while (rs.next()) 
      {
      int id = rs.getInt("id"); 
      String name = rs.getString("name"); 
      int age = rs.getInt("age");
      System.out.println("ID:" + id + " Name:" + name + " Age:" + age); 
      rs.updateInt("age", age * 10); // Updating the ResultSet
      rs.updateRow(); // Updating the database source, Student table
      }
     
     // Inserting a row to the ResultSet 
      rs.moveToInsertRow();
      rs.updateInt("id", 890);
      rs.updateString("name", "Smith");
      rs.updateInt("age", 43);

      rs.insertRow();
      rs.beforeFirst();
}

}//end JDBCExample

