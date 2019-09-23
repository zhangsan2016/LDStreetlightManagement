package com.ldgd.ldstreetlightmanagement.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.google.gson.Gson;
import com.ldgd.ldstreetlightmanagement.R;
import com.ldgd.ldstreetlightmanagement.base.BaseActivity;
import com.ldgd.ldstreetlightmanagement.entity.LoginJson;
import com.ldgd.ldstreetlightmanagement.entity.ProjectJson;
import com.ldgd.ldstreetlightmanagement.util.HttpConfiguration;
import com.ldgd.ldstreetlightmanagement.util.HttpUtil;
import com.ldgd.ldstreetlightmanagement.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BaiduMapActivity extends BaseActivity {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    // 登录返回的参数
    private LoginJson loginJson;


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
        mBaiduMap = mMapView.getMap();


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
     * 添加覆盖物
     *
     * @param projectList
     */
    public void initOverlay(List<ProjectJson.DataBeanX.ProjectInfo> projectList) {


        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (ProjectJson.DataBeanX.ProjectInfo projectInfo : projectList) {
            // add marker overlay
            LatLng ll = new LatLng(Double.parseDouble(projectInfo.getLat()),Double.parseDouble(projectInfo.getLng()));

            View markerView = View.inflate(this, R.layout.map_marker_item, null);
            TextView cameraName = markerView.findViewById(R.id.camera_name);
            cameraName.setText(projectInfo.getTitle());
            BitmapDescriptor bdA = BitmapDescriptorFactory.fromBitmap(getViewBitmap(markerView));

            MarkerOptions ooA = new MarkerOptions().position(ll).icon(bdA).zIndex(9).draggable(true);
            mBaiduMap.addOverlay(ooA);

            builder.include(ll);


        }
        try {
            LatLngBounds bounds = builder.build();
            /*MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(bounds.getCenter()); // 设置显示在屏幕中的地图地理范围
            mBaiduMap.animateMapStatus(u);*/
            // 设置显示在屏幕中的地图地理范围
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(bounds));
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(5));
        } catch (Exception e) {
            System.out.println("空指针异常： ");
        }


    }


    /**
     * 获取项目列表
     */
    public void getProject() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = HttpConfiguration.PROJECT_LIST_URL;

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

                        // 初始化覆盖物位置
                        initOverlay(projectList);


                    }
                }, loginJson.getData().getToken().getToken());


            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    public void getData() {
        loginJson = (LoginJson) getIntent().getSerializableExtra("loginInfo");
   /*     Gson gson = new Gson();
        String reString = gson.toJson(loginJson);
        System.out.println("BaiduMapActivity json = " + reString);*/
    }


}
