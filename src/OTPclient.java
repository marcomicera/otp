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
    public void start(Stage stage) {
        System.setProperty("javax.net.ssl.trustStore", "mySrvKeystore");
        SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
        
        try(Socket s = ssf.createSocket("localhost", 8080);
             ObjectOutputStream oout = new ObjectOutputStream(s.getOutputStream());
        ) {
            SSLSession session = ((SSLSocket)s).getSession();
            Certificate[] cchain = session.getPeerCertificates();
            
            // Prints
            System.out.println("The Certificates used by peer");
            for (int i = 0; i < cchain.length; i++)
                System.out.println(((X509Certificate) cchain[i]).getSubjectDN());
            System.out.println("Peer host is " + session.getPeerHost());
            System.out.println("Cipher is " + session.getCipherSuite());
            System.out.println("Protocol is " + session.getProtocol());
            System.out.println("ID is " + new BigInteger(session.getId()));
            System.out.println("Session created in " + session.getCreationTime());
            System.out.println("Session accessed in " + session.getLastAccessedTime());
            
            oout.writeObject("Hello, server!");
        } catch(IOException e) {
            e.printStackTrace(); 
        }
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