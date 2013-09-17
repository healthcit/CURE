<?php
/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

/**
 * SimpleXmlParser class implementation file
 * @file   SimpleXmlParser.php
 * @author Kirill Kolesnik <kkolesnik@qualium-systems.com>
 */

/**
 * 
 * @class   SimpleXmlParser
 * @version 
 * @author  Kirill Kolesnik <kkolesnik@qualium-systems.com>
 */
class SimpleXmlParser 
{
    public static function toArray($xml)
    {
      if (empty($xml)) {
        return null;
      }
      $simpleXml = new SimpleXMLElement($xml, LIBXML_PARSEHUGE);
      return self::parseSimpleXml($simpleXml);
    }

    protected static function parseSimpleXml($xml)
    {
        if ($xml instanceof SimpleXMLElement) {
            $attributes = $xml->attributes();
            foreach($attributes as $k=>$v) {
                if ($v) {
                    $a[$k] = (string) $v;
                }
            }
            $x = $xml;
            $xml = get_object_vars($xml);
        }
        if (is_array($xml)) {
            if (count($xml) == 0) {
                return (string) $x; // for CDATA
            }
            foreach($xml as $key=>$value) {
                $r[$key] = self::parseSimpleXml($value);
            }
            if (isset($a)) {
                $r['@attributes'] = $a;    // Attributes
            }
            return $r;
        }

        return (string) $xml;
    }            
}

?>
