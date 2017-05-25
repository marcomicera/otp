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
                            
                            // Sending response
                            if(!validCounterResponse(response)) {
                                cOos.writeInt(0);
                                System.out.println(user.getUsername() + " has not logged successfully.");
                            }
                            else {
                                if(!response.getLargeWindowOn()) {
                                    if(HOTPGeneratorServer.HOTPCheck(user.getOtp(), response.getDongleCounter(), response.getDongleKey(), false)) {
                                        //MANDO AGGIORNAMETO AL REMOTE SERVER CON COUNTER RICEVUTO + 1
                                        cOos.writeInt(1);                                                                    
                                        System.out.println(user.getUsername() + " has logged successfully.");
                                    }
                                    else
                                    {
                                        if(HOTPGeneratorServer.HOTPCheck(user.getOtp(), response.getDongleCounter(), response.getDongleKey(), true)) {
                                            //MANDO AL SERVER COMANDO PER ATTIVARE LARGE WINDOW + CODICE OTP
                                        }
                                        cOos.writeInt(0);
                                        System.out.println(user.getUsername() + " has not logged successfully.");
                                    }
                                } 
                                else{
                                    if(
                                        HOTPGeneratorServer.HOTPCheck(user.getOtp(), response.getDongleCounter(), response.getDongleKey(), true)
                                        &&
                                        response.getLargeWindowOtp().compareTo(user.getOtp()) != 0
                                    ) {
                                        //UPDATE USER COUNTER DB + DEATTIVO LA FINESTRA LARGA
                                        cOos.writeInt(1);                                                                    
                                        System.out.println(user.getUsername() + " has logged successfully.");
                                    }
                                    else {
                                        //DEATTIVO FINESTRA LARGA
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