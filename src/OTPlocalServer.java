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
    // Its own address
    // private final static String ADDRESS = "localhost";
    private final static int    PORT = 8080;
    
    // Its own certificate
    private final static String CERTIFICATE_NAME = "../../localServerCertificate",
                                CERTIFICATE_PASSWORD = "password";
    
    // Remote server address
    private final static String REMOTE_SERVER_ADDRESS = "localhost";
    private final static int    REMOTE_SERVER_PORT = 8081;
    
    // Remote server certificate
    private final static String REMOTE_SERVER_CERTIFICATE_NAME = "../../remoteServerCertificate",
                                REMOTE_SERVER_CERTIFICATE_PASSWORD = "password";
    
    public void start(Stage stage) {
        // Imports its own certificate
        System.setProperty("javax.net.ssl.keyStore", CERTIFICATE_NAME);
        System.setProperty("javax.net.ssl.keyStorePassword", CERTIFICATE_PASSWORD);
        
        SSLServerSocketFactory sf = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
        try(SSLServerSocket ss = (SSLServerSocket)sf.createServerSocket(PORT)) {
            System.out.println("Local server started\n");
            while(true) {
                SSLSocket s = (SSLSocket)ss.accept();

                Thread t = new Thread() {
                    public void run() {
                        try(ObjectInputStream ois = new ObjectInputStream(s.getInputStream())) {
                            SSLSession session = ((SSLSocket)s).getSession();
                            
                            // Receives user infos
                            UserInfos user = (UserInfos)ois.readObject(); // Netbeans gives error, but the class file is included in the classpath
                            System.out.println("Received: " + user);
                            
                            // Checking OTP
                            // ...

                            // Sending data to the remote server
                            sslSend(
                                user,
                                REMOTE_SERVER_ADDRESS,
                                REMOTE_SERVER_PORT,
                                REMOTE_SERVER_CERTIFICATE_NAME,
                                REMOTE_SERVER_CERTIFICATE_PASSWORD
                            );
                            
                            /**/Thread.sleep(2500);
                            
                            CounterResponse response = (CounterResponse)ois.readObject();
                            
                            System.out.print("\n");
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
    
    private void sslSend(Object message, String address, int port, String certificate, String password) {
        System.setProperty("javax.net.ssl.trustStore", certificate);
        System.setProperty("javax.net.ssl.trustStorePassword", password);
        SSLSocketFactory sf = (SSLSocketFactory)SSLSocketFactory.getDefault();

        try(SSLSocket s = (SSLSocket)sf.createSocket(address, port);
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