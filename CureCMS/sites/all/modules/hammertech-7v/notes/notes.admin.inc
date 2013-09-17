<?php 

function notes_settings_form($form, &$form_state) {
    $form = array();
    
    $form['titles'] = array(
        '#type' => 'fieldset',
        '#title' => t('Page Titles'),
        '#collapsible' => TRUE,
        '#collapsed' => FALSE,
    );
    $form['titles']['notes_title'] = array(
		'#type' => 'textfield',
		'#title' => t('Notes Page Title'),
		'#description' => t('The title of the notes page.'),
		'#size' => 60,
		'#maxlength' => 128,
		'#default_value' => variable_get('notes_title', 'My Note'),
	);
    $form['titles']['notes_add_title'] = array(
		'#type' => 'textfield',
		'#title' => t('Add Note Page Title'),
		'#description' => t('The title of the add notes page.'),
		'#size' => 60,
		'#maxlength' => 128,
		'#default_value' => variable_get('notes_add_title', 'Add Note'),
	);
    $form['titles']['notes_edit_title'] = array(
		'#type' => 'textfield',
		'#title' => t('Edit Note Page Title'),
		'#description' => t('The title of the edit notes page.'),
		'#size' => 60,
		'#maxlength' => 128,
		'#default_value' => variable_get('notes_edit_title', 'Edit Note'),
	);
	$form['titles']['notes_delete_title'] = array(
		'#type' => 'textfield',
		'#title' => t('Delete Note Page Title'),
		'#description' => t('The title of the delete notes page.'),
		'#size' => 60,
		'#maxlength' => 128,
		'#default_value' => variable_get('notes_delete_title', 'Delete Note'),
	);
    
    $form['notes'] = array(
        '#type' => 'fieldset',
        '#title' => t('Note Page Text Settings'),
        '#collapsible' => TRUE,
        '#collapsed' => FALSE,
    );
    $form['notes']['notes_page_description'] = array(
		'#type' => 'textarea',
		'#title' => t('Notes Page Description Text'),
        '#default_value' => variable_get('notes_page_description', ''),
	);
    $form['notes']['notes_add_link'] = array(
		'#type' => 'textfield',
		'#title' => t('Add Link'),
		'#description' => t('The text of the add note link.'),
		'#size' => 60,
		'#maxlength' => 128,
		'#default_value' => variable_get('notes_add_link', 'Add'),
	);
	$form['notes']['notes_edit_link'] = array(
		'#type' => 'textfield',
		'#title' => t('Edit Link'),
		'#description' => t('The text of the edit note link.'),
		'#size' => 60,
		'#maxlength' => 128,
		'#default_value' => variable_get('notes_edit_link', 'Edit'),
	);
	$form['notes']['notes_delete_link'] = array(
		'#type' => 'textfield',
		'#title' => t('Delete Link'),
		'#description' => t('The text of the delete note link.'),
		'#size' => 60,
		'#maxlength' => 128,
		'#default_value' => variable_get('notes_delete_link', 'Delete'),
	);
    
    $form['menu'] = array(
        '#type' => 'fieldset',
        '#title' => t('Menu Settings'),
        '#collapsible' => TRUE,
        '#collapsed' => FALSE,
    );
    $form['menu']['notes_menu_enabled'] = array(
        '#type' => 'checkbox',
        '#title' => t('Menu Enabled'),
        '#description' => t('Enable the notes in a menu by checking the box. <strong>You will still need to enable this menu item in the menu selected!</strong>'),
        '#default_value' => variable_get('notes_menu_enabled', 0),
    );
    $form['menu']['notes_menu_title'] = array(
		'#type' => 'textfield',
		'#title' => t('Menu Title'),
		'#description' => t('The title of the notes page menu item.'),
		'#size' => 60,
		'#maxlength' => 128,
		'#default_value' => variable_get('notes_menu_title', 'My Notes'),
	);
	$form['menu']['notes_menu'] = array(
	    '#type' => 'select',
	    '#title' => t('Menu'),
	    '#description' => t('Choose a menu to display the notes menu item.'),
	    '#options' => menu_get_menus(),
		'#default_value' => variable_get('notes_menu', 'primary-links'),
	);
	$form['menu']['notes_menu_weight'] = array(
        '#type' => 'weight',
        '#title' => t('Menu Weight'),
        '#description' => t('Optional. In the menu, the heavier items will sink and the lighter items will be positioned nearer the top.'),
        '#delta' => 50,
        '#default_value' => variable_get('notes_menu_weight', 0),
    );
    
    $form['#validate'] = array('notes_settings_form_validate');
    $form['#submit'] = array('notes_settings_form_submit');
    
    return system_settings_form($form);
}

function notes_settings_form_validate($form, &$form_state) {
    
}

function notes_settings_form_submit($form, &$form_state) {
    menu_rebuild();
    menu_cache_clear_all();
}


