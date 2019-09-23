package com.ldgd.ldstreetlightmanagement.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.ldgd.ldstreetlightmanagement.R;
import com.ldgd.ldstreetlightmanagement.entity.LoginJson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BaiduMapActivity extends AppCompatActivity {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMapCustomFile(this, "custom_map_config_luyexianzhong.json");
        // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏顶部的状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_baidu_map);

        // 获取传递过来的参数
        getData();

        // 初始化百度地图
        initBaiduMap();

        // 初始化覆盖物位置
        initOverlay();


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
     */
    public void initOverlay() {

        // add marker overlay
        LatLng llA = new LatLng(39.963175, 116.400244);
        LatLng llB = new LatLng(39.942821, 116.369199);
        LatLng llC = new LatLng(39.939723, 116.425541);
        LatLng llD = new LatLng(39.906965, 116.401394);


        View markerView = View.inflate(this, R.layout.map_marker_item, null);
        TextView cameraName = markerView.findViewById(R.id.camera_name);
        cameraName.setText("skdhfjsdlf");
        BitmapDescriptor bdA = BitmapDescriptorFactory.fromBitmap(getViewBitmap(markerView));


        MarkerOptions ooA = new MarkerOptions().position(llA).icon(bdA).zIndex(9).draggable(true);
        MarkerOptions ooB = new MarkerOptions().position(llB).icon(bdA).zIndex(9).draggable(true);
        MarkerOptions ooC = new MarkerOptions().position(llC).icon(bdA).zIndex(9).draggable(true);
        MarkerOptions ooD = new MarkerOptions().position(llD).icon(bdA).zIndex(9).draggable(true);
        mBaiduMap.addOverlay(ooA);
        mBaiduMap.addOverlay(ooB);
        mBaiduMap.addOverlay(ooC);
        mBaiduMap.addOverlay(ooD);

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
        LoginJson loginJson = (LoginJson) getIntent().getSerializableExtra("loginInfo");
        Gson gson = new Gson();
        String reString = gson.toJson(loginJson);
        System.out.println("BaiduMapActivity json = " + reString);
    }
}
