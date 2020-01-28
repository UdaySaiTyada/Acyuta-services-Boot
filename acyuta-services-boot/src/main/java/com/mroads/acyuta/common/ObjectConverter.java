/**
 * This computer program is the confidential information and proprietary trade
 * secret of mRoads LLC. Possessions and use of this program must conform 
 * strictly to the license agreement between the user and mRoads LLC,
 * and receipt or possession does not convey any rights to divulge, reproduce, 
 * or allow others to use this program without specific written authorization 
 * of mRoads LLC.
 * 
 * Copyright (c) 2015 mRoads LLC. All Rights Reserved.
 *
 */
package com.mroads.acyuta.common;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * 
 * @author P Vanisree
 *
 */
@Component("objectcoverter")
public class ObjectConverter {
	public static Object convert(Object src, Object dest) {
		if (null != src && null != dest) {
			BeanUtils.copyProperties(src, dest);
		}
		return dest;
	}

}
