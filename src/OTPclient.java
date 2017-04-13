import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.net.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

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