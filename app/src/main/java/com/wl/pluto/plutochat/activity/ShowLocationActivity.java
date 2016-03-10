package com.wl.pluto.plutochat.activity;

import android.content.Intent;
import android.os.Bundle;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.base.BaseActivity;
import com.wl.pluto.plutochat.constant.CommonConstant;
import com.wl.pluto.plutochat.logger.Log;

public class ShowLocationActivity extends BaseActivity implements AMap.OnMapLoadedListener {

    /**
     * log tag
     */
    private static final String TAG = "--ShowLocationActivity-->";

    /**
     * 高德地图
     */
    private AMap mGDAMap;

    /**
     * 地图控件
     */
    private MapView mGDMapView;

    /**
     * 经度
     */
    private double mLocationLatitude = 0;

    /**
     * 纬度
     */
    private double mLocationLongitude = 0;

    /**
     * 地址
     */
    private String mLocationAddress = null;

    /**
     * 坐标
     */
    private LatLng mLatLng = null;

    /**
     * 默认的缩放级别，值越大代表地图显示的范围越小，精度就越高  级别范围是4-20
     */
    public static final int LOCATION_ZOOM_DEFAULT = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location);

        Log.i(TAG, "onCreate");
        getDataFromIntent(getIntent());
        initLayout(savedInstanceState);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getDataFromIntent(intent);
    }

    private void getDataFromIntent(Intent intent) {

        if (intent != null) {
            mLocationLatitude = intent.getDoubleExtra(CommonConstant.LOCATION_LATITUDE_KEY, 0);
            mLocationLongitude = intent.getDoubleExtra(CommonConstant.LOCATION_LONGITUDE_KEY, 0);
            mLocationAddress = intent.getStringExtra(CommonConstant.LOCATION_ADDRESS_KEY);

            if (mLocationLatitude != 0 && mLocationLongitude != 0) {
                mLatLng = new LatLng(mLocationLatitude, mLocationLongitude);
            }
        } else {
            Log.i(TAG, "intent == null");
        }
    }

    private void initLayout(Bundle savedInstanceState) {

        mGDMapView = (MapView) findViewById(R.id.map_show_location);

        // 此方法必须重写
        mGDMapView.onCreate(savedInstanceState);

        setUpMap();
    }

    /**
     * 初始化AMap对象
     */
    private void setUpMap() {
        if (mGDAMap == null) {
            mGDAMap = mGDMapView.getMap();
            mGDAMap.setOnMapLoadedListener(this);
            initMarker();
        }
    }

    private void initMarker() {


        // 文字显示标注，可以设置显示内容，位置，字体大小颜色，背景色旋转角度,Z值等
//        TextOptions textOptions = new TextOptions()
//                .position(MapConstant.CHENGDU)
//                .text("Text")
//                .fontColor(Color.BLACK)
//                .backgroundColor(Color.BLUE)
//                .fontSize(30)
//                .rotate(20)
//                .align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_CENTER_VERTICAL)
//                .zIndex(1.f).typeface(Typeface.DEFAULT_BOLD);
        //mGDAMap.addText(textOptions);

//        if(mLatLng != null){
//
//            mGDAMap.addMarker(new MarkerOptions()
//                    .anchor(0.5f, 0.5f)
//                    .position(MapConstant.CHONGQING)
//                    .title("重庆市")
//                    .draggable(true));
//        }else{
//
//            Log.i(TAG, "mLatLng == null");
//        }
        drawMarkers();
    }

    /**
     * 绘制系统默认的1种marker背景图片
     */
    public void drawMarkers() {
        Marker marker = mGDAMap.addMarker(new MarkerOptions()
                .position(mLatLng)
                .title(mLocationAddress)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .draggable(true));

        // 设置默认显示一个info window
        marker.showInfoWindow();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mGDMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mGDMapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mGDMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {

        super.onDestroy();
        mGDMapView.onDestroy();
    }

    @Override
    public void onMapLoaded() {

        // 设置所有maker显示在当前可视区域地图中
        if(mLatLng != null){
            //定位范围
            LatLngBounds bounds = new LatLngBounds.Builder().include(mLatLng).build();

            //将地图移动到定位的地方
            mGDAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 18));

            //将地图放大一些
            changeCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, LOCATION_ZOOM_DEFAULT),null);
        }else {
            Log.i(TAG, "get location failed");
        }
    }

    private void changeCamera(CameraUpdate cameraUpdate, AMap.CancelableCallback callback){
        mGDAMap.animateCamera(cameraUpdate, callback);
    }
}
