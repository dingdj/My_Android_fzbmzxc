package com.android.ddj.fzbmzxc.activity;


import com.android.ddj.fzbmzxc.Location;
import com.android.ddj.fzbmzxc.LocationChanger;
import com.android.ddj.fzbmzxc.R;
import com.android.ddj.fzbmzxc.Station;
import com.android.ddj.fzbmzxc.StationManager;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 主界面
 * @author dingdj
 * Date:2013-11-8下午3:29:44
 *
 */
public class Main extends ListActivity implements LocationChanger {
	
	//是否已开启定位
	private boolean isStart = false;
	
	private LocationClient mLocClient;
	private StationManager stationManager;
	private boolean isDealData = false;
	private volatile Station[] stations;
	private PullToRefreshListView mPullRefreshListView;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			stations = (Station[]) msg.obj;
			adapter.notifyDataSetChanged();
			if(mPullRefreshListView != null){
				mPullRefreshListView.onRefreshComplete();
			}
		}
		
	};
	
	private LocationListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_content_simple);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		stationManager = StationManager.getInstance(this);
		stations = stationManager.getStations();
		mLocClient = ((Location)getApplication()).mLocationClient;
		setLocationOption();
		((Location)getApplication()).registerLocationChanger(this);
		mLocClient.start();
		isStart = true;
		adapter = new LocationListAdapter(this);
		ListView actualListView = mPullRefreshListView.getRefreshableView();
		actualListView.setAdapter(adapter);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
		    @Override
		    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		        // Do work to refresh the list here.
		    	Toast.makeText(refreshView.getContext(), "正在定位请稍候..", 2000).show();
				if(!isStart){
					mLocClient.requestLocation();
					isStart = true;
				}
				
				refreshView.postDelayed(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mPullRefreshListView.onRefreshComplete();
					}
					
				}, 5000);
		    }
		});

	}
	
	/**
	 * 启动定位配置
	 * @author dingdj
	 * Date:2013-11-8下午3:34:06
	 */
	private void setLocationOption(){
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setOpenGps(false);
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setPoiExtraInfo(false);
		option.setAddrType("all");
		option.setScanSpan(60000);
		option.setPoiNumber(10);
		option.disableCache(true);		
		mLocClient.setLocOption(option);
	}
	
	@Override
	public void onDestroy() {
		mLocClient.stop();
		isStart = false;
		super.onDestroy();
	}
	
	@Override
	protected void onPause(){
        super.onPause();
	}
	
	@Override
	protected void onResume(){
        super.onResume();
	}

	@Override
	public void locationChange() {
		isStart = false;
		if(!isDealData){
			isDealData = true;
			new Thread(new Runnable(){
				@Override
				public void run() {
					//stationManager.dump(stations);
					isDealData = false;
					//通知数据已经获得
					Message message = Message.obtain();
					try{
						//当第一次定位成功后 防止定位不成功 得不到当前位置导致crash
						if(((Location)getApplication()).isFirstLocate()){
							message.obj = stationManager.getTopNNearStation(10, ((Location)getApplication()).curLatLng);
							handler.sendMessage(message);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	
	/**
	 * listView显示数据集
	 * @author dingdj
	 * Date:2013-11-11下午3:28:40
	 *
	 */
	private class LocationListAdapter extends BaseAdapter{
		
		 private LayoutInflater mInflater;
		 
		 public LocationListAdapter(Context context){
			 mInflater = LayoutInflater.from(context);
		 }

		@Override
		public int getCount() {
			return stations.length;
		}

		@Override
		public Object getItem(int position) {
			return stations[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
                convertView = mInflater.inflate(R.layout.station, null);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.address = (TextView) convertView.findViewById(R.id.address);
                holder.description = (TextView) convertView.findViewById(R.id.description);
                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            holder.name.setText(stations[position].getName());
            holder.address.setText(stations[position].getAddress());
            holder.description.setText(stations[position].getLat()+" "+
            		stations[position].getLng()+ " 距离当前位置约 "+
            		 Math.round(stations[position].getDistance()*100.0)/100.0+" 米");

            return convertView;
		}
		
		class ViewHolder {
	            TextView name;
	            TextView address;
	            TextView description;
	        }
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Station station = stations[position];
		Intent intent = new Intent(this, LocationOverlayDemo.class);
		intent.putExtra("targetLocation", new double[]{station.getLat(), station.getLng()});
	    startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		/*if(item.getItemId() == R.id.menu_loc){
			Toast.makeText(this, "正在定位请稍候..", 2000).show();
			if(!isStart){
				mLocClient.requestLocation();
				isStart = true;
			}
		}else*/ 
		if(item.getItemId() == R.id.menu_share){
			//分享到
			UIHelper.showShareDialog(this, "我正在使用福州便民自行车，你也一起来吧...", "http://weibo.com");
		}
		return true;
	}
}
