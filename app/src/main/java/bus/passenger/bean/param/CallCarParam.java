package bus.passenger.bean.param;

/**
 * Created by Lilaoda on 2017/10/9.
 * Email:749948218@qq.com
 * 叫车 上传的参数
 */

public class CallCarParam {

    /**
     * appid : string
     * carType : string
     * destAddress : string
     * destLat : 0
     * destLng : 0
     * mapType : 0
     * originAddress : string
     * originBuscircleUuid : string
     * originCityUuid : string
     * originLat : 0
     * originLng : 0
     * source : 0
     * typeTime : 0
     */

   /*
     * 需要车辆类型，0：舒适型（默认），1：商务型，2:豪华型
     */
    private String carType;

    /**
     * "yyyy-MM-dd HH:mm:ss"
     */
    private String appointTime;

    /*
     * 出发纬度
     */
    private double originLat;

    /*
     * 出发经度
     */
    private double originLng;

    /*
     * 出发地址
     */
    private String originAddress;

    /*
     * 目的经度
     */
    private double destLng;

    /*
     * 目的维度
     */
    private double destLat;

    /*
     * 目的地地址
     */
    private String destAddress;

    /*
     * 订单类型（时效性）：1 实时订单， 2 预约订单
     */
    private int typeTime;

    /*
     * 订单来源：1 APP移动端；2 微信公众号；3 电话叫车；4 pc网站;5小程序
     */
    private int source;

    /*
     * appid
     */
    private String appid;


    /**
     * 使用导航地图类型：0为高德、1百度、2谷歌、3其他
     */
    private Integer mapType;

    /**
     * 出发城市uuid
     */
    private String originCityUuid;

    /**
     * 出发商圈uuid
     */
    private String originBuscircleUuid;


    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getDestAddress() {
        return destAddress;
    }

    public void setDestAddress(String destAddress) {
        this.destAddress = destAddress;
    }

    public double getDestLat() {
        return destLat;
    }

    public void setDestLat(double destLat) {
        this.destLat = destLat;
    }

    public double getDestLng() {
        return destLng;
    }

    public void setDestLng(double destLng) {
        this.destLng = destLng;
    }

    public int getMapType() {
        return mapType;
    }

    public void setMapType(int mapType) {
        this.mapType = mapType;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    public String getOriginBuscircleUuid() {
        return originBuscircleUuid;
    }

    public void setOriginBuscircleUuid(String originBuscircleUuid) {
        this.originBuscircleUuid = originBuscircleUuid;
    }

    public String getOriginCityUuid() {
        return originCityUuid;
    }

    public void setOriginCityUuid(String originCityUuid) {
        this.originCityUuid = originCityUuid;
    }

    public double getOriginLat() {
        return originLat;
    }

    public void setOriginLat(double originLat) {
        this.originLat = originLat;
    }

    public double getOriginLng() {
        return originLng;
    }

    public void setOriginLng(double originLng) {
        this.originLng = originLng;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getTypeTime() {
        return typeTime;
    }

    public void setTypeTime(int typeTime) {
        this.typeTime = typeTime;
    }

    public String getAppointTime() {
        return appointTime;
    }

    public void setAppointTime(String appointTime) {
        this.appointTime = appointTime;
    }

    public void setMapType(Integer mapType) {
        this.mapType = mapType;
    }
}
