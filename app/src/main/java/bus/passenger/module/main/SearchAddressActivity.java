package bus.passenger.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import bus.passenger.R;
import bus.passenger.adapter.CityAdapter;
import bus.passenger.adapter.SearchAdapter;
import bus.passenger.base.Constants;
import bus.passenger.bean.City;
import bus.passenger.bean.PoiInfo;
import bus.passenger.data.AMapManager;
import bus.passenger.data.SpManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lhy.lhylibrary.base.LhyActivity;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.utils.CommonUtils;
import lhy.lhylibrary.utils.ToastUtils;
import lhy.lhylibrary.view.SideLetterBar;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static bus.passenger.utils.RxUtils.wrapAsync;

public class SearchAddressActivity extends LhyActivity {

    public static final String RESULT_ADDRESS = "result_address";

    @BindView(R.id.text_city)
    TextView textCity;
    @BindView(R.id.edit_input)
    EditText editInput;
    @BindView(R.id.ibt_clear)
    ImageButton ibtClear;
    @BindView(R.id.Ll_loading)
    LinearLayout LlLoading;
    @BindView(R.id.text_error)
    TextView textError;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.letterBar)
    SideLetterBar letterBar;
    @BindView(R.id.head_list_view)
    StickyListHeadersListView headListView;
    @BindView(R.id.tv_letter_overlay)
    TextView tvLetterOverlay;
    @BindView(R.id.rl_city)
    RelativeLayout rlCity;
    @BindView(R.id.ll_content)
    LinearLayout llContent;

    private List<PoiInfo> mHistoryList;
    private SearchAdapter mAdapter;
    private SpManager mSpManager;
    private AMapManager mAMapManager;
    private City mSelectedCity;
    private View mClearHistoryHeadView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);
        ButterKnife.bind(this);
        mSpManager = SpManager.instance();
        mAMapManager = AMapManager.instance();
        initCity();
        initView();
        initCityView();
        intListener();
    }

    private void intListener() {
        editInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    ibtClear.setVisibility(View.VISIBLE);
                    queryAddress(s.toString().trim());
                } else {
                    ibtClear.setVisibility(View.GONE);
                    showHistoryView();
                }
            }
        });
    }

    private void showHistoryView() {
        mAdapter.setNewData(mHistoryList);
        if (mHistoryList == null || mHistoryList.size() == 0) {
            mClearHistoryHeadView.setVisibility(View.GONE);
        }else {
            mClearHistoryHeadView.setVisibility(View.VISIBLE);
        }
    }

    private void showResultView(List<PoiInfo> list) {
        mClearHistoryHeadView.setVisibility(View.GONE);
        mAdapter.setNewData(list);
    }

    private void initView() {
        LlLoading.setVisibility(View.GONE);
        textError.setVisibility(View.GONE);
        rlCity.setVisibility(View.GONE);
        mHistoryList = mSpManager.getHistoryAddress();
        mAdapter = new SearchAdapter(R.layout.item_search_address, null);
        View headView = View.inflate(this, R.layout.item_search_address_head, null);
        mClearHistoryHeadView = View.inflate(this, R.layout.item_history_address_head, null);
        mClearHistoryHeadView.findViewById(R.id.btn_clear_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHistoryList = new ArrayList<>();
                mSpManager.putHistoryAddress(mHistoryList);
                showHistoryView();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);
        mAdapter.addHeaderView(headView);
        mAdapter.addHeaderView(mClearHistoryHeadView);
        mAdapter.setHeaderAndEmpty(true);
        mAdapter.setEmptyView(R.layout.item_search_address_empty, recyclerView);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                PoiInfo poiInfo = mAdapter.getData().get(position);
                saveSearchHistory(poiInfo);
                setAddressResult(poiInfo);
            }
        });
        showHistoryView();
    }

    //初始化城市为当前定位城市
    private void initCity() {
//        City selectedCity = mSpManager.getSelectedCity();
//        if (selectedCity != null) {
//            mSelectedCity = selectedCity;
//        } else
        if (getIntent().hasExtra(Constants.CITY_INFO)) {
            PoiInfo poiInfo = getIntent().getParcelableExtra(Constants.CITY_INFO);
            mSelectedCity = new City();
            mSelectedCity.setAdcode(poiInfo.getAdCode());
            mSelectedCity.setCitycode(poiInfo.getCityCode());
            mSelectedCity.setAreaName(poiInfo.getCityName());
        }

        if (mSelectedCity != null) {
            textCity.setText(mSelectedCity.getAreaName());
        } else {
            textCity.setText("请选择城市");
        }
    }

    private void saveSearchHistory(PoiInfo poiInfo) {
        List<PoiInfo> poiInfos = mSpManager.getHistoryAddress();
        boolean flag = false;
        for (PoiInfo info : poiInfos) {
            if (info.getLongitude() == poiInfo.getLongitude() && info.getLatitude() == poiInfo.getLatitude()) {
                flag = true;
            }
        }
        if (!flag) {
            poiInfos.add(0, poiInfo);
            if (poiInfos.size() == 11) {
                poiInfos.remove(10);
            }
        }
        mSpManager.putHistoryAddress(poiInfos);
    }

    private void setAddressResult(PoiInfo poiInfo) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_ADDRESS, poiInfo);
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick({R.id.text_cancel, R.id.ibt_clear, R.id.text_city, R.id.edit_input})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.text_cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.ibt_clear:
                editInput.setText("");
                break;
            case R.id.edit_input:
                showCityView(false);
                break;
            case R.id.text_city:
                showCityView(true);
                break;
        }
    }

    private void showCityView(boolean visable) {
        if (visable) {
            rlCity.setVisibility(View.VISIBLE);
            llContent.setVisibility(View.GONE);
            editInput.setClickable(true);
            editInput.setFocusable(false);
        } else {
            rlCity.setVisibility(View.GONE);
            llContent.setVisibility(View.VISIBLE);
            editInput.setClickable(false);
            editInput.setFocusable(true);
            editInput.setFocusableInTouchMode(true);
            editInput.requestFocus();
        }
    }

    /**
     * 城市列表 VIEW
     */
    private void initCityView() {
        List<City> cityList = mAMapManager.getCityList();
        final CityAdapter adapter = new CityAdapter(this, cityList);
        headListView.setAdapter(adapter);
        letterBar.setOnLetterChangedListener(new SideLetterBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                int locationByLetter = adapter.getLocationByLetter(letter);
                headListView.setSelection(locationByLetter);
            }
        });
        letterBar.setOverlay(tvLetterOverlay);
        headListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedCity = adapter.getItem(position);
                textCity.setText(mSelectedCity.getAreaName());
                showCityView(false);
                mSpManager.putSelectedCity(mSelectedCity);
            }
        });
    }

    private void queryAddress(String keyWord) {
        if (mSelectedCity == null) {
            ToastUtils.showString(getString(R.string.select_city));
            return;
        }
        LlLoading.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        wrapAsync(mAMapManager.search(keyWord, "", mSelectedCity.getCitycode())).subscribe(new ResultObserver<List<PoiInfo>>() {
            @Override
            public void onSuccess(List<PoiInfo> value) {
                LlLoading.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(CommonUtils.getString(editInput))) {
                    //如果用户操作过快，输入框为空时应该显示历史内容，而此时如果网络较慢，则结果会覆盖掉历史，所以加此判断
                    showResultView(value);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                LlLoading.setVisibility(View.GONE);
                textError.setVisibility(View.VISIBLE);
            }
        });
    }

}
