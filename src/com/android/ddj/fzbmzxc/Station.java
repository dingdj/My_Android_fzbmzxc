package com.android.ddj.fzbmzxc;

import com.android.ddj.fzbmzxc.util.MathUtil;

/**
 * 站点类
 * @author dingdj
 * Date:2013-11-7下午3:23:19
 *
 */
public class Station implements Comparable<Station>{
	
	private String name;
	private String address;
	private double lng;//经度
	private double lat;//纬度
	private double distance;
	private int i_lng;
	private int i_lat;
	/**
	 * @param name
	 * @param address
	 * @param lng
	 * @param lat
	 */
	public Station(String name, String address, double lng, double lat) {
		super();
		this.name = name;
		this.address = address;
		this.lng = lng;
		this.lat = lat;
		this.i_lat = MathUtil.converDoubleToInt(lat);
		this.i_lng = MathUtil.converDoubleToInt(lng);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}

	public int getI_lng() {
		return i_lng;
	}

	public void setI_lng(int i_lng) {
		this.i_lng = i_lng;
	}

	public int getI_lat() {
		return i_lat;
	}

	public void setI_lat(int i_lat) {
		this.i_lat = i_lat;
	}

	@Override
	public String toString() {
		return "name:"+name+"|"+
				"address:"+address+"|"+
				"lng:"+i_lng+"|"+
				"lat:"+i_lat +"|"+
				"distance:"+distance;
	}
	
	

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	@Override
	public int compareTo(Station another) {
		if(another != null){
			if(another.getDistance() - this.getDistance() > 0){
				return -1;
			}else if(another.getDistance() - this.getDistance() == 0){
				return 0;
			}else{
				return 1;
			}
		}
		return 1;
	}
	
	
}
