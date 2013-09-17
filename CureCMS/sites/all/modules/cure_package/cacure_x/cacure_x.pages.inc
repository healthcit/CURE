<?php
/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

/**
 * Menu callback.
 * @return html
 */
function cacure_x_page_callback($context, $formId, $groupName) {
  global $user;
  cure_contexts_set_active_context($context);
  drupal_add_http_header('Content-Type', 'text/xml');
  drupal_add_http_header('Charset', 'UTF-8');
  drupal_add_http_header('X-UA-Compatible', 'chrome=1');

  $entityId = $user->entityId;
  $groupId = cure_groups_set_active_group($groupName);

  $restcall = RestCall::getInstance();

  $instanceId = null;
  if (isset($_GET['instanceId'])) {
    $instanceId = (int) $_GET['instanceId'];
  }

  if (!empty($_GET['parentInstanceId'])) {
    $parentInstanceId = $_GET['parentInstanceId'];
  }
  else {
    $parentInstanceId = _cacure_x_get_parent_instance_id($entityId, $groupId, $context, $formId, $instanceId);
    if (empty($parentInstanceId)) {
      $parentInstanceId = 'none';
    }
    $query = array('parentInstanceId' => $parentInstanceId);
    if (!empty($instanceId)) {
      $query['instanceId'] = $instanceId;
    }
    $url = 'form/' . $context . '/' . $formId . '/'  . $groupName;
    drupal_goto($url, array('query' => $query));
    die;
  }
  $parentInstanceIdParam = null;
  if ($parentInstanceId != 'none') {
    $parentInstanceIdParam = $parentInstanceId;
  }
  if (!empty($instanceId)) {
    $xml = $restcall->getFormInstance($entityId, $groupId, $formId, $instanceId, $parentInstanceIdParam);
  }
  else {
    $error = false;
    $instanceId = _cacure_x_get_new_instance_id($formId, $entityId, $groupId, $context, $parentInstanceId);
    if ($instanceId === false) {
      $error = true;
    }
    $xml = $restcall->getNewFormInstance($entityId, $groupId, $formId, $parentInstanceIdParam);
    if (strpos($xml, '"message":')) {
      $json = json_decode($xml, true);
      drupal_set_message(ucfirst($json['message']).'. '.ucfirst($json['details']), 'error');
      $_SESSION['xml_arg'] = array(
      );
      return '';
    }
    if (empty($xml)) {
      $error = true;
    }
    if ($error === true) {
      drupal_set_message('Unable to create new form instance', 'error');
      $_SESSION['xml_arg'] = array(
      );
      return '';
    }
  }

  if (empty($xml)) {
    drupal_set_message('Forms are not available at the moment');
    $_SESSION['xml_arg'] = array(
    );
    return '';
  } else if (strpos($xml, '"message":')) {
    $json = json_decode($xml, true);
    drupal_set_message(ucfirst($json['message']).'. '.ucfirst($json['details']), 'error');
    $_SESSION['xml_arg'] = array(
    );
    return '';
  }

  if (module_exists('cure_forms_logs')) {
    cure_forms_logs_form_accessed($formId, $groupId);
  }
  
  $baseUrl = '<base-url>' . '/formsubmit/' . $context . '/' . $formId . '/' . $instanceId . '/' . $groupId . '/' . $parentInstanceId . '</base-url>';

  $xml = str_replace('<base-url>xform.view</base-url>', $baseUrl, $xml);

  $string_open = strpos($xml, "<html");

  $xml = substr($xml, $string_open, strlen($xml));

  ini_set('pcre.backtrack_limit', 500000);

  preg_match('|<head>(.*)</head>|Uis', $xml, $out);
  $head = $out[1];

  $head = trim($head);

  preg_match('|<body(.)*>(.*)</body>|Uis', $xml, $out);
  $block = $out[2];

  $block = str_replace('id="container"', 'class="xform-block"', $block);

  if (!empty($head) || !empty($block)) {
    $_SESSION['xml_arg'] = array(
      'head' => $head,
      'body' => $block,
      'block_id' => $formId,
    );
  }

  return '';
}

/**
 * Menu callback
 */
function cacure_x_formsubmit_callback($context, $formId, $instanceId, $groupId, $parentInstanceId, $action = null) {
  global $user;
  global $base_url;

  $restcall = RestCall::getInstance();

  $entityId = $user->entityId;

  $partialSave = 'true';
  if ($action) {
    $partialSave = 'false';
  }

  if ('next' == $action || 'prev' == $action || 'submit' == $action || 'save' == $action) {
    if ($parentInstanceId == 'none') {
      $checkParentInstanceId = _cacure_x_get_parent_instance_id($entityId, $groupId, $context, $formId, $instanceId);
      if ($checkParentInstanceId != null) {
        $parentInstanceIdParam = $checkParentInstanceId;
      }
      else {
        $parentInstanceIdParam = null;
      }
    } else {
        $parentInstanceIdParam = $parentInstanceId;
    }
    $response = $restcall->saveFormInstance($entityId, $groupId, $formId, $instanceId, $partialSave, $parentInstanceIdParam);
    if ($response->status_message != 'OK') {
      drupal_set_message('Your form was not saved. Please, try later...', 'error');
      echo '<script type="text/javascript"> parent.location.href="/"; </script>';
      die;
    }
  }

  $groupName = cure_groups_get_group_name($groupId);

  if ($action) {
    $redirect = cacure_x_get_redirect();
    $defaultRedirect = variable_get(CACURE_X_SETTINGS_DEFAULT_REDIRECT, '/');
    $defaultCompletedRedirect = variable_get(CACURE_X_SETTINGS_COMPLETED_REDIRECT, '/');
    $completedMessage = variable_get(CACURE_X_SETTINGS_COMPLETED_MESSAGE, null);

    $p = '';
    $id = 'NONE';
    switch ($action) {
      case 'save' :
        $id = $formId;
        break;
      case 'prev' :
        $restcall->changeFormInstanceStatus($entityId, $groupId, $formId, $instanceId, RestCall::CHANGE_FORM_STATUS_SUBMIT);
        $json = $restcall->getPrevFormIdAndInstanceId($entityId, $groupId, $formId, $instanceId);
        if ($json == 'NONE') {
          $id = $formId;
        }
        else {
          $id = $json[0];
          $instanceId = $json[1];
        }
        break;
      case 'next' :
        $restcall->changeFormInstanceStatus($entityId, $groupId, $formId, $instanceId, RestCall::CHANGE_FORM_STATUS_SUBMIT);
        $json = $restcall->getNextFormIdAndInstanceId($entityId, $groupId, $formId, $instanceId);
        if ($json == 'NONE') {
          if (!empty($completedMessage)) {
            drupal_set_message($completedMessage);
          }
          $p = $defaultCompletedRedirect;
        }
        else {
          $id = $json[0];
          $instanceId = $json[1];
        }
        break;
      case 'submit' :
        $id = $formId;
        $restcall->changeFormInstanceStatus($entityId, $groupId, $formId, $instanceId, RestCall::CHANGE_FORM_STATUS_SUBMIT);
        break;
      case 'approve' :
        $restcall->changeFormInstanceStatus($entityId, $groupId, $formId, $instanceId, RestCall::CHANGE_FORM_STATUS_APPROVE);
        break;
      case 'decline' :
        $restcall->changeFormInstanceStatus($entityId, $groupId, $formId, $instanceId, RestCall::CHANGE_FORM_STATUS_DECLINE);
        break;
    }
    if ($id == 'NONE' && empty($p)) {
      $p  = !empty($redirect) ? $redirect : $defaultRedirect;
    }
    elseif (empty($p)) {
      $p = url('form/' . $context . '/' . $id . '/' .$groupName, array('query' => array('parentInstanceId' => $parentInstanceId, 'instanceId' => $instanceId)));
    }

  } else {
    $p = url('form/' . $context . '/' . $formId . '/' .$groupName, array('query' => array('parentInstanceId' => $parentInstanceId, 'instanceId' => $instanceId)));
  }
  $p = trim($p, '/');
  echo '<script type="text/javascript"> window.location.href="'.$base_url.'/'.$p.'"; </script>';
  die;
}

function _cacure_x_get_new_instance_id($formId, $entityId, $groupId, $context, $parentInstanceId) {
  $call = RestCall::getInstance();
  $modules = SimpleXmlParser::toArray($call->getAllUserModules($entityId, $groupId, $context));
  if (isset($modules['module']['@attributes'])) {
      $modules['module'] = array($modules['module']);
  }
  $form = null;
  $parentInstances = null;
  foreach ($modules['module'] as $module) {
    $forms = $module['form'];
    $form = _cacure_x_get_current_form($formId, $forms);
    if (!empty($form)) {
      break;
    }
  }
  foreach ($modules['module'] as $module) {
    $forms = $module['form'];
    $parentForm = _cacure_x_get_parent_form($formId, $forms);
    if (!empty($parentForm)) {
      $parentInstances = $parentForm['formInstances'];
      break;
    }
  }

  $instanceId = _cacure_x_get_new_instance_id_helper($form, $parentInstances, $parentInstanceId);
  return $instanceId;
}

function _cacure_x_get_current_form($formId, $forms) {
  $currentForm = null;
  if (isset($forms['@attributes'])) {
    $forms = array($forms);
  }
  foreach ($forms as $form) {
    if ($form['@attributes']['id'] == $formId) {
      $currentForm = $form;
      break;
    }
    elseif (isset($form['form'])) {
      $currentForm = _cacure_x_get_current_form($formId, $form['form']);
      if (!empty($currentForm)) {
        break;
      }
    }
  }
  return $currentForm;
}

function _cacure_x_get_new_instance_id_helper($form, $parentInstances = null, $parentInstanceId = 'none') {
  $formInstances = $form['formInstances'];
  $formInstancesAttr = $formInstances['@attributes'];
  if ($formInstancesAttr['existingInstances'] == 0) {
    return 1;
  }
  if (!empty($parentInstances) && $parentInstanceId != 'none') {
    $parentExistingInstances = 0;
    foreach ($formInstances as $instance) {
      if ($instance['@attributes']['parentInstanceId'] == $parentInstanceId) {
        $parentExistingInstances += 1;
      }
    }
    if ($parentExistingInstances >= $formInstancesAttr['maxInstances']) {
      return false;
    }
    else {
        $instances = $formInstances['instance'];
        if (isset($instances['@attributes'])) {
          $instances = array($instances);
        }
        $lastId = 0;
        foreach ($instances as $instance) {
          if ($instance['@attributes']['instanceId'] > $lastId) {
            $lastId = $instance['@attributes']['instanceId'];
          }
        }
        return $lastId + 1;
    }
  }
  else {
    if ($formInstancesAttr['existingInstances'] < $formInstancesAttr['maxInstances']) {
      $instances = $formInstances['instance'];
      if (isset($instances['@attributes'])) {
        $instances = array($instances);
      }
      $lastId = 0;
      foreach ($instances as $instance) {
        if ($instance['@attributes']['instanceId'] > $lastId) {
          $lastId = $instance['@attributes']['instanceId'];
        }
      }
      return $lastId + 1;
    }
  }
  return false;
}

function _cacure_x_get_parent_instance_id($entityId, $groupId, $context, $formId, $instanceId) {
  $call = RestCall::getInstance();
  $modules = SimpleXmlParser::toArray($call->getAllUserModules($entityId, $groupId, $context));
  if (isset($modules['module']['@attributes'])) {
    $modules['module'] = array($modules['module']);
  }
  foreach ($modules['module'] as $module) {
    $forms = $module['form'];
    $parentInstanceId = _cacure_x_get_parent_instance_id_helper($formId, $instanceId, $forms);
    if (!empty($parentInstanceId )) {
      return $parentInstanceId;
    }
  }
  return null;
}

function _cacure_x_get_parent_instance_id_helper($formId, $instanceId, $forms) {
  if (isset($forms['@attributes'])) {
    $forms = array($forms);
  }
  foreach ($forms as $form) {
    if (isset($form['form'])) {
      if (isset($form['form']['@attributes'])) {
        $form['form'] = array($form['form']);
      }
      foreach ($form['form'] as $child) {
        if ($child['@attributes']['id'] == $formId) {
          if ($child['formInstances']['@attributes']['existingInstances'] > 0) {
            $instances = $child['formInstances']['instance'];
            if (isset($instances['@attributes'])) {
              $instances = array($instances);
            }
            foreach ($instances as $instance) {
              if ($instance['@attributes']['instanceId'] == $instanceId) {
                if (isset($instance['@attributes']['parentInstanceId'])) {
                  return $instance['@attributes']['parentInstanceId'];
                }
              }
            }
          }
          $formInstancesAttr = $form['formInstances']['@attributes'];
          if ($formInstancesAttr['existingInstances'] > 0) {
            return 1;
          }
        }
        if (isset($child['form'])) {
          $parentInstanceId = _cacure_x_get_parent_instance_id_helper($formId, $instanceId, $child);
          if (!empty($parentInstanceId)) {
            return $parentInstanceId;
          }
        }
      }
    }
  }
  return null;
}

function _cacure_x_get_parent_form($formId, $forms) {
  $parentForm = null;
  if (isset($forms['@attributes'])) {
    $forms = array($forms);
  }
  foreach ($forms as $form) {
    if (isset($form['form'])) {
      if (isset($form['form']['@attributes'])) {
        $form['form'] = array($form['form']);
      }
      foreach ($form['form'] as $child) {
        if ($child['@attributes']['id'] == $formId) {
          return $form;
        }
      }
      $parentForm = _cacure_x_get_parent_form($formId, $form['form']);
      if (!empty($parentForm)) {
        break;
      }
    }
  }
  return $parentForm;
}