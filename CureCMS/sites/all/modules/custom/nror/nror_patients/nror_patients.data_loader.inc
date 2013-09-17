<?php

/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

module_load_include('php', 'nror_patients', 'CureDataLoader');

/**
 * Menu callback
 * @see nror_patients_menu()
 */
function nror_patients_data_loader_form($form, $form_state) {
  $form = array();
  
  nror_patients_check_tmp_dir(NROR_PATIENTS_TMP_DIR);
  
  $maxSize = ini_get('upload_max_filesize');   
  
  $form['import_file'] = array(
    '#type' => 'file',
    '#title' => t('Choose a file containing patients data'),
    '#description' => t('Only zip files are allowed. File size should not exceed ' . '<span style="color:red">' . $maxSize . '</span>')    
  );

  $form['upload'] = array(
    '#type' => 'submit',
    '#value' => t('Send'),
    '#name' => 'upload',
  );
  
  $form['back'] = array(
    '#type' => 'submit',
    '#value' => t('Back'),
    '#name' => 'back',
  );
  
  return $form;
}

/**
 * nror_patients_data_loader_form() validate funciton
 * @see nror_patients_data_loader_form()
 */
function nror_patients_data_loader_form_validate($form, &$form_state) {
  $op = $form_state['triggering_element']['#name'];
  if ($op == 'upload') {
    $fileInfo = pathinfo($_FILES['files']['name']['import_file']);    
    if ($fileInfo['extension'] != 'zip') {
      form_set_error('import_file', t('The specified file could not be uploaded. Only files with .zip extensions are allowed.'));
    }    
  }
}

/**
 * nror_patients_data_loader_form() submit funciton
 * @see nror_patients_data_loader_form()
 */
function nror_patients_data_loader_form_submit($form, &$form_state) {
  $op = $form_state['triggering_element']['#name'];
  if ($op == 'upload') {
    $fileName = $_FILES['files']['name']['import_file'];
    
    if (move_uploaded_file($_FILES['files']['tmp_name']['import_file'], NROR_PATIENTS_TMP_DIR . '/' . $fileName)) {
      $response = CureDataLoader::uploadFile(NROR_PATIENTS_TMP_DIR . '/' . $fileName);
      $responseDecode = json_decode($response, true);
      
      if ($responseDecode['status'] == CureDataLoader::RESPONSE_OK) {
        drupal_set_message('Data file was sent successfuly');
      }
      else {
        drupal_set_message(t('Data file was not sent. Please, try later'), 'error');
      }
      chmod(NROR_PATIENTS_TMP_DIR . '/' . $fileName, 0777);
      unlink(NROR_PATIENTS_TMP_DIR . '/' . $fileName);
    }     
  }
  else if ($op == 'back') {
    drupal_goto('dashboard');
  }
}