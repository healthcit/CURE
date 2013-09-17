<?php

/**
 * Gets the User First Name via the UID for display purposes
 */
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

/**
 * Removes white spaces and capitalization from variables for cleaner CSS
 */
function classify($element) {
  $class = str_replace(' ', '-', $element); //Strips empty spaces and replaces with dashes
  $class = strtolower($class); //makes letter lowercase for ease of CSS Styling

  return $class;
}

/**
 * Removes slashes from uri and capitalization from variables for cleaner CSS
 */
function change_uri($element) {
  $class = str_replace('/', '-', $element); //Strips empty spaces and replaces with dashes
  $class = strtolower($class); //makes letter lowercase for ease of CSS Styling
  $class = substr($class, 1);
  if (substr($class, 0, 8) == 'editform') {
    $class = 'editform';
  }
  return $class;
}

/**
 * Return a themed breadcrumb trail.
 *
 * @param $breadcrumb
 *   An array containing the breadcrumb links.
 * @return a string containing the breadcrumb output.
 */
function acquia_marina_breadcrumb($variables) {
  $breadcrumb = $variables['breadcrumb'];

  if (!empty($breadcrumb)) {
    // Provide a navigational heading to give context for breadcrumb links to
    // screen-reader users. Make the heading invisible with .element-invisible.
    $output = '<h2 class="element-invisible">' . t('You are here') . '</h2>';

    $output .= '<div class="breadcrumb">' . implode(' › ', $breadcrumb) . '</div>';
    return $output;
  }
}

/**
 * Override or insert variables into the maintenance page template.
 */
function acquia_marina_preprocess_maintenance_page(&$vars) {
  // While markup for normal pages is split into page.tpl.php and html.tpl.php,
  // the markup for the maintenance page is all in the single
  // maintenance-page.tpl.php template. So, to have what's done in
  // acquia_marina_preprocess_html() also happen on the maintenance page, it has to be
  // called here.
  acquia_marina_preprocess_html($vars);
}

/**
 * Override or insert variables into the html template.
 */
function acquia_marina_preprocess_html(&$vars) {
  // Toggle fixed or fluid width.
  if (theme_get_setting('acquia_marina_width') == 'fluid') {
    $vars['classes_array'][] = 'fluid-width';
  }

  // Add conditional CSS for IE6.
  drupal_add_css(path_to_theme() . '/css/fix-ie.css', array('group' => CSS_THEME, 'browsers' => array('IE' => 'lt IE 7', '!IE' => FALSE), 'preprocess' => FALSE));
}

/**
 * Override or insert variables into the page template.
 */
function acquia_marina_preprocess_page(&$vars) {
  global $user;

  if (user_is_logged_in()) {
    $link = l(getName($user->uid), 'user');
    $vars['page']['header_top']['hello_user'] = array(
      '#markup' => "<h2 class=\"user-name\">Hello {$link}</h2>"
    );
  }

  if ($user->uid == '0') {
    //if ($_GET['test'] == '1') {
    //print('<!--').print_r($vars).print('-->');
    //}
    if(module_exists('path')) {
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

  if (isset($vars['main_menu'])) {
    $vars['primary_nav'] = theme('links__system_main_menu', array(
      'links' => $vars['main_menu'],
      'attributes' => array(
        'class' => array('links', 'inline', 'main-menu'),
      ),
      'heading' => array(
        'text' => t('Main menu'),
        'level' => 'h2',
        'class' => array('element-invisible'),
      )
    ));
  }
  else {
    $vars['primary_nav'] = FALSE;
  }
  if (isset($vars['secondary_menu'])) {
    $vars['secondary_nav'] = theme('links__system_secondary_menu', array(
      'links' => $vars['secondary_menu'],
      'attributes' => array(
        'class' => array('links', 'inline', 'secondary-menu'),
      ),
      'heading' => array(
        'text' => t('Secondary menu'),
        'level' => 'h2',
        'class' => array('element-invisible'),
      )
    ));
  }
  else {
    $vars['secondary_nav'] = FALSE;
  }

  // Prepare header.
  $site_fields = array();
  if (!empty($vars['site_name'])) {
    $site_fields[] = $vars['site_name'];
  }
  if (!empty($vars['site_slogan'])) {
    $site_fields[] = $vars['site_slogan'];
  }
  $vars['site_title'] = implode(' ', $site_fields);
  if (!empty($site_fields)) {
    $site_fields[0] = '<span>' . $site_fields[0] . '</span>';
  }
  $vars['site_html'] = implode(' ', $site_fields);

  // Set a variable for the site name title and logo alt attributes text.
  $slogan_text = $vars['site_slogan'];
  $site_name_text = $vars['site_name'];
  $vars['site_name_and_slogan'] = $site_name_text . ' ' . $slogan_text;

  $args = arg();
  if ($args[0] == 'user' && !empty($args[1])) {
    $title = '';
    $req_note = '<span class="form-req"><span>*</span>Required</span>';
    if ($args[1] == 'register') {
      $title = t('Create Account');
    }
    else if ($args[1] == 'login') {
      $title = t('Login to My Account');
    }
    else if ($args[1] == 'password') {
      $title = t('Password Retrieval');
    }
    else if ($args[2] == 'edit') {
      $title = t('Edit account');
    }
    else if ($args[1] == $user->uid) {
      $title = t('My account');
      $req_note = '';
    }
    $vars['title'] = $title;
    $vars['require_note'] = $req_note;
    drupal_set_title($title);
  }

  if ($args[0] == 'node' && $args[1] == 73) {
    unset($vars['tabs']);
  }
}

/**
 * Override or insert variables into the node template.
 */
function acquia_marina_preprocess_node(&$vars) {
  $vars['submitted'] = $vars['date'] . ' — ' . $vars['name'];
}

/**
 * Override or insert variables into the comment template.
 */
function acquia_marina_preprocess_comment(&$vars) {
  $vars['submitted'] = $vars['created'] . ' — ' . $vars['author'];
}

/**
 * Override or insert variables into the block template.
 */
function acquia_marina_preprocess_block(&$vars) {
  $vars['classes_array'][] = 'clearfix';
}

/**
 * Override or insert variables into the page template.
 */
function acquia_marina_process_page(&$vars) {
/*
  
  if (drupal_is_front_page()) {
      $vars['logo'] = file_create_url(variable_get('file_public_path', conf_path() . '/files')) . '/images/qopi_logo.gif';
  }
  else {
      $vars['logo'] = file_create_url(variable_get('file_public_path', conf_path() . '/files')) . '/images/qopi_logo.gif';
  }
*/   
  if (module_exists('color')) {
    _color_page_alter($vars);
  }
}

/**
 * Override or insert variables into the region template.
 */
function acquia_marina_preprocess_region(&$vars) {
  if ($vars['region'] == 'header') {
    $vars['classes_array'][] = 'clearfix';
  }
}

function acquia_marina_preprocess_superfish_build(&$vars) {
  if (user_is_anonymous()) {
    $menu = &$vars['menu'];
    foreach ($menu as $id => $item) {
      $link = $item['link'];
      if ($link['menu_name'] == 'main-menu' && $link['link_path'] == '<front>') {
        unset($menu[$id]);
      }
    }
  }
  else {
      global $user;
      if (nror_patients_is_patient($user)) {
          $menu = &$vars['menu'];
          foreach ($menu as $id => $item) {
              $link = $item['link'];
              if ($link['menu_name'] == 'main-menu' && ($link['link_path'] == 'node/323'
                  || $link['link_path'] == 'node/322' || $link['link_path'] == 'node/320'
                  || $link['link_path'] == 'node/319' || $link['link_path'] == 'node/328')) {
                  unset($menu[$id]);
              }
          }
      }

  }
}