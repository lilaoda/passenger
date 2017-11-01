package bus.passenger.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import bus.passenger.R;
import bus.passenger.bean.City;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;


/**
 * 全国所有城市必须先进行排序，不然不会按字母进行分组
 */
public class CityAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private List<City> mCitys;
    private LayoutInflater inflater;

    public CityAdapter(Context context, List<City> mCitys) {
        this.inflater = LayoutInflater.from(context);
        this.mCitys = mCitys;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.view_city_head, parent, false);
            holder.textView = (TextView) convertView.findViewById(R.id.text_head);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        holder.textView.setText(mCitys.get(position).getLetter());
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
      return mCitys.get(position).getLetter().charAt(0);
    }

    /**
     * 根据字母得到位置，
     * @param letter
     * @return  如果不存在返回-1
     */
    public int getLocationByLetter(String letter) {
        int position = -1;
        for (int i = 0; i < mCitys.size(); i++) {
            if (TextUtils.equals(mCitys.get(i).getLetter(), letter)) {
                position = i;
                break;
            }
        }
        return position;
    }

    @Override
    public int getCount() {
        return mCitys.size();
    }

    @Override
    public City getItem(int position) {
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
            holder.textView = (TextView) convertView.findViewById(R.id.text_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(mCitys.get(position).getName());
        return convertView;
    }

    static class HeaderViewHolder {
        TextView textView;
    }

    static class ViewHolder {
        TextView textView;
    }

}
