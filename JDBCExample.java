
//STEP 1. Import required packages
import java.sql.*;

public class JDBCExample {
   // JDBC driver name and database URL
   
   static final String DB_URL = "jdbc:mysql://localhost/library?serverTimezone=UTC";
   
   static final String USER = "root";
   static final String PASS = "default$";
   
   public static void main(String[] args) {
   Connection conn = null;
   Statement stmt = null;
   ResultSet rs = null;
   try{
      //STEP 2: Register JDBC driver (automatically done since JDBC 4.0)
      // Class.forName("com.mysql.jdbc.Driver");

      //STEP 3: Open a connection
      System.out.println("Connecting to database...");
      conn = DriverManager.getConnection(DB_URL, USER, PASS);

      //STEP 4: Execute a query
      System.out.println("Creating a statement...");
      stmt = conn.createStatement();
      rs = stmt.executeQuery("select uid, uname from User");
      
      //STEP 5: Process the results
      while(rs.next()){
          System.out.println("User ID="+rs.getInt("uid")+", Name="+rs.getString("uname"));
      }
      
   }catch(SQLException se){
      //Handle errors for JDBC
      se.printStackTrace();
   }catch(Exception e){
      //Handle errors for Class.forName
      e.printStackTrace();
   }finally{
      //finally block used to close resources
      try{
         if(stmt!=null)
            stmt.close();
      }catch(SQLException se2){
      }// nothing we can do
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }//end finally try
   }//end try
   System.out.println("Goodbye!");
}//end main
}//end JDBCExample
