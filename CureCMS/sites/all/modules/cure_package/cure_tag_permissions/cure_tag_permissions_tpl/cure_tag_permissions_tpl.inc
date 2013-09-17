<?php
/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

function cure_tag_permissions_tpl_single_main($form, &$form_state) {
  if ($form_state['submitted'] == true && $form_state['triggering_element']['#name'] == 'delete') {
    $roles = $form_state['values']['roles'];
    helpers_filter_checkboxes($roles);
    return drupal_get_form('cure_tag_permissions_tpl_single_delete_multiple', $roles);
  }
  
  $form = array();
   
  $header = array(
    'role' => array('data' => t('User')),
    'operations' => array('data' => t('Actions'))
  );
  
  $relations = cure_tag_permissions_tpl_get_all_single_bindings();
  $options = array();
  
  if (!empty($relations)) {
    foreach($relations as $relation) {
      $options[$relation->role_id] = array();
      
      $options[$relation->role_id]['role'] = user_role_load($relation->role_id)->name;;
      
      $operations = array();
      
      $operations['edit'] = array(
        'title' => t('Edit'),
        'href' => 'admin/people/cure/cure_tags_permissions/templates/' . $relation->role_id . '/edit',
      );
      $operations['delete'] = array(
        'title' => t('Delete'),
        'href' => 'admin/people/cure/cure_tags_permissions/templates/' . $relation->role_id . '/delete',
      );
      $options[$relation->role_id]['operations'] = array(
        'data' => array(
          '#theme' => 'links__node_operations',
          '#links' => $operations,
          '#attributes' => array('class' => array('links', 'inline')),
        ),
      );
    }
  }
  
  $form['new_binding'] = array(
    '#type' => 'submit', 
    '#value' => t('Add new template'),
    '#name' => 'new_binding',
  );

  $form['roles'] = array(
    '#type'    => 'tableselect',
    '#header'  => $header,
    '#options' => $options,
    '#empty' => t('No templates found'),
  );
  
  if (!empty($relations)) {
    $form['delete'] = array(
      '#type' => 'submit',
      '#value' => t('Delete'),
      '#name' => 'delete',
    );
  }

  return $form;
}

function cure_tag_permissions_tpl_single_main_submit($form, &$form_state) {
  $roles = $form_state['values']['roles'];
  
  if ($form_state['triggering_element']['#name'] == 'new_binding') {
    drupal_goto('admin/people/cure/cure_tags_permissions/templates/add');
  }
  else if ($form_state['triggering_element']['#name'] == 'delete') {
    helpers_filter_checkboxes($roles);
    if (!empty($roles)) {
      $form_state['rebuild'] = true;
    }
  }
}

function cure_tag_permissions_tpl_single_delete_multiple($form, $form_state, $roles) {
  $form = array();

  $form['message'] = array('#markup' => '<p>'.t('Are you sure you want delete these templates?').'</p>');

  $form['list'] = array('#prefix' => '<ul>', '#suffix' => '</ul>', '#tree' => TRUE);

  foreach ($roles as $role) {
    $role_name = user_role_load($role)->name;
    $form['list'][$role] = array(
      '#type' => 'hidden',
      '#value' => $role,
      '#prefix' => '<li>',
      '#suffix' =>  $role_name . "</li>\n",
    );
  }

  $form['delete'] = array(
    '#type' => 'submit',
    '#value' => t('Delete'),
    '#name' => 'delete'
  );

  $form['cancel'] = array(
    '#type' => 'submit',
    '#value' => t('Cancel'),
    '#name' => 'cancel'
  );

  return $form;
}

function cure_tag_permissions_tpl_single_delete_multiple_submit($form, &$form_state) {
  if ($form_state['triggering_element']['#name'] == 'delete') {
    foreach ($form_state['values']['list'] as $role) {
      cure_tag_permissions_tpl_delete_single($role);
    }
    drupal_set_message(t('Bindings were successfully deleted.'));
  }
  drupal_goto('admin/people/cure/cure_tags_permissions/templates');
}

function cure_tag_permissions_tpl_single_form($form, &$form_state, $role_id = null) {
  $roles = user_roles(true);
  
  foreach ($roles as $rid => $role) {
    if (!cure_contexts_get_role_context($rid)) {
      unset($roles[$rid]);
    }
  }
  $tags = cure_tag_permissions_get_tags();

  if (isset($role_id)) {
    $template = cure_tag_permissions_tpl_get_single_bindings($role_id);
    
    $default_value = array();
    $data = unserialize($template->data);
    foreach ($data as $tag => $permissions) {
      foreach ($permissions as $permission) {
        $default_value[$tag . ':' . $permission] = $tag . ':' . $permission;
      }
    }

    $form['#template'] = $template;
  }

  if (!empty($tags)) {
    $settings = cure_tag_permissions_get_settings();
    if (isset($tags['tags']['tag']['@attributes'])) {
      $tag = $tags['tags']['tag'];
      $tags['tags']['tag'] = array($tag);
    }

    $tag_permission = array();
    foreach ($tags['tags']['tag'] as $tag) {
      $tag_attr = $tag['@attributes'];
      if ($tag_attr['type'] == 'formTag') {
        $tag_permission[] = array('#value' => t($tag_attr['id']));
        foreach ($settings as $perm) {
          $options[$tag_attr['id'].':'.$perm] = '';
        }
      }
    }

    $form['role'] = array(
      '#type' => 'select',
      '#options' => $roles,
      '#default_value' => isset($role_id) ? $role_id : ''
    );    
    
    $form['checkboxes'] = array(
      '#type' => 'checkboxes',
      '#options' => $options,
      '#theme' => 'cure_tag_permissions_tpl_checkboxes_form',
      '#tag_permission' => $tag_permission,
      '#miltiple_choice' => '<div class="multiple-choice"><a id="check_all" href="#">check all</a> / <a id="uncheck_all" href="#">uncheck all</a></div>'
    );
    
    if (!empty($default_value)) {
      $form['checkboxes']['#default_value'] = $default_value;
    }
    
    $form['actions']['submit_save'] = array('#type' => 'submit', '#value' => t('Save'), '#name' => 'save', '#weight' => 4);
    $form['actions']['submit_back'] = array('#type' => 'submit', '#value' => t('Back'), '#name' => 'back', '#weight' => 5, '#limit_validation_errors' => array(), '#submit' => array('cure_tag_permissions_tpl_single_form_submit'));
  }

  return $form;  
}

function cure_tag_permissions_tpl_single_form_validate($form, &$form_state) {
  $data = $form_state['values'];
   
  if (cure_tag_permissions_tpl_single_binding_exists($data['role'])) {
    if (!isset($form['#template']) || $form['#template']->role_id != $data['role']) {
      form_set_error('role', t('Role has been already binded'));
    }    
  }
}

function cure_tag_permissions_tpl_single_form_submit($form, $form_state) {
  $op = $form_state['triggering_element']['#name'];
  if ($op == 'save') {
    $role = $form_state['values']['role'];
       
    $message = t('Template was created successfully');
    if (isset($form['#template'])) {
      $message = t('Template was updated successfully');
    }
    
    $form_data = serialize(cure_tag_permissions_parse_post_data($form_state['values']));
    if (cure_tag_permissions_tpl_save_single_binding($role, $form_data)) {
      drupal_set_message($message);
    }
  }
  drupal_goto('admin/people/cure/cure_tags_permissions/templates');
}

function cure_tag_permissions_tpl_delete_single_form($form, &$form_state, $role_id) {
  $form = array();

  $form['message'] = array('#markup' => '<p>'.t('Are you sure you want to delete template?').'</p>');

  $form['#role'] = array(
    '#type' => 'value',
    '#value' => $role_id
  );

  $form['delete'] = array(
    '#type' => 'submit',
    '#value' => t('Delete'),
    '#name' => 'delete'
  );

  $form['cancel'] = array(
    '#type' => 'submit',
    '#value' => t('Cancel'),
    '#name' => 'cancel'
  );

  return $form;
}

function cure_tag_permissions_tpl_delete_single_form_submit($form, &$form_state) {
  if ($form_state['triggering_element']['#name'] == 'delete') {
    $role_id = $form['#role']['#value'];
    cure_tag_permissions_tpl_delete_single($role_id);
    drupal_set_message(t('Template was deleted successfully.'));
  }
  drupal_goto('admin/people/cure/cure_tags_permissions/templates');
}