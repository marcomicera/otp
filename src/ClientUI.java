import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class ClientUI {
    private final static int    WRAPPER_SPACING = 40;
    
    private final VBox wrapper;
    private final LoginUI loginUI;
    private final OTPUI otpUI;
    
    public ClientUI() {
        wrapper = new VBox(WRAPPER_SPACING);
        loginUI = new LoginUI();
        otpUI = new OTPUI();
        
        wrapper.getChildren().addAll(   loginUI.getWrapper(),
                                        otpUI.getWrapper()
        );
    }
   
    public VBox getWrapper() { return wrapper; }
}
