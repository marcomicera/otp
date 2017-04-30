import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;
import javax.crypto.*; // https://goo.gl/VtJyhY


public class OTPremoteServer extends Application {
    private static final String ENCR_ALGORITHM = "AES";
    private Cipher encrCipher;
    
    public void start(Stage stage) {
        System.out.println("Remote server started");
        
        /**/String username = "beppe89";

        try {
            encrCipher.getInstance(ENCR_ALGORITHM);
            SecretKey key = KeyGenerator.getInstance(ENCR_ALGORITHM).generateKey();
            
            // Encryption
            encrCipher.init(Cipher.ENCRYPT_MODE, key);
            System.out.println(encrCipher.doFinal(username.getBytes()));
        } catch(Exception e) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, e);
        }
        
        
    }
}