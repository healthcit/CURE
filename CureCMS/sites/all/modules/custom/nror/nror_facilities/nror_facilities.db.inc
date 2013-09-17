<?php

/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

/**
 * Returns all facilities available
 * @return mixed
 */
function nror_facilities_get_all()
{
  $query = db_select('nror_facilities', 'f');

  return $query->fields('f')->execute()->fetchAll();
}

/**
 * Returns facility by ID
 * @param $fid Facility ID
 * @return mixed
 */
function nror_facilities_get($fid)
{
  return db_select('nror_facilities', 'f')->fields('f')->condition('f.fid', $fid)->execute()->fetch();
}

/**
 * Returns facility by ID
 * @param int $fid Facility ID
 * @return mixed
 */
function nror_facilities_get_by_title($title)
{
  return db_select('nror_facilities', 'f')->fields('f')->condition('f.title', $title)->execute()->fetch();
}


/**
 * Returns facility by Group ID
 * @param string $gorupId Group ID
 * @return mixed
 */
function nror_facilities_get_by_group_id($groupId)
{
  return db_select('nror_facilities', 'f')->fields('f')->condition('f.group_id', $groupId)->execute()->fetch();
}

/**
 * Returns facility by ID
 * @param int $fid Facility ID
 * @return mixed
 */
function nror_facilities_get_by_code($code)
{
  return db_select('nror_facilities', 'f')->fields('f')->condition('f.code', $code)->execute()->fetch();
}

/**
 * Saves or updates facility.
 * @param $data Facility properties
 * @return bool|int
 */
function nror_facilities_save_facility($data)
{
  $record = array(
    'fid' => isset($data['fid']) ? $data['fid'] : null,
    'title' => $data['title'],
    'code' => strtoupper($data['code']),
    'group_id' => isset($data['group_id']) ? $data['group_id'] : null,
  );
  nror_trim_values($record);
  if (isset($record['fid'])) {
    $old = nror_facilities_get($record['fid']);
    if ($old->title != $record['title']) {
      cure_groups_rename_group($old->title, $record['title']);
    }
    return drupal_write_record('nror_facilities', $record, array('fid'));
  } else {
    $groupId = cure_groups_create_group($record['title']);
    if (empty($groupId)) {
      return false;
    }
    $record['group_id'] = $groupId;
    return drupal_write_record('nror_facilities', $record);
  }
}

/**
 * Deletes facility
 * @param $fid Facility ID
 * @return DatabaseStatementInterface|PDOStatement
 */
function nror_facilities_delete_facility($fid)
{
  module_invoke_all('nror_facility_delete_event', $fid);
  $facility = nror_facilities_get($fid);
  cure_groups_delete_group($facility->title);
  return db_delete('nror_facilities')->condition('fid', $fid)->execute();
}

/**
 * Checks whether facility's title is unique
 * @param $title Facility title
 * @return bool
 */
function nror_facilities_facility_title_unique($data)
{
  $select = db_select('nror_facilities', 'nf');
  $select->condition('title', $data['title']);
  if (isset($data['fid'])) {
    $select->condition('fid', $data['fid'], '!=');
  }
  $facility = $select->fields('nf', array('title'))
    ->execute()
    ->fetchAll(PDO::FETCH_ASSOC);
  if (!empty($facility)) {
    return false;
  }
  return true;
}

/**
 * Checks whether facility's code is unique
 * @param $title Facility title
 * @return bool
 */
function nror_facilities_facility_code_unique($data)
{
  $select = db_select('nror_facilities', 'nf');
  $select->condition('code', $data['code']);
  if (isset($data['fid'])) {
    $select->condition('fid', $data['fid'], '!=');
  }
  $facility = $select->fields('nf', array('code'))
    ->execute()
    ->fetchAll(PDO::FETCH_ASSOC);
  if (!empty($facility)) {
    return false;
  }
  return true;
}

/**
 * Saves user to facility relation
 * @param $fid Facility ID
 * @param $uid User ID
 * @return bool|int
 */
function nror_facilities_bind_facility_to_user($fid, $uid)
{
  $record = array('fid' => $fid, 'uid' => $uid);
  return drupal_write_record('nror_facility_users', $record);
}

/**
 * Deletes user to facility relation
 * @param $uid User ID
 * @return DatabaseStatementInterface|PDOStatement
 */
function nror_facilities_unbind_facility_from_user($uid)
{
  return db_delete('nror_facility_users')->condition('uid', $uid)->execute();
}

/**
 * Gets facility user belongs to
 * @param $uid
 * @return mixed
 */
function nror_facilities_get_user_facility($uid)
{
  $user_fid = db_select('nror_facility_users', 'fus')->fields('fus')->condition('fus.uid', $uid)->execute()->fetch();
  if (empty($user_fid)) {
    return false;
  }
  return nror_facilities_get($user_fid->fid);
}

/**
 * Gets all related to facility users
 * @param int $fid Facility ID
 * @return array
 */
function nror_facilities_get_facility_users($fid)
{
  return db_select('nror_facility_users', 'fus')->fields('fus')->condition('fus.fid', $fid)->execute()->fetchAll();
}

/**
 * Gets users by specified role
 * @param $rid Role ID
 * @return array
 */
function nror_facilities_get_table_data($rid, $fid = null)
{
  $query = db_select('users', 'u');
  $query->join('users_roles', 'ur', 'ur.uid=u.uid');
  $query->join('cure_users', 'cu', 'cu.uid=u.uid');
  $query->join('nror_facility_users', 'fu', 'fu.uid=u.uid');
  $query->join('nror_facilities', 'f', 'f.fid=fu.fid');
  $query->fields('u', array('uid', 'name', 'status'));
  $query->fields('cu', array('entity_id'));
  $query->addExpression('f.title', 'facility');
  $query->addExpression('f.fid', 'facility_id');
  $query->condition('ur.rid', $rid);
  if (!empty($fid)) {
    $query->condition('f.fid', $fid);
  }

  $result = $query->execute()->fetchAll();

  return $result;
}

function _nror_facilities_group_changed(&$facility, $newName)
{
  $newGroupId = cure_groups_create_group($newName);
  if (!empty($newGroupId)) {
    $entities = cure_groups_get_group_entities($facility->group_id);
    if (!empty($entities)) {
      foreach ($entities as $entityId) {
        cure_groups_delete_entity_from_group($entityId, $facility->group_id);
        cure_groups_assign_entity_to_gorup($entityId, $newGroupId);
      }
    }
    $facility->title = $newName;
    $facility->group_id = $newGroupId;
  }
}

/**
 * Gets all related to facility users with specified role name
 * @param type $fid
 * @param type $role_name
 * @return type
 */
function nror_facilities_get_facility_users_by_role($fid, $role_name)
{
  $facility_users = nror_facilities_get_facility_users($fid);
  $role = user_role_load_by_name($role_name);
  $role_users = nror_get_users_by_role_id($role->rid);
  foreach ($facility_users as $user) {
    foreach ($role_users as $uid => $entity) {
      if (in_array($uid, (array)$user)) {
        $result[$uid] = $uid;
      }
    }
  }

  return $result;
}

function nror_facilities_get_all_practice_statuses()
{
  return db_select('cure_practice_statuses', 'ps')
    ->fields('ps', array())
    ->condition('ps.type', 0)
    ->execute()
    ->fetchAll();
}

function nror_facilities_get_practice_status($practice_id)
{
  $query = db_select('cure_practice_status_relation', 'ps');
  $query->join('cure_practice_statuses', 's', 's.id=ps.status_id');
  $query->fields('s', array('status', 'id', 'link', 'type'));
  $query->condition('ps.practice_id', $practice_id);
  $query->condition('s.type', 0);
  $result = $query->execute()->fetch();

  return $result;
}

function nror_facilities_save_practice_status($id, $status_id)
{
  $existingStatus = nror_facilities_get_practice_status($id);

  if ($existingStatus) {
    return db_update('cure_practice_status_relation')->fields(array('status_id' => $status_id))
      ->condition('practice_id', $id)
      ->execute();
  } else {
    $record = array('id' => null, 'practice_id' => $id, 'status_id' => $status_id);
    drupal_write_record('cure_practice_status_relation', $record);
  }
}