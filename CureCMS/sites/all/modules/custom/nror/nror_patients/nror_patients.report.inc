<?php

/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

define('NROR_PATIENTS_REPORT_TMP_DIR', NROR_PATIENTS_TMP_DIR . '/report/');

define('NROR_PATIENTS_REPORT_ZIP_NAME', 'PatientsDataReport.zip');

define('NROR_PATIENTS_REPORT_QUESTION', 'question');
define('NROR_PATIENTS_REPORT_TABLE', 'table');

function nror_patients_report_form($form, $form_state) {
  nror_patients_check_tmp_dir(NROR_PATIENTS_TMP_DIR);
  nror_patients_check_tmp_dir(NROR_PATIENTS_REPORT_TMP_DIR);
  
  if ($form_state['submitted'] == true && !empty($form_state['values']['facility'])) {
    nror_patients_report_download_data($form_state['values']['facility']);
  }
  $facilities = nror_facilities_get_all();

  $options = array();
  foreach ($facilities as $facility) {
    $options[$facility->fid] = $facility->title;
  }

  $form = array();

  $form['facility'] = array(
    '#type' => 'select',
    '#title' => 'Select Practice',
    '#options' => $options
  );

  $form['submit'] = array(
    '#type' => 'submit',
    '#value' => 'Download'
  );

  return $form;
}

function nror_patients_report_form_submit($form, &$form_state) {
  $facility = $form_state['values']['facility'];
  if (!empty($facility)) {
    $form_state['rebuild'] = true;
  }
}

function nror_patients_report_download_data($facilityId) {
  $patients = nror_patients_get_all_by_facility($facilityId);
  if (!empty($patients)) {
    foreach ($patients as $patient) {
      $entityId = $patient->entityId;
      $groupId = cure_groups_get_group_id($patient->name);
      $data = _nror_patients_report_get_patient_data($entityId, $groupId);
      _nror_patients_report_save_xml($patient->uid, $data);
    }
    _nror_patients_report_create_zip();
    if (file_exists(NROR_PATIENTS_TMP_DIR.'/'.NROR_PATIENTS_REPORT_ZIP_NAME)) {
      header("Content-Length: " . filesize(NROR_PATIENTS_TMP_DIR.'/'.NROR_PATIENTS_REPORT_ZIP_NAME));
      header('Content-Type: application/zip');
      header('Content-Disposition: attachment;filename="'.NROR_PATIENTS_REPORT_ZIP_NAME.'"');
      readfile(NROR_PATIENTS_TMP_DIR.'/'.NROR_PATIENTS_REPORT_ZIP_NAME);
      _nror_patients_clear_tmp_dir();
    }
    else {
      drupal_set_message('Failed to create zip, maybe no data was found', 'error');
    }
  }
  else {
    drupal_set_message('No patients found', 'error');
  }
}

function _nror_patients_report_get_patient_data($entityId, $groupId) {
  $call = RestCall::getInstance();
  $request = SimpleXmlParser::toArray($call->getAllUserModules($entityId, $groupId, NROR_CONTEXT_PATIENT));
  $formData = array();
  if (!empty($request['module'])) {
    $module = $request['module'];
    if (!empty($module)) {
      if (isset($module[0])) {
        $module = $module[0];
      }
      if (!empty($module['form'])) {
        if (isset($module['form']['@attributes'])) {
          $module['form'] = array($module['form']);
        }
        foreach ($module['form'] as $form) {
          _nror_prepare_data_form($formData, $form, null, $entityId, $groupId);          
        }
      }
    }
  }
  return $formData;
}

function _nror_prepare_data_form(&$formData, $form, $instanceId, $entityId, $groupId) {
  $call = RestCall::getInstance();
  $formId = $form['@attributes']['id'];
  $formInstances = $form['formInstances']['instance'];
  if (isset($formInstances['@attributes'])) {
    $formInstances = array($formInstances);
  }
  foreach ($formInstances as $instance) {
    $currentInstanceId = $instance['@attributes']['instanceId'];
    if ($instanceId == $currentInstanceId || !isset($instanceId)) {
      $formData[$formId]['instances'][$currentInstanceId]['data'] = json_decode($call->getUserFormInstanceData($entityId, $groupId, $formId, $currentInstanceId, RestCall::FORM_DATA_FORMAT_JSON), true);
      if (isset($form['form'])) {
        $childForms = $form['form'];
        if (isset($childForms['@attributes'])) {
          $childForms = array($childForms);
        }

        foreach ($childForms as $childForm) {
          $childFormInstances = $childForm['formInstances']['instance'];
          if (isset($childForm['formInstances']['instance']['@attributes'])) {
            $childFormInstances = array($childForm['formInstances']['instance']);
          }
          foreach ($childFormInstances as $instance) {
            $parentInstanceId = $instance['@attributes']['parentInstanceId'];
            $childInstanceId = $instance['@attributes']['instanceId'];
            if (isset($childInstanceId) && isset($parentInstanceId) && $parentInstanceId == $currentInstanceId) {
              _nror_prepare_data_form($formData[$formId]['instances'][$currentInstanceId]['forms'], $childForm, $childInstanceId, $entityId, $groupId);
            }
          }
        }
      }
    }
  }
}

function _nror_patients_report_save_xml($name, array $data) {
  $tmpReportDir = NROR_PATIENTS_REPORT_TMP_DIR;
  $result = false;
  if (!empty($data)) {
    $xml = new DOMDocument('1.0', 'UTF-8');
    $owner = $xml->createElement('owner');
    $owner->setAttribute('userid', $name);   
    _nror_patients_report_process_forms($xml, $owner, $data);    
    $xml->appendChild($owner);
    $filename = $tmpReportDir . $name . '.xml';
    $result = $xml->save($filename);
  }
  return $result;
}

function _nror_patients_report_process_forms($dom, $domElement, $forms) {
  foreach ($forms as $formId => $form) {
    $xmlForm = $dom->createElement('form');
    $xmlForm->setAttribute('id', $formId);
    foreach ($form['instances'] as $instanceId => $instance) {
      $instanceElement = $dom->createElement('instance');
      $instanceElement->setAttribute('index', $instanceId);
      _nror_patients_report_process_questions($dom, $instanceElement, $instance['data']);
      _nror_patients_report_prepare_complex_tables($dom, $instanceElement, $instance['data']);
      $xmlForm->appendChild($instanceElement);
      if (isset($instance['forms'])) {
        _nror_patients_report_process_forms($dom, $instanceElement, $instance['forms']);
      }
    }

    $domElement->appendChild($xmlForm);
  }
}


function _nror_patients_report_process_questions($dom, $domElement, $data) {
  if (!empty($data['questions'])) {
    foreach ($data['questions'] as $questionInfo) {
      $question = $dom->createElement('question');
      $question->setAttribute('name', $questionInfo['questionSn']);
      $answer = $dom->createElement('answer');
      foreach ($questionInfo['answerValues'] as $answerInfo) {
        $answerValue = isset($answerInfo['ansValue']) ? $answerInfo['ansValue'] : '';
        $value = $dom->createElement('value', $answerValue);
        $answer->appendChild($value);
      }
      $question->appendChild($answer);
      $domElement->appendChild($question);
    }
  }
}

function _nror_patients_report_prepare_complex_tables($xml, $dataset, $form) {
  if (!empty($form['complex_tables'])) {
    foreach ($form['complex_tables'] as $table) {
      $question = $xml->createElement('question');
      $question->setAttribute('name', $table['metadata']['short_name']);
      foreach ($table['rows'] as $rowN => $row) {
        unset($row['rowId']);
        $record = $xml->createElement('record');
        $record->setAttribute('order', $rowN+1);
        foreach ($row as $cellId => $cell) {
          if (isset($cell['identifying']) && isset($cell['identifying']) == true) {
            continue;
          }
          $ansValue = isset($cell['answerValues'][0]['ansValue']) ? $cell['answerValues'][0]['ansValue'] : '';
          $field = $xml->createElement('field');
          $field->nodeValue = $ansValue;
          $order = _nror_patients_gete_field_order($cellId, $table['metadata']);
          $field->setAttribute('order', $order);
          $record->appendChild($field);
        }
        $question->appendChild($record);
      }
      $dataset->appendChild($question);
    }
  }
}

function _nror_patients_gete_field_order($fieldId, $tableMeta) {
  $fieldsOrder = $tableMeta['columns_order'];
  foreach ($fieldsOrder as $k => $id) {
    if ($id == $fieldId) {
      return $k;
    }
  }
  return false;
}

function _nror_patients_report_create_zip() {
  $tmpReportDir = NROR_PATIENTS_REPORT_TMP_DIR;
  $dirIterator = new DirectoryIterator($tmpReportDir);
  $zipArchive = new ZipArchive();
  $zipArchive->open(NROR_PATIENTS_TMP_DIR.'/'.NROR_PATIENTS_REPORT_ZIP_NAME, ZipArchive::CREATE);
  while ($dirIterator->valid()) {
    if ($dirIterator->isFile()) {
      $zipArchive->addFile($dirIterator->getPathname(), $dirIterator->getFilename());
    }
    $dirIterator->next();
  }
  $zipArchive->close();
}

function _nror_patients_clear_tmp_dir() {
  unlink(NROR_PATIENTS_TMP_DIR.'/'.NROR_PATIENTS_REPORT_ZIP_NAME);
  $tmpReportDir = NROR_PATIENTS_REPORT_TMP_DIR;
  $dirIterator = new DirectoryIterator($tmpReportDir);
  while ($dirIterator->valid()) {
    if ($dirIterator->isFile()) {
      unlink($dirIterator->getPathname());
    }
    $dirIterator->next();
  }
}
