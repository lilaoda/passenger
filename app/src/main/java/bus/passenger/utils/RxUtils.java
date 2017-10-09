package bus.passenger.utils;


import bus.passenger.data.remote.HttpResult;
import bus.passenger.data.remote.ResultFunction;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Liheyu on 2017/9/19.
 * Email:liheyu999@163.com
 */

public class RxUtils {

    public static <T> Observable<T> wrapAsync(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable<T> wrapHttp(Observable<HttpResult<T>> observable) {
        return observable.map(new ResultFunction<T>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
