<?php
/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

/**
 * Creates new context
 * @param array $context Values
 * @return bool|int
 */
function cure_contexts_save_context($context) {
  $fields = array(
    'name' => $context['name'],
    'description' => $context['description']
  );
  $context_id = db_insert('cure_contexts')->fields($fields)->execute();
  if ($context_id != false) {
    context_save((object)$context);
    _cure_contexts_save_primary_role($context_id, $context['role']);
  }
  return $context_id;
}

/**
 * Updates existing context
 * @param array $context Values
 * @return bool|int
 */
function cure_contexts_update_context($context) {
  $cure_context = cure_contexts_get($context['context_id']);
  $drupal_context = context_load($cure_context->name);
  $result = drupal_write_record('cure_contexts', $context, array('context_id'));
  if ($result != false) {
    if ($drupal_context->name != $context['name']) {
      context_delete((object)$drupal_context);
      context_save((object)$context);
    }
    _cure_contexts_update_primary_role($cure_context->context_id, $context['role']);
  }
  return $result;
}

/**
 * Deletes context
 * @param int $context_id Context ID
 * @return DatabaseStatementInterface
 */
function cure_contexts_delete_context($context_id) {
  $context = cure_contexts_get($context_id);
  if ($context) {
    $result = db_delete('cure_contexts')->condition('context_id', $context_id)->execute();
    if ($result != false) {
      context_delete($context);
      _cure_contexts_delete_primary_role($context->context_id);
      cure_contexts_unbind_all_roles_from_context($context->context_id);
    }
    return $result;
  }
}

/**
 * Returns CURE contexts to forms to be used in table (pager limited).
 * @return array
 */
function cure_contexts_table_get_all() {
  $query = db_select('cure_contexts', 'ccx')->extend('PagerDefault');
  $query->fields('ccx');
  $query = $query->limit(CURE_CONTEXT_TABLE_LIMIT);
  return $query->execute()->fetchAll(PDO::FETCH_ASSOC);
}

/**
 * Returns all available CURE contexts to forms.
 * @return array
 */
function cure_contexts_get_all() {
  return db_select('cure_contexts', 'ccx')->fields('ccx')->execute()->fetchAll(PDO::FETCH_ASSOC);
}

/**
 * Returns context
 * @param int $context_id Context ID
 * @return mixed
 */
function cure_contexts_get($context_id) {
  return db_select('cure_contexts', 'ccx')->fields('ccx')->condition('ccx.context_id', $context_id)->execute()->fetch();
}

/**
 * Returns context by name
 * @param string $name Context name
 * @return mixed
 */
function cure_contexts_get_by_name($name) {
  return db_select('cure_contexts', 'ccx')->fields('ccx')->condition('ccx.name', $name)->execute()->fetch();
}

/**
 * Returns context by role ID
 * @param int $role_id Role ID
 * @return mixed
 */
function cure_contexts_get_by_role($role) {
  return db_select('cure_contexts', 'ccx')->fields('ccx')->condition('ccx.role', $role)->execute()->fetch();
}

/**
 * Checks whether context role name is unique
 * @param string $name Role name
 * @return bool
 */
function cure_contexts_is_role_unique($name, $context_id = null) {
  $role = user_role_load_by_name($name);
  if (!empty($role)) {

    if (!empty($context_id)) {
      $context_role = cure_contexts_get_primary_role($context_id);
      if ($context_role->name == $name) {
        return true;
      }
    }
    return false;
  }
  return true;
}

/**
 * Checks whether context name is unique
 * @param string $name Context name
 * @return bool
 */
function cure_contexts_is_unique($name, $context_id = null) {
  $context = cure_contexts_get_by_name($name);
  $drupal_context = context_load($name);
  if (!empty($context) || !empty($drupal_context)) {
    if (empty($context_id) || $context_id != $context->context_id) {
      return false;
    }
  }
  return true;
}

/**
 * Creates CURE main context Drupal role
 * @param string $name Role name
 */
function _cure_contexts_save_primary_role($context_id, $name) {
  $role = array('name' => $name);
  $role_id = db_insert('role')->fields($role)->execute();
  _cure_contexts_assign_default_permissions($role_id);
  cure_contexts_bind_role_to_context($role_id, $context_id, true);
  if ($role_id != false && module_exists('autoassignrole')) {
    _cure_contexts_add_to_autoassign($role_id);
  }
  return $role_id;
}

/**
 * Updates CURE context corresponding Drupal role
 * @param string $name Role name
 * @param string $old Old role name
 */
function _cure_contexts_update_primary_role($context_id, $name) {
  $role = cure_contexts_get_primary_role($context_id);
  if ($role != false && $name != $role->name) {
    return db_update('role')->fields(array ('name' => $name))->condition('rid', $role->rid)->execute();
  }
  return false;
}

/**
 * Removes CURE context corresponding Drupal role and all related to the role data
 * @param int $context_id Context
 */
function _cure_contexts_delete_primary_role($context_id) {
  $role = cure_contexts_get_primary_role($context_id);
  if ($role) {
    cure_contexts_unbind_role_from_context($role->rid);
    db_delete('role')->condition('rid', $role->rid)->execute();
    db_delete('role_permission')->condition('rid', $role->rid)->execute();
    db_delete('users_roles')->condition('rid', $role->rid)->execute();
    if (module_exists('autoassignrole')) {
      _cure_contexts_remove_from_autoassign($role->rid);
    }
  }
}

/**
 * Creates permissions for role
 * @param int $role_id
 */
function _cure_contexts_assign_default_permissions($role_id) {
  $permissions = array(
    'access news feeds', 'access mail template', 'view faq page',
    'access frontpage', 'access content', 'access print', 'access refer_a_friend',
    'switch theme', 'change own username', 'delete own account',
  );
  foreach ($permissions as $perm) {
    $record = array('rid' => $role_id, 'permission' => $perm);
    drupal_write_record('role_permission', $record);
  }
}

function cure_contexts_get_all_roles() {
  return db_select('cure_roles_contexts', 'ccr')->fields('ccr')->execute()->fetchAll(PDO::FETCH_ASSOC);
}

function cure_contexts_get_primary_role($context_id) {
  $role_id = db_select('cure_roles_contexts', 'ccr')
    ->fields('ccr')
    ->condition('context_id', $context_id)
    ->condition('primary_role', 1)
    ->execute()
    ->fetch();
  if ($role_id != false) {
    return user_role_load($role_id->role_id);
  }
  return false;
}

function cure_contexts_bind_role_to_context($role_id, $context_id, $primary_role = false) {
  if (cure_contexts_get_primary_role($context_id) != false) {
    $primary_role = false;
  }

  $record = array(
    'role_id' => $role_id,
    'context_id' => $context_id,
    'primary_role' => $primary_role == true ? 1 : 0
  );

  if (cure_contexts_get_role_context($role_id) != false) {
    return drupal_write_record('cure_roles_contexts', $record, array('role_id'));
  } else {
    return drupal_write_record('cure_roles_contexts', $record);
  }
}

function cure_contexts_unbind_role_from_context($role_id) {
  return db_delete('cure_roles_contexts')->condition('role_id', $role_id)->execute();
}

function cure_contexts_unbind_all_roles_from_context($context_id) {
  return db_delete('cure_roles_contexts')->condition('context_id', $context_id)->execute();
}

function cure_contexts_get_role_context($rid) {
  $row = db_select('cure_roles_contexts', 'ccr')
    ->fields('ccr')
    ->condition('role_id', $rid)
    ->execute()
    ->fetch();
  if ($row != false) {
    return cure_contexts_get($row->context_id);
  }
  return false;
}

function cure_contexts_get_all_context_roles($context_id) {
  return db_select('cure_roles_contexts', 'ccr')
    ->fields('ccr')
    ->condition('context_id', $context_id)
    ->execute()
    ->fetchAll(PDO::FETCH_ASSOC);
}

function cure_contexts_is_role_primary($rid) {
  $check = db_select('cure_roles_contexts', 'ccr')->fields('ccr')
    ->fields('ccr')
    ->condition('role_id', $rid)
    ->condition('primary_role', 1)
    ->execute()
    ->fetch();
  if ($check != false) {
    return true;
  }
  return false;
}

