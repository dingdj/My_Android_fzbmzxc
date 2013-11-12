package com.android.ddj.fzbmzxc;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.android.ddj.fzbmzxc.util.MathUtil;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.content.Context;
import android.util.Log;

/**
 * 站点管理类
 * @author dingdj
 * Date:2013-11-7下午3:27:33
 *
 */
public class StationManager {
	private static final String TAG = "StationManager";
	private static final String STATIONFILE = "stations.xml";
	private static final String STATION = "station";
	private static final boolean DEBUG = true;
	
	private Station[] stations = new Station[0];
	
	private static StationManager instance;
	
	public static StationManager getInstance(Context context){
		if(instance == null){
			instance = new StationManager(context);
		}
		return instance;
	}
	
	private StationManager(Context context){
		loadStations(context);
	}
	
	/**
	 * 从xml中加载数据
	 * @author dingdj
	 * Date:2013-11-7下午3:33:59
	 *  @param context
	 */
	private void loadStations(Context context){
		try{
			ArrayList<Station> stationList = new ArrayList<Station>();
			InputStream is;
			is = context.getAssets().open(STATIONFILE);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(is);
			Element root = document.getDocumentElement();
			NodeList nodeList = root.getElementsByTagName(STATION);
			if (nodeList == null || nodeList.getLength() == 0) {
	            return;
	        }
	        for (int i = 0; i < nodeList.getLength(); i++) {
	            Element element = (Element) nodeList.item(i);
	            int id = Integer.parseInt(element.getAttribute("id"));
	            String stationName = getAttrText(element, "name");
	            String stationAddress = getAttrText(element, "address");
	            String lng = getAttrText(element, "lng");
	            double stationLng = 0;
	            if(lng != null){
	            	stationLng = Double.parseDouble(lng);
	            }
	            String lat = getAttrText(element, "lat");
	            double stationLat = 0;
				if(lat != null){
					stationLat = Double.parseDouble(lat);
				}
				Station station = new Station(stationName, stationAddress, stationLng, stationLat);
				stationList.add(station);
	        }
			stations = stationList.toArray(stations);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
     * 获得指定元素的子元素中的文本内容
     */
    private String getAttrText(Element element, String name) {
        NodeList nodeList2 = element.getChildNodes();
        Node node = null;
        for (int i = 0; i < nodeList2.getLength(); i++) {
            node = nodeList2.item(i);
            if (node.getNodeName().equals(name)) {
                return node.getTextContent();
            }
        }
        return null;
    }
	
	/**
	 * 打印状态
	 * @author dingdj
	 * Date:2013-11-7下午3:58:36
	 */
	public void dump(Station[] stationArray){
		if(DEBUG){
			for (Station station : stationArray) {
				Log.d(TAG, station.toString());
			}
		}
	}

	public Station[] getStations() {
		return stations;
	}
	
	/**
	 * 获取前n个离指定位置最近的站点
	 * @author dingdj
	 * Date:2013-11-8下午3:50:14
	 *  @param n 0 或 -1 返回全部
	 *  @param sourceLngLat
	 *  @return
	 */
	public Station[] getTopNNearStation(int n, double[] sourceLatLng){
		if(n <= 0 || n > stations.length){
			n = stations.length;
		}
		Station[] rtn = new Station[n];
		GeoPoint sourcePoint = new GeoPoint(MathUtil.converDoubleToInt(sourceLatLng[0]), 
				MathUtil.converDoubleToInt(sourceLatLng[1]));
		Set<Station> set = new TreeSet<Station>();
		for (Station station : stations) {
			double distance = getDistance(station, sourcePoint);
			station.setDistance(distance);
			set.add(station);
		}
		int index = 0;
		for (Station station : set) {
			if(index < n){
				rtn[index] = station;
				index++;
			}else{
				break;
			}
		}
		return rtn;
	}
	
	/**
	 * 返回两点间的距离
	 * @author dingdj
	 * Date:2013-11-8下午4:01:01
	 *  @param station
	 *  @param sourceLngLat
	 *  @return
	 */
	private double getDistance(Station station, GeoPoint sourcePoint){
		GeoPoint targetPoint = new GeoPoint(station.getI_lat(), station.getI_lng());//lat lng
		double distance = DistanceUtil.getDistance(sourcePoint, targetPoint);
		return distance;
	}

}
