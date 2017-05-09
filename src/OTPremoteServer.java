import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

/*

Algorithm: AES ?
    • Why it is better than DES
Encryption mode: CBC ?
    • Why it is better than ECB
Padding: PKCS#5 ?
    • Why

*/

public class OTPremoteServer extends Application {
    
    //private Cipher encrCipher;
    
    
    public void start(Stage stage) {
        System.out.println("Remote server started");
        
       
        // Utenti di prova già inseriti
        //insertUser("giovanni862", "camaleonte9", "4@sdaàwq#5".getBytes(), 0);
        //insertUser("ste778", "ponte73", "@ads_wqe".getBytes(), 2);
        //insertUser("giginoto", "a32stro", "1221#]_qwe".getBytes(), Long.MAX_VALUE);
        
        //loginCheck("giovanni862", "camaleonte9");
        //loginCheck("giovanni862", "camaleont9");
    }
    
    /*private void initializeCipher() {
        try {
            encrCipher = encrCipher.getInstance(ENCR_ALGORITHM);
            key = KeyGenerator.getInstance(ENCR_ALGORITHM).generateKey();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    
    /*private byte[] encrypt(Object data) {
        byte[] encrypted_data = null;
        
        try {
            encrCipher.init(Cipher.ENCRYPT_MODE, key);
            encrypted_data = encrCipher.doFinal(serialize(data));
        } catch(InvalidKeyException | IllegalBlockSizeException | IOExceptio/ | BadPaddingException ex) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return encrypted_data;
    }*/
    
    /*public void loginCheck(String username, String password) {
        String query = "";
        
        try(// SSL not used in the bank
            // trovare un modo per far connettere solo questa applicazione al database
            Connection co = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bank?user=root&password=root&autoReconnect=true&useSSL=false"
            );
            Statement st = co.createStatement();
        ) {
            //System.out.println(encrCipher.doFinal(username.getBytes()));
            query = "SELECT * FROM users WHERE username = \"" +
                encrypt(username) +
                "\""
            ;
        
            ResultSet rs = st.executeQuery(query);
            
            rs.next();
            //System.out.println(rs.getString("password"));
            
            if(encrCipher.doFinal(password.getBytes()) == rs.getString("password").getBytes())
                System.out.println(username + " logged successfully.");
            else
                System.out.println(username + ": password do not match.");
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    
    /*public void insertUser(String username, String password, byte[] key, long counter) {
        String query = "";

        // long to byte[] conversion
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(counter);
        
        try {
            query =
                "INSERT INTO users VALUES (\"" +
                encrypt(username) +
                "\", \"" +
                encrypt(password) +
                "\", \"" +
                encrypt(key) +
                "\", \"" +
                encrypt(counter) +
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
    }*/
    
    // from Object to byte[]
    /*private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }*/

    // from byte[] to Object
    /*private static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }*/
}