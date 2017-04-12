import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class OTPserver extends Application {
    public void start(Stage stage) {
        try(ServerSocket servs = new ServerSocket(8080, 7)) { // Socket di accettazione, bidirezionale
            while(true) { 
                try( Socket s = servs.accept(); // Socket normale 
                     ObjectInputStream oin = new ObjectInputStream(s.getInputStream());
                   ) { System.out.println("Ricevuto: " + oin.readObject()); }
                Thread.sleep(3000); // elaborazione
            }
        } catch(Exception e) { e.printStackTrace(); }

        Group root = new Group();
        Scene scene = new Scene(root, 500, 500);

        stage.setTitle("One Time Password server interface");
        stage.setScene(scene);
        stage.show();
    }
}
