<?php 


/**
 * Add javascript files for front-page jquery slideshow.
 */
if (drupal_is_front_page()) {
  drupal_add_js(drupal_get_path('theme', 'bluemasters') . '/js/bluemasters.js');
}

/**
 * GT
 * Returns the terms of a specific vocabulary.
 * Paremeters: Node id, Vocabulary id
 *
 */
function skodaxanthifc_print_only_terms_of($node, $vid) {
	$terms = taxonomy_node_get_terms_by_vocabulary($node, $vid);
	if ($terms) {
		$links = array();
		$output = '';
		foreach ($terms as $term) {
			//$links[] = l($term->name, taxonomy_term_path($term), array('rel' => 'tag', 'title' => strip_tags($term->description)));
			$output .= $term->name;
		}
	//$output .= implode(', ', $links);
	$output .= ' ';
	}
	$output .= '';
	return $output;
}

/**
  * Theme override for search form.
  */
function skodaxanthifc_search_theme_form($form) {

  unset($form['search_theme_form']['#title']);
  //$form['submit']['#value'] = '';
  //$form['search_theme_form']['#value'] = 'Search.';
  // $form['search_theme_form']['#attributes'] = array('onblur' => "if (this.value == '') {this.value = 'Search.';}", 'onfocus' => "if (this.value == 'Search.') {this.value = '';}" );

  $output = drupal_render($form);
  return $output;
}

// Gets the User First Name via the UID for display purposes

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

//Removes white spaces and capitalization from variables for cleaner CSS

function classify($element) {
  $class = str_replace(' ', '-', $element); //Strips empty spaces and replaces with dashes
  $class = strtolower($class); //makes letter lowercase for ease of CSS Styling

  return $class;
}

/**
 * Override or insert PHPTemplate variables into the templates.
 */
function bluemasters_preprocess_page(&$vars) {   
  global $user;
  
  if ($user->uid == '0') {
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
  $body_classes = array($vars['body_classes']);
  if (!$vars['is_front']) {
    // Add unique classes for each page and website section
    $path = drupal_get_path_alias($_GET['q']);
    list($section,) = explode('/', $path, 2);
    $body_classes[] = bluemasters_id_safe('page-' . $path);
    $body_classes[] = bluemasters_id_safe('section-' . $section);
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
  $vars['body_classes'] = implode(' ', $body_classes); // Concatenate with spaces

  if ($secondary = menu_secondary_local_tasks()) {
    $output = '<span class="clear"></span>';
    $output .= "<ul class=\"tabs secondary\">\n". render($secondary) ."</ul>\n";
    $vars['tabs2'] = $output;
  }

  // Hook into color.module
  if (module_exists('color')) {
    _color_page_alter($vars);
  }
  
}

function bluemasters_id_safe($string) {
  if (is_numeric($string{0})) {
    // If the first character is numeric, add 'n' in front
    $string = 'n' . $string;
  }
  return strtolower(preg_replace('/[^a-zA-Z0-9-]+/', '-', $string));
}

/**
 * Return a themed breadcrumb trail.
 *
 * @param $breadcrumb
 *   An array containing the breadcrumb links.
 * @return a string containing the breadcrumb output.
 */
function bluemasters_breadcrumb($variables) {
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
function bluemasters_preprocess_maintenance_page(&$vars) {
  // While markup for normal pages is split into page.tpl.php and html.tpl.php,
  // the markup for the maintenance page is all in the single
  // maintenance-page.tpl.php template. So, to have what's done in
  // bluemasters_preprocess_html() also happen on the maintenance page, it has to be
  // called here.
  bluemasters_preprocess_html($vars);
}

/**
 * Override or insert variables into the html template.
 */
function bluemasters_preprocess_html(&$vars) {
  // Toggle fixed or fluid width.
  if (theme_get_setting('bluemasters_width') == 'fluid') {
    $vars['classes_array'][] = 'fluid-width';
  }

  // Add conditional CSS for IE6.
  drupal_add_css(path_to_theme() . '/fix-ie.css', array('group' => CSS_THEME, 'browsers' => array('IE' => 'lt IE 7', '!IE' => FALSE), 'preprocess' => FALSE));
}

/**
 * Override or insert variables into the html template.
 */
function bluemasters_process_html(&$vars) {    
  // Hook into color.module
  if (module_exists('color')) {
    _color_html_alter($vars);
  }
}



/**
 * Override or insert variables into the node template.
 */
function bluemasters_preprocess_node(&$vars) {
  $vars['submitted'] = $vars['date'] . ' — ' . $vars['name'];
}

/**
 * Override or insert variables into the comment template.
 */
function bluemasters_preprocess_comment(&$vars) {
  $vars['submitted'] = $vars['created'] . ' — ' . $vars['author'];
}

/**
 * Override or insert variables into the block template.
 */
function bluemasters_preprocess_block(&$vars) {
  $vars['title_attributes_array']['class'][] = 'title';
  $vars['classes_array'][] = 'clearfix';
}



