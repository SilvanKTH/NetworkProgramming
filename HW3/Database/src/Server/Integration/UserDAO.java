/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Integration;

import Common.ClientMethods;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author silvanzeller
 */
public class UserDAO {
    
    private final String url = "jdbc:derby://localhost:1527/JDBC";
    private final String user = "jdbc";
    private final String password = "jdbc";
    private static final String TABLE_NAME = "userdata";
    private static final int MAX_USER = 1024;
    private final Connection connection;
    private final Statement statement;
    private PreparedStatement createUser;
    private PreparedStatement findUser;
    private PreparedStatement findAuthUser;
    private PreparedStatement deleteUser;
    private PreparedStatement findAllUsers;
    private final Random idGenerator = new Random();
    
    public UserDAO() throws ClassNotFoundException, SQLException{
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        connection = DriverManager.getConnection(url, user, password);
        createTable(connection);
        statement = connection.createStatement();
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
        if(!tableExists(connection)){
            Statement createTable = connection.createStatement();
            createTable.executeUpdate("create table "+TABLE_NAME
                    +"(userid bigint primary key"
                            + "username varchar(32)"
                            + "password varchar(32)");
        }
    }
    
    private void prepareStatements(Connection connection) throws SQLException{
        createUser = connection.prepareStatement("insert into "+TABLE_NAME+
                " values (?, ?, ?)");
        findUser = connection.prepareStatement("select * from "+TABLE_NAME+
                " where username = ?");
        findAuthUser = connection.prepareStatement("select * from "+TABLE_NAME+
                " where username = ? and password = ?");
        deleteUser = connection.prepareStatement("delete from "+TABLE_NAME+
                " where username = ? and password = ?");
        findAllUsers = connection.prepareStatement("select * from "+TABLE_NAME);
    }
    
    public boolean registerUser (String username, String password){
        boolean success = false;
        try {
            prepareStatements(connection);
            findUser.setString(1, username);
            ResultSet resultSet = findUser.executeQuery();
            while(resultSet.next()){
                if(username.equalsIgnoreCase(resultSet.getString("username"))){
                    success = false;
                }
            }
            long userId = 0;
            boolean isValid = false;
            while(userId < 10 && !isValid){
                userId = idGenerator.nextInt(MAX_USER);
                resultSet = findAllUsers.executeQuery();
                isValid = true;
                while(resultSet.next()){
                    if (userId == resultSet.getLong(1)){
                        isValid = false;
                        break;
                    }
                }
                createUser.setLong(1, userId);
                createUser.setString(2, username);
                createUser.setString(3, password);
                createUser.executeUpdate();
                success = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            success = false;
        }
        return success;
    }
    
    public boolean unregisterUser (String username, String password){
        boolean success = false;
        try {
            prepareStatements(connection);
            findAuthUser.setString(1, username);
            findAuthUser.setString(2, password);
            ResultSet resultSet = findAuthUser.executeQuery();
            while(resultSet.next()){
                deleteUser.setString(1, username);
                deleteUser.setString(2, password);
                deleteUser.executeUpdate();
                success = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return success;
    }
    
    public long loginDB (ClientMethods user, String username, String password){
        try {
            prepareStatements(connection);
            findAuthUser.setString(1, username);
            findAuthUser.setString(2, password);
            ResultSet resultSet = findAuthUser.executeQuery();
            while(resultSet.next()){
                return resultSet.getLong("userid");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }
}
