<?php

/**
 * Refer A Friend Admin Settings Form 
 */
function refer_a_friend_settings_form($form, &$form_state) {
  $form = array();
    
  $form['menu_settings'] = array(
    '#type' => 'fieldset',
    '#title' => t('Menu Settings'),
    '#collapsible' => TRUE,
    '#collapsed' => TRUE,
  );
  $form['menu_settings']['refer_a_friend_menu_enabled'] = array(
    '#type' => 'checkbox',
    '#title' => t('Menu Enabled'),
    '#description' => t('Enable Refer A Friend in a menu by checking this box. <strong>You will still need to enable this menu item in the menu selected!</strong>'),
    '#default_value' => variable_get('refer_a_friend_menu_enabled', 0),
  );
  $form['menu_settings']['refer_a_friend_menu_title'] = array(
		'#type' => 'textfield',
		'#title' => t('Menu Title'),
		'#description' => t('The title of the refer a friend page menu item.'),
		'#size' => 60,
		'#maxlength' => 128,
		'#default_value' => variable_get('refer_a_friend_menu_title', 'Refer A Friend'),
	);
	$form['menu_settings']['refer_a_friend_menu'] = array(
    '#type' => 'select',
    '#title' => t('Menu'),
    '#description' => t('Choose a menu to display the refer a friend menu item.'),
    '#options' => menu_get_menus(),
		'#default_value' => variable_get('refer_a_friend_menu', 'primary-links'),
	);
	$form['menu_settings']['refer_a_friend_menu_weight'] = array(
    '#type' => 'weight',
    '#title' => t('Menu Weight'),
    '#description' => t('Optional. In the menu, the heavier items will sink and the lighter items will be positioned nearer the top.'),
    '#delta' => 50,
    '#default_value' => variable_get('refer_a_friend_menu_weight', 0),
  );
    
  $form['form_settings'] = array(
    '#type' => 'fieldset',
    '#title' => t('Page and Form Settings'),
    '#collapsible' => TRUE,
    '#collapsed' => FALSE,
  );

  $form['form_settings']['refer_a_friend_title'] = array(
		'#type' => 'textfield',
		'#title' => t('Page Title'),
		'#description' => t('The title of the refer a friend page.'),
		'#size' => 60,
		'#maxlength' => 128,
		'#default_value' => variable_get('refer_a_friend_title', 'Refer A Friend'),
	);
	$form['form_settings']['refer_a_friend_description'] = array(
		'#type' => 'textarea',
		'#title' => t('Description'),
    '#description' => t('The description of the Refer a Friend page, displayed under the title.'),
    '#default_value' => variable_get('refer_a_friend_description', ''),
	);
  $form['form_settings']['refer_a_friend_name'] = array(
		'#type' => 'textfield',
		'#title' => t('Name Label'),
		'#description' => t('The label for the name form input.'),
		'#size' => 60,
    '#maxlength' => 128,
		'#default_value' => variable_get('refer_a_friend_name', 'Your Name'),
	);
	$form['form_settings']['refer_a_friend_email'] = array(
		'#type' => 'textfield',
		'#title' => t('Your Email Label'),
		'#description' => t('The label for the your email form input.'),
		'#size' => 60,
    '#maxlength' => 128,
		'#default_value' => variable_get('refer_a_friend_email', 'Your Email'),
	);
	$form['form_settings']['refer_a_friend_emails'] = array(
		'#type' => 'textfield',
		'#title' => t('Friend\'s Emails Label'),
		'#description' => t('The label for the name form input.'),
		'#size' => 60,
    '#maxlength' => 128,
		'#default_value' => variable_get('refer_a_friend_emails', 'Friend\'s Emails'),
	);
	$form['form_settings']['refer_a_friend_emails_help'] = array(
		'#type' => 'textfield',
		'#title' => t('Friend\'s Emails Description'),
		'#description' => t('The text description for the name form input.'),
		'#size' => 60,
		'#default_value' => variable_get('refer_a_friend_emails_help', 'Please enter a comma separated list of email addresses.'),
	);
	$form['form_settings']['refer_a_friend_message'] = array(
		'#type' => 'textfield',
		'#title' => t('Message Label'),
		'#description' => t('The label for the message textarea.'),
		'#size' => 60,
        '#maxlength' => 128,
		'#default_value' => variable_get('refer_a_friend_message', 'Message'),
	);
	$form['form_settings']['refer_a_friend_default_message'] = array(
		'#type' => 'textarea',
		'#title' => t('Default Message'),
    '#description' => t('The default message to be sent in the referral email.'),
    '#default_value' => variable_get('refer_a_friend_default_message', ''),
	);
	$form['form_settings']['refer_a_friend_submit'] = array(
		'#type' => 'textfield',
		'#title' => t('Submit Button Text'),
		'#description' => t('The text of the submit button.'),
		'#size' => 60,
    '#maxlength' => 128,
		'#default_value' => variable_get('refer_a_friend_submit', 'Submit'),
	);
    $form['form_settings']['refer_a_friend_success_message'] = array(
		'#type' => 'textarea',
		'#title' => t('Success Message'),
    '#description' => t('The message shown after a user has submitted the form successfully.'),
    '#default_value' => variable_get('refer_a_friend_success_message', ''),
	);
	$form['form_settings']['refer_a_friend_success_color'] = array(
		'#type' => 'textfield',
		'#title' => t('Color of Success'),
		'#description' => t('The hexidecimal color code for the success text. You <strong>do not</strong> need to precede it with a # sign.'),
		'#size' => 10,
    '#maxlength' => 6,
		'#default_value' => variable_get('refer_a_friend_success_color', 'FF0000'),
	);
	
	$form['email'] = array(
    '#type' => 'fieldset',
    '#title' => t('Email Settings'),
    '#collapsible' => TRUE,
    '#collapsed' => FALSE,
  );
	$form['email']['refer_a_friend_from_txt'] = array(
		'#type' => 'textfield',
		'#title' => t('From Email'),
		'#description' => t('The from email in the referral emails. You may use the following tokens: !email( email of the user sending referral ), !site_email( email address of site ).'),
		'#size' => 120,
    '#maxlength' => 80,
		'#default_value' => variable_get('refer_a_friend_from_txt', '!email'),
	);
    $form['email']['refer_a_friend_subject_txt'] = array(
		'#type' => 'textfield',
		'#title' => t('Email Subject'),
		'#description' => t('The subject of the referral emails. You may use the following tokens: !name( name of the user sending referral ), !site( name of site ).'),
		'#size' => 120,
    '#maxlength' => 120,
		'#default_value' => variable_get('refer_a_friend_subject_txt', 'Message from !name on !site'),
	);
	$form['email']['refer_a_friend_email_txt'] = array(
		'#type' => 'textarea',
		'#title' => t('Email Body'),
		'#description' => t('The subject of the referral emails. You may use the following tokens: !name( name of the user sending referral ), !email ( email of user sending referral ), !message ( message entered in form ), !site( name of site ), !link( link to the site ).'),
    '#default_value' => variable_get('refer_a_friend_email_txt', ''),
	);
	$form['email']['refer_a_friend_token_site'] = array(
		'#type' => 'textfield',
		'#title' => t('Site Token'),
		'#description' => t('The value of the !site token. By default, it is the drupal site name.'),
		'#size' => 30,
    '#maxlength' => 60,
		'#default_value' => variable_get('refer_a_friend_token_site', variable_get('site_name', '')),
	);
    $form['email']['refer_a_friend_token_link'] = array(
		'#type' => 'textfield',
		'#title' => t('Link Token'),
		'#description' => t('The value of the !link token. By default, it is root path to this drupal installation.'),
    '#size' => 30,
    '#maxlength' => 60,
		'#default_value' => variable_get('refer_a_friend_token_link', 'http://' . $_SERVER["SERVER_NAME"] . base_path()),
	);
	
    return system_settings_form($form);
}