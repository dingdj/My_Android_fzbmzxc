package com.android.ddj.fzbmzxc;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

/**
 * @author dingdj
 * Date:2013-11-8上午9:28:55
 *
 */
public class Location extends Application{
	
	public static final String BAIDU_MAP_KEY = "DDd84c3134edbc8fadf80761efd90ff0";
	
	public LocationClient mLocationClient = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	public double[] curLatLng = new double[2]; //当前位置的经纬度信息
	private LocationChanger locationChanger;
	public BMapManager mBMapMan = null;
	private static Location mInstance;
	
	@Override
	public void onCreate() {
		mLocationClient = new LocationClient( this );
		mLocationClient.setAK(BAIDU_MAP_KEY);
		mLocationClient.registerLocationListener(myListener);
		super.onCreate();
		mBMapMan=new BMapManager(this);
		mBMapMan.init(Location.BAIDU_MAP_KEY, null);
		mInstance = this;
	}

	/**
	 * 监听函数，得到当前位置信息 并通知界面进行刷新
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null){
				return ;
			}
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}
			sb.append("\nsdk version : ");
			sb.append(mLocationClient.getVersion());
			sb.append("\nisCellChangeFlag : ");
			sb.append(location.isCellChangeFlag());
			logMsg(sb.toString());
			curLatLng[0] = location.getLatitude();
			curLatLng[1] = location.getLongitude();
			notityLocationChanger();
		}
		
		public void onReceivePoi(BDLocation poiLocation) {
			Log.v("location", "location is null poi");
			if (poiLocation == null){
				return ; 
			}
			StringBuffer sb = new StringBuffer(256);
			sb.append("Poi time : ");
			sb.append(poiLocation.getTime());
			sb.append("\nerror code : "); 
			sb.append(poiLocation.getLocType());
			sb.append("\nlatitude : ");
			sb.append(poiLocation.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(poiLocation.getLongitude());
			sb.append("\nradius : ");
			sb.append(poiLocation.getRadius());
			if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(poiLocation.getAddrStr());
			} 
			if(poiLocation.hasPoi()){
				sb.append("\nPoi:");
				sb.append(poiLocation.getPoi());
			}else{				
				sb.append("noPoi information");
			}
			logMsg(sb.toString());
		}
	}
	
	/**
	 * 显示请求字符串
	 * @param str
	 */
	public void logMsg(String str) {
		Log.d("Location", str);
	}

	public void registerLocationChanger(LocationChanger locationChanger) {
		this.locationChanger = locationChanger;
	}
	
	public void notityLocationChanger(){
		if(this.locationChanger != null){
			locationChanger.locationChange();
		}
	}

	@Override
	public void onTerminate() {
		if(mBMapMan!=null){
            mBMapMan.destroy();
            mBMapMan=null;
		}
		super.onTerminate();
	}
	public static Location getInstance() {
		return mInstance;
	}
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
    public static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(Location.getInstance().getApplicationContext(), "您的网络出错啦！",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(Location.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //授权Key错误：
                Toast.makeText(Location.getInstance().getApplicationContext(), 
                        "key错误！", Toast.LENGTH_LONG).show();
            }
        }
    }
	

}
