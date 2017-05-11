import java.nio.charset.StandardCharsets;
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
    
    public void start(Stage stage) {
        System.out.println("Remote server started");
        
        try {
            byte[] plainText = "Hello world!".getBytes();
            Encryptor encr = new Encryptor();
            byte[] cipherText = encr.encrypt(plainText);
            byte[] decryptedCipherText = encr.decrypt(cipherText);
            
            System.out.println(new String(plainText));
            System.out.println(new String(cipherText));
            System.out.println(new String(decryptedCipherText));
        } catch (Exception ex) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
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