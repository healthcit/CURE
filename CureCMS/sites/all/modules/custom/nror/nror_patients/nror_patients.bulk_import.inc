<?php

/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

require_once libraries_get_path('phpexcel', false) . '/PHPExcel/IOFactory.php';
 
/**
 * Menu callback
 * @see nror_menu()
 */
function nror_patients_bulk_import_form($form, $form_state) {
  global $user;
  drupal_add_css(drupal_get_path('module', 'nror_dashboard').'/css/nror_dashboard.css', 'file');
  drupal_add_js(drupal_get_path('module', 'nror_dashboard').'/js/jquery.tablesorter.js'); 
  drupal_add_js(drupal_get_path('module', 'nror_dashboard').'/js/nror_dashboard.js');

  _nror_patients_check_attending_physicians();

  $form = array();

  $form['import_file'] = array(
    '#type' => 'file',
    '#title' => t('Choose a file containing patients data'),
    '#description' => t('Only files with xls, xlsx and csv extensions are allowed.'),
  );

  $form['topControlsBar'] = array(
    '#type' => 'container',
    '#attributes' => array(
      'class' => array('import-top-controls-bar')
    )
  );

  $form['topControlsBar']['upload'] = array(
    '#type' => 'submit',
    '#value' => t('Upload'),
    '#name' => 'upload',
  );
  
  $form['topControlsBar']['back'] = array(
    '#type' => 'submit',
    '#value' => t('Back'),
    '#name' => 'back',
  );

  if (!empty($form_state['values']['uploaded'])) {
    $file = $form_state['values']['uploaded'];
    $uri = $file->uri;
    $data = _cure_patients_parse_excel_file($uri);
    if (!empty($data) && is_array($data)) {
      $table_data = _nror_patients_prepare_form_table_data($data);
      if (nror_rc_is_coordinator($user)) {
        unset($table_data['header']['facility']);
      }
      foreach ($table_data['header'] as $name => $title) {
        $header[$name] = '<p class="header-sort">'. $title . '</p>';
      }
      $options = $table_data['options'];
      if (!empty($options)) {
        $form['find'] = array('#markup' => '<div class="search">Search: <input type="text" class="search-box"></div>');
          
        $form['patients'] = array(
          '#prefix' => '<div class="dashboard-table">',
          '#suffix' => '</div>',
          '#type'    => 'tableselect',
          '#header'  => $header,
          '#options' => $options,
          '#attributes' => array(
            'class' => array(
              'nror_administration',
              'tablesorter',
              'users-table'
            )
          ),          
        );
        $form['import'] = array(
          '#prefix' => '<div class="import-control">',
          '#suffix' => '</div>',
          '#type' => 'submit',
          '#value' => t('Import'),
          '#name' => 'import',
          '#attributes' => array(
            'class' => array(
              'nror_import_button',
            )
          ),
        );
      }
      else {
        drupal_set_message(t('No existing practice and providers were recognized'), 'error');
      }
    }
    else {
      drupal_set_message(t('No patient data was loaded'), 'error');
    }
  }

  return $form;
}

/**
 * nror_patients_bulk_import_form() validate funciton
 * @see nror_patients_bulk_import_form()
 */
function nror_patients_bulk_import_form_validate($form, &$form_state) {
  $op = $form_state['triggering_element']['#name'];
  if ($op == 'upload') {
    $file = file_save_upload('import_file', array(
      'file_validate_extensions' => array('xls xlsx csv'),
    ));
    if ($file) {
      $form_state['storage']['file'] = $file;
    }
    else {
      form_set_error('import_file', t('No file was uploaded.'));
    }
  }
}

/**
 * nror_patients_bulk_import_form() submit funciton
 * @see nror_patients_bulk_import_form()
 */
function nror_patients_bulk_import_form_submit($form, &$form_state) {
  $op = $form_state['triggering_element']['#name'];
  if ($op == 'upload') {
    $form_state['values']['uploaded'] = $form_state['storage']['file'];
    unset($form_state['storage']['file']);
    $form_state['rebuild'] = true;
  }
  else if($op == 'back') {
    drupal_goto('dashboard');
  }
  else if ($op == 'import') {
    $patients = $form_state['values']['patients'];
    helpers_filter_checkboxes($patients);
    $options = $form['patients']['#options'];
    $role = user_role_load_by_name(NROR_USER_ROLE_PATIENT);

    foreach ($options as $key => $values) {
      if (isset($patients[$key])) {
        $facility = nror_facilities_get_by_title(trim($values['facility']));
        $physician = nror_get_uid_by_name(trim($values['physician']));
        $values['name'] = $facility->code . "-" . $values['name'];
        $values['facility'] = $facility->fid;
        $values['physician'] = $physician;
        $data = array();
        foreach ($values as $param => $value) {
          $data[$param] = $value;
        }
        $data['status'] = 1;
        $data['user_roles'] = $role->rid;
        $data['notify'] = 0;
        _nror_patients_save_patient(drupal_anonymous_user(), $data);       
      }
    }
    drupal_goto('dashboard');
  }
}

/**
 * Processes uploaded file and returns data
 * Uses PHPExcel
 * @see PHPExcel_IOFactory
 */
function _cure_patients_parse_excel_file($file) {
  $info = pathinfo($file);
  $extension = $info['extension'];

  if ($extension == 'xls') {
    $objReader = PHPExcel_IOFactory::createReader('Excel5');
  }
  else if ($extension == 'csv') {
    $objReader = PHPExcel_IOFactory::createReader('CSV');
  }
  else if ($extension == 'xlsx') {
    require_once libraries_get_path('phpexcel', false) . '/XlsxToCsvConverter.php';
    
    $converter = new XlsxToCsvConverter();
    $converter->setTempDir('sites/default/files');
    $newcsvfile = $converter->getCsvFile($file);
    
    $objReader = PHPExcel_IOFactory::createReader('CSV');
    $objPHPExcel = $objReader->load($newcsvfile);
    $data = $objPHPExcel->getActiveSheet()->toArray(null,true,true,true);
    
    $converter->clearTempData();
    
    return $data;
  }
 
  $objPHPExcel = $objReader->load($file);
  $data = $objPHPExcel->getActiveSheet()->toArray(null,true,true,true);
   
  return $data;
}

/**
 * Prepares import form table data
 * @see _nror_patients_filter_import_data()
 */
function _nror_patients_prepare_form_table_data($data) {
  $titles = $data[1];
  unset($data[1]);
  $header = array();
  $options = array();
  $params = _nror_patients_prepare_params($titles);
  foreach ($titles as $cell => $title) {
    $header[$params[$cell]] = t($title);
  }

  if (!empty($data)) {
    foreach ($data as $row => $values) {
      foreach ($values as $cell => $value) {
        if (!isset($options[$row])) {
          $options[$row] = array();
        }
        $options[$row][$params[$cell]] = $value;
      }
    }
    $options = _nror_patients_filter_import_data($options);
  }
  return array(
    'header' => $header,
    'options' => $options
  );
}

/**
 * Prepares db table params array
 */
function _nror_patients_prepare_params($header) {
  $params = array();
  foreach ($header as $key => $value) {
    if ($value == 'First name') {
      $param = 'profile_first_name';
    }
    elseif ($value == 'Last name') {
      $param = 'profile_last_name';
    }
    elseif ($value == 'Email') {
      $param = 'mail';
    }
    elseif ($value == 'Provider') {
      $param = 'physician';
    }
    elseif ($value == 'Practice') {
      $param = 'facility';
    }
    elseif ($value == 'External ID') {
      $param = 'name';
    }
    $params[$key] = $param;
  }
  return $params;
}

/**
 * Filters imported data.
 * Returns rows with both existing facility and physician
 */
function _nror_patients_filter_import_data($data) {
  global $user;
  $result = array();
  $mails = array();
  foreach ($data as $row => $params) {
    $attending_physician_name = trim($params['physician']);
    if (nror_rc_is_coordinator($user)) {
      $params['facility'] = nror_facilities_get_user_facility($user->uid)->title;
    }
    $facility_title = trim($params['facility']);
    $mail = trim($params['mail']);
    $first_name = trim($params['profile_first_name']);
    $last_name = trim($params['profile_last_name']);
    $external_id = trim($params['name']);
 
    if (!empty($attending_physician_name) && !empty($facility_title) && !empty($mail) && !empty($external_id)) {
      $facility = nror_facilities_get_by_title($facility_title);
      $attending_physician = nror_physicians_get_by_name($attending_physician_name);
      if (!empty($attending_physician) && !empty($facility) && $attending_physician->facility == $facility->fid) {
        if (user_load_by_mail($mail) != false) {
          drupal_set_message(t('Patient with email address %m is already registered.', array('%m' => $mail)));
          continue;
        }
        if (user_load_by_name($external_id) != false) {
          drupal_set_message(t('Patient with external ID %m is already registered.', array('%m' => $external_id)));
          continue;
        }
        if (!empty($first_name) && cure_users_validate_name($first_name)) {
          drupal_set_message('%v : Invalid format. Must contain only upper and lower case letters, dashes and spaces.', array('%v' => $first_name));
          continue;
        }
        if (!empty($last_name) && cure_users_validate_name($last_name)) {
          drupal_set_message('%v : Invalid format. Must contain only upper and lower case letters, dashes and spaces.', array('%v' => $last_name));
          continue;
        }
        $mails[] = $mail;
        $result[$row] = $params;
      }
    }
  }

  return $result;
}