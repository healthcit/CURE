<?php
/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */
?>

<?php if ($isAjax != true) : ?>
  <div class="dashboard-header">
    <div class="dashboard-title">
      <?php print $facilityName; ?>
    </div>
    <span style="font-size: 13px;"> (<?php print l('edit', 'facility'); ?>)</span>
  </div>

  <ul class="dashboard-tabs">
    <li<?php $active == NROR_DASHBOARD_PATIENTS_TAB ? print ' class="active"' : ''; ?>>
      <a href="<?php print url(NROR_DASHBOARD_PATIENTS_TAB_URL); ?>" class="dashboard-tab" title="Patients">
        Patients
      </a>
    </li>
    <li<?php $active == NROR_DASHBOARD_PHYSICIANS_TAB ? print ' class="active"' : ''; ?>>
      <a href="<?php print url(NROR_DASHBOARD_PHYSICIANS_TAB_URL); ?>" class="dashboard-tab" title="Providers">
        Providers
      </a>
    </li>
    <li <?php $active == NROR_DASHBOARD_FACILITY_FORMS_TAB ? print ' class="active"' : ''; ?>>
      <a href="<?php print url(NROR_DASHBOARD_FACILITY_FORMS_TAB_URL); ?>" class="dashboard-tab" title="Practice forms">
        Practice
      </a>
    </li>
  </ul>
<fieldset class="dashboard-frame">
  <?php endif; ?>

  <?php print $content; ?>

  <?php if ($isAjax != true) : ?>
</fieldset>
<?php endif; ?>

