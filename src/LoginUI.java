import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class LoginUI {
    // Local server address
    private final static String LOCAL_SERVER_ADDRESS = "localhost";
    private final static int    LOCAL_SERVER_PORT = 8080;
    
    // Local server certificate
    private final static String LOCAL_SERVER_CERTIFICATE_NAME = "../../localServerCertificate",
                                LOCAL_SERVER_CERTIFICATE_PASSWORD = "password";
    
    // Style
    private final static int    WRAPPER_SPACING = 5,
                                BOTTOM_BOX_SPACING = 267,
                                USERNAME_MINIMUM_LENGTH = 1, //8
                                USERNAME_MAXIMUM_LENGTH = 32,
                                PASSWORD_MINIMUM_LENGTH = 1, //8
                                PASSWORD_MAXIMUM_LENGTH = 32,
                                OTP_LENGTH = 6;
    private final static String FONT = "Arial";

    // UI elements
    private final VBox wrapper;
    private final Label title;
    private final Label usernameLabel;
    private TextField usernameField;
    private final Label passwordLabel;
    private PasswordField passwordField;
    private final Label otpLabel;
    private TextField otpField;
    private final HBox bottom_box;
    private Text dialog;
    private Button signInButton;
    
    public LoginUI(OTPUI otpUI) {
        wrapper = new VBox(WRAPPER_SPACING);
        title = new Label("Home banking login");
        usernameLabel = new Label("Username: ");
        usernameField = new TextField("stefanbotti");
        passwordLabel = new Label("Password: ");
        passwordField = new PasswordField();
        passwordField.setText("ciao456michela");
        otpLabel = new Label("OTP: ");
        otpField = new TextField();
            
        bottom_box = new HBox(BOTTOM_BOX_SPACING);
        signInButton = new Button("Sign In");
        dialog = new Text();
        
        bottom_box.getChildren().addAll(signInButton,
                                        dialog
        );
        
        wrapper.getChildren().addAll(   title,
                                        usernameLabel,
                                        usernameField,
                                        passwordLabel,
                                        passwordField,
                                        otpLabel,
                                        otpField,
                                        bottom_box
        );
        
        signInButton.setOnAction(
            (ActionEvent ae) -> {
                if(validInputs()) // Checking user inputs 
                    sendUserInfos();
            }
        );

        setStyle();
    }
    
    private void setStyle() {
        title.setFont(Font.font(FONT, 40));
        dialog.setTextAlignment(TextAlignment.RIGHT);
    }
    
    public void updateDialog(String s, Color c) {
        dialog.setText(s);
        dialog.setFill(c);
    }
    
    private boolean validInputs() {
        // Regexp by: https://goo.gl/QPOQVR
        return  Pattern.matches(
                    "^(?=.{" + USERNAME_MINIMUM_LENGTH + 
                        "," + USERNAME_MAXIMUM_LENGTH + 
                        "}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$",
                    usernameField.getText()
                )
                &&
                Pattern.matches(
                    "^(?=.{" + PASSWORD_MINIMUM_LENGTH + 
                        "," + PASSWORD_MAXIMUM_LENGTH + 
                        "}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$", 
                    passwordField.getText()
                )
                &&
                Pattern.matches(
                    "\\d{" + OTP_LENGTH + "}", 
                    otpField.getText()
                );
    }
    
    private void sendUserInfos() {
        // Imports local server's certificate
        System.setProperty("javax.net.ssl.trustStore", LOCAL_SERVER_CERTIFICATE_NAME);
        System.setProperty("javax.net.ssl.trustStorePassword", LOCAL_SERVER_CERTIFICATE_PASSWORD);

        SSLSocketFactory lsSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        try(SSLSocket lsSocket = (SSLSocket)lsSocketFactory.createSocket(LOCAL_SERVER_ADDRESS, LOCAL_SERVER_PORT);
            ObjectOutputStream lsOos = new ObjectOutputStream(lsSocket.getOutputStream());
            ObjectInputStream lsOis = new ObjectInputStream(lsSocket.getInputStream());
        ) {
            // Sends user infos
            UserInfos infos = new UserInfos(
                usernameField.getText(),
                passwordField.getText(),
                otpField.getText()
            );
            lsOos.writeObject(infos);
            //System.out.println("\"" + infos + "\" sent.");

            // Receives a reply
            int reply = lsOis.readInt();
            if(reply == 1)
                updateDialog("Logged in successfully", Color.GREEN);
            else
                updateDialog("Login error: please try again", Color.RED);
        } catch(IOException e) {
            e.printStackTrace(); 
        }
    }
    
    public VBox getWrapper() { return wrapper; }
}
