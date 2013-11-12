/**
 * @author dingdj
 * Date:2013-11-8下午4:07:17
 *
 */
package com.android.ddj.fzbmzxc.util;

/**
 * @author dingdj
 * Date:2013-11-8下午4:07:17
 *
 */
public class MathUtil {
	
	/**
	 * 将double中的.去掉并转成int
	 * @author dingdj
	 * Date:2013-11-8下午4:07:43
	 *  @param d
	 *  @return
	 */
	public static int converDoubleToInt(double d){
		String str = d+"";
		int size = str.indexOf(".");
		int length = str.length();
		for(int i =0; i <= 6 -(length-size); i++){
			str +="0";
		}
		str = str.replace(".", "");
		return Integer.parseInt(str);
	}

}
