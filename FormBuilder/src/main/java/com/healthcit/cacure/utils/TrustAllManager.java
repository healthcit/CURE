/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.utils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * TrustManager description
 * 
 * @author nik
 */

public class TrustAllManager implements X509TrustManager {

  public TrustAllManager() {
    // create/load keystore
  }

  @Override
public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
  }

  @Override
public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
  }

  @Override
public X509Certificate[] getAcceptedIssuers() {
    return null;
  }

}
