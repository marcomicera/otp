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

    /*protected void setUsername(String username) { this.username = username; }
    protected void setPassword(String password) { this.password = password; }
    protected void setOtp(int otp) { this.otp = otp; }*/
    
    // to be deleted in the future
    /**/public String getUsername() { return username; }
    /**/public String getPassword() { return password; }
    /**/public int getOtp() { return otp; }
}
