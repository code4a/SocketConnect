package com.sk.socketconnect;

import android.os.Bundle;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.sk.socketconnect.base.BaseActivity;

public class JBaiduMapActivity extends BaseActivity {

    private MapView mMapView = null;
    private BaiduMap mBaiduMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaiduMap = mMapView.getMap();
        double pointX = 39.963175;
        double pointY = 116.400244;

        getMarkPoint(pointX, pointY, R.drawable.icon_marka);
    }

    private Marker getMarkPoint(double pointX, double pointY, int markId) {
        //Marker mMarker = null;
        // 定义Maker坐标点
        LatLng point = new LatLng(pointX, pointY);
        // 构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(markId);
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point)
                .icon(bitmap).zIndex(9).draggable(true);
        // 在地图上添加Marker，并显示
        return (Marker) mBaiduMap.addOverlay(option);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void mFindViewByIdAndSetListener() {
        // 获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_baidu_map;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

}
