/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.services;

import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.CryptoTools.CryptoBase64;
import com.CryptoTools.CryptoMD5;

/**
 * Carry-over class from legacy code.
 * Used to decrypt the authentication token required to register new users.
 * @author Oawofolu
 *
 */
public class Crypto {
	private static Logger log = LoggerFactory.getLogger(Crypto.class);
	
	// The Private Key
	private String privateKey;
	
	// default constructor
	public Crypto(){}
	
	// explicit constructor
	public Crypto( String privateKey ) {
		this.privateKey = privateKey;
	}	
	
	public boolean validateHash(String token) {

		CryptoBase64 b64 = new CryptoBase64();
		CryptoMD5 md5 = new CryptoMD5();
		
		try {

			if( token == null || token.length() == 0 ) {
				return false;
			}
			
			String[] tokenArray = token.split("\\|",2);
			
			if( tokenArray.length < 2 ) {
				return false;
			}
			
			String passedInKey = tokenArray[0];
			String passedInHash = tokenArray[1];
				
			String decryptedTxt = b64.decryptText(passedInKey);
			String decryptedKey = decryptedTxt + "|" + privateKey;
			String computedHash = md5.encryptText(decryptedKey);
			
			if( passedInHash.equals(computedHash) ) {
				return true;
			} 
			
		} catch (InvalidKeyException e) {
			log.error("Invalid Key while decoding hash.", e);
		} catch (BadPaddingException e) {
			log.error("Bad padding found while decoding hash.", e);
		} catch (IllegalBlockSizeException e) {
			log.error("Illegal Block Size while decoding hash.", e);
		} catch ( Exception e ) {
			log.error("Exception while decrypting.", e);	
		}
		
		return false;
	}
}
