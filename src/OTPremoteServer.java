import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;

/*

Algorithm: AES ?
    • Why it is better than DES
Encryption mode: CBC ?
    • Why it is better than ECB
Padding: PKCS#5 ?
    • Why

*/

public class OTPremoteServer extends Application {
    Encryptor encr;
    
    public void start(Stage stage) {
        System.out.println("Remote server started");
        
        try {
            encr = new Encryptor();
            
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
            
        } catch (Exception ex) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        insertUser("giovanni283", "gvn28__2", "@14klL_.,4ifk?ç".getBytes(), Long.MAX_VALUE);
        loginCheck("giovanni283", "gvn28__2");
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
            
            String decryptedPassword = encr.decrypt(rs.getString("password").getBytes()).toString();
            if(decryptedPassword == password)
                System.out.println(username + " logged successfully.");
            else
                System.out.println(username + ": password do not match.");
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        } catch(GeneralSecurityException ex) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
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
                encr.encrypt(password) +
                "\", \"" +
                encr.encrypt(key) +
                "\", \"" +
                encr.encrypt(counter) +
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