package outils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    private String url = "jdbc:mysql://localhost:3306/suivitable";
    private String user = "root";
    private String password = "";
    private Connection conn;
    private static MyDataBase instance;

    public static MyDataBase getInstance() {
        if (instance == null) {
            instance = new MyDataBase();
        }
        return instance;
    }

    public Connection getConn() {
        return conn;
    }

    private MyDataBase() {
        try {
            this.conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connection established");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }
}
