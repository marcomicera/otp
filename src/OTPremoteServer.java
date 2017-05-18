import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    Encryptor encr;
    
    public void start(Stage stage) {
        // Imports its own certificate
        System.setProperty("javax.net.ssl.keyStore", "../../remoteServerCertificate");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");
        
        SSLServerSocketFactory sf = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
        encr = new Encryptor();
        
        try(SSLServerSocket ss = (SSLServerSocket)sf.createServerSocket(8081)) {
            System.out.println("Remote server started");
            while(true) {
                SSLSocket s = (SSLSocket)ss.accept();

                Thread t = new Thread() {
                    public void run() {
                        try(ObjectInputStream ois = new ObjectInputStream(s.getInputStream())) {
                            SSLSession session = ((SSLSocket)s).getSession();
                            Certificate[] cchain2 = session.getLocalCertificates();
                            
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
    
    public void insertUser(String username, String password, byte[] key, long counter) {
        String query = "";
        byte[] counterBytes = encr.longToBytes(counter); 
        
        try {
            query =
                "INSERT INTO users VALUES (\"" +
                username +
                "\", \"" +
                new String(encr.encrypt(password)) +
                "\", \"" +
                new String(encr.encrypt(key)) +
                "\", \"" +
                new String(encr.encrypt(counter)) +
                "\");"
            ;
        } catch(GeneralSecurityException ex) { 
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        /**/System.out.println(query);
        
        try(// SSL not used in the bank
            // trovare un modo per far connettere solo questa applicazione al database
            Connection co = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bank?user=root&password=root&autoReconnect=true&useSSL=false"
            );
            Statement st = co.createStatement();
        ) {
            st.executeUpdate(query);
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void loginCheck(String username, String password) {
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
            /**/System.out.println("Decrypted " + username + "'s password: " + decryptedPassword);
            
            if(decryptedPassword.compareTo(password) == 0)
                System.out.println(username + " logged successfully.");
            else
                System.out.println(username + ": password do not match.");
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        } catch(GeneralSecurityException ex) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Test
    private void stringEncryptionTest() {
        /*try {
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
        }*/
    }
    
    // Test
    private void longEncryptionTest() {
        /*try {
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
        }*/
    }
    
    // Inserts
    private void inserts() {
        /*insertUser("giovanni283", "gvn28__2", "@14klL_.,4ifk?ç-".getBytes(), 182);
        insertUser("giovanni.scalzi", "mkde1227.14", "@rk302l.;fXk@è'^".getBytes(), 641);
        insertUser("giorgio_mariani_71", "ggg1513285", "@.1_ek'30^d-*eò£".getBytes(), 121);
        insertUser("milianti16", "settembre1999gkv", "5ràd_qò1305^'eG".getBytes(), 17);
        insertUser("ciccio_tognoli", "palegrete12", "-;3dlrLa.èe*[àae".getBytes(), 45);
        insertUser("sandr0231", "loppdk3", "2-l£edL+aks;.ck4".getBytes(), 611);
        insertUser("stefanbotti", "ciao456michela", "L#w3aWò8]ì?ì1kdF".getBytes(), 141);
        insertUser("claudia-de-santis", "giorgiatiamo46", ".3;4102)$2kEros#".getBytes(), 212);
        insertUser("bortanzi.filippo", "filip_bici12", "w.1Wlt1-éàçòg4a3".getBytes(), 108);*/
    }
}