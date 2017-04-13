import javafx.scene.layout.VBox;

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
