import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class ClientUI {
    public static VBox showUI() {
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
        
        return wrapper;
    }
}
