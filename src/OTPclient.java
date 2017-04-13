import java.io.IOException;
import java.io.ObjectOutputStream;
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
import javax.net.ssl.*;

// Package di sicurezza
// https://docs.oracle.com/javase/7/docs/api/javax/crypto/package-summary.html
import javax.crypto.*;

public class OTPclient extends Application {
    public void start(Stage stage) {
        // Sending an example string
        SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try(Socket s = sf.createSocket("localhost", 8080); // Normal bidirectional socket
            ObjectOutputStream oout = new ObjectOutputStream(s.getOutputStream());
        ) { oout.writeObject("ciao, server!");
        } catch(IOException e) { e.printStackTrace(); }
        System.out.println("Message sent.");
        
        // Ciphers code blocks
        /*private Cipher encrCipher;
        encrCipher.getInstance("DES");
        SecretKey key = KeyGenerator.getInstance("DES").generateKey();
        encrCipher.init(Cipher.ENCRYPT_MODE, key);*/
        
        Scene scene = new Scene(ClientUI.showUI(), 500, 500);
        stage.setTitle("One Time Password client interface");
        stage.setScene(scene);
        //stage.show();
    }
}