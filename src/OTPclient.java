import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class OTPclient extends Application {
    private ClientUI ui;
    
    public void start(Stage stage) throws IOException{
        ui = new ClientUI();
     
        stage.setTitle("One Time Password client interface");
        stage.setScene(new Scene(ui.getWrapper(), 500, 500));
        stage.show();
    }
}
