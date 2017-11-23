package com.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by winter on 2015/9/22.
 * @author winter
 */
public class MD5Utils {

	private static final int BUFFER_SIZE = 8192;

	public static byte[] digest(String message) throws NoSuchAlgorithmException {
		return digest(message.getBytes());
	}

	public static byte[] digest(String message, String charsetName) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return digest(message.getBytes(charsetName));
	}

	public static byte[] digest(byte[] input) throws NoSuchAlgorithmException {
		return digest(input, 0, input.length);
	}

	public static byte[] digest(byte[] input, int offset, int len) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(input, offset, len);
		return messageDigest.digest();
	}

	public static byte[] digest(InputStream inputStream) throws NoSuchAlgorithmException, IOException {
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		byte[] bytes = new byte[BUFFER_SIZE];
		int byteCount;
		while ((byteCount = inputStream.read(bytes)) > 0) {
			messageDigest.update(bytes, 0, byteCount);
		}
		return messageDigest.digest();
	}

    public static String toHexString(byte[] md5Bytes) {
        StringBuilder hexValue = new StringBuilder();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = md5Bytes[i] & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    public static String md5(String str) {
		try {
			return toHexString(digest(str));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String md5sign(String str) {
		try {
			return toHexString(digest(str+"&55356dasdFzc4"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
}
