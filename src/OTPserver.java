import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.math.BigInteger;
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
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

public class OTPserver extends Application {
    public void start(Stage stage) {
        System.setProperty("javax.net.ssl.keyStore", "mySrvKeystore");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");
        
        SSLServerSocketFactory ssf = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
        
        try(ServerSocket ss = ssf.createServerSocket(8080)) {
            while(true) {
                Socket s = ss.accept();

                Thread t = new Thread() {
                    public void run() {
                        try(ObjectInputStream ois = new ObjectInputStream(s.getInputStream())) {
                            SSLSession session = ((SSLSocket) s).getSession();
                            Certificate[] cchain2 = session.getLocalCertificates();

                            // Prints
                            /*for (int i = 0; i < cchain2.length; i++)
                                System.out.println(((X509Certificate) cchain2[i]).getSubjectDN());
                            System.out.println("Peer host is " + session.getPeerHost());
                            System.out.println("Cipher is " + session.getCipherSuite());
                            System.out.println("Protocol is " + session.getProtocol());
                            System.out.println("ID is " + new BigInteger(session.getId()));
                            System.out.println("Session created in " + session.getCreationTime());
                            System.out.println("Session accessed in " + session.getLastAccessedTime());*/
                            
                            System.out.println("Server received: " + ois.readObject());
                            Thread.sleep(3000);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                        
                t.start();
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
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
