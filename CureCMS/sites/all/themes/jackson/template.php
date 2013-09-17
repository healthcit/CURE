<?php

/**
 * Set count variables so column numbers can be dynamic.
 */
function jackson_preprocess_page (&$vars) {
  $vars['preface'] = count(array_filter(array($vars['page']['preface_one'], $vars['page']['preface_two'], $vars['page']['preface_three'])));
  $vars['bottom'] = count(array_filter(array($vars['page']['bottom_one'], $vars['page']['bottom_two'], $vars['page']['bottom_three'], $vars['page']['bottom_four'])));
  // Display user account links.
  $vars['columns'] = count(array_filter(array($vars['page']['sidebar_first'], $vars['page']['sidebar_second'])));
  count(array_filter(array($vars['page']['sidebar_first'], $vars['page']['sidebar_second']))) == 2 ? $vars['two_sidebars'] = true : $vars['two_sidebars'] = false;
  // Display user account links.
  $vars['user_links'] = _jackson_user_links();
  //dpm($vars);

  global $user;
  if ($user->uid == '0') {
    //if ($_GET['test'] == '1') {
    //print('<!--').print_r($vars).print('-->');
    //}
    if(module_exists('path'))
    {
      //gets the "clean" URL of the current page
      $alias = drupal_get_path_alias($_GET['q']);
      if ($alias == 'my-account/my-account-overview' || $vars['node']->nid == "71") {
        drupal_goto('user/login');
      }
    }
  }
  else {
    // check for the consent forms for the join process and, if the user is logged in, redirect them to the logged-in alias
    if ($vars['node']->nid){
      if ($vars['node']->nid == "46" || $vars['node']->nid == "3") {
        drupal_goto('node/109');
      }
    }
  }

  // Classes for body element. Allows advanced theming based on context
  // (home page, node of certain type, etc.)
  $body_classes = array($vars['classes']);
  if (!$vars['is_front']) {
    // Add unique classes for each page and website section
    $path = drupal_get_path_alias($_GET['q']);
    list($section,) = explode('/', $path, 2);
    $body_classes[] = jackson_id_safe('page-' . $path);
    $body_classes[] = jackson_id_safe('section-' . $section);
    if (arg(0) == 'node') {
      if (arg(1) == 'add') {
        if ($section == 'node') {
          array_pop($body_classes); // Remove 'section-node'
        }
        $body_classes[] = 'section-node-add'; // Add 'section-node-add'
      }
      elseif (is_numeric(arg(1)) && (arg(2) == 'edit' || arg(2) == 'delete')) {
        if ($section == 'node') {
          array_pop($body_classes); // Remove 'section-node'
        }
        $body_classes[] = 'section-node-' . arg(2); // Add 'section-node-edit' or 'section-node-delete'
      }
    }
    // Add a unique class when viewing a node
    if (arg(0) == 'node' && is_numeric(arg(1))) {
      $body_classes[] = 'node-full-view'; // Add 'node-full-view'
    }
  }
  $vars['classes'] = implode(' ', $body_classes); // Concatenate with spaces
  $vars['tabs2'] = menu_secondary_local_tasks();

  if (!empty($vars['styles_unlimited_css']))
  {
    $vars['styles'] = $vars['styles_unlimited_css'];
  }
}

function jackson_id_safe($string) {
  if (is_numeric($string{0})) {
    // If the first character is numeric, add 'n' in front
    $string = 'n' . $string;
  }
  return strtolower(preg_replace('/[^a-zA-Z0-9-]+/', '-', $string));
}

/**
 * User/account related links.
 */
function _jackson_user_links() {
  // Add user-specific links
  global $user;
  $user_links = array();
  if (empty($user->uid)) {
    $user_links['login'] = array('title' => t('Login'), 'href' => 'user');
    // Do not display register link if registration is not allowed.
    if (variable_get('user_register', 1)) {
      $user_links['register'] = array('title' => t('Register'), 'href' => 'user/register');
    }
  }
  else {
    $user_links['account'] = array('title' => t('Hello @username', array('@username' => $user->name)), 'href' => 'user', 'html' => TRUE);
    $user_links['logout'] = array('title' => t('Logout'), 'href' => "logout");
  }
  return $user_links;
}

function getName($uid) {
  $result = db_select('profile_value', 'pv')
    ->fields('pv', array('value'))
    ->condition('pv.fid', '1')
    ->condition('uid', $uid)
    ->execute()
    ->fetchAssoc();

  $name = $result['value'];

  return $name;
}

function classify($element) {
  $class = str_replace(' ', '-', $element); //Strips empty spaces and replaces with dashes
  $class = strtolower($class); //makes letter lowercase for ease of CSS Styling

  return $class;
}

function jackson_preprocess_html(&$vars) {

  // Hook into color.module
  if (module_exists('color')) {
    _color_html_alter($vars);
  }
}
