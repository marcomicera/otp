import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class OTPremoteServer extends Application {
    private static final String ENCR_ALGORITHM = "AES"; // blocks of 16B
    private Cipher encrCipher;
    
    public void start(Stage stage) {
        System.out.println("Remote server started");
        
        /**/String username = "beppe89"; // 7 chars long, 7B

        try {
            encrCipher = encrCipher.getInstance(ENCR_ALGORITHM);
            SecretKey key = KeyGenerator.getInstance(ENCR_ALGORITHM).generateKey();
            
            // Encryption
            encrCipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encr_username = encrCipher.doFinal(username.getBytes());
            
            System.out.println("What has been crypted: " + username);
            
            for(int i = 0; i < encr_username.length; ++i)
                System.out.println(new Byte(encr_username[i]));
            
            System.out.println("Encrypted message: " + encr_username);
            System.out.println("Encrypted message length: " + encr_username.length);
        } catch(Exception e) {
            Logger.getLogger(OTPremoteServer.class.getName()).log(Level.SEVERE, null, e);
        }
        
        
    }
}