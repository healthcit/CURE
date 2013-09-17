<?php
/*
* Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
* Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
* Proprietary and confidential
*/
?>

<div class="news-and-notifications">
    <h2>News and Notifications</h2>
    <?php if (!empty($practiceStatusInfo)) : ?>
        <?php if ($practiceStatusInfo->status == 'Approved - Pending payment' || $practiceStatusInfo->status == 'Eligible') : ?>
        <div class='qcp-notification-content'>
            <?php if ($practiceStatusInfo->status == 'Approved - Pending payment') : ?>
                Your certification application has been approved. Click
                <a href="<?php print $practiceStatusInfo->link; ?>">here</a>
                to proceed.
            <?php elseif ($practiceStatusInfo->status == 'Eligible') : ?>
                Congratulations! Your practice has met the required performance criteria to apply the certification.
                Click
                <a href="<?php print $practiceStatusInfo->link; ?>">here</a>
                to proceed.
            <?php endif; ?>
        </div>
        <?php endif; ?>
    <?php endif; ?>

    <?php if (!empty($newsNodes)) : ?>
        <?php foreach ($newsNodes as $newsNode) : ?>
            <div class="qcp-notification-content">
                    <?php print $newsNode->content; ?>
            </div>
        <?php endforeach; ?>
    <?php endif; ?>
</div>