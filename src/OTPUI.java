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
        
        otpButton.setOnAction(
            (ActionEvent ae) -> {
                // HOTP generation function (to be put in a dedicated class)
                /* RFC 4226 http://www.ietf.org/rfc/rfc4226.txt
                    5.1.  Notation and Symbols
                        • HOTP must be a numeric-only 6-digit value
                        • 8-byte counter value
                    5.3.  Generating an HOTP Value
                        1) Generate an HMAC-SHA-1 value 
                            • Let HS = HMAC-SHA-1(K,C)
                            • HS is a 20-byte string
                        2) Generate a 4-byte string (Dynamic Truncation)
                            • Let Sbits = DT(HS)
                            • DT, defined below, returns a 31-bit string
                        3) Compute an HOTP value
                            • Let Snum  = StToNum(Sbits)
                            • Convert S to a number in 0...2^{31}-1
                        4) Return the HOTP value
                            • Return D = Snum mod 10^Digit
                            • D is a number in the range 0...10^{Digit}-1
                
                // Practical implementation: https://goo.gl/sANcmD
                */
            }
        );
        
        setStyle();
    }
    
    private void setStyle() {
        title.setFont(Font.font("Arial", 40));
    }
    
    public VBox getWrapper() { return wrapper; }
}
