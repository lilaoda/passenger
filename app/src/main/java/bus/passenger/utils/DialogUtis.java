package bus.passenger.utils;

import android.app.Activity;

import com.bigkoo.pickerview.TimePickerView;

import java.util.Calendar;

/**
 * Created by Lilaoda on 2017/11/1.
 * Email:749948218@qq.com
 */

public class DialogUtis {

    public static TimePickerView createTimePickView(Activity context, TimePickerView.OnTimeSelectListener listener) {
        //显示预约时间 显示三天之内
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.DAY_OF_MONTH, 2);
        return new TimePickerView.Builder(context, listener)
                .setType(new boolean[]{false, true, true, true, true, false})// 默认全部显示
                .setContentSize(15)//滚轮文字大小
                .isCyclic(false)//是否循环滚动
                .setRangDate(startCalendar, endCalendar)//起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
                .isCenterLabel(true)
                .build();
    }
}
