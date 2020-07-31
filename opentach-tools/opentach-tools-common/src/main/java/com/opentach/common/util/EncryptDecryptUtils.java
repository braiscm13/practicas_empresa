package com.opentach.common.util;

import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

/**
 * Encrypt/Decrypt utils. Configure secret pass first and then you can use it to encrypt and decrypt
 *
 */
public class EncryptDecryptUtils {
	private final static Logger	LOGGER	= Logger.getLogger(EncryptDecryptUtils.class);
	private static Key			key;
	private static Cipher		cipher;

	/**
	 * Configure secret password
	 *
	 * @param secret
	 */
	public static void configure(String secret) {
		try {
			EncryptDecryptUtils.cipher = Cipher.getInstance("AES");
			EncryptDecryptUtils.key = new SecretKeySpec(secret.getBytes(), "AES");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			EncryptDecryptUtils.LOGGER.error(null, e);
		}
	}

	/**
	 * Encrypt plain text
	 *
	 * @param str
	 *            plain message
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String str) throws Exception {
		if (EncryptDecryptUtils.key == null) {
			throw new Exception("ConfigureFirst");
		}
		// Configure encrypt
		EncryptDecryptUtils.cipher.init(Cipher.ENCRYPT_MODE, EncryptDecryptUtils.key);
		// Encode the string into bytes using utf-8
		byte[] utf8 = str.getBytes("UTF8");
		// Encrypt
		byte[] enc = EncryptDecryptUtils.cipher.doFinal(utf8);
		// Encode bytes to base64 to get a string
		return Base64.encodeBase64String(enc);
	}

	/**
	 * Decrypt encode text
	 *
	 * @param messageEnc
	 *            encrypted message
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String messageEnc) throws Exception {
		if (EncryptDecryptUtils.key == null) {
			throw new Exception("ConfigureFirst");
		}
		// Configure decrypt
		EncryptDecryptUtils.cipher.init(Cipher.DECRYPT_MODE, EncryptDecryptUtils.key);
		// Decode base64 to get bytes
		byte[] dec = Base64.decodeBase64(messageEnc);
		// Decrypt
		byte[] utf8 = EncryptDecryptUtils.cipher.doFinal(dec);

		// Decode using utf-8
		return new String(utf8, "UTF8");
	}

}
