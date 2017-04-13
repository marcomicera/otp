import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.*;
import javax.net.ssl.*;

public class OTPserver extends Application {
    
private static final int PORT = 8080;
    @Override
    public void start(Stage stage) throws IOException {
        // Server bidirectional socket
        SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        ServerSocket ss = ssf.createServerSocket(PORT);
        Socket s = ss.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        String line = null;
        while (((line = in.readLine()) != null)) {
        System.out.println(line);
        }
        /*
        // Connecting to database
        try(Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/<database_name>?user=root&password=");
            Statement st = co.createStatement();
        ) {
            ResultSet rs = st.executeQuery("select * from prova");
            while(rs.next())
                System.out.println(rs.getInt("<integer_column_name>") + rs.getString("<string_column_name>"));
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }

        Group root = new Group();
        Scene scene = new Scene(root, 500, 500);

        stage.setTitle("One Time Password server interface");
        stage.setScene(scene);
        stage.show();*/
    }
}
