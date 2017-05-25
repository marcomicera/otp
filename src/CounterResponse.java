import java.io.Serializable;
 
public class CounterResponse implements Serializable {
    private Long dongle_counter;
    private String dongle_key;
    private boolean large_window_on;
    private String large_window_otp;

    protected CounterResponse(Long c, String k, boolean lw_on, String lw_otp) {
        dongle_counter = c;
        dongle_key = k;
        large_window_on = lw_on;
        large_window_otp = lw_otp;
    }
    
    protected CounterResponse(Long c, String k) {
        this(c, k, false, null);
    }

    @Override
    public String toString() {
        return "[counter: " + dongle_counter + " | key: " + dongle_key + "]";
    }
    
    public Long getDongleCounter() { return dongle_counter; }
    public String getDongleKey() { return dongle_key; }
    public boolean getLargeWindowOn() { return large_window_on; }
    public String getLargeWindowOtp() { return large_window_otp; }
}