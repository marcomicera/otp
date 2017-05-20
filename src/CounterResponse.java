import java.io.Serializable;

public class CounterResponse implements Serializable {
    private Long dongle_counter;
    private String dongle_key;
    
    protected CounterResponse(Long c, String k) {
        dongle_counter = c;
        dongle_key = k;
    }
    
    @Override
    public String toString() {
        return "[counter: " + dongle_counter + " | key: " + dongle_key + "]";
    }
    
    public Long getDongleCounter() { return dongle_counter; }
    public String getDongleKey() { return dongle_key; }
}
