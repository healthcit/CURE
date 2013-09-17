<?php
/**
 * @file
 * Default theme implementation to display a single Drupal page.
 *
 * Available variables:
 *
 * General utility variables:
 * - $base_path: The base URL path of the Drupal installation. At the very
 *   least, this will always default to /.
 * - $directory: The directory the template is located in, e.g. modules/system
 *   or themes/bartik.
 * - $is_front: TRUE if the current page is the front page.
 * - $logged_in: TRUE if the user is registered and signed in.
 * - $is_admin: TRUE if the user has permission to access administration pages.
 *
 * Site identity:
 * - $front_page: The URL of the front page. Use this instead of $base_path,
 *   when linking to the front page. This includes the language domain or
 *   prefix.
 * - $logo: The path to the logo image, as defined in theme configuration.
 * - $site_name: The name of the site, empty when display has been disabled
 *   in theme settings.
 * - $site_slogan: The slogan of the site, empty when display has been disabled
 *   in theme settings.
 *
 * Navigation:
 * - $main_menu (array): An array containing the Main menu links for the
 *   site, if they have been configured.
 * - $secondary_menu (array): An array containing the Secondary menu links for
 *   the site, if they have been configured.
 * - $breadcrumb: The breadcrumb trail for the current page.
 *
 * Page content (in order of occurrence in the default page.tpl.php):
 * - $title_prefix (array): An array containing additional output populated by
 *   modules, intended to be displayed in front of the main title tag that
 *   appears in the template.
 * - $title: The page title, for use in the actual HTML content.
 * - $title_suffix (array): An array containing additional output populated by
 *   modules, intended to be displayed after the main title tag that appears in
 *   the template.
 * - $messages: HTML for status and error messages. Should be displayed
 *   prominently.
 * - $tabs (array): Tabs linking to any sub-pages beneath the current page
 *   (e.g., the view and edit tabs when displaying a node).
 * - $action_links (array): Actions local to the page, such as 'Add menu' on the
 *   menu administration interface.
 * - $feed_icons: A string of all feed icons for the current page.
 * - $node: The node object, if there is an automatically-loaded node
 *   associated with the page, and the node ID is the second argument
 *   in the page's path (e.g. node/12345 and node/12345/revisions, but not
 *   comment/reply/12345).
 *
 * Regions:
 * - $page['sidebar_first'] = Sidebar First
 * - $page['header'] = Header
 * - $page['header_top'] = Header Top
 * - $page['banner'] = Banner
 * - $page['content_top'] = Content top
 * - $page['content'] = Content
 * - $page['content_bottom'] = Content bottom
 * - $page['home_area_1'] = Home area 1
 * - $page['home_area_2'] = Home area 2
 * - $page['home_area_3'] = Home area 3
 * - $page['home_area_3_b'] = Home area 3 b
 * - $page['footer_left_1'] = Footer left 1
 * - $page['footer_left_2'] = Footer left 2
 * - $page['footer_center'] = Footer center
 * - $page['footer_right'] = Footer right
 * - $page['footer'] = Footer
 * 
 * @see template_preprocess()
 * @see template_preprocess_page()
 * @see template_process()
 */
?>

<div id="page">

<div id="header-top"><!--header-top-->

  <div id="header-top-inside" class="clearfix">
    <?php if ($logged_in): ?>
    <h2 class="user-name">Hello <?php print l(getName($user->uid), 'user/edit'); ?></h2>
    <!-- Displays User Name when Logged in -->
  <?php endif; ?>
    <div id="header-top-inside-left">
      <div id="header-top-inside-left-content"><?php print render($page['header_top']); ?> </div>
    </div>
  </div>
</div><!--/header-top-->

<div id="wrapper">

	<div id="header" class="clearfix">
	
	    <div id="logo">
<?php
            // Prepare header
  $site_fields = array();
  if ($site_name) {
    $site_fields[] = check_plain($site_name);
  }
  if ($site_slogan) {
    $site_fields[] = check_plain($site_slogan);
  }
  $site_title = implode(' ', $site_fields);
  if ($site_fields) {
    $site_fields[0] = '<span>' . $site_fields[0] . '</span>';
  }
  $site_html = implode(' ', $site_fields);

  if ($logo || $site_title) {
    print '<a href="' . check_url($front_page) . '" title="' . $site_title . '">';
    if ($logo) {
      print '<img src="' . check_url($logo) . '" alt="' . $site_title . '" id="logo-image" />';
    }
    print '<div style="display:none">' . $site_html . '</div></a>';
  }
  ?>
      </div> <!--logo-->

  <div id="header-reg">
    <?php print render($page['header']); ?>
  </div>
	    
	     <div id="navigation">	    	
	        <?php print render(menu_tree('main-menu')); ?>
	    </div><!--navigation-->
	
	</div><!--header-->

<div id="main-area" class="clearfix">

<div id="main-area-inside" class="clearfix">

    <div id="main"  class="inside clearfix">  
    	<div id="content-top">
    	<?php print render($page['content_top']);?>
    	</div>
    	<?php if ($title): ?>
     <h1 class="title"><?php print $title; ?></h1>
                            <?php endif; ?>
		<?php print $messages; ?>
        <?php print render($tabs); ?>
        <?php print $tabs2; ?>
        <?php print render($page['content']); ?>
    
    <div id="content-bottom">
    	<?php print render($page['content_bottom']);?>
    	</div>
    </div><!--main-->

    <div id="right" class="clearfix">
    	<?php print render($page['sidebar_first']);?>
    </div><!--right-->
    
</div>

</div><!--main-area-->
</div><!-- /#wrapper-->


<div id="footer-bottom">
    <div id="footer-bottom-inside" class="clearfix">
    	<div style="float:left">
    		<?php print render($page['footer']);?> 
    	</div>
    	<div style="float:right">
	        <?php if (isset($secondary_menu)) : ?>
	          <?php print theme('links', array('links' => $secondary_menu, 'attributes' => array('class' => 'links secondary-links'))); ?>
	        <?php endif; ?>      	
    	</div>
    </div>
</div>    


</div><!-- /page-->

