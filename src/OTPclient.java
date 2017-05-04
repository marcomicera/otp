import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class OTPclient extends Application {
    private ClientUI ui;
    
    public void start(Stage stage) throws IOException{
        ui = new ClientUI();
      /*
        try {
            HOTPGenerator htopgen = new HOTPGenerator();
            String HOTPString;
            byte[] secret = HOTPGenerator.hexStringToByteArray("18732149");
            long movingFactor = HOTPGenerator.getCounter();
            System.out.println("Valore counter: " + movingFactor);
            int codeDigits = 6;
            boolean addChecksum = false;
            int truncationOffset = 0;
            HOTPString = HOTPGenerator.generateOTP(secret, movingFactor, codeDigits, addChecksum, truncationOffset);
            System.out.println("Codice otp generato: " + HOTPString);
            System.out.println("Movingfactor counter: " + movingFactor);
            HOTPGenerator.updateCounter((int) (movingFactor + 1));
        }catch (InvalidKeyException | NoSuchAlgorithmException ex) {
            Logger.getLogger(OTPclient.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        stage.setTitle("One Time Password client interface");
        stage.setScene(new Scene(ui.getWrapper(), 500, 500));
        stage.show();
    }
    


}
