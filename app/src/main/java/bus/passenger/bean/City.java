package bus.passenger.bean;

/**
 * Created by Lilaoda on 2017/10/19.
 * Email:749948218@qq.com
 */

public class City {


    /**
     * name : 北京市市辖区
     * citycode : 10,110100
     * adcode : 20100
     */

    private String name;
    private String citycode;
    private String adcode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    @Override
    public String toString() {
        return "City{" +
                "name='" + name + '\'' +
                ", citycode='" + citycode + '\'' +
                ", adcode='" + adcode + '\'' +
                '}';
    }
}
