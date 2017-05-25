import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javafx.application.Application;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.*;

public class OTPlocalServer extends Application {
    // Its own address
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
        
        // Imports remote server's certificate
        System.setProperty("javax.net.ssl.trustStore", REMOTE_SERVER_CERTIFICATE_NAME);
        System.setProperty("javax.net.ssl.trustStorePassword", REMOTE_SERVER_CERTIFICATE_PASSWORD);
        
        // Socket factories
        SSLSocketFactory rsSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        SSLServerSocketFactory cSocketFactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
        
        try(// Socket for client communications
            SSLServerSocket cServerSocket = (SSLServerSocket)cSocketFactory.createServerSocket(PORT);
        ) {
            System.out.println("Local server started\n");
            while(true) {
                SSLSocket cSocket = (SSLSocket)cServerSocket.accept();

                Thread clientThread = new Thread() {
                    public void run() {
                        /**/System.out.println("Thread " + Thread.currentThread().getId());
                        
                        try(// Socket for remoteServer communication
                            SSLSocket rsSocket = (SSLSocket)rsSocketFactory.createSocket(REMOTE_SERVER_ADDRESS, REMOTE_SERVER_PORT);
                            ObjectOutputStream cOos = new ObjectOutputStream(cSocket.getOutputStream());
                            ObjectOutputStream rsOos = new ObjectOutputStream(rsSocket.getOutputStream());
                        ) {
                            ObjectInputStream cOis = new ObjectInputStream(cSocket.getInputStream());
                            ObjectInputStream rsOis = new ObjectInputStream(rsSocket.getInputStream());

                            // Receiving user infos
                            UserInfos user = (UserInfos)cOis.readObject();
                            System.out.println("Received: " + user);
                            
                            // Sending data to the remote server
                            rsOos.writeObject(user);
                            
                            // Receiving remote server response
                            CounterResponse response = (CounterResponse)rsOis.readObject();
                            System.out.println(user.getUsername() + "'s large_window_on value: " + response.getLargeWindowOn());

                            // Username or password incorrect
                            if(!validCounterResponse(response)) {
                                /*rsOos.writeObject(
                                    new CounterResponse(
                                        null,   // dongle_counter
                                        null,   // dongle_key
                                        null,   // large_window_on
                                        null    // large_window_otp
                                    )
                                );*/
                                
                                // Login unsuccessful
                                cOos.writeInt(0);
                                System.out.println(user.getUsername() + " has not logged successfully.");
                            }
                            // Username and password correct
                            else {
                                Long align_counter;
                                
                                // User is not in large window mode
                                if(!response.getLargeWindowOn()) {
                                    align_counter = HOTPGeneratorServer.HOTPCheck(user.getOtp(), response.getDongleCounter(), response.getDongleKey(), false);

                                    // User's OTP is in narrow window
                                    if(align_counter != -1) {
                                        // Updating user's counter value in database
                                        rsOos.writeObject(
                                            new CounterResponse(
                                                align_counter,  // dongle_counter
                                                null,           // dongle_key
                                                false,           // large_window_on
                                                null            // large_window_otp
                                            )
                                        );
                                        
                                        // Login successful
                                        cOos.writeInt(1);                                                                    
                                        System.out.println(user.getUsername() + " has logged successfully.");
                                    }
                                    // User's OTP is not in narrow window
                                    else {
                                        align_counter = HOTPGeneratorServer.HOTPCheck(user.getOtp(), response.getDongleCounter(), response.getDongleKey(), true);
                                        
                                        // User's OTP is in large window
                                        if(align_counter != -1) {
                                            // Activating large window for the user
                                            rsOos.writeObject(
                                                new CounterResponse(
                                                    null,           // dongle_counter
                                                    null,           // dongle_key
                                                    true,           // large_window_on
                                                    align_counter   // large_window_otp
                                                )
                                            );
                                        }
                                        // User's OTP is not in large window
                                        else {
                                            // Tells remoteServer to do nothing
                                            rsOos.writeObject(
                                                new CounterResponse(
                                                    null,           // dongle_counter
                                                    null,           // dongle_key
                                                    false,           // large_window_on
                                                    null   // large_window_otp
                                                )
                                            );
                                        }
                                        
                                        // Login usuccessful
                                        cOos.writeInt(0);
                                        System.out.println(user.getUsername() + " has not logged successfully.");
                                    }
                                }
                                // User is in large window mode                    
                                else{
                                    // User's OTP in large window and different from the previous one
                                    if( HOTPGeneratorServer.HOTPCheck(user.getOtp(), response.getDongleCounter(), response.getDongleKey(), true)
                                        &&
                                        response.getLargeWindowOtp().compareTo(user.getOtp()) != 0
                                    ) {
                                        // Updating user's counter value in database
                                        rsOos.writeObject(
                                            new CounterResponse(
                                                align_counter,  // dongle_counter
                                                null,           // dongle_key
                                                false,          // large_window_on
                                                null            // large_window_otp
                                            )
                                        );
                                        
                                        // Login successful
                                        cOos.writeInt(1);                                                                    
                                        System.out.println(user.getUsername() + " has logged successfully.");
                                    }
                                    // User's OTP not in large window or is equal to the previous one
                                    else {
                                        // Deactivating large window for the user
                                        rsOos.writeObject(
                                            new CounterResponse(
                                                null,           // dongle_counter
                                                null,           // dongle_key
                                                false,          // large_window_on
                                                null            // large_window_otp
                                            )
                                        );
                                        
                                        // Login unsuccessful
                                        cOos.writeInt(0);
                                        System.out.println(user.getUsername() + " has not logged successfully.");
                                    }
                                }
                            }
                            System.out.print("\n");
                        } catch(IOException ex) {
                            Logger.getLogger(OTPlocalServer.class.getName()).log(Level.SEVERE, null, ex);
                        } catch(ClassNotFoundException ex) {
                            Logger.getLogger(OTPlocalServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                };
                        
                clientThread.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(OTPlocalServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean validCounterResponse(CounterResponse response) {
        return response.getDongleCounter() != null && response.getDongleKey() != null;
    }

    
        
    /*private void printCertificate(SSLSession session, Certificate[] certificate) {
        for (int i = 0; i < certificate.length; i++)
            System.out.println(((X509Certificate)certificate[i]).getSubjectDN());
        System.out.println("Peer host is " + session.getPeerHost());
        System.out.println("Cipher is " + session.getCipherSuite());
        System.out.println("Protocol is " + session.getProtocol());
        System.out.println("ID is " + new BigInteger(session.getId()));
        System.out.println("Session created in " + session.getCreationTime());
        System.out.println("Session accessed in " + session.getLastAccessedTime());
    }*/
}