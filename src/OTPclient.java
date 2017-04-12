import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Package di sicurezza
// https://docs.oracle.com/javase/7/docs/api/javax/crypto/package-summary.html
import javax.crypto.*;

public class OTPclient extends Application {
    public void start(Stage stage) {
        // Invio stringa
        try(Socket s = new Socket("localhost", 8080); // Socket normale, bidirezionale
            ObjectOutputStream oout = new ObjectOutputStream(s.getOutputStream());
        ) { oout.writeObject("ciao, server!");
        } catch(IOException e) { e.printStackTrace(); }
        System.out.println("messaggio inviato");
        
        //  Spezzoni
        /*private Cipher encrCipher;
        encrCipher.getInstance("DES");
        SecretKey key = KeyGenerator.getInstance("DES").generateKey();
        encrCipher.init(Cipher.ENCRYPT_MODE, key);*/

        Group root = new Group();
        Scene scene = new Scene(root, 500, 500);
        
        stage.setTitle("One Time Password client interface");
        stage.setScene(scene);
        stage.show();
    }
}