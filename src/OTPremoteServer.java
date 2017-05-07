import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class OTPremoteServer extends Application {
    private static final String ENCR_ALGORITHM = "AES"; // blocks of 16B
    private Cipher encrCipher;
    private SecretKey key;
    
    public void start(Stage stage) {
        System.out.println("Remote server started");
        
        initializeCipher();
        
        // Utenti di prova già inseriti
        /*insertUser("giovanni862", "camaleonte9", "4@sdaàwq#5".getBytes(), 0);
        insertUser("ste778", "ponte73", "@ads_wqe".getBytes(), 2);
        insertUser("giginoto", "a32stro", "1221#]_qwe".getBytes(), 0);*/
        
        loginCheck("giovanni862", "camaleonte9");
        loginCheck("giovanni862", "camaleont9");
        
        /*try {
            // Prints infos
            System.out.println("What has been crypted: " + username);
            for(int i = 0; i < encr_username.length; ++i)
                System.out.println(new Byte(encr_username[i]));
            System.out.println("Encrypted message: " + encr_username);
            System.out.println("Encrypted message length: " + encr_username.length);
            
            //loginCheck("username_prova", "password_prova");
        } catch(Exception e) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, e);
        }*/
    }
    
    private void initializeCipher() {
        try {
            encrCipher = encrCipher.getInstance(ENCR_ALGORITHM);
            key = KeyGenerator.getInstance(ENCR_ALGORITHM).generateKey();
            encrCipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
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
            query = "SELECT * FROM users WHERE username = \"" +
                encrCipher.doFinal(username.getBytes()) +
                "\""
            ;
        
            ResultSet rs = st.executeQuery(query);
            
            rs.next();
            System.out.println(rs.getString("password"));
            
            /*if(encrCipher.doFinal(password.getBytes()) == rs.getString("password").getBytes())
                System.out.println(username + " logged successfully.");
            else
                System.out.println(username + ": password do not match.");*/
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void insertUser(String username, String password, byte[] key, long counter) {
        String query = "";

        // long to byte[] conversion
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(counter);
        
        try {
            query =
                "INSERT INTO users VALUES (\"" +
                encrCipher.doFinal(username.getBytes()) +
                "\", \"" +
                encrCipher.doFinal(password.getBytes()) +
                "\", \"" +
                encrCipher.doFinal(key) +
                "\", \"" +
                encrCipher.doFinal(buffer.array()) +
                "\");"
            ;
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
}