package bus.passenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.List;

import bus.passenger.R;
import bus.passenger.bean.City;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class CityLocationAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private List<City> mCitys;
    private LayoutInflater inflater;

    public CityLocationAdapter(Context context, List<City> mCitys) {
        this.inflater = LayoutInflater.from(context);
        this.mCitys = mCitys;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.view_city_head, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text_head);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        holder.text.setText(getLetter(mCitys.get(0)));
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return getLetter(mCitys.get(position));
    }

    private char getLetter(City city) {
        return Pinyin.toPinyin(city.getName().charAt(0)).charAt(0);
    }

    @Override
    public int getCount() {
        return mCitys.size();
    }

    @Override
    public Object getItem(int position) {
        return mCitys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.view_city_item, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(mCitys.get(position).getName());
        return convertView;
    }

    /**
     * 根据字母得到位置，
     *
     * @param letter
     * @return 如果不存在返回-1
     */
    public int getLocationByLetter(String letter) {
        int position = -1;
        for (int i = 0; i < mCitys.size(); i++) {
//            if (TextUtils.equals(mCitys.get(i).getLetter(), letter)) {
//                position = i;
//                break;
//            }
        }
        return position;
    }

    class HeaderViewHolder {
        TextView text;
    }

    class ViewHolder {
        TextView text;
    }

}
