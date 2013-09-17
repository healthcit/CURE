<?php
/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

function cure_forms_logs_get($fid) {
  return db_select('cure_forms', 'cf')->fields('cf')->condition('cf.fid', $fid)->execute()->fetch();
}

function cure_forms_logs_get_all_forms() {
  return db_select('cure_forms', 'cf')->fields('cf')->execute()->fetchAll();
}

function cure_forms_logs_get_logs_by_form_id($formId) {
  $query = db_select('cure_forms_logs', 'fl');
  $query->join('users', 'u', 'u.uid=fl.user_id');
  $query->fields('u', array('name'));
  $query->fields('fl', array('date', 'owner_id'));
  $query->condition('fl.form_id', $formId);
  $query->orderBy('fl.date', 'DESC');
  
  $result = $query->execute()->fetchAll();

  return $result;   
}

function cure_forms_logs_save_form($data) {
  $record =  array(
    'fid' => $data['fid'],
    'name' => $data['name']
  );
  
  if (!cure_forms_logs_get($data['fid'])) {
      drupal_write_record('cure_forms', $record);
  }
}

function cure_forms_logs_write_access_log($data) {
  $date = date("Y-m-d H:i:s", time());  
    
  $record =  array(
    'flid' => null,
    'form_id' => $data['form_id'],
    'owner_id' => $data['owner_id'],
    'user_id' => $data['user_id'],
    'date' => $date 
  );
  
  db_query('INSERT into `cure_forms_logs` (`form_id`, `owner_id`, `user_id`, `date`) 
    VALUES (\'' . $data['form_id'] . '\',\'' . $data['owner_id'] . '\',\'' . $data['user_id'] .  '\', NOW())');
  
  //return drupal_write_record('cure_forms_logs', $record);  
}