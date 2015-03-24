package com.sk.socketconnect;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.sk.socketconnect.base.BaseActivity;
import com.sk.socketconnect.utils.Constant;

public class JBaiduMapActivity extends BaseActivity implements
        OnGetGeoCoderResultListener {

    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private RelativeLayout mMapView_rl;

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    // UI相关
    // OnCheckedChangeListener radioButtonListener;
    Button requestLocButton;
    boolean isFirstLoc = true;// 是否首次定位
    // private RadioGroup group;

    private Marker[] mMarkers;
    private InfoWindow mInfoWindow;
    private Button locationView;
    OnInfoWindowClickListener oiwclistener = null;

    private double currentPositonX;
    private double currentPositonY;
    private String currentLocationStr;
    private long recordLastLocTime;
    BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.hasExtra("x") && intent.hasExtra("y")) {
            // 当用intent参数时，设置中心点为指定点
            Bundle b = intent.getExtras();
            LatLng p = new LatLng(b.getDouble("y"), b.getDouble("x"));
            mMapView = new MapView(this,
                    new BaiduMapOptions().mapStatus(new MapStatus.Builder()
                            .target(p).build()));
        } else {
            mMapView = new MapView(this, new BaiduMapOptions());
        }

        mMapView_rl.addView(mMapView);
        mBaiduMap = mMapView.getMap();
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mBaiduMap.setMapStatus(msu);
        // double pointX = 39.963175;
        // double pointY = 116.400244;
        String pointResult = intent
                .getStringExtra(Constant.GETTASKPOINT_RESULT);
        if (pointResult != null) {
            // String[] pointArr = pointResult.split(",");
            // for (int i = 0; i < pointArr.length; i++) {
            // String[] pointXY = pointArr[i].split(" ");
            // getMarkPoint(Double.parseDouble(pointXY[0]),
            // Double.parseDouble(pointXY[1]), R.drawable.icon_marka);
            // }
            String[] pointArr = new String[] { "39.963175 116.400244",
                    "116.400244 39.963175" };
            if (pointArr != null) {
                mMarkers = new Marker[pointArr.length];
                initOverlay(pointArr);
                // initOverlay("");
            }
        }
        String[] pointArr = new String[] { "39.963175 116.400244",
                "39.942821 116.369199", "39.939723 116.425541" , "39.906965 116.401394"};
        mMarkers = new Marker[pointArr.length];
        initOverlay(pointArr);
        mBaiduMap
                .setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

                    @Override
                    public boolean onMarkerClick(final Marker marker) {
                        // if (locationView == null) {
                        // initPopuButton();
                        // }
                        // OnInfoWindowClickListener listener = null;
                        // button.setText("更改位置");
                        LatLng ptCenter = marker.getPosition();
                        // 反Geo搜索
                        // oiwclistener = new OnInfoWindowClickListener() {
                        // public void onInfoWindowClick() {
                        // // TODO
                        // mBaiduMap.hideInfoWindow();
                        // }
                        // };
                        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                                .location(ptCenter));
                        // LatLng ll = marker.getPosition();
                        // mInfoWindow = new
                        // InfoWindow(BitmapDescriptorFactory.fromView(button),
                        // ll, -47, listener);
                        // mBaiduMap.showInfoWindow(mInfoWindow);
                        return true;
                    }
                });
        initLocationData();
    }

    private void initOverlay(String[] pointArr) {
        for (int i = 0; i < pointArr.length; i++) {
            String[] pointXY = pointArr[i].split(" ");
            double pointX = Double.parseDouble(pointXY[0]);
            double pointY = Double.parseDouble(pointXY[1]);
            showShortToast(pointX + "," + pointY);
            // double pointX = 39.963175;
            // double pointY = 116.400244;
            LatLng llPoint = new LatLng(pointX, pointY);
            // 构建Marker图标
            OverlayOptions option = new MarkerOptions();
            // 构建MarkerOption，用于在地图上添加Marker
            ((MarkerOptions) option).position(llPoint).icon(bd).zIndex(9)
                    .draggable(true);
            mMarkers[i] = (Marker) mBaiduMap.addOverlay(option);
        }
    }

    @Override
    public void onBackPressed() {
        if (getIntent() != null
                && getIntent().getBooleanExtra(Constant.GET_POSITION_INFO,
                        false)) {
            Intent data = new Intent();
            data.putExtra(Constant.CURRENTLOCATIONSTR, currentLocationStr);
            data.putExtra(Constant.CURRENTPOSITONX, currentPositonX);
            data.putExtra(Constant.CURRENTPOSITONY, currentPositonY);
            setResult(UnLoadImageDetial.RESULT_LOCATION_CODE, data);
            showShortToast(currentLocationStr);
            finish();
        }
        super.onBackPressed();
    }

    private void initLocationData() {
        mCurrentMode = LocationMode.NORMAL;
        requestLocButton.setText("普通");
        requestLocButton.setOnClickListener(this);

        // radioButtonListener = new OnCheckedChangeListener() {
        // @Override
        // public void onCheckedChanged(RadioGroup group, int checkedId) {
        // if (checkedId == R.id.defaulticon) {
        // // 传入null则，恢复默认图标
        // mCurrentMarker = null;
        // mBaiduMap
        // .setMyLocationConfigeration(new MyLocationConfiguration(
        // mCurrentMode, true, null));
        // }
        // if (checkedId == R.id.customicon) {
        // // 修改为自定义marker
        // mCurrentMarker = BitmapDescriptorFactory
        // .fromResource(R.drawable.icon_geo);
        // mBaiduMap
        // .setMyLocationConfigeration(new MyLocationConfiguration(
        // mCurrentMode, true, mCurrentMarker));
        // }
        // }
        // };
        // group.setOnCheckedChangeListener(radioButtonListener);

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        // mLocClient.
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setAddrType("all");// 返回的定位结果包含地址信息
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(60 * 1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        // int requestOfflineLocation = mLocClient.requestOfflineLocation();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.jbmap_act_start_offline:
            openActivity(OfflineMapActivity.class);
            break;
        case R.id.switch_location_icon:
            // mLocClient.requestLocation();
            // mLocClient.requestOfflineLocation();
            switch (mCurrentMode) {
            case NORMAL:
                requestLocButton.setText("跟随");
                mCurrentMode = LocationMode.FOLLOWING;
                mBaiduMap
                        .setMyLocationConfigeration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                break;
            case COMPASS:
                requestLocButton.setText("普通");
                mCurrentMode = LocationMode.NORMAL;
                mBaiduMap
                        .setMyLocationConfigeration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                break;
            case FOLLOWING:
                requestLocButton.setText("罗盘");
                mCurrentMode = LocationMode.COMPASS;
                mBaiduMap
                        .setMyLocationConfigeration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                break;
            }
            break;

        default:
            break;
        }
    }

    @Override
    protected void mFindViewByIdAndSetListener() {
        mMapView_rl = $(R.id.bmapView_rl);
        $(R.id.jbmap_act_start_offline).setOnClickListener(this);
        requestLocButton = $(R.id.switch_location_icon);
        // group = $(R.id.radioGroup);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_baidu_map;
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.unRegisterLocationListener(myListener);
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        // mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        super.onResume();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            int locType = location.getLocType();
            showShortToast("请求结果：" + locType);
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            currentPositonX = location.getLatitude();
            currentPositonY = location.getLongitude();
            currentLocationStr = location.getAddrStr();
            if (isFirstLoc) {
                isFirstLoc = false;
                recordLastLocTime = System.currentTimeMillis();
                LatLng ll = new LatLng(currentPositonX, currentPositonY);
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
            // if (locationView == null) {
            // initPopuButton();
            // }
            if (currentLocationStr != null) {
                locationView = new Button(getApplicationContext());
                locationView.setBackgroundResource(R.drawable.popup);
                locationView.setText(currentLocationStr);
                locationView.setTextColor(Color.GRAY);
                oiwclistener = new OnInfoWindowClickListener() {

                    @Override
                    public void onInfoWindowClick() {
                        // TODO Auto-generated method stub
                        mBaiduMap.hideInfoWindow();
                    }
                };
                mInfoWindow = new InfoWindow(
                        BitmapDescriptorFactory.fromView(locationView),
                        new LatLng(currentPositonX, currentPositonY), -20,
                        oiwclistener);
                mBaiduMap.showInfoWindow(mInfoWindow);
            }
            // save location

        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    protected void onPause() {
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onFailed() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSuccess(String result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(JBaiduMapActivity.this, "抱歉，未能找到结果",
                    Toast.LENGTH_LONG).show();
            return;
        } else {
            LatLng location = result.getLocation();
            locationView = new Button(getApplicationContext());
            locationView.setBackgroundResource(R.drawable.popup);
            locationView.setText(result.getAddress());
            locationView.setTextColor(Color.GRAY);
            oiwclistener = new OnInfoWindowClickListener() {
                public void onInfoWindowClick() {
                    // TODO
                    mBaiduMap.hideInfoWindow();
                }
            };
            mInfoWindow = new InfoWindow(
                    BitmapDescriptorFactory.fromView(locationView), location,
                    -47, oiwclistener);
            mBaiduMap.showInfoWindow(mInfoWindow);
        }
    }

}
