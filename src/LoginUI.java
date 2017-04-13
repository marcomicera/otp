import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.cert.Certificate;
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
    private final static int    WRAPPER_SPACING = 5;

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
                sendCredentials();
            }
        );

        setStyle();
    }
    
    private void setStyle() {
        title.setFont(Font.font("Arial", 40));
    }
    
    private void sendCredentials() {
        System.setProperty("javax.net.ssl.trustStore", "mySrvKeystore");
        SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();

        try(Socket s = ssf.createSocket("localhost", 8080);
             ObjectOutputStream oout = new ObjectOutputStream(s.getOutputStream());
        ) {
            SSLSession session = ((SSLSocket)s).getSession();
            Certificate[] cchain = session.getPeerCertificates();

            // Prints
            /*System.out.println("The Certificates used by peer");
            for (int i = 0; i < cchain.length; i++)
                System.out.println(((X509Certificate) cchain[i]).getSubjectDN());
            System.out.println("Peer host is " + session.getPeerHost());
            System.out.println("Cipher is " + session.getCipherSuite());
            System.out.println("Protocol is " + session.getProtocol());
            System.out.println("ID is " + new BigInteger(session.getId()));
            System.out.println("Session created in " + session.getCreationTime());
            System.out.println("Session accessed in " + session.getLastAccessedTime());*/

            oout.writeObject("Hello, server!");
        } catch(IOException e) {
            e.printStackTrace(); 
        }
        System.out.println("Message sent.");
    }
    
    public VBox getWrapper() { return wrapper; }
}
