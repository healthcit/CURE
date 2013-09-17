<?php
/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */
?>

<?php if ($isAjax != true) : ?>
<ul class="dashboard-tabs">
    <li<?php $active == NROR_DASHBOARD_FACILITIES_TAB ? print ' class="active"' : '';?>>
        <a href="<?php print url(NROR_DASHBOARD_FACILITIES_TAB_URL); ?>" title="Practices" class="dashboard-tab">
            Practices
        </a>
    </li>
    <li<?php $active == NROR_DASHBOARD_RC_TAB ? print ' class="active"' : '';?>>
        <a href="<?php print url(NROR_DASHBOARD_RC_TAB_URL); ?>" class="dashboard-tab" title="QOPI Administrator">
            QOPI Administrator
        </a>
    </li>
</ul>
  <div class="dashboard-frame">
<?php endif; ?>

<?php print $content; ?>

<?php if ($isAjax != true) : ?>
  </div>
<?php endif; ?>

