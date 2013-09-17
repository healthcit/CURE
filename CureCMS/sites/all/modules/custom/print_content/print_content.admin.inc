<?php

function print_content_config_form($form, &$form_state) {
  $form = array();

  $form['print_content_link_type'] = array(
    '#type' => 'radios',
    '#title' => t('Link type'),
    '#options' => array(
      PRINT_CONTENT_LINK_IMAGE => ucfirst(t(PRINT_CONTENT_LINK_IMAGE)),
      PRINT_CONTENT_LINK_TEXT => ucfirst(t(PRINT_CONTENT_LINK_TEXT)),
      PRINT_CONTENT_LINK_BOTH => ucfirst(t(PRINT_CONTENT_LINK_BOTH))
    ),
    '#default_value' => variable_get('print_content_link_type', PRINT_CONTENT_DEFAULT_LINK_TYPE),
    '#description' => t('Select the type of link to be displayed.'),
  );

  $form['print_content_link_text'] = array(
    '#title' => t('Link title'),
    '#type' => 'textfield',
    '#default_value' => variable_get('print_content_link_text', PRINT_CONTENT_DEFAULT_LINK_TEXT),
  );

  $form['print_content_custom_link_image'] = array(
    '#title' => t('Custom link image'),
    '#type' => 'textfield',
    '#default_value' => variable_get('print_content_custom_link_image', ''),
    '#description' => t('Path to custom link image. Should be absolute path begining from server root.')
  );

  $form['print_content_custom_css'] = array(
    '#title' => t('Custom css classes'),
    '#type' => 'textfield',
    '#default_value' => variable_get('print_content_custom_css', ''),
    '#description' => t('Enter a space-separated classes. Classes will be applied to main link container. ')
  );

  return system_settings_form($form);
}

