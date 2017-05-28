import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class OTPUI {
    private final static int    WRAPPER_SPACING = 5,
                                BOTTOM_BOX_SPACING = 5;

    private final VBox wrapper;
    private final Label title;
    private final HBox bottom_box;
    private TextField otpField;
    private final Button otpButton;

    public OTPUI() {
        wrapper = new VBox(WRAPPER_SPACING);
        title = new Label("OTP dongle");
        
        bottom_box = new HBox(BOTTOM_BOX_SPACING);
        otpField = new TextField();
        otpButton = new Button("Generate OTP");
        otpButton.setOnAction(
                (ActionEvent ae) -> {
                    otpField.setText("ciao");
                }
        );
        
        bottom_box.getChildren().addAll(otpButton,
                                        otpField
        );

        wrapper.getChildren().addAll(   title,
                                        bottom_box
        );

        otpButton.setOnAction(
                (ActionEvent ae) -> {
                    //Interrompo la generazione di un nuovo codice
                    new Thread("Timer thread on going") {
                        @Override
                        public void run() {
                            otpButton.setDisable(true); // Disattivo tasto generate otp
                            try {
                                Thread.sleep(5000); // Tasto generate otp bloccato per tot secondi
                            } catch (InterruptedException ex) {
                                Logger.getLogger(OTPUI.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            System.out.println("Thread: " + getName() + " running");
                            otpButton.setDisable(false); // Lo riattivo
                        }
                    }.start();

                    String HOTPString ="";
                    try {
                        HOTPGenerator htopgen = new HOTPGenerator(); // A che serve? Non viene mai usato e la classe ha tutti metodi statici
                        //byte[] secret = HOTPGenerator.hexStringToByteArray("14FEA54A019BC73A14FEA54A019BC73A"); //Giustificare perché la dongle key è 16 byte
                        byte[] secret = "L#w3aWò8]ì?ì1kdF".getBytes(); // L#w3aWÃ²8]Ã¬?Ã¬1kdF
                        long movingFactor;
                        movingFactor = HOTPGenerator.getCounter();
                        System.out.println("Valore counter: " + movingFactor);
                        int codeDigits = 6;
                        boolean addChecksum = false;
                        int truncationOffset = 0;
                        HOTPString = HOTPGenerator.generateOTP(secret, movingFactor, codeDigits, addChecksum, truncationOffset);
                        System.out.println("Codice otp generato: " + HOTPString);
                        System.out.println("Movingfactor counter: " + movingFactor);
                        HOTPGenerator.updateCounter(movingFactor + 1);
                    } catch (IOException ex) {
                        Logger.getLogger(OTPUI.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(OTPUI.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvalidKeyException ex) {
                        Logger.getLogger(OTPUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    otpField.setText(HOTPString);
                    
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
                        3) Compute -an HOTP value
                            • Let Snum  = StToNum(Sbits)
                            • Convert S to a number in 0...2^{31}-1
                        4) Return the HOTP value
                            • Return D = Snum mod 10^Digit
                            • D is a number in the range 0...10^{Digit}-1
                
                        X) Practical implementation: https://goo.gl/sANcmD
                     */
                }
        );

        setStyle();
    }

    private void setStyle() {
        title.setFont(Font.font("Arial", 40));
        otpField.setEditable(false);
        otpField.setFont(Font.font("Dialog", 15));
    }

    public VBox getWrapper() {
        return wrapper;
    }
}
