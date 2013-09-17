<?php

/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

class CureDataLoader
{
  const RESPONSE_OK = 'ok';
  
  private static $_ip = '127.0.0.1';

  /**
   * Class constructor
   */
  private function __construct()
  {

  }

  /**
   * Clone magic method.
   */
  private function __clone()
  {
  }
  
  private static function apiUri()
  {
    $settings = parse_ini_file('settings.ini');
    $ip = self::$_ip;
    $port = $settings['port'];
    $context = $settings['context'];
    return 'http://' . $ip . ':' . $port . '/' . $context . '/api';
  }
  
  public static function url($action)
  {
    $url = self::apiUri();
    $url .= '/' . $action;
    
    return $url;
  }
  
  
  public static function uploadFile($file)
  {
    $settings = parse_ini_file('settings.ini');
    $accountId = $settings['account_id'];
    $token = $settings['token'];
    $url = self::url('UploadFile');
    $data = array(
      'file'       => '@' . $file, 
      'account_id' => $accountId,
      'token'      => $token
    );
    
    return self::send($url, $data);
  }
  
  private function send($url, $data = array())
  {
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, $data);
    $result = curl_exec($ch);
    curl_close($ch);
    
    return $result;
  }
}
  
