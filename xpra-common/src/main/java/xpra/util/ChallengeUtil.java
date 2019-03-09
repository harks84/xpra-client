package xpra.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ChallengeUtil {
	
	public static String generateDigest(String digestType, String key, String salt) {
		String digest = "";
		if (digestType.startsWith("hmac")) {
			String hash="MD5";
			if (digestType.indexOf("+")>0) {
				hash = digestType.split("\\+")[1];
			}
			
			
			SecretKeySpec signingKey = new SecretKeySpec((key).getBytes(), "Hmac" + hash.toUpperCase());
			Mac mac;
			try {
				mac = Mac.getInstance("Hmac" + hash.toUpperCase());
				mac.init(signingKey);

				digest = toHexString(mac.doFinal((salt).getBytes()));
			} catch (NoSuchAlgorithmException e) {
				return null;
			} catch (InvalidKeyException e) {
				return null;
			}
			

			
		} else if ("xor".equals(digestType)) {
			String trimmed_salt = salt.substring(0, key.length());
			return xorString(trimmed_salt, key);
		} else {
			return null;
		}
		return digest;
	}
	
	private static String toHexString(byte[] bytes) {
		 StringBuffer hash = new StringBuffer();
	      for (int i = 0; i < bytes.length; i++) {
	        String hex = Integer.toHexString(0xFF & bytes[i]);
	        if (hex.length() == 1) {
	          hash.append('0');
	        }
	        hash.append(hex);
	      }
	      return hash.toString();
	}
	
	private static String xorString(String str1, String str2){
		String result = "";

		for(int i = 0; i < str1.length(); i++) {
			result += Character.toChars((int)str1.charAt(i) ^ (int)str2.charAt(i))[0];
		}
		return result;
	}
	
	public static String getClientSalt(int length) {
		UUID uuid = UUID.randomUUID();
		String salt = uuid.toString() + uuid.toString();
		//TODO actually increase uuid to correct length
		return salt.substring(0,length);
	}
	

}
