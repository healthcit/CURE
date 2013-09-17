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
 * - $page['top_menu'] = Top menu
 * - $page['header'] = Header
 * - $page['superfish_menu'] = Superfish Menu
 * - $page['highlighted'] = Highlighted
 * - $page['banner'] = Banner
 * - $page['preface_one'] = Preface One
 * - $page['preface_two'] = Preface Two
 * - $page['preface_three'] = Preface Three
 * - $page['pre_content'] = Pre Content
 * - $page['content'] = Content
 * - $page['sidebar_first'] = Sidebar First
 * - $page['sidebar_second'] = Sidebar Second
 * - $page['post_content'] = Post Content
 * - $page['bottom_one'] = Bottom One
 * - $page['bottom_two'] = Bottom Two
 * - $page['bottom_three'] = Bottom Three
 * - $page['bottom_four'] = Bottom Four
 * - $page['footer'] = Footer
 *
 * @see template_preprocess()
 * @see template_preprocess_page()
 * @see template_process()
 */
?>


  <div id="page-wrapper">
    <div id="page">

      <div id="top-menu-wrap">
        <div id="top-menu">
          <?php if ($logged_in): ?>
          <!-- Displays User Name when Logged in -->
          <div class="top-user-name">Hello <?php print l(getName($user->uid), 'user/edit'); ?></div>
          <?php endif; ?>
          <?php print render($page['top_menu']); ?>
        </div>
      </div>

      <div id="header"><div class="container section header clearfix">

        <?php if ($logo): ?>
          <a href="<?php print $front_page; ?>" title="<?php print t('Home'); ?>" rel="home" id="logo" class="logo">
            <img src="<?php print $logo; ?>" alt="<?php print t('Home'); ?>" />
          </a>
        <?php endif; ?>

        <?php if ($site_name || $site_slogan): ?>
          <div id="name-and-slogan">
            <?php if ($site_name): ?>
              <?php if ($title): ?>
                <div id="site-name"><strong>
                  <a href="<?php print $front_page; ?>" title="<?php print t('Home'); ?>" rel="home"><span><?php print $site_name; ?></span></a>
                </strong></div>
              <?php else: /* Use h1 when the content title is empty */ ?>
                <h1 id="site-name">
                  <a href="<?php print $front_page; ?>" title="<?php print t('Home'); ?>" rel="home"><span><?php print $site_name; ?></span></a>
                </h1>
              <?php endif; ?>
            <?php endif; ?>

            <?php if ($site_slogan): ?>
              <div id="site-slogan"><?php print $site_slogan; ?></div>
            <?php endif; ?>
          </div> <!-- /#name-and-slogan -->
        <?php endif; ?>
          <?php print render($page['header']); ?>
          <?php if($main_menu || $page['superfish_menu'] ): ?>
        <?php if($page['superfish_menu']) {
          print render($page['superfish_menu']);
        } else {
          print theme('links', array('links' => $main_menu, 'attributes' => array('id' => 'main-menu', 'class' => array('links', 'clearfix'))));
        }
        ?>
      <?php endif; ?>
      </div><!-- /.section .header -->
      
    </div> <!-- /#header -->



       <div id="main-wrapper">
        <?php print $messages; ?>

          <?php if ($page['preface_one'] || $page['preface_two'] || $page['preface_three']): ?>
            <div id="preface" class="clearfix">
              <div class="container preface clearfix">
                 <?php if ($page['preface_one']): ?>
                <div class="section preface-one<?php print' preface-'.  $preface; ?>">
                  <div class="gutter">
                    <?php print render($page['preface_one']); ?>
                  </div>
                </div>
                <?php endif; ?>
                <?php if ($page['preface_two']): ?>
                <div class="section preface-two<?php print' preface-'.  $preface; ?>">
                  <div class="gutter">
                    <?php print render($page['preface_two']); ?>
                  </div>
                </div>
                <?php endif; ?>
                <?php if ($page['preface_three']): ?>
                <div class="section preface-three<?php print' preface-'. $preface; ?>">
                  <div class="gutter">
                    <?php print render($page['preface_three']); ?>
                  </div>
                </div>
                <?php endif; ?>
              </div>
            </div>
          <?php endif; ?>

        <div id="content-wrap" class="container content-wrap clearfix">
          <div id="main" class="main clearfix">

            <div id="content" class="column clear-fix">
            <?php if ($page['sidebar_first']): ?>
              <div id="first-sidebar" class="column sidebar first-sidebar">
                <div class="section">
                  <div class="gutter">
                    <?php print render($page['sidebar_first']); ?>
                  </div>
                </div><!-- /.section -->
              </div><!-- /#sidebar-first -->
            <?php endif; ?>

              <div class="page-content content-column section">
                <div class="gutter">
                  <?php if ($page['highlighted']): ?><div id="highlighted"><?php print render($page['highlighted']); ?></div><?php endif; ?>
                  <a id="main-content"></a>
                  <?php print render($title_prefix); ?>
                  <?php if ($title): ?><h1 class="title" id="page-title"><?php print $title; ?></h1><?php endif; ?>
                    <?php print render($title_suffix); ?>
                  <?php print render($page['help']); ?>
                  <?php print render($page['content']); ?>
                  <?php print $feed_icons; ?>
                </div>
              </div><!-- /.section .content .gutter -->
            </div> <!-- /#content -->

          </div><!-- /#main -->

          <?php if ($page['sidebar_second']): ?>
            <div id="second-sidebar" class="column sidebar second-sidebar">
              <div class="section">
                <div class="gutter">
                  <?php print render($page['sidebar_second']); ?>
                </div><!-- /.gutter -->
              </div><!-- /.section -->
            </div> <!-- /#sidebar-second -->
          <?php endif; ?>

        </div> <!-- /#main-wrapper -->

      </div><!-- /#content-main -->

    </div><!-- /#page -->

    <div id="footer">
      <?php if ($page['bottom_one'] || $page['bottom_two'] || $page['bottom_three'] || $page['bottom_four']): ?>
      <div id="bottom" class="container clearfix">
        <?php if ($page['bottom_one']): ?>
          <div class="region bottom bottom-one<?php print ' bottom-' .  $bottom; ?>">
            <div class="gutter">
              <?php print render($page['bottom_one']); ?>
            </div>
          </div>
        <?php endif; ?>
        <?php if ($page['bottom_two']): ?>
          <div class="region bottom bottom-two<?php print ' bottom-' .  $bottom; ?>">
            <div class="gutter">
              <?php print render($page['bottom_two']); ?>
            </div>
          </div>
        <?php endif; ?>
        <?php if ($page['bottom_three']): ?>
          <div class="region bottom bottom-three<?php print ' bottom-' . $bottom; ?>">
            <div class="gutter">
              <?php print render($page['bottom_three']); ?>
            </div>
          </div>
        <?php endif; ?>
        <?php if ($page['bottom_four']): ?>
          <div class="region bottom bottom-four<?php print ' bottom-'. $bottom; ?>">
            <div class="gutter">
              <?php print render($page['bottom_four']); ?>
            </div>
          </div>
        <?php endif; ?>
      </div>
      <?php endif; ?>
      <div class="container section footer">
        <?php print render($page['footer']); ?>
      </div><!-- /.section -->
    </div> <!-- /#footer -->

  </div> <!-- /#page-wrapper -->
