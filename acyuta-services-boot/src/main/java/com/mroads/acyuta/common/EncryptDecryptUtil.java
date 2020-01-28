package com.mroads.acyuta.common;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;

/**
 * @author sameer
 *
 */
public class EncryptDecryptUtil {

	private static Logger log = Logger.getLogger(EncryptDecryptUtil.class);


	/**
	 * This method is using for encrypt string
	 * @param value
	 * @return
	 */
	public static String getEncryptKey(String value){
		String encryptKey = null;
		try{
			byte[] bytesEncoded = Base64.encodeBase64(value.getBytes());
			encryptKey = new String(bytesEncoded);
		}catch(Exception e){
			log.error("Exception while encrypting key ",e);
		}
		return encryptKey;
	}

	/**
	 * This method is using decrypt code
	 * @param key
	 * @return
	 */
	public static String getDecryptKey(String key){
		String decryptKey=null;
		try{
			byte[] valueDecoded = Base64.decodeBase64(key);
			decryptKey = new String(valueDecoded);
		}catch(Exception e){
			log.error("Exception while decrypting key ",e);
		}
		return decryptKey;
	}

}
