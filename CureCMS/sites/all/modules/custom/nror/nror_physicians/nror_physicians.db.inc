<?php

/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

function nror_physicians_get_all_provider_statuses()
{
  return db_select('cure_practice_statuses', 'ps')
    ->fields('ps', array())
    ->condition('ps.type', 1)
    ->execute()
    ->fetchAll();
}

function nror_physicians_get_provider_status($provider_id)
{
  $query = db_select('cure_practice_status_relation', 'ps');
  $query->join('cure_practice_statuses', 's', 's.id=ps.status_id');
  $query->fields('s', array('status', 'id', 'link', 'type'));
  $query->condition('ps.practice_id', $provider_id);
  $query->condition('s.type', 1);
  $result = $query->execute()->fetch();

  return $result;
}

function nror_physicians_save_provider_status($id, $status_id)
{
  $existingStatus = nror_physicians_get_provider_status($id);

  if ($existingStatus) {
    return db_update('cure_practice_status_relation')->fields(array('status_id' => $status_id))
      ->condition('practice_id', $id)
      ->execute();
  } else {
    $record = array('id' => null, 'practice_id' => $id, 'status_id' => $status_id);
    drupal_write_record('cure_practice_status_relation', $record);
  }
}