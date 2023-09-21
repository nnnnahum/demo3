package com.demo.utils;

import java.net.URLEncoder;
import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordEncrypter {

	private static final Logger log = LoggerFactory.getLogger(PasswordEncrypter.class);

	public static String encrypt(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			return URLEncoder.encode(new String(md.digest(password.getBytes())), "UTF-8");
		}catch(Exception e) {
			log.error("Failed to encode: ", e);
			return null;
		}
	}
}
