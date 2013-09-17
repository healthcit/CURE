<?php
  global $base_url;
  $link_type = variable_get('print_content_link_type', PRINT_CONTENT_DEFAULT_LINK_TYPE);
  $print_text = variable_get('print_content_link_text', PRINT_CONTENT_DEFAULT_LINK_TEXT);
  $img = $base_url . PRINT_CONTENT_DEFAULT_LINK_IMAGE;
  $custom_img = variable_get('print_content_custom_link_image', '');
  if (!empty($custom_img)) {
    $img = $base_url . '/' . trim($custom_img, '/');
  }
  $custom_classes = variable_get('print_content_custom_css', '');
?>

<script type="text/javascript">
  jQuery(function (){
    jQuery('#print_content_link').show();
  });
</script>


<div id="print_content_link" visibility = "hidden" class="<?php echo $custom_classes; ?>">
  <a id="print-content-print-link" onclick="window.print(); return false;" href = "#">
    <?php if ($link_type == PRINT_CONTENT_LINK_IMAGE || $link_type == PRINT_CONTENT_LINK_BOTH): ?>
      <img src="<?php echo $img; ?>" title="<?php echo $print_text; ?>" alt="<?php echo $print_text; ?>">
    <?php endif; ?>
    <?php if ($link_type == PRINT_CONTENT_LINK_TEXT || $link_type == PRINT_CONTENT_LINK_BOTH) { echo '<span>' . $print_text . '</span>'; } ?>
  </a>
</div>