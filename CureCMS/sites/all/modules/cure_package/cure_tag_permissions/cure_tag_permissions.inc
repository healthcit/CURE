<?php
/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

define('CURE_PERMISSIONS_TABLE_LIMIT', 10);

/** 
 * Returns array of form elements. 
 * Menu callback argument.
 * @param array $form_state
 * @return array
 */
function cure_tag_permissions_form($form, &$form_state) {
  $users = _cure_tag_permissions_get_all_users();
      
  $form = array();

  if (!empty($users)) {
    $settings = cure_tag_permissions_get_settings();
  
    $entity_perm = cure_tag_permissions_get_all_permissions();

    if (empty($entity_perm)) {
      drupal_set_message('No permissions were found.', 'error');
      return '';
    }

    $current_perms = cure_tag_permissions_get_current_perms($users, $entity_perm, $settings);
        
    $header = array(
      'title' => t('Users'),
    );
  
    $form['permissions'] = array(
      '#type' => 'fieldset',
      '#title' => t('Permissions'),
      '#attributes' => array('class' => array('container-inline'), 'style' => 'border: none;'),
      '#tree' => true,
      '#weight' => 0,
    );
    
    foreach($settings as $permission) {
      $form['permissions'][$permission] = array(
        '#type' => 'checkbox', 
        '#title' => t($permission),
      );
    }
  
    $tids = cure_tag_permissions_prepare_tags();
       
    if (!empty($tids)) {
      $form['tags'] = array(
        '#type' => 'container', 
        '#tree' => true,
      );
      
      foreach ($tids as $tag) {
        $header[$tag] = '<a href="#" class="cure_tag">' . $tag . '</a>';
        $form['tags'][$tag] = array(
          '#type' => 'checkbox', 
          '#title' => t($tag),
          '#id' => 'tag_' . $tag,
          '#attributes' => array('style' => 'display:none'),
          '#title_display' => 'invisible',
        );
      }
    }
  
    if (!empty($users)) {
      foreach ($users as $identifier => $user) {
        $options[$identifier] = array(
          'title' => $user,
        );
        foreach ($tids as $tag) {
          $str = '';
          foreach ($settings as $perm) {
            $val = $current_perms[$identifier][$tag][$perm];
            if (!empty($val)) {
              $str .= $val . '<br/>';
            }
            if (!is_null($val))
              $old_perm[$identifier][$tag][] = $val;
          }
          $options[$identifier][$tag] = $str;
        }
      }
    }
    
    $form['old_perm'] = array(
      '#type' => 'value',
      '#value' =>  $old_perm,
    );
  
    $form['select'] = array(
      '#markup' => '<div class="multiple-choice"><a id="select_all_tags" href="#">select all tags</a> / <a id="deselect_all_tags" href="#">deselect all tags</a></div>',
      '#weight' => 1,
    );
     
    $form['users'] = array(
      '#type'    => 'tableselect',
      '#header'  => $header,
      '#options' => $options,
      '#empty' => t('Groups were not found.'),
      '#prefix' => '<div id="ajax-groups">',
      '#suffix' => '</div>',
      '#weight' => 2,
    );
    
    $form['pager'] = array(
      '#markup' => theme('pager'),
      '#weight' => 3,
    );

    $form['save'] = array(
      '#type' => 'submit',
      '#value' => t('Save'),
      '#name' => 'save',
      '#weight' => 4,
    );
  }
  
  else {
    $form['message'] = array('#markup' => t('No users were found'));
  }
  
  return $form;
}

/**
 * Implementation of {@see cacure_tag_permissions_form()} submit function.
 * @param array $form
 * @param array $form_state
 * @return null 
 */
function cure_tag_permissions_form_submit($form, &$form_state) {
 $old_perm = $form_state['values']['old_perm'];
  
  $permissions = $form_state['values']['permissions'];
  helpers_filter_checkboxes($permissions);
  
  $tags = $form_state['values']['tags'];

  helpers_filter_checkboxes($tags);
  
  if (!empty($tags)) {
    foreach ($tags as $tid => $t_value) {
      if (!empty($permissions)) {
        foreach ($permissions as $pid => $p_value) {
          $form_data[$tid][] = $pid;
        }
      }
      else {
        $form_data[$tid] = array();
      }
    }
  }
  
  $users = array();
  
  $all_users = $form_state['values']['users'];
  foreach ($all_users as $key => $user) {
    if ($user !== 0) {
      $users[$key] = $user;
    }
  }
  if (!empty($users)) {
    foreach ($users as $eid => $user) {
      $table_data = $old_perm[$eid];
      if (!empty($table_data)) {
        foreach ($table_data as $tag => $val) {
          if (empty($form_data) || !array_key_exists($tag, $form_data)) {
            $form_data[$tag] = $table_data[$tag];
          }
        }
      }
      if (!empty($form_data)) {
        $xml = cure_tag_permissions_prepare_xml_for_entity($eid, $form_data);    
        $call = RestCall::getInstance();
        $response = $call->savePermissions($eid, $xml);
        if ($response->code != '200') {
          drupal_set_message('Error occurred. Form tags permissions were not changed.', 'error');
        }
      }
    }
  }
}

function cure_tag_permissions_get_current_perms($users, $entity_perm, $permissions) {
  $current_perms = array();
  
  if (!empty($users)) {
    foreach ($users as $id => $name) {
      if (isset($entity_perm['permissions']['entityTagPermissions']['@attributes'])) {
        $tag_permission = $entity_perm['permissions']['entityTagPermissions'];
        $entity_perm['permissions']['entityTagPermissions'] = array($tag_permission);
      }

      foreach ($entity_perm['permissions']['entityTagPermissions'] as $tag_permission) {
        $eid = $tag_permission['@attributes']['entityId'];
        if ($id === $eid) {
          if (!empty($tag_permission['formTagPermissions'])) {
            foreach ($tag_permission['formTagPermissions'] as $tags) {
              foreach ($tags as $key => $tag) {
                $tags_perm = array();
                if (is_numeric($key)) {
                  $tag_id = $tag['@attributes']['id'];
                  if (is_string($tag['formTagPermission'])) {
                    $tags_perm[] = $tag['formTagPermission'];
                  }
                  else if (is_array($tag['formTagPermission'])){
                    $tags_perm = $tag['formTagPermission'];
                  }
                }
                else if (is_string($key)) {
                  $tag_id = $tags['@attributes']['id'];
                  if (is_string($tags['formTagPermission'])) {
                    $tags_perm[] = $tags['formTagPermission'];
                  }
                  else if (is_array($tags['formTagPermission'])) {
                    $tags_perm = $tags['formTagPermission'];
                  }
                }
                foreach ($permissions as $perm) {
                  if (in_array($perm, $tags_perm)) {
                    $current_perms[$eid][$tag_id][$perm] = $perm;
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  return $current_perms;
}

function cure_tag_permissions_prepare_tags() {
  $tags = cure_tag_permissions_get_tags();
  if (!empty($tags)) {
    if (isset($tags['tags']['tag']['@attributes'])) {
      $tag = $tags['tags']['tag'];
      $tags['tags']['tag'] = array($tag);
    }
    foreach ($tags['tags']['tag'] as $tag) {
      $tag_attr = $tag['@attributes'];
      if ($tag_attr['type'] == 'formTag') {
        $tag_id = $tag['@attributes']['id'];
        if (!empty($tag_id)) {
          $tids[$tag_id] = $tag_id;
        }
      }
    }
  }
  return $tids;
}

/**
 * Used to prepare an associative array in which key is entity's identifier (generated by API server)
 * and value is Drupal user's name.
 * @return array
 */
function _cure_tag_permissions_get_all_users() {
  $users = array();
  
  $query = db_select('cure_users', 'cu')->extend('PagerDefault');
  $query->join('users', 'u', 'cu.uid=u.uid');
  $query->fields('cu', array('entity_id'));
  $query->fields('u', array('name'));  
  $query = $query->limit(CURE_PERMISSIONS_TABLE_LIMIT);
  
  $result = $query->execute()->fetchAll();
  
  $users = array();
  foreach ($result as $user) {
    $users[$user->entity_id] = $user->name;
  }
  
  return $users;
}