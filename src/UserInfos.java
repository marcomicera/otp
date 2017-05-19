import java.io.Serializable;

class UserInfos implements Serializable {
    private String username;
    private String password;
    private int otp;
    
    protected UserInfos(String u, String p, int o) {
        username = u;
        password = p;
        otp = o;
    }
    
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getOtp() { return otp; }
    
    @Override
    public String toString() {
        return "[user: " + username + " | password: " + password + " | otp: " + otp + "]";
    }
}
