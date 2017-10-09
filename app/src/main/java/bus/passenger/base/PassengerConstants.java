package bus.passenger.base;


public class PassengerConstants {

    public static final int STATUSBAR_ALPHA = 30;

    //    缓存目录名称及大小
    public static final String CACHE_FILE_NAME = "file_cache";
    public static final long cacheFileSize = 1024 * 1024 * 100;

    //个人信息中，修改昵称，公司名，职业，设置邮箱，INTENT传递的KEy,及SP写入的值，联网上传的值
    public static final String EMAIL = "email";
    public static final String NICK_NAME = "name";
    public static final String COMPANY_NAME = "company_name";
    public static final String OCCUPATION = "occupation";
    public static final String PHONE = "phone";

    public static final String token = "token";
    public static final String password = "password";
    public static final String sid = "sid";
    public static final String sign = "sign";
    public static final String timestamp = "timestamp";

    /**
     * 使用导航地图类型：0为高德、1百度、2谷歌、3其他
     */
    public static final int MAP_TYPE_GAODE = 0;
    public static final int MAP_TYPE_BAIDU = 1;
    public static final int MAP_TYPE_GOOGLE = 2;
    public static final int MAP_TYPE_OTHER = 3;

}

