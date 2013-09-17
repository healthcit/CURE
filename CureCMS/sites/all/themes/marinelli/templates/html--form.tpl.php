<?php
  global $base_path;
 
?>
<?php if (isset($_SESSION['xml_arg']['head']) || isset($_SESSION['xml_arg']['body'])): ?>
<!DOCTYPE ROOT_XML_ELEMENT
  [<!ENTITY copy  "&#169;"> <!ENTITY nbsp  "&#160;">]
>
<?php endif; ?>

<?php print '<?xml-stylesheet href="' . $base_path . drupal_get_path('module', 'cacure_x').'/xsltforms/xsltforms.xsl" type="text/xsl"?>'."\n"; ?>
<?php echo '<?css-conversion no?>' ?>
<?php echo '<?xsltforms-options debug="no"?>' ?>

<html xmlns="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xform="http://www.w3.org/2002/xforms">
	<head ><!--start head section-->
	  <?php print $head; ?>
	  <title><?php print $head_title; ?></title>
	  <?php print $styles; ?>
	  <?php print $scripts; ?>
    <?php
    if (isset($_SESSION['xml_arg'])) {
      $xml = $_SESSION['xml_arg'];
      if (isset($xml['head'])) {
        echo $xml['head'];
      }
    }
    ?>
	</head>
	<!--[if lt IE 7 ]> <body class="marinelli ie6 <?php print $classes; ?>"> <![endif]-->
    <!--[if IE 7 ]>    <body class="marinelli ie7 <?php print $classes; ?>"> <![endif]-->
    <!--[if IE 8 ]>    <body class="marinelli ie8 <?php print $classes; ?>"> <![endif]-->
    <!--[if IE 9 ]>    <body class="marinelli ie9 <?php print $classes; ?>"> <![endif]-->
    <!--[if gt IE 9]>  <body class="marinelli <?php print $classes; ?>"> <![endif]-->
    <!--[if !IE]><!--> <body class="marinelli <?php print $classes; ?>"> <!--<![endif]-->
	  <div id="skip-link">
	    <a href="#content" title="<?php print t('Jump to the main content of this page'); ?>" class="element-invisible"><?php print t('Jump to Content'); ?></a>
	  </div>
	  <?php print $page_top; ?>
	  <?php print $page; ?>
	  <?php print $page_bottom; ?>
	</body><!--end body-->
</html>