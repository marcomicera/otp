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
                    otpField.setText("");
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
                                Thread.sleep(5000); // Tasto generate otp bloccato per 5 secondi
                            } catch (InterruptedException ex) {
                                Logger.getLogger(OTPUI.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            System.out.println("Thread: " + getName() + " running");
                            otpButton.setDisable(false); // Lo riattivo
                        }
                    }.start();

                    String HOTPString ="";
                    try {
                        byte[] secret = "L#w3aWò8]ì?ì1kdF".getBytes(); //Dongle Key hardwritten for stefanbotti
                        long movingFactor;
                        movingFactor = HOTPGenerator.getCounter();
                        System.out.println("Valore counter: " + movingFactor);
                        int codeDigits = 6;
                        int truncationOffset = 0;
                        HOTPString = HOTPGenerator.generateOTP(secret, movingFactor, codeDigits, truncationOffset);
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
