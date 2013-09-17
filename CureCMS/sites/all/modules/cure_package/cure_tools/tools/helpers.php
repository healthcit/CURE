<?php
/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

/**
 * Removes not selected checkboxes
 * @param array $checkboxes
 */
function helpers_filter_checkboxes(&$checkboxes) {
  foreach ($checkboxes as $key => $value) {
    if($value == 0) {
      unset($checkboxes[$key]);
    }
  }
}

/**
 * Sorts two-level array by specified parameter
 * @param array $array Array to be sorted
 * @param string $param Param to be sorted by
 * @param bool $asc Whether to sort ascending or descending
 * @return array Sorted array
 */
function helpers_sort_two_level_array(&$array, $param, $asc = true) {
  $sorter = array();
  $result = array();
  reset($array);
  foreach ($array as $key => $course) {
    $sorter[$key] = $course[$param];
  }
  if ($asc == true) {
    asort($sorter);
  }
  else {
    arsort($sorter);
  }
  foreach ($sorter as $key => $value) {
    $result[$key] = $array[$key];
  }
  return $result;
}
