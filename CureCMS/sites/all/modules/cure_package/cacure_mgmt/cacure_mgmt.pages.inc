<?php
/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

/**
 * Menu callback.
 */
function cacure_mgmt_submit_path_callback() {
  return '<h2 class="title">'. t('Paths settings') .'</h2><div>'. drupal_render(drupal_get_form('cacure_mgmt_submit_path_form')) .'</div>';
}

/**
 * Form construct.
 */
function cacure_mgmt_submit_path_form() {
  $form = array();

  $submit_path = variable_get('cacure_mgmt_submit_path', '');

  $form['submit_path'] = array(
    '#type' => 'textfield',
    '#title' => 'After module submit',
    '#default_value' => $submit_path,
    '#description' => t('Please, enter new redirect path after submit. You should use only internal urls, like "user/logout" or "<front>"')
  );

  $form['submit'] = array(
    '#type' => 'submit',
    '#value' => 'Save'
  );
  
  return $form;
}

/**
 * Form submit.
 */
function cacure_mgmt_submit_path_form_submit($form, &$form_state) {
  if ($form_state['values']['submit_path'] == '/') {
    $form_state['values']['submit_path'] = '<front>';
  }
  variable_set('cacure_mgmt_submit_path', $form_state['values']['submit_path']);
  drupal_set_message('All paths has been saved.', 'status');
}

/**
 * Menu callback().
 */
function cacure_mgmt_submit_module_callback($moduleId, $groupId = null) {
  global $user;
  $redirect = cacure_mgmt_get_custom_redirect();
  $restcall = RestCall::getInstance();
  if (empty($groupId)) {
    $groupId = cure_groups_get_active_group($user->name);
  }
  $response = $restcall->changeModuleStatus($user->entityId, $groupId, $moduleId, RestCall::CHANGE_MODULE_STATUS_SUBMIT);
  if($response == 'OK') {
    drupal_set_message('Questionnaire was submitted successfully');
  }
  else {
    drupal_set_message('Questionnaire was not submitted', 'error');
  }
  drupal_goto($redirect);
}

/**
 * Menu callback().
 */
function cacure_mgmt_reopen_module_callback($moduleId, $groupId = null) {
  global $user;
  $redirect = cacure_mgmt_get_custom_redirect();
  $restcall = RestCall::getInstance();
  if (empty($groupId)) {
    $groupId = cure_groups_get_active_group($user->name);
  }
  $response = $restcall->changeModuleStatus($user->entityId, $groupId, $moduleId, RestCall::CHANGE_MODULE_STATUS_REOPEN);
  if($response == 'OK') {
    drupal_set_message('Questionnaire was re-opened successfully');
  }
  else {
    drupal_set_message('Questionnaire was not re-opened', 'error');
  }
  drupal_goto($redirect);
}

