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
                                USERNAME_MINIMUM_LENGTH = 1, //8
                                USERNAME_MAXIMUM_LENGTH = 32,
                                PASSWORD_MINIMUM_LENGTH = 1, //8
                                PASSWORD_MAXIMUM_LENGTH = 32,
                                OTP_LENGTH = 6;
    private final static String FONT = "Arial";
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
                // Checking user inputs (regexp by: https://goo.gl/QPOQVR)
                if( Pattern.matches(
                        "^(?=.{" + USERNAME_MINIMUM_LENGTH + "," + USERNAME_MAXIMUM_LENGTH + "}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$",
                        usernameField.getText()
                    )
                    &&
                    Pattern.matches(
                        "^(?=.{" + PASSWORD_MINIMUM_LENGTH + "," + PASSWORD_MAXIMUM_LENGTH + "}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$", 
                        passwordField.getText()
                    )
                    &&
                    Pattern.matches(
                        "\\d{" + OTP_LENGTH + "}", 
                        otpField.getText()
                    )
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
        title.setFont(Font.font(FONT, 40));
    }
    
    private void sslSend(Object message) {
        System.setProperty("javax.net.ssl.trustStore", "../../localServerCertificate");
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
