<?php
/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

class RestCall
{
  const REST_RESPONSE_OK = 'OK';

  const REST_STATUS_CODE_SUCCESS = 200;
  const REST_STATUS_CODE_INTERNAL_ERROR = 500;
  const REST_STATUS_CODE_NOT_FOUND = 404;
  const REST_STATUS_CODE_FORBIDDEN = 403;
  const REST_STATUS_CODE_BAD_REQUEST = 400;

  const CHANGE_MODULE_STATUS_SUBMIT = 'submit';
  const CHANGE_MODULE_STATUS_REOPEN = 'reopen';

  const FORM_DATA_FORMAT_XML = 'XML';
  const FORM_DATA_FORMAT_JSON = 'JSON';

  const CHANGE_FORM_STATUS_APPROVE = 'approve';
  const CHANGE_FORM_STATUS_SUBMIT = 'submit';
  const CHANGE_FORM_STATUS_DECLINE = 'decline';

  private $_server;

  private $_port;

  private $_context;

  private $_showErrors;

  private $_errors = array(
    'Module deployment error' => 'The module deployment process has not yet been completed for at least 1 module. Administrative action is required to proceed.',
    'There has been an error processing the request' => 'There has been an error processing the request.'
  );

  private $_handledErrors = array();

  private static $_instance = null;

  /**
   * Returns RestCall instance
   * @static
   * @return RestCall
   */
  public static function getInstance()
  {
    if (self::$_instance == null) {
      self::$_instance = new self();
    }
    return self::$_instance;
  }

  /**
   * Class constructor
   */
  private function __construct()
  {
    $settings = cacure_api_get_settings();
    $this->_showErrors = $settings['show_errors'];
    $this->_server = $settings['server'];
    $this->_port = $settings['port'];
    $this->_context = $settings['context'];
  }

  /**
   * Clone magic method.
   */
  private function __clone()
  {
  }

  /**
   * Builds REST call URI
   * @return string REST call URI
   */
  private function apiUri()
  {
    return 'http://' . $this->_server . ':' . $this->_port . '/' . $this->_context . '/api';
  }

  /**
   * Builds full request URL without query string
   * @param string $action REST call
   * @param string|null $entityId (optional) Entity ID
   * @param string|null $groupId (optional) Group name
   * @return string Full request URL without query string
   */
  private function url($action, $entityId = null, $groupId = null)
  {
    $url = $this->apiUri();
    if (!empty($entityId) && is_string($entityId)) {
      $url .= '/' . $entityId;
    }
    if (!empty($groupId) && is_string($groupId)) {
      $url .= '/' . $groupId;
    }
    $url .= '/' . $action;
    return $url;
  }

  /**
   * Sends data to specified URL
   * @see drupal_http_request()
   * @param string $url Destination URL
   * @param mixed|null $data Data to be sent
   * @param string $method Either post or get request methods
   * @return object Response object
   */
  private function send($url, $data = null, $method = 'POST')
  {
    $request = drupal_http_request(
      $url,
      array(
        'headers' => array("text/xml"),
        'method' => $method,
        'data' => $data
      )
    );
//    $this->logRequest($request);
    return $request;
  }

  /**
   * Gets data from specified URL
   * @see RestCall::buildQuery()
   * @see RestCall::set_error_message()
   * @see drupal_http_request()
   * @param string $url Destination URL
   * @param array $data Http query data
   * @return string Response data on success, empty string on failure
   */
  private function get($url, $data = array())
  {
    $url .= $this->buildQuery($data);

//    $this->logRequestsUri($url);

    $call = drupal_http_request($url, array('timeout' => 90));

//    $this->logRequest($call);

    if ($call->code == self::REST_STATUS_CODE_SUCCESS) {
      $data = $call->data;
      if (strpos($data, '"status":"error"')) {
        $json = json_decode($data, true);
        $status = null;
        if (isset($json['details'])) {
          $status = $json['details'];
        } elseif (isset($json['message'])) {
          $status = $json['message'];
        }
        if (!empty($status) && isset($this->_errors[$status]) && !in_array($status, $this->_handledErrors)) {
          $this->_handledErrors[] = $status;
          drupal_set_message($this->_errors[$status], 'error');
        }
        return '';
      }
      return $data;
    }
    else {
      $this->setErrorMessage($url, $call->code, $call->data, $call->error, $call->status_message);
      return '';
    }
  }

  /**
   * Displays and records errors
   * @see watchdog()
   * @see drupal_set_message()
   * @param $code
   * @param $response_data
   * @param string $error_message
   * @param string $status_message
   */
  private function setErrorMessage($url, $code, $response_data, $error_message = '', $status_message = '') {
    $message = '';
    $message .= 'Error ' . $code . '.';
    if (!empty($error_message)) {
      $message .= ' ' . $error_message . '.';
    }
    else {
      $message .= ' ' . $status_message . '.';
    }
    $report = 'Url ' . $url . '<br />' . $message . '<br />' . ' Response data: ' . $response_data;
    watchdog('error', $report, array(), WATCHDOG_ERROR);
    if ($this->_showErrors == true) {
      drupal_set_message($message, 'error');
    }
  }

  /**
   * Builds query string
   * @see http_build_query()
   * @param array $data Query params
   * @return string Query string
   */
  private function buildQuery($data = array())
  {
    $result = '';
    if(!empty($data) && is_array($data)) {
      $result = '?' . http_build_query($data);
    }
    return $result;
  }

  /**
   * Debug method
   * Logs request uri to a file log.txt
   * @param string $uri Request uri
   * @return void
   */
  private function logRequestsUri($uri)
  {
    if (file_exists(dirname(__FILE__).'/log.txt')) {
      $file = file_get_contents(dirname(__FILE__).'/log.txt');
    } else {
      $file = '';
    }
    $file .= $uri."\r\n";
    file_put_contents(dirname(__FILE__).'/log.txt',$file);
  }

  /**
   * Debug method
   * Logs request uri to a file log.txt
   * @param string $uri Request uri
   * @return void
   */
  private function logRequest($call)
  {
    if (file_exists(dirname(__FILE__).'/requestlog.txt')) {
      $file = file_get_contents(dirname(__FILE__).'/requestlog.txt');
    } else {
      $file = '';
    }
    ob_start();
    print_r($call);
    $dump = ob_get_clean();
    $file .= $dump."\r\n\r\n\r\n";
    file_put_contents(dirname(__FILE__).'/requestlog.txt',$file);
  }

  // Module metadata API

  /**
   * Get All modules
   * @see RestCall::url()}
   * @see RestCall::get()}
   * @return string Modules data in XML format
   */
  public function allModules()
  {
    $url = $this->url('AllModules');
    return $this->get($url);
  }

  /**
   * Get modules by user
   * @see RestCall::url()
   * @see RestCall::get()
   * @param string $moduleId Module ID
   * @return string Modules data in XML format
   */
  public function getModuleStatusByOwner($moduleId)
  {
    $url = $this->url('GetModuleStatusByOwner');
    return $this->get($url, array('moduleId' => $moduleId));
  }

  /**
   * Get All user modules
   * @see RestCall::url()
   * @see RestCall::get()
   * @param string $entityId CURE user identification
   * @param string $groupId ID of the group
   * @param string $context Context
   * @return string Modules data in XML format
   */
  public function getAllUserModules($entityId, $groupId, $context)
  {
    $url = $this->url('AllUserModules', $entityId, $groupId);
    return $this->get($url, array('ctx' => $context));
  }

  /**
   * Get Available Modules (status = new)
   * @see RestCall::url()
   * @see RestCall::get()
   * @param string $entityId CURE user identification
   * @param string $groupId ID of the group
   * @param string $context
   * @return string Modules data in XML format
   */
  public function getAvailableModules($entityId, $groupId, $context)
  {
    $url = $this->url('AvailableModules', $entityId, $groupId);
    return $this->get($url, array('ctx' => $context));
  }

  /**
   * Get Current Module
   * @see RestCall::url()
   * @see RestCall::get()
   * @param string $entityId CURE user identification
   * @param string $groupId ID of the group
   * @return string Current module data in XML format
   */
  public function getCurrentModule($entityId, $groupId)
  {
    $url = $this->url('CurrentModule', $entityId, $groupId);
    return $this->get($url);
  }

  /**
   * Change module status (submit, reopen)
   * @see RestCall::url()
   * @see RestCall::get()
   * @param string $entityId CURE user identification
   * @param string $groupId ID of the group
   * @param string $moduleId Module ID
   * @param string $status Status to be set
   * @return bool|string
   */
  public function changeModuleStatus($entityId, $groupId, $moduleId, $status)
  {    
    if ($status != self::CHANGE_MODULE_STATUS_SUBMIT && $status != self::CHANGE_MODULE_STATUS_REOPEN) {
      return false;
    }
    $url = $this->url('ChangeModuleStatus', $entityId, $groupId);
    $data = json_decode($this->get($url, array('id' => $moduleId, 'status' => $status)), true);
    $result = '';
    if ($data['status'] == 'OK') {
      $result = $data['status'];
    }
    return $result;
  }

  /**
   * Get stale form instances
   * @see RestCall::url()
   * @see RestCall::get()
   * @return string Stale forms in XML format
   */
  public function getStaleFormInstances()
  {
    $url = $this->url('GetStaleFormInstances');
    return $this->get($url);
  }

  /**
   * Get next form data by form and instance id's
   * @see RestCall::url()
   * @see RestCall::get()
   * @param string $entityId CURE user identification
   * @param string $groupId ID of the group
   * @param string $formId Current form ID
   * @param string $instanceId Current form instance ID
   * @return string Next form ID or NONE if there are no more forms
   */
  public function getNextFormIdAndInstanceId($entityId, $groupId, $formId, $instanceId)
  {
    $url = $this->url('NextFormIdAndInstanceId', $entityId, $groupId);
    $data = json_decode($this->get($url, array('formId' => $formId, 'instanceId' => $instanceId)), true);
    $result = '';
    if ($data['status'] == 'OK') {
      $result = $data['content'];
    }
    if (empty($result) || $result == 'NONE') {
      return 'NONE';
    }
    else {
      return $result;
    }
  }

  /**
   * Get previous form data by form and instance id's
   * @see RestCall::url()
   * @see RestCall::get()
   * @param string $entityId CURE user identification
   * @param string $groupId ID of the group
   * @param string $formId Current form ID
   * @param string $instanceId Current form instance ID
   * @return string Previous form ID or NONE if there are no more forms
   */
  public function getPrevFormIdAndInstanceId($entityId, $groupId, $formId, $instanceId)
  {
    $url = $this->url('PreviousFormIdAndInstanceId', $entityId, $groupId);
    $data = json_decode($this->get($url, array('formId' => $formId, 'instanceId' => $instanceId)), true);
    $result = '';
    if ($data['status'] == 'OK') {
      $result = $data['content'];
    }
    if (empty($result) || $result == 'NONE') {
      return 'NONE';
    }
    else {
      return $result;
    }
  }

  // Form Data APIs

  /**
   * Create new form instance
   * @see RestCall::url()
   * @see RestCall::get()
   * @param string $entityId CURE user identification
   * @param string $groupId ID of the group
   * @param string $formId Form ID
   * @return string XForms formatted XML data
   */
  public function getNewFormInstance($entityId, $groupId, $formId, $parentInstanceId = null)
  {
    $url = $this->url('GetNewFormInstance', $entityId, $groupId);
    $data = array('formId' => $formId);
    if (!empty($parentInstanceId)) {
      $data['parentInstanceId'] = $parentInstanceId;
    }
    return $this->get($url, $data);
  }

  /**
   * Get form instance
   * @see RestCall::url()
   * @see RestCall::get()
   * @param string $entityId CURE user identification
   * @param string $groupId ID of the group
   * @param string $formId Form ID
   * @param string $instanceId Form instance ID
   * @return string XForms formatted XML data
   */
  public function getFormInstance($entityId, $groupId, $formId, $instanceId, $parentInstanceId = null)
  {
    $url = $this->url('GetFormInstance', $entityId, $groupId);
    $data = array('formId' => $formId, 'instanceId' => $instanceId);
    if (!empty($parentInstanceId)) {
      $data['parentInstanceId'] = $parentInstanceId;
    }
    return $this->get($url, $data);
  }

  /**
   * Get User Form Data
   * @see RestCall::url()
   * @see RestCall::get()
   * @param string $entityId CURE user identification
   * @param string $groupId ID of the group
   * @param string $formId Form ID
   * @param string $instanceId Form instance ID
   * @param string $format Response format (either xml or json)
   * @return string Form data in XML/JSON format
   */
  public function getUserFormInstanceData($entityId, $groupId, $formId, $instanceId, $format = self::FORM_DATA_FORMAT_XML)
  {
    $url = $this->url('GetUserFormInstanceData', $entityId, $groupId);
    if ($format != self::FORM_DATA_FORMAT_XML && $format != self::FORM_DATA_FORMAT_JSON) {
      $format = self::FORM_DATA_FORMAT_XML;
    }
    return $this->get($url, array('formId' => $formId, 'instanceId' => $instanceId, 'format' => $format));
  }

  /**
   * Get Form Data
   * @see RestCall::url()
   * @see RestCall::get()
   * @param string $formId Form ID
   * @param string $format Response format (either xml or json)
   * @return string Form data in XML/JSON format
   */
  public function getFormData($formId, $format = self::FORM_DATA_FORMAT_XML)
  {
    $url = $this->url('GetFormData');
    if ($format != self::FORM_DATA_FORMAT_XML && $format != self::FORM_DATA_FORMAT_JSON) {
      $format = self::FORM_DATA_FORMAT_XML;
    }
    return $this->get($url, array('id' => $formId, 'format' => $format));
  }

  /**
   * Save form instance data
   * @see RestCall::url()
   * @see RestCall::send()
   * @param string $entityId CURE user identification
   * @param string $groupId ID of the group
   * @param string $formId Form ID
   * @param string $instanceId Form instance ID
   * @param bool $partial_save
   * @return object Response object
   */
  public function saveFormInstance($entityId, $groupId, $formId, $instanceId, $partial_save = true, $parentInstanceId = null)
  {
    $data = $GLOBALS['HTTP_RAW_POST_DATA'];
    $query = array('formId' => $formId, 'instanceId' => $instanceId, 'partialSave' => $partial_save, 'parentInstanceId' => $parentInstanceId);
    $url = $this->url('SaveFormInstance', $entityId, $groupId) . $this->buildQuery($query);
    return $this->send($url, $data);
  }

  /**
   * Save and submit form instance data
   * @see RestCall::url()
   * @see RestCall::send()
   * @param string $entityId CURE user identification
   * @param string $groupId ID of the group
   * @param string $formId Form ID
   * @param string $instanceId Form instance ID
   * @param bool $partial_save
   * @return object Response object
   */
  public function saveAndSubmitFormInstance($entityId, $groupId, $formId, $instanceId, $partial_save = true)
  {
    $data = $GLOBALS['HTTP_RAW_POST_DATA'];
    $query = array('formId' => $formId, 'instanceId' => $instanceId, 'partialSave' => $partial_save);
    $url = $this->url('SaveAndSubmitFormInstance', $entityId, $groupId) . $this->buildQuery($query);
    return $this->send($url, $data);
  }

  /**
   * Change form instance status
   * @see RestCall::url()
   * @see RestCall::get()
   * @param string $entityId CURE user identification
   * @param string $groupId ID of the group
   * @param string $formId Form ID
   * @param string $instanceId Form instance ID
   * @param string $status Form status
   * @return bool|string
   */
  public function changeFormInstanceStatus($entityId, $groupId, $formId, $instanceId, $status)
  {    
    if ($status != self::CHANGE_FORM_STATUS_APPROVE && $status != self::CHANGE_FORM_STATUS_SUBMIT && $status != self::CHANGE_FORM_STATUS_DECLINE) {
      return false;
    }
    $url = $this->url('ChangeFormInstanceStatus', $entityId, $groupId);
    $data = json_decode($this->get($url, array('formId' => $formId, 'instanceId' => $instanceId, 'status' => $status)), true);
    $result = '';
    if ($data['status'] == 'OK') {
      $result = $data['status'];
    }
    return $result;
  }

  // Access and Security APIs

  /**
   * Get All groups
   * @see RestCall::url()
   * @see RestCall::get()
   * @return string Groups data in XML format
   */
  public function getAllSharingGroups()
  {
    $url = $this->url('GetAllSharingGroups');
    return $this->get($url);
  }

  /**
   * Create new entity within existing group
   * @see RestCall::url()
   * @see RestCall::get()
   * @param string $groupId Group ID
   * @return string New entity identification
   */
  public function getNewEntityInGroup($groupId)
  {
    $url = $this->url('GetNewEntityInGroup');
    $data = json_decode($this->get($url, array('grpid' => $groupId)), true);
    $eid = '';
    if ($data['status'] == 'OK') {
      $eid = $data['content'];
    }
    return $eid;
  }

  /**
   * Create new entity within a new group
   * @see RestCall::url()
   * @see RestCall::get()
   * @param string $groupName Name of the group (possibly username)
   * @return string New entity identification
   */
  public function getNewEntityInNewGroup($groupName)
  {
    $url = $this->url('GetNewEntityInNewGroup');
    $data = json_decode($this->get($url, array('name' => $groupName)), true);
    $eid = '';
    if ($data['status'] == 'OK') {
      $eid = $data['content'];
    }
    return $eid;
  }

  /**
   * Assign entity to existing group
   * @see RestCall::url()
   * @see RestCall::get()
   * @param string $entityId Entity ID
   * @param string $groupId Group ID
   * @return string OK on success
   */
  public function assignEntityToGroup($entityId, $groupId)
  {
    $url = $this->url('AssignEntityToGroup', $entityId);
    $data = json_decode($this->get($url, array('grpid' => $groupId)), true);
    $result = '';
    if ($data['status'] == 'OK') {
      $result = $data['status'];
    }
    return $result;
  }

  /**
   * Delete entity from existing group
   * @see RestCall::url()
   * @see RestCall::get()
   * @param string $entityId Entity ID
   * @param string $groupId Group ID
   * @return string OK on success
   */
  public function deleteEntityFromGroup($entityId, $groupId)
  {
    $url = $this->url('DeleteEntityFromGroup', $entityId);
    $data = json_decode($this->get($url, array('grpid' => $groupId)), true);
    $result = '';
    if ($data['status'] == 'OK') {
      $result = $data['status'];
    }
    return $result;
  }

  /**
   * Create a new sharing group
   * @see RestCall::url()
   * @see RestCall::get()
   * @param string $groupName Name of the group (possibly username)
   * @return string New group identification
   */
  public function createNewSharingGroup($groupName)
  {
    $url = $this->url('CreateNewSharingGroup');
    $data = json_decode($this->get($url, array('name' => $groupName)), true);
    $groupId = '';
    if ($data['status'] == 'OK') {
      $groupId = $data['content'];
    }
    return $groupId;
  }

  /**
   * Get group identifier by name
   * @see RestCall::url()
   * @see RestCall::get()
   * @param string $groupName Name of the group (possibly username)
   * @return string Group identification
   */
  public function getGroupId($groupName)
  {
    $url = $this->apiUri() . '/GetGroupId';
    $data = json_decode($this->get($url, array('name' => $groupName)), true);
    $eid = '';
    if ($data['status'] == 'OK') {
      $eid = $data['content'];
    }
    return $eid;
  }


  /**
   * Get group identifier by name
   * @see RestCall::url()
   * @see RestCall::get()
   * @param string $groupName Name of the group (possibly username)
   * @return string Group identification
   */
  public function renameSharingGroup($oldGroup, $newGroup)
  {
    $url = $this->apiUri() . '/RenameSharingGroup';
    $data = json_decode($this->get($url, array('oldName' => $oldGroup, 'newName' => $newGroup)), true);
    $result = '';
    if ($data['status'] == 'OK') {
      $result = $data['status'];
    }
    return $result;
  }


  /**
   * Delete all entity related data
   * @see RestCall::url()
   * @see RestCall::get()
   * @param string $entityId Entity ID
   * @return string OK on success
   */
  public function deleteEntity($entityId)
  {
    $url = $this->url('DeleteEntity', $entityId);
    $data = json_decode($this->get($url), true);
    $result = '';
    if ($data['status'] == 'OK') {
      $result = $data['content'];
    }
    return $result;
  }

  /**
   * Returns permissions for all the entities
   * @see RestCall::url()
   * @see RestCall::get()
   * @return string Permissions data in XML format
   */
  public function getPermissions()
  {
    $url = $this->url('GetPermissions');
    return $this->get($url);
  }

  /**
   * Returns permissions for a specific entity for all groups the entity belongs to.
   * If groupName is provided, returns permissions for a specific entity in a specific group.
   * @see RestCall::url()
   * @see RestCall::get()
   * @see RestCall::getGroupId()
   * @param $entityId Entity ID
   * @param string|null $groupId (optional) Id of the group
   * @return string Permissions data in XML format
   */
  public function getPermissionsForEntity($entityId)
  {
    $url = $this->url('GetPermissionsForEntity', $entityId);
    return $this->get($url);
  }

  /**
   * Saves permissions for a specific entity
   * @see RestCall::url()
   * @see RestCall::send()
   * @param string $entityId Entity ID
   * @param string $xml Permissions data to be saved in XML format
   * @return object Response object
   */
  public function savePermissions($entityId, $xml)
  {
    $url = $this->url('SavePermissions', $entityId);
    return $this->send($url, $xml);
  }

  /**
   * Retrieves entities for a specified group
   * @see RestCall::url()
   * @see RestCall::getGroupId()
   * @see RestCall::get()
   * @param string $groupId Id of the group
   * @return string Entities data in XML format
   */
  public function getEntitiesForSharingGroup($groupId)
  {
    $url = $this->url('GetEntitiesForSharingGroup');
    return $this->get($url, array('grpid' => $groupId));
  }

  /**
   * Returns all tags in the system
   * @see RestCall::url()
   * @see RestCall::get()
   * @return string Tags info in XML format
   */
  public function getTags()
  {
    $url = $this->url('GetTags');
    return $this->get($url);
  }
}
