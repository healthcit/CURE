<?php
/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */
?>

<?php  global $base_path; $mpath = $base_path . drupal_get_path('module', 'cacure_mgmt'); $context = cure_contexts_get_active_context();?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="/">
    <link href="<?php echo $mpath?>/css/management-ajax-default.css" media="all" rel="stylesheet" type="text/css"/>
    <xsl:element name="div">
      <xsl:attribute name="class">health-element</xsl:attribute>    
      <xsl:element name="h2">
        Available submitted questionnaires
      </xsl:element>
      <?php if(isset($arg['message'])): ?>
        <xsl:element name="div">
          <xsl:attribute name="class">message</xsl:attribute>
          <?php echo $arg['message']?>
        </xsl:element>
      <?php else:?>
        <xsl:element name="table">
          <xsl:attribute name="class">submitted-modules</xsl:attribute>
          <xsl:element name="thead">
            <xsl:element name="tr">
              <xsl:element name="td">
                Name
              </xsl:element>
              <xsl:element name="td">
                Actions
              </xsl:element>
            </xsl:element> 
          </xsl:element>  
          <xsl:element name="tbody">
            <xsl:for-each select="modules/module[@status='completed']">
              <xsl:element name="tr">
                <xsl:element name="td">
                  <xsl:attribute name="class">module-name</xsl:attribute>
                  <xsl:value-of select="@name"/>
                </xsl:element>
                <xsl:element name="td" >
                  <xsl:element name="a" >
                    <xsl:attribute name="href">/management/reopen-module/<xsl:value-of select="@id"/></xsl:attribute>
                    <xsl:attribute name="title">Reopen</xsl:attribute>
                    Reopen questionnaire
                  </xsl:element>
                </xsl:element>
              </xsl:element> 
            </xsl:for-each>
          </xsl:element>
        </xsl:element>  
      <?php endif?>
    </xsl:element>  
  </xsl:template>
</xsl:stylesheet>

