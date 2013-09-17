<?php
/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

function cure_tag_permissions_tpl_get_all_single_bindings() {
  $query = db_select('cure_permissions_tpl_single', 'tpl');
  $query->fields('tpl');
  
  return $query->execute()->fetchAll();
}

function cure_tag_permissions_tpl_get_single_bindings($role_id) {
  $query = db_select('cure_permissions_tpl_single', 'tpl');
  $query->fields('tpl');
  $query->condition('tpl.role_id', $role_id);
  
  return $query->execute()->fetch();
}

function cure_tag_permissions_tpl_delete_single($role) {
  return db_delete('cure_permissions_tpl_single')->condition('role_id', $role)->execute();
}

function cure_tag_permissions_tpl_save_single_binding($role_id, $data) {
  $record =  array(
    'role_id' => !empty($role_id) ? $role_id : null,
    'data' => $data,
  );
  if (!empty($role_id) && cure_tag_permissions_tpl_single_binding_exists($role_id)) {
    return db_update('cure_permissions_tpl_single')->fields(array('data' => $data))->condition('role_id', $role_id)->execute();
  }
  else {
    return drupal_write_record('cure_permissions_tpl_single', $record);
  }
}

function cure_tag_permissions_tpl_single_binding_exists($role_id) {
  $query = db_select('cure_permissions_tpl_single', 'tpl');
  $query->fields('tpl');
  $query->condition('tpl.role_id', $role_id);
  $result = $query->execute()->fetchAll();
  if (!empty($result)) {
    return true;
  }
  else {
    return false;
  }
}

function cure_tag_permissions_tpl_get_tpl_single($role_id) {
  $query = db_select('cure_permissions_tpl_single', 'tpl');
  $query->fields('tpl', array('data'));
  $query->condition('tpl.role_id', $role_id);
  $result = $query->execute()->fetch();
  if (empty($result)) {
    return false;
  }
  return unserialize($result->data);
}
