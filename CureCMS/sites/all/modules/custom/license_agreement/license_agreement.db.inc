<?php

/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

function license_agreement_get_user_info($userId) {
    return db_select(LICENSE_AGREEMENT_TABLE, 'la')
        ->fields('la', array())
        ->condition('user_id', $userId)
        ->execute()
        ->fetch();
}

function license_agreement_user_has_agreed($userId) {
    $userRecord = license_agreement_get_user_info($userId);
    if (!empty($userRecord) && $userRecord->has_agreed == 1) {
        return true;
    }
    return false;
}

function license_agreement_create_user_record($userId) {
    $record = array('user_id' => $userId, 'has_agreed' => 0);
    return drupal_write_record(LICENSE_AGREEMENT_TABLE, $record);
}

function license_agreement_set_user_has_agreed($userId) {
    $record = license_agreement_get_user_info($userId);
    $primaryKeys = array('user_id');
    if (empty($record)) {
        $record = array('user_id' => $userId, 'has_agreed' => 1);
        $primaryKeys = array();
    }
    return drupal_write_record(LICENSE_AGREEMENT_TABLE, $record, $primaryKeys);
}

function license_agreement_delete_user_record($userId) {
    db_delete(LICENSE_AGREEMENT_TABLE)
        ->condition('user_id', $userId)
        ->execute();
}

function license_agreement_delete_all() {
    db_delete(LICENSE_AGREEMENT_TABLE)->execute();
}

function _license_agreement_get_module_block() {
    return db_select("block", 'b')
        ->fields('b', array())
        ->condition('b.delta', LICENSE_AGREEMENT_BLOCK_DELTA)
        ->execute()
        ->fetch();
}


function license_agreement_get_all_countries() {
    return db_select(CURE_COUNTRIES_TABLE, 'cc')
        ->fields('cc', array())
        ->execute()
        ->fetchAll();
}

function license_agreement_bind_country_to_user($user_id, $country_id) {
    $existingCountry = license_agreement_get_user_country($user_id);
    if ($existingCountry) {
        return db_update(CURE_COUNTRIES_USERS_TABLE)->fields(array('country_id' => $country_id))
            ->condition('user_id', $user_id)
            ->execute();
    } else {
        $record = array('user_id' => $user_id, 'country_id' => $country_id);
        return drupal_write_record(CURE_COUNTRIES_USERS_TABLE, $record);
    }
}

function license_agreement_delete_country_user_relation($userId) {
    db_delete(CURE_COUNTRIES_USERS_TABLE)
        ->condition('user_id', $userId)
        ->execute();
}

function license_agreement_get_user_country($userId) {
    $query = db_select(CURE_COUNTRIES_USERS_TABLE, 'cu');
    $query->join(CURE_COUNTRIES_TABLE, 'cc', 'cu.country_id=cc.id');
    $query->fields('cc', array('long_name', 'id'));
    $query->condition('cu.user_id', $userId);
    $result = $query->execute()->fetch();

    return $result;
}

function license_agreement_get_license_agreement_settings($countryId, $roleId) {
    $query = db_select(LICENSE_AGREEMENT_SETTINGS_COUNTRY_ROLE, 'cr');
    $query->join(LICENSE_AGREEMENT_SETTINGS, 's', 's.id=cr.setting_id');
    $query->fields('s', array());
    $query->fields('cr', array('country_id', 'role_id'));
    $query->condition('cr.country_id', $countryId);
    $query->condition('cr.role_id', $roleId);
    $result = $query->execute()->fetch();

    return $result;
}

function license_agreement_save_settings($data) {
    $record = array(
        'id' => null,
        'url' => $data[LICENSE_AGREEMENT_PAGE_VAR],
        'checkbox_label' => $data[LICENSE_AGREEMENT_LABEL_VAR],
        'button_label' => $data[LICENSE_AGREEMENT_SUBMIT_LABEL_VAR],
        'message' => $data[LICENSE_AGREEMENT_ERROR_MESSAGE_VAR],
    );

    $setting = license_agreement_get_setting_by_url($data[LICENSE_AGREEMENT_PAGE_VAR]);

    if (!$setting) {
        drupal_write_record (LICENSE_AGREEMENT_SETTINGS, $record);
    } else {
        $record['id'] = $setting->id;
        drupal_write_record (LICENSE_AGREEMENT_SETTINGS, $record, array('id'));
    }

    return $record['id'];
}

function license_agreement_get_setting_by_url($url) {
    return db_select(LICENSE_AGREEMENT_SETTINGS, 's')
        ->fields('s', array())
        ->condition('url', $url)
        ->execute()
        ->fetch();
}

function license_agreement_save_setting_country_role($settingId, $countryId, $roleId) {
    $record = array(
        'id' => null,
        'setting_id' => $settingId,
        'country_id' => $countryId,
        'role_id' => $roleId,
    );

    $relation = license_agreement_get_relation_by_country_role($countryId, $roleId);

    if (!$relation) {
        drupal_write_record (LICENSE_AGREEMENT_SETTINGS_COUNTRY_ROLE, $record);
    } else {
        $record['id'] = $relation->id;
        drupal_write_record(LICENSE_AGREEMENT_SETTINGS_COUNTRY_ROLE, $record, array('id'));
    }

    return $record['id'];
}

function license_agreement_get_relation_by_country_role($country, $role) {
    return db_select(LICENSE_AGREEMENT_SETTINGS_COUNTRY_ROLE, 'cr')
        ->fields('cr', array())
        ->condition('country_id', $country)
        ->condition('role_id', $role)
        ->execute()
        ->fetch();
}