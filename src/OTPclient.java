import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Package di sicurezza
// https://docs.oracle.com/javase/7/docs/api/javax/crypto/package-summary.html
import javax.crypto.*;

public class OTPclient extends Application {
    private ClientUI ui;
    
    public void start(Stage stage) {
        ui = new ClientUI();
        
        stage.setTitle("One Time Password client interface");
        stage.setScene(new Scene(ui.getWrapper(), 500, 500));
        stage.show();
    }
}

// Useful code snippets for ciphers
/*private Cipher encrCipher;
encrCipher.getInstance("DES");
SecretKey key = KeyGenerator.getInstance("DES").generateKey();
encrCipher.init(Cipher.ENCRYPT_MODE, key);*/