package bus.passenger.bean.param;

/**
 * Created by Lilaoda on 2017/10/9.
 * Email:749948218@qq.com
 */

public class LoginParam {

    private String userName;
    private String password;
    private String accountType;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
