<?php

/**
 * @file
 * Fusion theme implementation to display a block.
 *
 * Available variables:
 * - $block->subject: Block title.
 * - $content: Block content.
 * - $block->module: Module that generated the block.
 * - $block->delta: An ID for the block, unique within each module.
 * - $block->region: The block region embedding the current block.
 * - $classes: String of classes that can be used to style contextually through
 *   CSS. It can be manipulated through the variable $classes_array from
 *   preprocess functions. The default values can be one or more of the following:
 *   - block: The current template type, i.e., "theming hook".
 *   - block-[module]: The module generating the block. For example, the user module
 *     is responsible for handling the default user navigation block. In that case
 *     the class would be "block-user".
 * - $title_prefix (array): An array containing additional output populated by
 *   modules, intended to be displayed in front of the main title tag that
 *   appears in the template.
 * - $title_suffix (array): An array containing additional output populated by
 *   modules, intended to be displayed after the main title tag that appears in
 *   the template.
 *
 * Helper variables:
 * - $classes_array: Array of html class attribute values. It is flattened
 *   into a string within the variable $classes.
 * - $block_zebra: Outputs 'odd' and 'even' dependent on each block region.
 * - $zebra: Same output as $block_zebra but independent of any block region.
 * - $block_id: Counter dependent on each block region.
 * - $id: Same output as $block_id but independent of any block region.
 * - $is_front: Flags true when presented in the front page.
 * - $logged_in: Flags true when the current user is a logged-in member.
 * - $is_admin: Flags true when the current user is an administrator.
 *
 * @see template_preprocess()
 * @see template_preprocess_block()
 * @see template_process()
 */
?>

<? $class = str_replace(' ', '-', $block->subject); //Strips empty spaces and replaces with dashes ?>
<? $class = strtolower($class); //makes letter lowercase for ease of CSS Styling ?>
<div id="block-<?php print $block->module .'-'. $block->delta; ?>" class="clear-block block <?php echo $block->region ?> <?php echo $class ?>">

  <h4>My Notes - Latest Entry</h4>
  		<p class="line">Date Added</p>
		<?php
      $notes = notes_get_user_notes($user->uid);
		  if($notes[0]) {
        $noter = $notes[0];
      }
 		?>
 	<div class="content">
 		<? if($noter){ ?>
 			<? $note_body = $noter->body;
				$note_title = $noter->title;
				$note_date = $noter->created;
				$href = "/notes/edit/".$noter->nid;
			 ?>
  			<p class="note"><a href="<? echo $href ?>" title="Edit Note" ><? echo $note_title ?></a> - <? echo $note_body ?></p>
            <p class="created"><?  print date(n."/".j."/".Y, $note_date); ?></p>
        <? }else{ ?>
        <p class="note">You do not have any notes.  <a href="/notes/add" title"Add a Notes">Click Here</a> to add one.</p>
        
        <? } ?>
  
  	</div><!-- end content -->
    
</div><!-- end block -->
