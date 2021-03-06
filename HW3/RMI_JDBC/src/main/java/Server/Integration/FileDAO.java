/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Integration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author silvanzeller
 */
public class FileDAO {
    
    private final String url = "jdbc:derby://localhost:1527/JDBC";
    private final String user = "jdbc";
    private final String password = "jdbc";
    private static final String TABLE_NAME = "filedata";
    private final Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;
    
    public FileDAO () throws ClassNotFoundException, SQLException{
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        connection = DriverManager.getConnection(url, user, password);
        createTable(connection);
        statement = connection.createStatement();
    }
 
    private void prepareStatements(Connection connection) throws SQLException{
        preparedStatement = connection.prepareStatement("insert into "+TABLE_NAME+" values (?, ?, ?, ?, ?, ?)");
    }
 
    private boolean tableExists(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet table = metaData.getTables(null, null, null, null);
        while(table.next()){
            String key = table.getString(3);
            if (key.equalsIgnoreCase(TABLE_NAME)){
                return true;
            }
        }
        return false;
    }

    private void createTable(Connection connection) throws SQLException {
        System.out.println("in createTable");
        if(!tableExists(connection)){
            Statement createTable = connection.createStatement();
            System.out.println("Table is being created ...");
            System.out.println("In executeUpdate");
            createTable.executeUpdate("create table "+TABLE_NAME+" ("
                    + "filename varchar(32) primary key, "
                    + "owner varchar(32), "
                    + "publicAccess boolean, "
                    + "writePermission boolean, "
                    + "notify boolean)");
        }
    }
    
    public List<String> getList(long userId){
        List<String> allFiles = new ArrayList<>();
        String attributes;
        String user = Long.toString(userId);
        try {
            ResultSet resultSet = statement.executeQuery("select * from "+TABLE_NAME+" "
                    + "where owner = '"+user+"'");
            while(resultSet.next()){
                attributes = "Filename: "+resultSet.getString("filename")
                        +"\tFilesize: "+resultSet.getDouble("size")
                        +"\tOwner: "+resultSet.getString("owner")
                        +"\tWritable: "+resultSet.getBoolean("writePermission");
                allFiles.add(attributes);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return allFiles;
    }
    
    public boolean uploadFile(String filename, double size, String userId, boolean writePermission){
        try {
            prepareStatements(connection);
            boolean publicAccess = true;
            boolean notify = true;  
            preparedStatement.setString(1, filename);
            preparedStatement.setDouble(2, size);
            preparedStatement.setString(3, userId);
            preparedStatement.setBoolean(4, writePermission);
            preparedStatement.setBoolean(5, publicAccess);
            preparedStatement.setBoolean(6, notify);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    public boolean getFile(String filename, String userId){
        try {
            ResultSet resultSet = statement.executeQuery("select * from "+TABLE_NAME+" "
                    + "where (name = '"+filename+"' "
                            + "and (owner = '"+userId+"') "
                                    + "(or publicAccess = 'true'))");
            
            if(resultSet.next()){
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteFile(String filename, String userId){
        try {
            ResultSet resultSet = statement.executeQuery("select * from "+TABLE_NAME+" "
                    + "where (filename = '"+filename+"' "
                            + "and (owner = '"+userId+"') "
                                    + "(or publicAccess = 'true'))");
            if(resultSet.next()){
                statement.executeUpdate("delete from "+TABLE_NAME+" where name = '"+filename+"'");
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    public boolean hasWritePermission(String filename, String userId){
        try{
            ResultSet resultSet = statement.executeQuery("select * from "+TABLE_NAME+" "
                    + "where (filename = '"+filename+"') "
                            + "and (owner = '"+userId+"' or writePermission = 'true')");
            if(resultSet.next()){
                return true;
            }
        } catch (SQLException ex){
            ex.printStackTrace();
        }
        return false;
    }
    
    
    public boolean writeFile(String filename, String userId){
        try {
            ResultSet resultSet = statement.executeQuery("select * from "+TABLE_NAME+" "
                    + "where (filename = '"+filename+"') "
                            + "and ((owner = '"+userId+"') "
                                    + "or (publicAccess = 'true' and writePermission = 'true'))");
            if(resultSet.next()){
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public String notifyOwner(String filename, String userId){
        try {
            ResultSet resultSet = statement.executeQuery("select * from "+TABLE_NAME+" "
                    +"where (filename = '"+filename+"') "
                            + "and (not owner = '"+userId+"') "
                                    + "and (notify = 'true')");
            if (resultSet.next()){
                return resultSet.getString("owner");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "";
    }
}


