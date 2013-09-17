<?php
/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

/**
 * Returns user entity ID.
 * @param int $uid User ID
 * @return bool|string
 */
function cure_users_get_entity_id($uid) {
  $result = db_select('cure_users', 'cu')->fields('cu', array('entity_id'))->condition('cu.uid', $uid)->execute()->fetch();
  return !empty($result) ? $result->entity_id : false;
}

/**
 * Returns user ID.
 * @param string $eid Entity ID
 * @return bool|int
 */
function cure_users_get_id($entity_id) {
  $result = db_select('cure_users', 'cu')->fields('cu', array('uid'))->condition('cu.entity_id', $entity_id)->execute()->fetch();
  return !empty($result) ? $result->uid : false;
}

/**
 * Returns all CURE users stored in Drupal base
 * @return array
 */
function cure_users_get_all_entities() {
  $result = db_select('cure_users', 'cu')->fields('cu')->execute()->fetchAll();
  foreach ($result as $user) {
    $users[$user->uid] = $user->entity_id;
  }
  return !empty($users) ? $users : false;
}

/**
 * Function save entity.
  * @param  array $account
 * @return void
 */
function cure_users_create_entity($account, $edit) {
  $entityId = cure_groups_new_entity_in_group($account->name);
  if (empty($entityId)) {
    drupal_set_message(t('Failed to create CURE account : ' . $account->name . '.'), 'error');
  }
  else {
    $account->entityId = $entityId;
    $record = array('uid' => $account->uid, 'entity_id' => $entityId);
    if (drupal_write_record('cure_users', $record)) {
      module_invoke_all('cure_users_create_event', $account, $edit);
    }    
  }  
}

/**
 * Deletes user record from cure_users table
 * @param int $uid User ID
 * @return DatabaseStatementInterface
 */
function cure_users_delete_user($uid) {
  return db_delete('cure_users')->condition('uid', $uid)->execute();
}

/**
 * Returns all Drupal users.
 * @return array Array of fully loaded users objects
 */
function cure_users_get_all_users() {
  $query = new EntityFieldQuery;

  $entities = $query
    ->entityCondition('entity_type', 'user')
    ->execute();
  $ids = array_keys($entities['user']);
  return user_load_multiple($ids);
}