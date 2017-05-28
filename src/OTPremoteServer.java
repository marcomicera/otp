import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

/*  Algorithm: AES
        • Why it is better than DES
    Encryption mode: CBC
        • Why it is better than ECB
    Padding: PKCS#5
        • Why                           */

public class OTPremoteServer extends Application {
    // Cipher
    Encryptor encr;
    
    // Its own address
    private final static int    PORT = 8081;
    
    // Its own certificate
    private final static String CERTIFICATE_NAME = "../../remoteServerCertificate",
                                CERTIFICATE_PASSWORD = "password";
    
    // Remote server certificate
    private final static String LOCAL_SERVER_CERTIFICATE_NAME = "../../localServerCertificate",
                                LOCAL_SERVER_CERTIFICATE_PASSWORD = "password";
    
    public void start(Stage stage) {
        // Imports its own certificate
        System.setProperty("javax.net.ssl.keyStore", CERTIFICATE_NAME);
        System.setProperty("javax.net.ssl.keyStorePassword", CERTIFICATE_PASSWORD);
        
        SSLServerSocketFactory lsSocketFactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
        encr = new Encryptor();
        
        emptyDatabase(); inserts();
        
        /*try {
            System.out.println(new String(encr.encrypt("ciao456michela")));
            System.out.println(new String(encr.encrypt("ciao456michela")));
            System.out.println(new String(encr.encrypt("ciao456michela")));
            System.out.println(new String(encr.encrypt("ciao456michela")));
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        try(SSLServerSocket lsServerSocket = (SSLServerSocket)lsSocketFactory.createServerSocket(PORT)) {
            System.out.println("Remote server started\n");
            while(true) {
                SSLSocket lsSocket = (SSLSocket)lsServerSocket.accept();

                Thread t = new Thread() {
                    public void run() {
                        try(ObjectOutputStream lsOos = new ObjectOutputStream(lsSocket.getOutputStream());
                            ObjectInputStream lsOis = new ObjectInputStream(lsSocket.getInputStream());
                        ) {
                            // Receives user infos
                            UserInfos user = (UserInfos)lsOis.readObject();
                            System.out.println("Received: " + user);
                            
                            // Sends CounterResponse containing dongle_key, dongle_counter and large_window mode
                            // If dongle_key and dongle_counter are null, login was not successful
                                // System.setProperty("javax.net.ssl.trustStore", LOCAL_SERVER_CERTIFICATE_NAME);
                                // System.setProperty("javax.net.ssl.trustStorePassword", LOCAL_SERVER_CERTIFICATE_PASSWORD);
                            CounterResponse reply = loginCheck(
                                user.getUsername(),
                                user.getPassword()
                            );
                            lsOos.writeObject(reply);
                            
                            // Login unsuccessful
                            if(reply.getDongleCounter() == null || reply.getDongleKey() == null) {
                                // Thread terminates
                            }
                            // Login successful
                            else {
                                // Reads localServer response in order to understand
                                // what operations it needs to perform on database
                                CounterResponse response = (CounterResponse)lsOis.readObject();
                                System.out.println("localServer response received. Commands:\n" +
                                    "Dongle counter:\t" + response.getDongleCounter() +
                                    "\nDongle key:\t" + response.getDongleKey() +
                                    "\nLarge window mode:\t" + response.getLargeWindowOn() +
                                    "\nLarge window OTP:\t" + response.getLargeWindowOtp() + "\n"
                                );
                                
                                // User's OTP is in large window
                                if(reply.getLargeWindowOn()) {
                                    // User's OTP is different from the previous one
                                    if(response.getDongleCounter() != null) {
                                        updateCounter(
                                            user.getUsername(),
                                            response.getDongleCounter()
                                        );
                                    }
                                    
                                    // Deactivating large windows for the user
                                    updateLargeWindow(
                                        user.getUsername(),
                                        false,
                                        null
                                    );
                                }
                                // User's OTP is not in large window
                                else {
                                    if(response.getDongleCounter() != null) {
                                        updateCounter(
                                            user.getUsername(),
                                            response.getDongleCounter()
                                        );
                                    }
                                    else if(response.getLargeWindowOn()) {
                                        updateLargeWindow(
                                            user.getUsername(),
                                            true,
                                            response.getLargeWindowOtp()
                                        );
                                    }
                                }
                            }
                        } catch(SocketException se) {
                            if(se.getLocalizedMessage().compareTo("Connection reset") == 0)
                                System.out.println("Local server disconnected");
                            else
                                Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, se);
                        } catch(IOException | ClassNotFoundException ex) {
                            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                };
                        
                t.start();
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public CounterResponse loginCheck(String username, String password) {
        System.out.println("Starting login check");
        
        String query = "";
        
        try(// SSL not used in the bank
            // trovare un modo per far connettere solo questa applicazione al database
            Connection co = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bank?user=root&password=root&autoReconnect=true&useSSL=false"
            );
            Statement st = co.createStatement();
        ) {
            query = "SELECT * FROM users WHERE username = \"" + username + "\"";
            ResultSet rs = st.executeQuery(query);
            rs.next();
            
            String decryptedPassword = new String(encr.decrypt(rs.getString("password")));
            
            if(decryptedPassword.compareTo(password) == 0) {
                String message_to_prompt =
                    username + " logged successfully." +
                    "\nHis/her password:\t" + password +
                    "\nUser's large window mode:\t" + 
                    encr.bytesToInt(encr.decrypt(rs.getString("large_window_on")))
                ;
                    
                System.out.println(
                    message_to_prompt
                );
                
                String read_dongle_counter = rs.getString("dongle_counter");
                String read_dongle_key = rs.getString("dongle_key");
                String read_window_on = rs.getString("large_window_on");
                String read_window_otp = rs.getString("large_window_otp");
                
                boolean test = rs.wasNull();
                System.out.println("read_window_otp = " + test);
                if(test)
                    return new CounterResponse(
                        // dongle_counter
                        encr.bytesToLong(encr.decrypt(read_dongle_counter)),
                        // dongle_key
                        new String(encr.decrypt(read_dongle_key)),
                        // large_window_on
                        (encr.bytesToInt(encr.decrypt(read_window_on)) == 0) ? false : true,
                        // large_window_otp
                        null
                    );
                else
                    return new CounterResponse(
                    // dongle_counter
                    encr.bytesToLong(encr.decrypt(read_dongle_counter)),
                    // dongle_key
                    new String(encr.decrypt(read_dongle_key)),
                    // large_window_on
                    (encr.bytesToInt(encr.decrypt(read_window_on)) == 0) ? false : true,
                    // large_window_otp
                    new String(encr.decrypt(read_window_otp))
                );
                
                /*return new CounterResponse(
                    // dongle_counter
                    encr.bytesToLong(encr.decrypt(read_dongle_counter)),
                    // dongle_key
                    new String(encr.decrypt(read_dongle_key)),
                    // large_window_on
                    (encr.bytesToInt(encr.decrypt(read_window_on)) == 0) ? false : true,
                    // large_window_otp
                        (!rs.wasNull()) ?
                        new String(encr.decrypt(read_window_otp))
                        :
                        null
                );*/
            }
            else {
                System.out.println(username + ": password do not match.\n" +
                    "His/her actual password: " + decryptedPassword +
                    "\nGuessed password: " + password + "\n"
                );
                
                return new CounterResponse(null, null);
            }
        } catch(GeneralSecurityException ex) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new CounterResponse(null, null);
    }
    
    public void insertUser(String username, String password, String k, long counter, boolean lw_on, String lw_otp) {
        byte[] key = k.getBytes();
        String query = "INSERT INTO users VALUES (?, ?, ?, ?, ?, ?);";

        try(// SSL not used in the bank
            // trovare un modo per far connettere solo questa applicazione al database
            Connection co = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bank?user=root&password=root&autoReconnect=true&useSSL=false"
            );
            PreparedStatement ps = co.prepareStatement(query);
        ) {
            ps.setString(1, username);
            ps.setString(2, new String(encr.encrypt(password)));
            ps.setString(3, new String(encr.encrypt(key)));
            ps.setString(4, new String(encr.encrypt(counter)));
            
            // 0 viene sempre criptato nello stesso modo
            // usare AES in CBC mode con IV
            ps.setString(5, new String(encr.encrypt(lw_on)));
            if(lw_otp != null)
                ps.setString(6, new String(encr.encrypt(lw_otp)));
            else
                ps.setNull(6, Types.VARCHAR);

            ps.executeUpdate();
        } catch(SQLException | GeneralSecurityException e) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void updateCounter(String username, long new_counter_value) {
        String query = "UPDATE users SET dongle_counter = ? WHERE username = ?;";

        try(// SSL not used in the bank
            // trovare un modo per far connettere solo questa applicazione al database
            Connection co = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bank?user=root&password=root&autoReconnect=true&useSSL=false"
            );
            PreparedStatement ps = co.prepareStatement(query);
        ) {
            ps.setString(2, username);
            ps.setString(1, new String(encr.encrypt(new_counter_value)));
            ps.executeUpdate();
        } catch(SQLException | GeneralSecurityException e) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void updateLargeWindow(String username, boolean new_lw_value, String lw_otp) {
        String query = "UPDATE users SET large_window_on = ?, large_window_otp = ? WHERE username = ?;";

        try(// SSL not used in the bank
            // trovare un modo per far connettere solo questa applicazione al database
            Connection co = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bank?user=root&password=root&autoReconnect=true&useSSL=false"
            );
            PreparedStatement ps = co.prepareStatement(query);
        ) {
            int i = 1;
            ps.setString(i++, new String(encr.encrypt(new_lw_value)));
            if(new_lw_value)
                ps.setString(i++, new String(encr.encrypt(lw_otp)));
            else
                ps.setNull(i++, Types.VARCHAR);
            ps.setString(i, username);
            
            ps.executeUpdate();
        } catch(SQLException | GeneralSecurityException e) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    // Test
    private void stringEncryptionTest() {
        try {
            // String encrypting and decrypting test
            System.out.println("\n *** String encrypting and decrypting test ***");
            String stringPlainText = "Hello world! 2ws";
            byte[] stringCipherText = encr.encrypt(stringPlainText);
            byte[] decryptedStringCipherText = encr.decrypt(stringCipherText);
            System.out.println("Plaintext:\t\t" + new String(stringPlainText) +
                    "\t\t\tSize:\t" + stringPlainText.length()
            );
            System.out.println("Ciphertext:\t\t" + new String(stringCipherText) +
                    "\tSize:\t" + stringCipherText.length
            );
            System.out.println("Decrypted plaintext:\t" + new String(decryptedStringCipherText) +
                    "\t\t\tSize:\t" + decryptedStringCipherText.length  + "\n"
            );
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Test
    private void longEncryptionTest() {
        try {
            // Long encrypting and decrypting test
            System.out.println("*** Long encrypting and decrypting test ***");
            long longPlainText = Long.MAX_VALUE;
            byte[] longCipherText = encr.encrypt(longPlainText);
            byte[] decryptedLongCipherText = encr.decrypt(longCipherText);
            System.out.println("Plaintext:\t\t" + longPlainText +
                    "\t\tSize:\t" + Long.BYTES
            );
            System.out.println("Ciphertext:\t\t" + encr.bytesToLong(longCipherText) +
                    "\t\tSize:\t" + longCipherText.length
            );
            System.out.println("Decrypted plaintext:\t" + encr.bytesToLong(decryptedLongCipherText) +
                    "\t\tSize:\t" + decryptedLongCipherText.length + "\n"
            );
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Inserts
    private void inserts() {
        insertUser("giovanni283", "gvn28__2", "@14klL_.,4ifk?ç-", 182, false, null);
        insertUser("giovanni.scalzi", "mkde1227.14", "@rk302l.;fXk@è'^", 641, false, null);
        insertUser("giorgio_mariani_71", "ggg1513285", "@.1_ek'30^d-*eò£", 121, false, null);
        insertUser("milianti16", "settembre1999gkv", "5ràd_qò1305^'eG", 17, false, null);
        insertUser("ciccio_tognoli", "palegrete12", "-;3dlrLa.èe*[àae", 45, false, null);
        insertUser("sandr0231", "loppdk3", "2-l£edL+aks;.ck4", 611, false, null);
        insertUser("stefanbotti", "ciao456michela", "L#w3aWò8]ì?ì1kdF", 141, false, null);
        insertUser("claudia-de-santis", "giorgiatiamo46", ".3;4102)$2kEros#", 212, false, null);
        insertUser("bortanzi.filippo", "filip_bici12", "w.1Wlt1-éàçòg4a3", 108, false, null);
    }
    
    // Empties database
    public void emptyDatabase() {
        String query = "DELETE from users WHERE 1;";

        try(// SSL not used in the bank
            // trovare un modo per far connettere solo questa applicazione al database
            Connection co = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bank?user=root&password=root&autoReconnect=true&useSSL=false"
            );
            PreparedStatement ps = co.prepareStatement(query);
        ) {
            ps.executeUpdate();
        } catch(SQLException e) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    // Test
    private void printCertificate(SSLSession session, Certificate[] certificate) {
        /*for (int i = 0; i < certificate.length; i++)
            System.out.println(((X509Certificate)certificate[i]).getSubjectDN());
        System.out.println("Peer host is " + session.getPeerHost());
        System.out.println("Cipher is " + session.getCipherSuite());
        System.out.println("Protocol is " + session.getProtocol());
        System.out.println("ID is " + new BigInteger(session.getId()));
        System.out.println("Session created in " + session.getCreationTime());
        System.out.println("Session accessed in " + session.getLastAccessedTime());*/
    }
}