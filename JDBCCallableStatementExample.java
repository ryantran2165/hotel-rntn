import java.sql.*;

public class JDBCCallableStatementExample 
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

      // Class.forName(JDBC_DRIVER); //Register JDBC Driver
      createDatabase(); 
      createTable();
      insertDataIntoTable();
      createProcedures();
      callProcedures();
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
      conn = DriverManager.getConnection(DB_URL+"CS?serverTimezone=UTC", USER, PASS);
      statement = conn.createStatement();

    String queryDrop = "DROP TABLE IF EXISTS Faculty";
    Statement stmtDrop = conn.createStatement();
    stmtDrop.execute(queryDrop);


      String createTableSQL = "CREATE TABLE Faculty("
				+ "id INTEGER NOT NULL, "
				+ "name VARCHAR(20), "
				+ "age INTEGER, " 
				+ "PRIMARY KEY (ID))";
      statement.execute(createTableSQL); 
      System.out.println("Table called Faculty created successfully...");

}
private static void insertDataIntoTable() throws SQLException
{
      System.out.println("insert data from a file faculty.txt");
      statement.executeUpdate("INSERT INTO Faculty " + "VALUES (598, 'Nathaniel Hasson', 40)");
      statement.executeUpdate("INSERT INTO Faculty " + "VALUES (765, 'James Sonnier', 55)");
      statement.executeUpdate("INSERT INTO Faculty " + "VALUES (123, 'Peter J. McCleary', 61)");
      statement.executeUpdate("INSERT INTO Faculty " + "VALUES (456, 'Mark H. Stephens', 68)");
      statement.executeUpdate("INSERT INTO Faculty " + "VALUES (789, 'Joseph D. Morehouse', 46)");
      statement.executeUpdate("INSERT INTO Faculty " + "VALUES (012, 'Mary M. Mahle', 59)");
    
}

private static void createProcedures() throws SQLException
{
   String queryDrop = "DROP PROCEDURE IF EXISTS getFacultyByName";
   Statement stmtDrop = conn.createStatement();
   stmtDrop.execute(queryDrop); 

   String createInParameterProcedure = "CREATE PROCEDURE getFacultyByName(IN facultyName VARCHAR(50)) BEGIN SELECT * FROM Faculty WHERE name=facultyName; END";
   statement.executeUpdate(createInParameterProcedure);

   String createOutParameterProcedure ="CREATE PROCEDURE countByAge(IN retirementAge INT, OUT total INT) BEGIN SELECT count(*) INTO total FROM Faculty WHERE retirementAge < age; END" ;
   statement.executeUpdate(createOutParameterProcedure);
}

private static void callProcedures() throws SQLException
{
   System.out.println("\nCalling the procedure getFacultyByName");
   CallableStatement cs = conn.prepareCall("{CALL getFacultyByName(?)}");
   cs.setString(1,"James Sonnier");
   ResultSet rs = cs.executeQuery();
   printResultSet(rs);

   cs = conn.prepareCall("{CALL countByAge(?, ?)}");
   cs.setInt(1,50);
   cs.registerOutParameter(2, Types.INTEGER);
   boolean hasResult = cs.execute();
   if (hasResult){  rs = cs.getResultSet(); printResultSet(rs); }
   
   System.out.println("Faculty who is younger than 50");
   System.out.println(cs.getInt(2));   //prints 3
   System.out.println(cs.getInt("total")); // can get the value by the output parameter name.
}

private static void printResultSet(ResultSet rs) throws SQLException
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
