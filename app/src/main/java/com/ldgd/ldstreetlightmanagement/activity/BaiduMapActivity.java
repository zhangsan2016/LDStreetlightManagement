package com.ldgd.ldstreetlightmanagement.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.baidu.mapapi.clusterutil.clustering.Cluster;
import com.baidu.mapapi.clusterutil.clustering.ClusterItem;
import com.baidu.mapapi.clusterutil.clustering.ClusterManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.google.gson.Gson;
import com.ldgd.ldstreetlightmanagement.R;
import com.ldgd.ldstreetlightmanagement.base.BaseActivity;
import com.ldgd.ldstreetlightmanagement.entity.DeviceLampJson;
import com.ldgd.ldstreetlightmanagement.entity.LoginJson;
import com.ldgd.ldstreetlightmanagement.entity.ProjectJson;
import com.ldgd.ldstreetlightmanagement.util.HttpConfiguration;
import com.ldgd.ldstreetlightmanagement.util.HttpUtil;
import com.ldgd.ldstreetlightmanagement.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BaiduMapActivity extends BaseActivity {

    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    // 登录返回的参数
    private LoginJson loginJson;


    private ClusterManager<MyItem> mClusterManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMapCustomFile(this, "custom_map_config_luyexianzhong.json");
        // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏顶部的状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_baidu_map);


        // 获取传递过来的参数
        getData();

        // 初始化百度地图
        initBaiduMap();

        // 获取项目信息
        getProject();


    }

    private Bitmap getViewBitmap(View addViewContent) {

        addViewContent.setDrawingCacheEnabled(true);
        addViewContent.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        addViewContent.layout(0, 0,
                addViewContent.getMeasuredWidth(),
                addViewContent.getMeasuredHeight());
        addViewContent.buildDrawingCache();

        Bitmap cacheBitmap = addViewContent.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        return bitmap;
    }


    private void initBaiduMap() {

        //开启个性化地图
        MapView.setMapCustomEnable(true);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        // 百度地图LoGo -> 正式版切记不能这么做，本人只是觉得logo丑了
        mMapView.removeViewAt(1);
        // 比例尺控件
        mMapView.showScaleControl(true);
        mBaiduMap = mMapView.getMap();
        //是否显示缩小放大按钮// 设置地图监听，当地图状态发生改变时，进行点聚合运算
        mMapView.showZoomControls(false);
        //不倾斜
        mBaiduMap.getUiSettings().setOverlookingGesturesEnabled(false);
        //不旋转
        mBaiduMap.getUiSettings().setRotateGesturesEnabled(false);


        // 初始化聚合地图
        initClusterMap();


    }

    /**
     * 初始化聚合地图
     */
    private void initClusterMap() {

        // 定义点聚合管理类ClusterManager
        mClusterManager = new ClusterManager<MyItem>(this, mBaiduMap);
        // 设置地图监听，当地图状态发生改变时，进行点聚合运算
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        // 设置maker点击时的响应
        mBaiduMap.setOnMarkerClickListener(mClusterManager);
        // 设置地图允许的最小/大级别
        //   mBaiduMap.setMaxAndMinZoomLevel(4,16);
        // 设置地图的可动范围
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(43.56912, 101.123014));
        builder.include(new LatLng(20.226576, 115.767262));
        mBaiduMap.setMapStatusLimits(builder.build());


        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBaiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(Cluster<MyItem> cluster) {
                // showToast("有" + cluster.getSize() + "个点");
                ClusterOnClick(cluster);
                return false;
            }
        });
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem marker) {
                // showToast("点击单个Item");



               DeviceLampJson.DataBeanX.DeviceLamp deviceLamp = marker.getDeviceLamp();

                InfoWindow mInfoWindow;
                LatLng position = marker.getPosition();
                final double latitude = position.latitude;
                final double longitude = position.longitude;
                // showToast("latitude" + latitude + "\n  " + "longitude" +
                // longitude );

                // 将marker所在的经纬度的信息转化成屏幕上的坐标
                Point p = mBaiduMap.getProjection().toScreenLocation(
                        position);
                p.y -= 120;
                LatLng llInfo = mBaiduMap.getProjection()
                        .fromScreenLocation(p);
                View location = View.inflate(BaiduMapActivity.this,
                        R.layout.baidu_map_marker_info_item, null);
                mInfoWindow = new InfoWindow(location, llInfo, 0);
                // 显示InfoWindow
                mBaiduMap.showInfoWindow(mInfoWindow);

                return false;
            }
        });

    }

    /**
     * 聚合点击
     */
    private void ClusterOnClick(Cluster<MyItem> clusterBaiduItems) {
        if (mBaiduMap == null) {
            return;
        }
        if (clusterBaiduItems.getItems().size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (MyItem clusterBaiduItem : clusterBaiduItems.getItems()) {
                builder.include(clusterBaiduItem.getPosition());
            }
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                    .newLatLngBounds(builder.build()));
        }
    }

    /**
     * 每个Marker点，包含Marker点坐标以及图标
     */
    public class MyItem implements ClusterItem {
        private final LatLng mPosition;
        private DeviceLampJson.DataBeanX.DeviceLamp deviceLamp;

        public DeviceLampJson.DataBeanX.DeviceLamp getDeviceLamp() {
            return deviceLamp;
        }

        /*    public MyItem(LatLng latLng) {
                        mPosition = latLng;
                    }*/
        public MyItem(LatLng latLng, DeviceLampJson.DataBeanX.DeviceLamp deviceLamp) {
            mPosition = latLng;
            this.deviceLamp = deviceLamp;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Override
        public BitmapDescriptor getBitmapDescriptor() {


            View markerView = View.inflate(BaiduMapActivity.this, R.layout.map_marker_item, null);
            TextView cameraName = markerView.findViewById(R.id.camera_name);
            cameraName.setText(deviceLamp.getNAME());
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getViewBitmap(markerView));
            return bitmapDescriptor;

            //   return BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);

        }
    }

    /**
     * 将个性化文件写入本地后调用MapView.setCustomMapStylePath加载
     *
     * @param context
     * @param fileName assets目录下自定义样式文件的文件名
     */
    private void setMapCustomFile(Context context, String fileName) {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        String moduleName = null;
        try {
            inputStream = context.getAssets().open("customConfigDir/" + fileName);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            moduleName = context.getFilesDir().getAbsolutePath();
            File file = new File(moduleName + "/" + fileName);
            if (file.exists()) file.delete();
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            //将自定义样式文件写入本地
            fileOutputStream.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //设置自定义样式文件
        MapView.setCustomMapStylePath(moduleName + "/" + fileName);
    }


    /**
     * 获取项目列表
     */
    public void getProject() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = HttpConfiguration.PROJECT_LIST_URL;
                String token = loginJson.getData().getToken().getToken();
                String contentType = HttpConfiguration.CONTENT_TYPE_PROJECT_LIST;

                HttpUtil.sendHttpRequest(url, new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtil.e("xxx" + "失败" + e.toString());
                        showToast("连接服务器异常！");
                        stopProgress();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String json = response.body().string();
                        LogUtil.e("xxx" + "成功" + json);

                        // 解析返回过来的json
                        Gson gson = new Gson();
                        ProjectJson project = gson.fromJson(json, ProjectJson.class);
                        List<ProjectJson.DataBeanX.ProjectInfo> projectList = project.getData().getData();


                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (ProjectJson.DataBeanX.ProjectInfo projectInfo : projectList) {
                            // 用于计算当前显示范围
                            LatLng ll = new LatLng(Double.parseDouble(projectInfo.getLat()), Double.parseDouble(projectInfo.getLng()));
                            builder.include(ll);
                            // 获取当前项目下的所有路灯
                            getDeviceLampList(projectInfo.getTitle());
                        }

                        LatLngBounds bounds = builder.build();
                        // 设置显示在屏幕中的地图地理范围
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(bounds));
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(6));


                    }
                }, token, contentType, null);


            }
        }).start();

    }


    public void getData() {
        loginJson = (LoginJson) getIntent().getSerializableExtra("loginInfo");
   /*     Gson gson = new Gson();
        String reString = gson.toJson(loginJson);
        System.out.println("BaiduMapActivity json = " + reString);*/
    }


    /**
     * 获取设备下管理的所有路灯
     */
    public void getDeviceLampList(final String title) {


        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = HttpConfiguration.DEVICE_LAMP_LIST_URL;
                String token = loginJson.getData().getToken().getToken();
                String contentType = HttpConfiguration.CONTENT_TYPE_DEVICE_LAMP_LIST;

                // 创建请求的参数body
                //   String postBody = "{\"where\":{\"PROJECT\":" + title + "},\"size\":5000}";
                String postBody = "{\"where\":{\"PROJECT\":\"" + title + "\"},\"size\":5000}";
                RequestBody body = FormBody.create(MediaType.parse("application/json"), postBody);

                LogUtil.e("xxx postBody = " + postBody);

                HttpUtil.sendHttpRequest(url, new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtil.e("xxx" + "失败" + e.toString());
                        showToast("连接服务器异常！");
                        stopProgress();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String json = response.body().string();
                        LogUtil.e("xxx" + "成功" + json);

                        // 解析返回过来的json
                        Gson gson = new Gson();
                        DeviceLampJson deviceLampJson = gson.fromJson(json, DeviceLampJson.class);
                        List<DeviceLampJson.DataBeanX.DeviceLamp> projectList = deviceLampJson.getData().getData();

                        List<MyItem> items = new ArrayList<MyItem>();
                        for (DeviceLampJson.DataBeanX.DeviceLamp deviceLamp : projectList) {

                            if (deviceLamp.getLAT().equals("") || deviceLamp.getLNG().equals("")) {
                                break;
                            }

                            LatLng ll = new LatLng(Double.parseDouble(deviceLamp.getLAT()), Double.parseDouble(deviceLamp.getLNG()));
                            items.add(new MyItem(ll, deviceLamp));

                        }
                        mClusterManager.addItems(items);
                        //更新页面
                        if (mClusterManager != null)
                            mClusterManager.cluster();

                    }
                }, token, contentType, body);
            }
        }).start();


    }


    /**
     * 清除所有Overlay
     *
     * @param view
     */
    public void clearOverlay(View view) {
        mBaiduMap.clear();
    }


    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
        LogUtil.e("baidumap = onPause");
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
        LogUtil.e("baidumap = onResume");
    }

    @Override
    protected void onDestroy() {
        LogUtil.e("baidumap = onDestroy");
      /*  mBaiduMap.clear();
        mClusterManager.clearItems();
        mMapView.onDestroy();*/
        mMapView = null;
        super.onDestroy();

    }
}
