import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import javafx.application.Application;
import javafx.stage.Stage;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

public class OTPlocalServer extends Application {
    private final static String REMOTE_SERVER_ADDRESS = "localhost";
    private final static int    REMOTE_SERVER_PORT = 8081,
                                PORT = 8080;
    
    public void start(Stage stage) {
        // Imports its own certificate
        System.setProperty("javax.net.ssl.keyStore", "../../localServerCertificate");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");
        
        SSLServerSocketFactory sf = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
        try(SSLServerSocket ss = (SSLServerSocket)sf.createServerSocket(PORT)) {
            System.out.println("Local server started");
            while(true) {
                SSLSocket s = (SSLSocket)ss.accept();

                Thread t = new Thread() {
                    public void run() {
                        try(ObjectInputStream ois = new ObjectInputStream(s.getInputStream())) {
                            SSLSession session = ((SSLSocket)s).getSession();
                            // Certificate[] certificate = session.getLocalCertificates(); // localServerCertificate
                            
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
    
    private void sslSend(Object message) {
        System.setProperty("javax.net.ssl.trustStore", "../../remoteServerCertificate");
        SSLSocketFactory sf = (SSLSocketFactory)SSLSocketFactory.getDefault();

        try(SSLSocket s = (SSLSocket)sf.createSocket(REMOTE_SERVER_ADDRESS, REMOTE_SERVER_PORT);
            ObjectOutputStream oout = new ObjectOutputStream(s.getOutputStream());
        ) {
            SSLSession session = ((SSLSocket)s).getSession();
            Certificate[] cchain = session.getPeerCertificates();

            oout.writeObject(message);
        } catch(IOException e) {
            e.printStackTrace(); 
        }
        System.out.println("\"" + message + "\" sent.");
    }
    
    private void printCertificate(SSLSession session, Certificate[] certificate) {
        for (int i = 0; i < certificate.length; i++)
            System.out.println(((X509Certificate)certificate[i]).getSubjectDN());
        System.out.println("Peer host is " + session.getPeerHost());
        System.out.println("Cipher is " + session.getCipherSuite());
        System.out.println("Protocol is " + session.getProtocol());
        System.out.println("ID is " + new BigInteger(session.getId()));
        System.out.println("Session created in " + session.getCreationTime());
        System.out.println("Session accessed in " + session.getLastAccessedTime());
    }
}