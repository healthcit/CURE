<?php
/**
 *
 * Theme implementation to display a list of notes.
 *
 * Available variables:
 * - $nids: an array of note nids.
 *
 */
?>

<div class="notes">
	<p class="notes-description"><?php print variable_get('notes_page_description', '') ?></p>
	<a class="notes-add-note-link" href="<?php print url('notes/add'); ?>"><?php print variable_get('notes_add_link', 'Add Note'); ?></a>
    <?php if(is_array($nids)): ?>
    	<?php foreach ($nids as $nid): ?>
    	  <div class="note note-<?php print $nid->nid; ?>">
	        <h2 class="note-title"><?php print $nid->title; ?></h2>
	        <p class="note-created">Added on <?php print date('F j, o', $nid->created); ?></p>
	        <p class="note-body"><?php print $nid->body; ?></p>
	        <div class="note-links">
		        <a class="note-edit" href="<?php print url('notes/edit/'. $nid->nid); ?>"><?php print variable_get('notes_edit_link', 'Edit'); ?></a>
		        <a class="note-delete" href="<?php print url('notes/delete/'. $nid->nid); ?>"><?php print variable_get('notes_delete_link', 'Delete'); ?></a>
	        </div>
        </div>
    	<?php endforeach;?>
    <?php endif; ?>
</div>