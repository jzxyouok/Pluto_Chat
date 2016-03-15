package com.wl.pluto.plutochat.chat.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.chat.base.BaseActivity;
import com.wl.pluto.plutochat.chat.constant.CommonConstant;
import com.wl.pluto.plutochat.chat.utils.SDCardUtils;

import java.io.FileOutputStream;

/**
 * AMapV1地图中简单介绍显示定位小蓝点
 */
public class LocationActivity extends BaseActivity implements LocationSource,
        AMapLocationListener, AMap.OnMapScreenShotListener {

    public static final String TAG = "--LocationActivity-->";

    /**
     * 高德地图服务组件
     */
    private AMap mGDAMap;

    /**
     * 高德地图地图控件
     */
    private MapView mGDMapView;

    /**
     * 地理位置改变的监听接口
     */
    private OnLocationChangedListener mLocationChangedListener;

    /**
     * 高德地图客户
     */
    private AMapLocationClient mLocationClient;

    /**
     * 高德地图初始化配置选项
     */
    private AMapLocationClientOption mLocationOption;

    /**
     * 经度
     */
    private double mLocationLatitude;

    /**
     * 纬度
     */
    private double mLocationLongitude;

    /**
     * 地理位置
     */
    private String mLocationAddress;

    /**
     * 默认的缩放级别，值越大代表地图显示的范围越小，精度就越高  级别范围是4-20
     */
    public static final int LOCATION_ZOOM_DEFAULT = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        initLayout(savedInstanceState);
        initGDMap();
    }

    private void initLayout(Bundle savedInstanceState) {
        mGDMapView = (MapView) findViewById(R.id.map);
        mGDMapView.onCreate(savedInstanceState);// 此方法必须重写
    }

    /**
     * 初始化AMap对象
     */
    private void initGDMap() {
        if (mGDAMap == null) {
            mGDAMap = mGDMapView.getMap();
            setUpMap();
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();


        // 设置小蓝点的图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.mipmap.location_marker));

        // 设置圆形的边框颜色
        myLocationStyle.strokeColor(Color.BLACK);

        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));

        // 设置小蓝点的锚点
        // myLocationStyle.anchor(int,int)

        // 设置圆形的边框粗细
        myLocationStyle.strokeWidth(1.0f);

        mGDAMap.setMyLocationStyle(myLocationStyle);

        // 设置定位监听
        mGDAMap.setLocationSource(this);

        // 设置默认定位按钮是否显示
        mGDAMap.getUiSettings().setMyLocationButtonEnabled(true);


        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        mGDAMap.setMyLocationEnabled(true);
        // mGDAMap.setMyLocationType()
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
        deactivate();
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

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mLocationChangedListener != null && amapLocation != null) {

            //如果定位成功
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {

                // 显示系统小蓝点
                mLocationChangedListener.onLocationChanged(amapLocation);

                mLocationLatitude = amapLocation.getLatitude();
                mLocationLongitude = amapLocation.getLongitude();
                mLocationAddress = amapLocation.getAddress();

                LatLng latLng = new LatLng(mLocationLatitude, mLocationLongitude);
                changeCamera(CameraUpdateFactory.newLatLngZoom(latLng, LOCATION_ZOOM_DEFAULT), null);

                Log.i(TAG, "" + mLocationLatitude);
                Log.i(TAG, "" + mLocationLongitude);
                Log.i(TAG, mLocationAddress);
            } else {

                String errText = "定位失败," + amapLocation.getErrorCode() + ": "
                        + amapLocation.getErrorInfo();
                Log.e(TAG, errText);
            }
        }
    }

    private void changeCamera(CameraUpdate cameraUpdate, AMap.CancelableCallback callback){
        mGDAMap.animateCamera(cameraUpdate, callback);
    }

    /**
     * 激活定位，重写这个方法，当Activity启动的时候，机会自动开始定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mLocationChangedListener = listener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();

            // 设置定位监听
            mLocationClient.setLocationListener(this);

            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);

            //设置是否为一次定位,true为只需要一次定位即可，false为每隔两秒来定位一次，考虑到电量和流量因素，这里需要慎重考虑
            //默认是每隔两秒定位一次，
            mLocationOption.setOnceLocation(true);

            //如果是连续定位，可以设置定位时间间隔,最小值为2000，如果小于2000，按照2000算
            // mLocationOption.setInterval(2000);

            // 设置定位参数
            mLocationClient.setLocationOption(mLocationOption);

            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mLocationChangedListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_loacation_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_location_send:
                onSendLocationClick();
                break;

            case R.id.menu_location_map_capture:

                onMapCaptureClick();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onMapCaptureClick(){
        mGDAMap.getMapScreenShot(this);
        mGDAMap.invalidate();
    }
    private void onSendLocationClick() {

        Intent intent = getIntent();
        intent.putExtra(CommonConstant.LOCATION_LATITUDE_KEY, mLocationLatitude);
        intent.putExtra(CommonConstant.LOCATION_LONGITUDE_KEY, mLocationLongitude);
        intent.putExtra(CommonConstant.LOCATION_ADDRESS_KEY, mLocationAddress);

        onMapCaptureClick();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onMapScreenShot(Bitmap bitmap) {

        try{

            FileOutputStream fileOutputStream = new FileOutputStream(SDCardUtils.getMapShotName("1234"));

            boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

            try {

                fileOutputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }

            if(b){
                toast(R.string.text_screen_shot_success);
            }else {
                toast(R.string.text_screen_shot_failed);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
