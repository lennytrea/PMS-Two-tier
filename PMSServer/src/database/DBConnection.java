package database;

import java.sql.*;

public class DBConnection {
    public Connection con;
    String url="jdbc:postgresql://localhost:5432/prisonmanagementdb";
    String password ="postgres";
    String user = "postgres";
    
    public DBConnection(){
        con = getconnections();
    }
   public final  Connection getconnections(){
       //load driver
       Connection con = null;
       try{
        Class.forName("org.postgresql.Driver");
        System.out.println("load driver success");
       }catch (ClassNotFoundException cnfe){
           System.out.println("load driver failed"+ cnfe.getMessage());
       }
       //establishhhhhhhh the connection 
       try{
           con  = DriverManager.getConnection(url, user, password);
           System.out.println("driver loaded success");
       }
       catch(SQLException sqle) {
    System.out.println("driver loader failed "+ sqle.getMessage());
}
       return con;
   }

}
