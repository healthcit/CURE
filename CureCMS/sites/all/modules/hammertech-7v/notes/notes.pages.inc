<?php
function notes_page() {
    global $user;
    $output = theme('notes', array('nids' => notes_get_user_notes($user->uid)));
    return $output;
}

function notes_form_add($form, &$form_state) {

  $form['title'] = array(
		'#type' => 'textfield',
		'#title' => t('Title'),
    '#required' => true,
	);
	$form['body'] = array(
		'#type' => 'textarea',
		'#title' => t('Body'),
	    '#required' => true,
	);
	$form["submit"] = array(
		'#type' => 'submit',
		'#value' => t('Add'),
	);
	$form['cancel'] = array(
		'#type' => 'button',
		'#attributes' => array('onClick' => 'location.replace("'. notes_referer_uri() .'"); return false;'),
		'#value' => t('Cancel'),
  );

	return $form;
}

function notes_form_add_submit($form, &$form_state) {
    global $user;
    $data = array(
    	'uid' => $user->uid,
    	'created' => time(),
    	'title' => $form_state['values']['title'],
    	'body' => $form_state['values']['body']
    );
    if(drupal_write_record('notes', $data) == 1) {
      drupal_set_message(t('Your note has been added.'));
      drupal_goto('notes');
    }
    else {
      drupal_set_message(t('There was a problem adding this note.'));
    }
}

function notes_form_edit($form, &$form_state, $nid) {
  $note = notes_get_note_by_id($nid);
  $form['title'] = array(
	  '#type' => 'textfield',
	  '#title' => t('Title'),
    '#required' => true,
    '#default_value' => $note->title,
	);
	$form['body'] = array(
		'#type' => 'textarea',
		'#title' => t('Body'),
	  '#required' => true,
	  '#default_value' => $note->body,
	);
	$form["nid"] = array(
	  '#type' => 'hidden',
	  '#value' => $note->nid,
	);
	$form["submit"] = array(
		'#type' => 'submit',
		'#value' => t('Save'),
	);
	$form['cancel'] = array(
		'#type' => 'button',
		'#attributes' => array('onClick' => 'location.replace("'. notes_referer_uri() .'"); return false;'),
		'#value' => t('Cancel'),
  );

	return $form;
    
}

function notes_form_edit_submit($form, &$form_state) {
    global $user;
    $data = array(
    	'nid' => $form_state['values']['nid'],
    	'title' => $form_state['values']['title'],
    	'body' => $form_state['values']['body']
    );
    if(drupal_write_record('notes', $data, 'nid') == 2) {
      drupal_set_message(t('Note '.$form_state['values']['title'].' has been updated.'));
      drupal_goto('notes');
    }
    else {
      drupal_set_message(t('There was a problem updating this note.'));
    }
}

function notes_form_delete($form, &$form_state, $nid) {
  $note = notes_get_note_by_id($nid);
  $form = array();
  $form["#prefix"] = t('Are you sure you want to delete note '.$note->title.'?');
  $form["nid"] = array(
    '#type' => 'hidden',
    '#value' => $note->nid,
	);
	$form["title"] = array(
	  '#type' => 'hidden',
	  '#value' => $note->title,
	);
    $form["submit"] = array(
		'#type' => 'submit',
		'#value' => t('Delete'),
	);
	$form['cancel'] = array(
		'#type' => 'button',
		'#attributes' => array('onClick' => 'location.replace("'. notes_referer_uri() .'"); return false;'),
		'#value' => t('Cancel'),
  );
	return $form;
}

function notes_form_delete_submit($form, &$form_state) {
  global $user;
  $delete = db_delete('notes')->condition('nid', $form_state['values']['nid'])->condition('uid', $user->uid);
  if($delete->execute()) {
    drupal_set_message(t('Note '.$form_state['values']['title'].' has been deleted.'));
    drupal_goto('notes');
  }
  else {
    drupal_set_message(t('There was a problem deleting this note.'));
  }
}

function notes_referer_uri() {
  $referer = '';
  if (isset($_SERVER['HTTP_REFERER'])) {
    $referer = $_SERVER['HTTP_REFERER'];
  }
  return $referer;
}