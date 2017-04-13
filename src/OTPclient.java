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

// Package di sicurezza
// https://docs.oracle.com/javase/7/docs/api/javax/crypto/package-summary.html
import javax.crypto.*;

public class OTPclient extends Application {
    public void start(Stage stage) {
        // Sending an example string
        /*try(Socket s = new Socket("localhost", 8080); // Normal bidirectional socket
            ObjectOutputStream oout = new ObjectOutputStream(s.getOutputStream());
        ) { oout.writeObject("ciao, server!");
        } catch(IOException e) { e.printStackTrace(); }
        System.out.println("Message sent.");*/
        
        //  Spezzoni
        /*private Cipher encrCipher;
        encrCipher.getInstance("DES");
        SecretKey key = KeyGenerator.getInstance("DES").generateKey();
        encrCipher.init(Cipher.ENCRYPT_MODE, key);*/
        
        VBox wrapper = new VBox(40);
        VBox loginInterface = new VBox(5);
        VBox otpInterface = new VBox(5);
        
        // Login interface
        Label loginTitle = new Label("Home banking login");
        loginTitle.setFont(Font.font("Arial", 40));
        Label usernameLabel = new Label("Username: ");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password: ");
        PasswordField passwordField = new PasswordField();
        Button signInButton = new Button("Sign In");
        
        // OTP interface
        Label otpTitle = new Label("OTP dongle");
        otpTitle.setFont(Font.font("Arial", 40));
        TextField otpField = new TextField();
        Button otpButton = new Button("Generate OTP");
        otpButton.setOnAction(
            (ActionEvent ae) -> {
                otpField.setText("ciao");
            }
        );
        
        loginInterface.getChildren().addAll(loginTitle,
                                            usernameLabel,
                                            usernameField,
                                            passwordLabel,
                                            passwordField,
                                            signInButton
        );
        otpInterface.getChildren().addAll(  otpTitle,
                                            otpField,
                                            otpButton
        );
        wrapper.getChildren().addAll(   loginInterface,
                                        otpInterface
        );

        Scene scene = new Scene(wrapper, 500, 500);
        stage.setTitle("One Time Password client interface");
        stage.setScene(scene);
        stage.show();
    }
    
    private int calculateOTP() {
        
        return 1;
    }
}