<?php

/**
 * Withdraw Admin Form
 */
function withdraw_settings_form($form_state) {
  $form = array();
    
  $form['form'] = array(
    '#type' => 'fieldset',
    '#title' => t('Withdraw Form Settings'),
    '#collapsible' => TRUE,
    '#collapsed' => FALSE,
  );

  $form['form']['withdraw_title'] = array(
		'#type' => 'textfield',
		'#title' => t('Title'),
		'#description' => t('The title of the withdraw form.'),
		'#size' => 60,
		'#maxlength' => 128,
		'#default_value' => variable_get('withdraw_title', 'Withdraw from Study'),
	);

	$form['form']['withdraw_prefix'] = array(
		'#type' => 'textarea',
		'#title' => t('Form Prefix'),
		'#description' => t('The text shown above the form'),
		'#default_value' => variable_get('withdraw_prefix', ''),
	);

	$form['form']['withdraw_suffix'] = array(
		'#type' => 'textarea',
		'#title' => t('Form Suffix'),
		'#description' => t('The text shown below the form'),
		'#default_value' => variable_get('withdraw_suffix', ''),
	);
	
	$form['form']['withdraw_block'] = array(
		'#type' => 'textarea',
		'#title' => t('Block Text'),
		'#description' => t('The text shown for the block radio input.'),
		'#default_value' => variable_get('withdraw_block', 'Block'),
	);

	$form['form']['withdraw_block_suffix'] = array(
		'#type' => 'textarea',
		'#title' => t('Block Suffix'),
		'#description' => t('The text shown below block radio input.'),
		'#default_value' => variable_get('withdraw_block_suffix', ''),
	);

	$form['form']['withdraw_block_dynamic'] = array(
		'#type' => 'textarea',
		'#title' => t('Block Dynamic Message'),
		'#description' => t('The text shown below block radio suffix when the radio is selected.'),
		'#default_value' => variable_get('withdraw_block_dynamic', ''),
	);

	$form['form']['withdraw_block_success'] = array(
		'#type' => 'textarea',
		'#title' => t('Block Success Message'),
		'#description' => t('The text shown after the from has been submitted.'),
		'#default_value' => variable_get('withdraw_block_success', ''),
	);
	
	$form['form']['withdraw_remove'] = array(
		'#type' => 'textarea',
		'#title' => t('Remove Text'),
		'#description' => t('The text shown for the remove radio input.'),
		'#default_value' => variable_get('withdraw_remove', 'Remove'),
	);

	$form['form']['withdraw_remove_suffix'] = array(
		'#type' => 'textarea',
		'#title' => t('Remove Suffix'),
		'#description' => t('The text shown below remove radio input.'),
		'#default_value' => variable_get('withdraw_remove_suffix', ''),
	);

	$form['form']['withdraw_remove_dynamic'] = array(
		'#type' => 'textarea',
		'#title' => t('Remove Dynamic Message'),
		'#description' => t('The text shown below remove radio suffix when the radio is selected.'),
		'#default_value' => variable_get('withdraw_remove_dynamic', ''),
	);

	$form['form']['withdraw_remove_success'] = array(
		'#type' => 'textarea',
		'#title' => t('Remove Success Message'),
		'#description' => t('The text shown after the form has been submitted.'),
		'#default_value' => variable_get('withdraw_remove_success', ''),
	);

	$form['form']['withdraw_submit'] = array(
		'#type' => 'textfield',
		'#title' => t('Submit'),
		'#description' => t('The text of the submit button.'),
		'#size' => 20,
    '#maxlength' => 128,
		'#default_value' => variable_get('withdraw_submit', 'Submit'),
	);

	$form['form']['withdraw_cancel'] = array(
		'#type' => 'textfield',
		'#title' => t('Cancel'),
		'#description' => t('The text of the cancel button.'),
		'#size' => 20,
    '#maxlength' => 128,
		'#default_value' => variable_get('withdraw_cancel', 'Cancel'),
	);

	$form['form']['withdraw_destination'] = array(
		'#type' => 'textfield',
		'#title' => t('Success Destination'),
		'#description' => t('The destination when clicking the success submit button. Enter <front> to go to the homepage.'),
		'#size' => 20,
    '#maxlength' => 128,
		'#default_value' => variable_get('withdraw_destination', '<front>'),
	);

	$form['form']['withdraw_cancel_destination'] = array(
		'#type' => 'textfield',
		'#title' => t('Cancel Destination'),
		'#description' => t('The destination when clicking the cancel button. Enter <front> to go to the homepage.'),
		'#size' => 20,
    '#maxlength' => 128,
		'#default_value' => variable_get('withdraw_cancel_destination', '<front>'),
	);

	$form['form']['withdraw_success_submit'] = array(
		'#type' => 'textfield',
		'#title' => t('Success Submit'),
		'#description' => t('The text of the success submit button.'),
		'#size' => 20,
    '#maxlength' => 128,
		'#default_value' => variable_get('withdraw_success_submit', 'Submit'),
	);

	$form['form']['withdraw_submit_error'] = array(
		'#type' => 'textarea',
		'#title' => t('Submit Error'),
		'#description' => t('An error message to show when a user submitted the form without selecting an option.'),
		'#default_value' => variable_get('withdraw_submit_error', 'You must select an option.'),
	);

	$form['form']['withdraw_reason_title'] = array(
		'#type' => 'textarea',
		'#title' => t('Reason Title'),
		'#description' => t('The title of the Reason for Withdrawing textarea.'),
		'#default_value' => variable_get('withdraw_reason_title', 'Why are you withdrawing?'),
	);
	
	$form['email'] = array(
    '#type' => 'fieldset',
    '#title' => t('Alert Email Settings'),
    '#collapsible' => TRUE,
    '#collapsed' => FALSE,
  );

	$form['email']['withdraw_email'] = array(
		'#type' => 'textfield',
		'#title' => t('To Address'),
		'#description' => t('The email that will recieve a message when a user does withdraw. Leave this blank to disable emails.'),
		'#size' => 120,
    '#maxlength' => 80,
		'#default_value' => variable_get('withdraw_email', ''),
	);

    $form['email']['withdraw_email_subject'] = array(
		'#type' => 'textfield',
		'#title' => t('Email Subject'),
		'#description' => t('The subject of the emails. You may use the following tokens: !name ( the name of the user who cancelled ), !action ( the action taken, either deleted or blocked ), !site ( the name of the site )'),
		'#size' => 120,
    '#maxlength' => 120,
		'#default_value' => variable_get('withdraw_email_subject', '!name !action account on !site'),
	);

	$form['email']['withdraw_email_body'] = array(
		'#type' => 'textarea',
		'#title' => t('Email Body'),
		'#description' => t('The body of the emails. You may use the following tokens: !time ( time of withdraw ), !action ( the action taken, either deleted or blocked ), !reason ( the reason for taking action ), !site ( site name ), !uid ( uid of user ), !name( name of the user ), !email ( email of user ), !street ( street address of user ), !city ( city of user ), !state ( state or province of user ), !zip ( zip of user ), !country ( country of user ), !phone ( phone of user ).'),
    '#default_value' => variable_get('withdraw_email_body', ''),
	);
    
	$form['menu'] = array(
    '#type' => 'fieldset',
    '#title' => t('Menu Settings'),
    '#collapsible' => TRUE,
    '#collapsed' => FALSE,
  );

  $form['menu']['withdraw_menu_enabled'] = array(
    '#type' => 'checkbox',
    '#title' => t('Menu Enabled'),
    '#description' => t('Enable withdraw in a menu by checking the box. <strong>You will still need to enable this menu item in the menu selected!</strong>'),
    '#default_value' => variable_get('withdraw_menu_enabled', 0),
  );

  $form['menu']['withdraw_menu_title'] = array(
		'#type' => 'textfield',
		'#title' => t('Menu Title'),
		'#description' => t('The title of the withdraw page menu item.'),
		'#size' => 60,
		'#maxlength' => 128,
		'#default_value' => variable_get('withdraw_menu_title', 'Withdraw from Study'),
	);

	$form['menu']['withdraw_menu'] = array(
    '#type' => 'select',
    '#title' => t('Menu'),
    '#description' => t('Choose a menu to display the withdraw menu item.'),
    '#options' => menu_get_menus(),
		'#default_value' => variable_get('withdraw_menu', 'primary-links'),
	);

	$form['menu']['withdraw_menu_weight'] = array(
    '#type' => 'weight',
    '#title' => t('Menu Weight'),
    '#description' => t('Optional. In the menu, the heavier items will sink and the lighter items will be positioned nearer the top.'),
    '#delta' => 50,
    '#default_value' => variable_get('withdraw_menu_weight', 0),
  );
	
  return system_settings_form($form);
}