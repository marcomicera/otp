import java.io.Serializable;

public class counterResponse {
    private long dongle_counter;
    private byte[] dongle_key;
    
    protected counterResponse(long c, byte[] k) {
        dongle_counter = c;
        dongle_key = k;
    }
}
