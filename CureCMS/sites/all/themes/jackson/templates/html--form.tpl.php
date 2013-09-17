<?php
  global $base_path;

  if(drupal_is_front_page()) {
    $class = "home"; 
  } 
  else {
    $class = $classes . " " . classify($title);
  }  
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

<head>
  <meta http-equiv="X-UA-Compatible" content="IE=8" />
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
<body id="xform-page" class="<?php print $classes; ?>" <?php print $attributes;?>>
  <div id="skip-link">
    <a href="#main-content" class="element-invisible element-focusable"><?php print t('Skip to main content'); ?></a>
  </div>
  <?php print $page_top; ?>
  <?php print $page; ?>
  <?php print $page_bottom; ?>
</body>
</html>
