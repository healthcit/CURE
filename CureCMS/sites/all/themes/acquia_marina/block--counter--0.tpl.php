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

<div id="block-<?php print $block->module .'-'. classify($block->title); ?>" class="clear-block block <?php echo $block->region ?> <?php echo classify($block->title) ?>">

<?php
	$start = strpos($content, '<div>') + 5;
	$end = strpos($content, '</div>');
	$length = $end - $start;
	$content = substr($content, $start, $length);
  while(strlen($content) < 7){
    $content = "0" . $content;
  }
?>
<?php if (!empty($block->subject)): ?>
  <h2><?php print $block->subject ?></h2>
<?php endif;?>

  <div class="content">
  	<span class="date">As of <? echo date('n\/j\/Y'); ?></span>
  	<div id="counter"><?php print $content ?></div>
    <p class="rteright">
    <a href="join-now" title="Join Now">Sign up now</a>
    <a href="join-now" title="Join Now">&gt;</a>
    </p>
    </div><!-- end content -->

</div>