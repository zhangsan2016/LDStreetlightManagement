/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package baidumapsdk.demo;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.VersionInfo;

import java.util.ArrayList;

import baidumapsdk.demo.cloud.CloudSearchDemo;
import baidumapsdk.demo.map.BaseMapDemo;
import baidumapsdk.demo.map.FavoriteDemo;
import baidumapsdk.demo.map.GeometryDemo;
import baidumapsdk.demo.map.HeatMapDemo;
import baidumapsdk.demo.map.IndoorMapDemo;
import baidumapsdk.demo.map.LayersDemo;
import baidumapsdk.demo.map.LocationDemo;
import baidumapsdk.demo.map.MapControlDemo;
import baidumapsdk.demo.map.MapFragmentDemo;
import baidumapsdk.demo.map.MarkerAnimationDemo;
import baidumapsdk.demo.map.MarkerClusterDemo;
import baidumapsdk.demo.map.MultiMapViewDemo;
import baidumapsdk.demo.map.OfflineDemo;
import baidumapsdk.demo.map.OpenglDemo;
import baidumapsdk.demo.map.OverlayDemo;
import baidumapsdk.demo.map.TextureMapViewDemo;
import baidumapsdk.demo.map.TileOverlayDemo;
import baidumapsdk.demo.map.TrackShowDemo;
import baidumapsdk.demo.map.UISettingDemo;
import baidumapsdk.demo.search.BusLineSearchDemo;
import baidumapsdk.demo.search.DistrictSearchDemo;
import baidumapsdk.demo.search.GeoCoderDemo;
import baidumapsdk.demo.search.IndoorSearchDemo;
import baidumapsdk.demo.search.PoiSearchDemo;
import baidumapsdk.demo.search.RoutePlanDemo;
import baidumapsdk.demo.search.ShareDemo;
import baidumapsdk.demo.util.OpenBaiduMap;


public class BMapApiDemoMain extends Activity {
    private static final String LTAG = BMapApiDemoMain.class.getSimpleName();

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }

            TextView text = (TextView) findViewById(R.id.text_Info);
            text.setTextColor(Color.RED);
            if (action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                // 开放鉴权错误信息描述
                text.setText("key 验证出错! 错误码 :"
                        + intent.getIntExtra(SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_CODE, 0)
                        + " ; 错误信息 ："
                        + intent.getStringExtra(SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_MESSAGE));
            } else if (action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
                text.setText("key 验证成功! 功能可以正常使用");
                text.setTextColor(Color.GREEN);
            } else if (action.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                text.setText("网络出错");
            }
        }
    }

    private SDKReceiver mReceiver;
    private boolean isPermissionRequested;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TextView text = (TextView) findViewById(R.id.text_Info);
        text.setTextColor(Color.GREEN);
        text.setText("欢迎使用百度地图Android SDK v" + VersionInfo.getApiVersion());
        setTitle(getTitle() + " v" + VersionInfo.getApiVersion());

        ListView mListView = (ListView) findViewById(R.id.listView);
        // 添加ListItem，设置事件响应
        mListView.setAdapter(new DemoListAdapter());
        mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
                onListItemClick(index);
            }
        });

        requestPermission();

        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);
    }

    void onListItemClick(int index) {
        Intent intent;
        intent = new Intent(BMapApiDemoMain.this, DEMOS[index].demoClass);
        this.startActivity(intent);
    }

    private static final DemoInfo[] DEMOS = {
            new DemoInfo(R.string.demo_title_basemap, R.string.demo_desc_basemap, BaseMapDemo.class),
            new DemoInfo(R.string.demo_title_map_fragment, R.string.demo_desc_map_fragment, MapFragmentDemo.class),
            new DemoInfo(R.string.demo_title_layers, R.string.demo_desc_layers, LayersDemo.class),
            new DemoInfo(R.string.demo_title_multimap, R.string.demo_desc_multimap, MultiMapViewDemo.class),
            new DemoInfo(R.string.demo_title_control, R.string.demo_desc_control, MapControlDemo.class),
            new DemoInfo(R.string.demo_title_ui, R.string.demo_desc_ui, UISettingDemo.class),
            new DemoInfo(R.string.demo_title_location, R.string.demo_desc_location, LocationDemo.class),
            new DemoInfo(R.string.demo_title_geometry, R.string.demo_desc_geometry, GeometryDemo.class),
            new DemoInfo(R.string.demo_title_overlay, R.string.demo_desc_overlay, OverlayDemo.class),
            new DemoInfo(R.string.demo_title_marker_animation, R.string.demo_desc_marker_animation, MarkerAnimationDemo.class),
            new DemoInfo(R.string.demo_title_heatmap, R.string.demo_desc_heatmap, HeatMapDemo.class),
            new DemoInfo(R.string.demo_title_geocode, R.string.demo_desc_geocode, GeoCoderDemo.class),
            new DemoInfo(R.string.demo_title_poi, R.string.demo_desc_poi, PoiSearchDemo.class),
            new DemoInfo(R.string.demo_title_route, R.string.demo_desc_route, RoutePlanDemo.class),
            new DemoInfo(R.string.demo_title_districsearch, R.string.demo_desc_districsearch, DistrictSearchDemo.class),
            new DemoInfo(R.string.demo_title_bus, R.string.demo_desc_bus, BusLineSearchDemo.class),
            new DemoInfo(R.string.demo_title_share, R.string.demo_desc_share, ShareDemo.class),
            new DemoInfo(R.string.demo_title_offline, R.string.demo_desc_offline, OfflineDemo.class),
            new DemoInfo(R.string.demo_title_open_baidumap, R.string.demo_desc_open_baidumap, OpenBaiduMap.class),
            new DemoInfo(R.string.demo_title_favorite, R.string.demo_desc_favorite, FavoriteDemo.class),
            new DemoInfo(R.string.demo_title_cloud, R.string.demo_desc_cloud, CloudSearchDemo.class),
            new DemoInfo(R.string.demo_title_opengl, R.string.demo_desc_opengl, OpenglDemo.class),
            new DemoInfo(R.string.demo_title_cluster, R.string.demo_desc_cluster, MarkerClusterDemo.class),
            new DemoInfo(R.string.demo_title_tileoverlay, R.string.demo_desc_tileoverlay, TileOverlayDemo.class),
            new DemoInfo(R.string.demo_desc_texturemapview, R.string.demo_desc_texturemapview, TextureMapViewDemo.class),
            new DemoInfo(R.string.demo_title_indoor, R.string.demo_desc_indoor, IndoorMapDemo.class),
            new DemoInfo(R.string.demo_title_indoorsearch, R.string.demo_desc_indoorsearch, IndoorSearchDemo.class),
            new DemoInfo(R.string.demo_track_show, R.string.demo_desc_track_show, TrackShowDemo.class),
         /*   new DemoInfo(R.string.demo_title_custom_map_preview, R.string.demo_desc_custom_map_preview, CustomMapPreview.class)*/
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消监听 SDK 广播
        unregisterReceiver(mReceiver);
    }

    /**
     * Android6.0之后需要动态申请权限
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {

            isPermissionRequested = true;

            ArrayList<String> permissionsList = new ArrayList<>();

            String[] permissions = {
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
            };

            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
                    permissionsList.add(perm);
                    // 进入到这里代表没有权限.
                }
            }

            if (!permissionsList.isEmpty()) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), 0);
            }
        }
    }

    private class DemoListAdapter extends BaseAdapter {
        DemoListAdapter() {
            super();
        }

        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            convertView = View.inflate(BMapApiDemoMain.this, R.layout.demo_info_item, null);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView desc = (TextView) convertView.findViewById(R.id.desc);
            title.setText(DEMOS[index].title);
            desc.setText(DEMOS[index].desc);

            return convertView;
        }

        @Override
        public int getCount() {
            return DEMOS.length;
        }

        @Override
        public Object getItem(int index) {
            return DEMOS[index];
        }

        @Override
        public long getItemId(int id) {
            return id;
        }
    }

    private static class DemoInfo {
        private final int title;
        private final int desc;
        private final Class<? extends Activity> demoClass;

        DemoInfo(int title, int desc, Class<? extends Activity> demoClass) {
            this.title = title;
            this.desc = desc;
            this.demoClass = demoClass;
        }
    }
}