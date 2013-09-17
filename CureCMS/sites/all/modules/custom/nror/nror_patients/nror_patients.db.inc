<?php

/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

function nror_patients_get_all() {
  $role = user_role_load_by_name(NROR_USER_ROLE_PATIENT);
  $users = nror_get_users_by_role_id($role->rid);
  return $users;
}

function nror_patients_get_all_by_facility($fid) {
  $role = user_role_load_by_name(NROR_USER_ROLE_PATIENT);
  $users = nror_get_users_by_role_id($role->rid);
  foreach ($users as $id => $user) {
    if (empty($user->facility) || $user->facility != $fid) {
      unset($users[$id]);
    }
  }
  return $users;
}

function nror_patients_get_all_by_physician($physicianId) {
  $role = user_role_load_by_name(NROR_USER_ROLE_PATIENT);
  $users = nror_get_users_by_role_id($role->rid);
  foreach ($users as $id => $user) {
    if (empty($user->physician) || $user->physician != $physicianId) {
      unset($users[$id]);
    }
  }
  return $users;
}

/**
 * Returns all patients
 * @return array
 */
function nror_patients_get_table_data($fid = null) {
  $role = user_role_load_by_name(NROR_USER_ROLE_PATIENT);
  return nror_facilities_get_table_data($role->rid, $fid);
}

/**
 * Saves patient-doctor relation
 * @param $patientId
 * @param $physicianId
 * @return bool|int
 */
function nror_patients_assign_patient_to_doctor($patientId, $physicianId) {
  $record = array(
    'pid' => $patientId,
    'did' => $physicianId
  );
  $patient = user_load($patientId);
  $physician = user_load($physicianId);

  $old_doctor = nror_patients_get_patient_doctor($patientId);
  if ($old_doctor != false) {
    $old_doctor = user_load($old_doctor);
    nror_patients_clear_doctor_patient_relation($patient, $old_doctor);
  }
  $groupId = cure_groups_get_group_id($patient->name);
  if (cure_groups_assign_entity_to_gorup($physician->entityId, $groupId)) {
    return drupal_write_record('nror_patient_doctor', $record);
  }

}

/**
 * Removes patient-doctor relation
 * @param $pid
 * @return DatabaseStatementInterface
 */
function nror_patients_clear_doctor_patient_relation($patient, $doctor) {
  $call = RestCall::getInstance();
  $patientGroup = $call->getGroupId($patient->name);
  if (cure_groups_delete_entity_from_group($doctor->entityId, $patientGroup)) {
    return db_delete('nror_patient_doctor')
      ->condition('pid', $patient->uid)
      ->execute();
  }
  return false;
}

/**
 * Returns patients doctor ID
 * @param $uid
 */
function nror_patients_get_patient_doctor($pid) {
  $result = db_select('nror_patient_doctor', 'pd')
    ->fields('pd', array('did'))
    ->condition('pd.pid', $pid)
    ->execute()
    ->fetch();
  return $result->did;
}

/**
 * Checks whether patient has an attending physician
 * @param $pid
 * @return bool
 */
function nror_patients_patient_has_doctor($pid) {
  $result = nror_patients_get_patient_doctor($pid);
  if (!empty($result)) {
    return true;
  }
  return false;
}

function nror_patients_is_patient($user) {
  $role = user_role_load_by_name(NROR_USER_ROLE_PATIENT);
  if (isset($user->roles[$role->rid])) {
    return true;
  }
  return false;
}