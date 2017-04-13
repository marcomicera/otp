import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class OTPUI {
    private final static int    WRAPPER_SPACING = 5;
    
    private final VBox wrapper;
    private final Label title;
    private TextField otpField;
    private final Button otpButton;
    
    public OTPUI() {
        wrapper = new VBox(WRAPPER_SPACING);
        title = new Label("OTP dongle");
        otpField = new TextField();
        otpButton = new Button("Generate OTP");
        otpButton.setOnAction(
            (ActionEvent ae) -> {
                otpField.setText("ciao");
            }
        );
        
        wrapper.getChildren().addAll(   title,
                                        otpField,
                                        otpButton
        );
        
        setStyle();
    }
    
    private void setStyle() {
        title.setFont(Font.font("Arial", 40));
    }
    
    public VBox getWrapper() { return wrapper; }
}
