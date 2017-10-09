package bus.passenger.bean.param;

/**
 * Created by Lilaoda on 2017/10/9.
 * Email:749948218@qq.com
 */

public class RegistParam {

    /**
     * accountType : 0
     * appVersion : string
     * currentCityUuid : string
     * deviceImei : string
     * deviceModel : string
     * deviceType : 0
     * face : string
     * mobile : string
     * nickName : string
     * password : string
     * sex : 0
     * userAccount : string
     */

    private int accountType;
    private String appVersion;
    private String currentCityUuid;
    private String deviceImei;
    private String deviceModel;
    private int deviceType;
    private String face;
    private String mobile;
    private String nickName;
    private String password;
    private int sex;
    private String userAccount;

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getCurrentCityUuid() {
        return currentCityUuid;
    }

    public void setCurrentCityUuid(String currentCityUuid) {
        this.currentCityUuid = currentCityUuid;
    }

    public String getDeviceImei() {
        return deviceImei;
    }

    public void setDeviceImei(String deviceImei) {
        this.deviceImei = deviceImei;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }
}
