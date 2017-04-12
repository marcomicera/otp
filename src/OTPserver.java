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

public class OTPserver extends Application {
    public void start(Stage stage) {
        // Server bidirectional socket
        try(ServerSocket servs = new ServerSocket(8080, 7)) {
            while(true) { 
                try( Socket s = servs.accept(); // Socket to client
                     ObjectInputStream oin = new ObjectInputStream(s.getInputStream());
                   ) { System.out.println("Ricevuto: " + oin.readObject()); }
                Thread.sleep(3000); // processing
            }
        } catch(Exception e) { e.printStackTrace(); }

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
        stage.show();
    }
}
