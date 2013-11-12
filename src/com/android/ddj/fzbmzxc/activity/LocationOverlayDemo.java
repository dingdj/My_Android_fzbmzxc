package com.android.ddj.fzbmzxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.ddj.fzbmzxc.Location;
import com.android.ddj.fzbmzxc.R;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

/**
 * @author dingdj Date:2013-11-11下午5:25:10
 * 
 */
public class LocationOverlayDemo extends Activity {
	
	private static final String TAG = "LocationOverlayDemo";

	private MapView mMapView = null;
	/**
	 * 用MapController完成地图控制
	 */
	private MapController mMapController = null;

	private MyLocationOverlay myLocationOverlay;
	
	private MyLocationOverlay targetOverlay;
	
	private double[] targetLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/**
		 * 使用地图sdk前需先初始化BMapManager. BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
		 * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
		 */
		Location app = (Location) this.getApplication();
		if (app.mBMapMan == null) {
			app.mBMapMan = new BMapManager(this);
			/**
			 * 如果BMapManager没有初始化则初始化BMapManager
			 */
			app.mBMapMan.init(Location.BAIDU_MAP_KEY,
					new Location.MyGeneralListener());
		}
		/**
		 * 由于MapView在setContentView()中初始化,所以它需要在BMapManager初始化之后
		 */
		setContentView(R.layout.activity_locationoverlay);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.setBuiltInZoomControls(true);
		/**
		 * 获取地图控制器
		 */
		mMapController = mMapView.getController();
		/**
		 * 设置地图是否响应点击事件 .
		 */
		mMapController.enableClick(true);
		/**
		 * 设置地图缩放级别
		 */
		mMapController.setZoom(16);


		/**
		 * 将地图移动至指定点
		 * 使用百度经纬度坐标，可以通过http://api.map.baidu.com/lbsapi/getpoint/index
		 * .html查询地理坐标 如果需要在百度地图上显示使用其他坐标系统的位置，请发邮件至mapapi@baidu.com申请坐标转换接口
		 */
		/*
		 * GeoPoint p = new GeoPoint((int)(app.curLatLng[0] * 1E6),
		 * (int)(app.curLatLng[1] * 1E6)); mMapController.setCenter(p);
		 */
		myLocationOverlay = new MyLocationOverlay(mMapView);
		mMapView.getOverlays().add(myLocationOverlay);
		
		targetOverlay = new MyLocationOverlay(mMapView);
		targetOverlay.setMarker(getResources().getDrawable(R.drawable.icon_geo));
		mMapView.getOverlays().add(targetOverlay);
		if (getIntent()!=null && getIntent().hasExtra("targetLocation")) {
			// 当用intent参数时，设置中心点为指定点
			Bundle b = getIntent().getExtras();
			targetLocation = b.getDoubleArray("targetLocation");
		}
	}
	
	

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.hasExtra("targetLocation")) {
			// 当用intent参数时，设置中心点为指定点
			Bundle b = intent.getExtras();
			targetLocation = b.getDoubleArray("targetLocation");
		}
	}



	@Override
	protected void onPause() {
		/**
		 * MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		 */
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		/**
		 * MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		 */
		mMapView.onResume();
		super.onResume();
		LocationData locData = new LocationData();
		locData.latitude = ((Location) this.getApplication()).curLatLng[0];
		locData.longitude = ((Location) this.getApplication()).curLatLng[1];
		locData.direction = 2.0f;
		myLocationOverlay.setData(locData);
		mMapView.refresh();
		mMapView.getController().animateTo(
				new GeoPoint((int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)));
		
		if(targetLocation != null){
			locData = new LocationData();
			locData.latitude = targetLocation[0];
			Log.v(TAG, targetLocation[0]+"");
			locData.longitude = targetLocation[1];
			locData.direction = 2.0f;
			targetOverlay.setData(locData);
			mMapView.refresh();
		}
	}

	@Override
	protected void onDestroy() {
		/**
		 * MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		 */
		mMapView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

}
