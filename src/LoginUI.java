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
                                PASSWORD_MINIMUM_LENGTH = 1,
                                OTP_LENGTH = 6;
    private final VBox wrapper;
    private final Label title;
    private final Label usernameLabel;
    private TextField usernameField;
    private final Label passwordLabel;
    private PasswordField passwordField;
    private final Label otpLabel;
    private TextField otpField;
    private Button signInButton;
    
    public LoginUI(OTPUI otpUI) {
        wrapper = new VBox(WRAPPER_SPACING);
        
        title = new Label("Home banking login");
        
        usernameLabel = new Label("Username: ");
        usernameField = new TextField();
        passwordLabel = new Label("Password: ");
        passwordField = new PasswordField();
        otpLabel = new Label("OTP: ");
        otpField = new TextField();
        signInButton = new Button("Sign In");
        
        wrapper.getChildren().addAll(   title,
                                        usernameLabel,
                                        usernameField,
                                        passwordLabel,
                                        passwordField,
                                        otpLabel,
                                        otpField,
                                        signInButton
        );
        
        signInButton.setOnAction(
            (ActionEvent ae) -> {
                if( Pattern.matches("\\w{" + USERNAME_MINIMUM_LENGTH + ",}", usernameField.getText())
                    &&
                    Pattern.matches("\\w{" + PASSWORD_MINIMUM_LENGTH + ",}", passwordField.getText())
                    // add otp check!
                ) {
                    sslSend(
                        new UserInfos(
                            usernameField.getText(),
                            passwordField.getText(),
                            Integer.parseInt(otpField.getText())
                        )
                    );
                    
                }
            }
        );

        setStyle();
    }
    
    private void setStyle() {
        title.setFont(Font.font("Arial", 40));
    }
    
    private void sslSend(Object message) {
        System.setProperty("javax.net.ssl.trustStore", "../../serverCertificate");
        SSLSocketFactory sf = (SSLSocketFactory)SSLSocketFactory.getDefault();

        try(SSLSocket s = (SSLSocket)sf.createSocket("localhost", 8080);
            ObjectOutputStream oout = new ObjectOutputStream(s.getOutputStream());
        ) {
            SSLSession session = ((SSLSocket)s).getSession();
            Certificate[] cchain = session.getPeerCertificates();

            oout.writeObject(message);
        } catch(IOException e) {
            e.printStackTrace(); 
        }
        System.out.println("\"" + message + "\" sent.");
    }
    
    public VBox getWrapper() { return wrapper; }
}
