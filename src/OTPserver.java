import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javafx.application.Application;
import javafx.stage.Stage;
import java.security.cert.Certificate;
import javax.net.ssl.*;

public class OTPserver extends Application {
    public void start(Stage stage) {
        // Imports its own certificate
        System.setProperty("javax.net.ssl.keyStore", "../../serverCertificate");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");
        
        SSLServerSocketFactory sf = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
        try(SSLServerSocket ss = (SSLServerSocket)sf.createServerSocket(8080)) {
            /**/System.out.println("Server started");
            while(true) {
                SSLSocket s = (SSLSocket)ss.accept();

                Thread t = new Thread() {
                    public void run() {
                        try(ObjectInputStream ois = new ObjectInputStream(s.getInputStream())) {
                            SSLSession session = ((SSLSocket) s).getSession();
                            Certificate[] cchain2 = session.getLocalCertificates();
                            
                            // Receives username
                            UserInfos user = (UserInfos)ois.readObject(); // Netbeans gives error, but the class file is included in the classpath
                            System.out.println(
                                Thread.currentThread().getName() + " received: " + 
                                "Username: " + user.getUsername() +
                                " | Password: " + user.getPassword() + 
                                " | OTP: " + user.getOtp()
                            );
                            
                            //Thread.sleep(2500);
                            
                            // Receives password
                            /*String password = (String)ois.readObject();
                            System.out.println(Thread.currentThread().getName() + " received " + password + " as password.");*/
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
    }
}

// Useful prints for the future
/*for (int i = 0; i < cchain2.length; i++)
    System.out.println(((X509Certificate) cchain2[i]).getSubjectDN());
System.out.println("Peer host is " + session.getPeerHost());
System.out.println("Cipher is " + session.getCipherSuite());
System.out.println("Protocol is " + session.getProtocol());
System.out.println("ID is " + new BigInteger(session.getId()));
System.out.println("Session created in " + session.getCreationTime());
System.out.println("Session accessed in " + session.getLastAccessedTime());*/

// Code snippet for connecting to a database
/*try(Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/<database_name>?user=root&password=");
    Statement st = co.createStatement();
) {
    ResultSet rs = st.executeQuery("select * from prova");
    while(rs.next())
        System.out.println(rs.getInt("<integer_column_name>") + rs.getString("<string_column_name>"));
} catch(SQLException e) {
    System.err.println(e.getMessage());
}*/