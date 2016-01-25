package com.vidmt.of.plugin.sub.extdb.pwdstrategy;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import javax.security.sasl.SaslException;
import javax.xml.bind.DatatypeConverter;

import org.jivesoftware.openfire.auth.ScramUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScramStrategy extends BlowfishStrategy {
	private static Logger log = LoggerFactory.getLogger(ScramStrategy.class);
	private static final Random random = new Random();
	
	public static void main(String[] args){
		byte[] saltShaker = DatatypeConverter.parseBase64Binary("4Ab7yqZf8lAz/s+MPly5Kw/cdrkjkUHeK5hhWfsGOKA=");
		System.out.println(saltShaker.length);
		System.out.println(Arrays.toString(saltShaker));
		String salt=DatatypeConverter.printBase64Binary(saltShaker);
		System.out.println(salt);
		
		int iterations = ScramUtils.DEFAULT_ITERATION_COUNT;
		// JiveGlobals.getIntProperty("sasl.scram-sha-1.iteration-count",
		// ScramUtils.DEFAULT_ITERATION_COUNT);
		byte[] saltedPassword = null, clientKey = null, storedKey = null, serverKey = null;
		try {
			saltedPassword = ScramUtils.createSaltedPassword(saltShaker, "123456", iterations);
			clientKey = ScramUtils.computeHmac(saltedPassword, "Client Key");
			storedKey = MessageDigest.getInstance("SHA-1").digest(clientKey);
			serverKey = ScramUtils.computeHmac(saltedPassword, "Server Key");
		} catch (SaslException | NoSuchAlgorithmException e) {
			log.warn("Unable to persist values for SCRAM authentication.", e);
		}
		
		String storedKeyStr=DatatypeConverter.printBase64Binary(storedKey);
		String serverKeyStr=DatatypeConverter.printBase64Binary(serverKey);
		String keyString = String.format("%s@%s@%s@%s@%s", "OS6Tik088cWEay6", storedKeyStr, serverKeyStr, salt, iterations);
		System.out.println(keyString);
	}

	@Override
	protected String fmtpwd(String alg, String keyString, String plainpwd) {
		byte[] saltShaker = new byte[32];
		random.nextBytes(saltShaker);
//		byte[] saltShaker = DatatypeConverter.parseBase64Binary("4Ab7yqZf8lAz/s+MPly5Kw/cdrkjkUHeK5hhWfsGOKA=");
//		plainpwd="123456";

		int iterations = ScramUtils.DEFAULT_ITERATION_COUNT;
		// JiveGlobals.getIntProperty("sasl.scram-sha-1.iteration-count",
		// ScramUtils.DEFAULT_ITERATION_COUNT);
		byte[] saltedPassword = null, clientKey = null, storedKey = null, serverKey = null;
		try {
			saltedPassword = ScramUtils.createSaltedPassword(saltShaker, plainpwd, iterations);
			clientKey = ScramUtils.computeHmac(saltedPassword, "Client Key");
			storedKey = MessageDigest.getInstance("SHA-1").digest(clientKey);
			serverKey = ScramUtils.computeHmac(saltedPassword, "Server Key");
		} catch (SaslException | NoSuchAlgorithmException e) {
			log.warn("Unable to persist values for SCRAM authentication.", e);
		}
		
		String salt = DatatypeConverter.printBase64Binary(saltShaker);
		String storedKeyStr=DatatypeConverter.printBase64Binary(storedKey);
		String serverKeyStr=DatatypeConverter.printBase64Binary(serverKey);
		keyString = String.format("%s@%s@%s@%s@%s", keyString, storedKeyStr, serverKeyStr, salt, iterations);
		return super.fmtpwd("scram", keyString, plainpwd);
	}
	
	@Override
	protected String getKeyString(String rawkeystring) {
		String[] keyarr = rawkeystring.split("@");
		return keyarr[0];
	}

}
