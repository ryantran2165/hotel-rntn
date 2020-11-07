//STEP 1. Import required packages
import java.sql.*;

public class ExceptionExample 
{
   // JDBC driver name and database URL
   
   static final String DB_URL = "jdbc:mysql://localhost/library?serverTimezone=UTC";
   //  Database credentials
   static final String USER = "root";
   static final String PASS = "default$";
   private static Connection conn = null;
   private static Statement statement = null;
   
   public static void main(String[] args) throws SQLException
   {
   try
   {

      // Class.forName(JDBC_DRIVER); //Register JDBC Driver
      createDatabase(); 
      createTable();
      loadDataIntoTable();
      insertKeyViolation();
   }
   catch(SQLException se){
	   int count = 1;
	   while(se != null)
	   { 
		   System.out.println("SQLException " + count); 
		   System.out.println("Code " + se.getErrorCode()); 
		   System.out.println("SQLState " + se.getSQLState()); 
		   System.out.println("Error Message " + se.getMessage()); 
		   se = se.getNextException();
		   count++;
	   }
   }
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
      conn = DriverManager.getConnection(DB_URL, USER, PASS);

     String queryDrop = "DROP DATABASE IF EXISTS cs";
    Statement stmtDrop = conn.createStatement();
    stmtDrop.execute(queryDrop);


      // Create a database named CS
      System.out.println("Creating database...");
      statement = conn.createStatement();
      
      String sql = "CREATE DATABASE CS";
      statement.executeUpdate(sql);
      System.out.println("Database created successfully...");
}
private static void createTable() throws SQLException
{
      // Open a connection and select the database named CS
      
      System.out.println("Connecting to database...");
      conn = DriverManager.getConnection(DB_URL+"CS", USER, PASS);
      statement = conn.createStatement();
      
    String queryDrop = "DROP TABLE IF EXISTS Students";
    Statement stmtDrop = conn.createStatement();
    stmtDrop.execute(queryDrop);


      String createTableSQL = "CREATE TABLE STUDENTS("
				+ "id INTEGER NOT NULL, "
				+ "name VARCHAR(20), "
				+ "age INTEGER, " 
				+ "PRIMARY KEY (ID))";
      statement.execute(createTableSQL); 
      System.out.println("Table called STUDENTS created successfully...");

}
private static void loadDataIntoTable() throws SQLException
{
      System.out.println("Load data from a file students.txt");
      String loadDataSQL = "LOAD DATA LOCAL INFILE 'C:/JDBC/students.txt' INTO TABLE STUDENTS";
      statement.execute(loadDataSQL); 
}

private static void insertKeyViolation() throws SQLException
{     ResultSet rs = null;

      statement.executeUpdate("INSERT INTO Students " + "VALUES (123, 'Megan Poulin', 19)");

}
}

