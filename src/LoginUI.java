import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.cert.Certificate;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class LoginUI {
    private final static int    WRAPPER_SPACING = 5,
                                USERNAME_MINIMUM_LENGTH = 1,
                                PASSWORD_MINIMUM_LENGTH = 1;

    private final VBox wrapper;
    private final Label title;
    private final Label usernameLabel;
    private TextField usernameField;
    private final Label passwordLabel;
    private PasswordField passwordField;
    private Button signInButton;
    
    public LoginUI() {
        wrapper = new VBox(WRAPPER_SPACING);
        
        title = new Label("Home banking login");
        
        usernameLabel = new Label("Username: ");
        usernameField = new TextField();
        passwordLabel = new Label("Password: ");
        passwordField = new PasswordField();
        signInButton = new Button("Sign In");
        
        wrapper.getChildren().addAll(   title,
                                        usernameLabel,
                                        usernameField,
                                        passwordLabel,
                                        passwordField,
                                        signInButton
        );
        
        signInButton.setOnAction(
            (ActionEvent ae) -> {
                if( Pattern.matches("\\w{" + USERNAME_MINIMUM_LENGTH + ",}", usernameField.getText())
                    &&
                    Pattern.matches("\\w{" + PASSWORD_MINIMUM_LENGTH + ",}", passwordField.getText())
                ) {
                    sslSend(usernameField.getText());
                    sslSend(passwordField.getText());
                }
            }
        );

        setStyle();
    }
    
    private void setStyle() {
        title.setFont(Font.font("Arial", 40));
    }
    
    private void sslSend(String message) {
        System.setProperty("javax.net.ssl.trustStore", "mySrvKeystore");
        SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();

        try(Socket s = ssf.createSocket("localhost", 8080);
             ObjectOutputStream oout = new ObjectOutputStream(s.getOutputStream());
        ) {
            SSLSession session = ((SSLSocket)s).getSession();
            Certificate[] cchain = session.getPeerCertificates();

            oout.writeObject(message);
        } catch(IOException e) {
            e.printStackTrace(); 
        }
        System.out.println("Message sent.");
    }
    
    public VBox getWrapper() { return wrapper; }
}
