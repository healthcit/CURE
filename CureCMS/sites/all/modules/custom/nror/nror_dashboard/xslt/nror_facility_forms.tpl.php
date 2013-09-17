<?php

/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

global $base_path;

$context = NROR_CONTEXT_FACILITY;

if (empty($facilityName)) {
    throw new Exception("ASCO dashboard module. Practice name was not specified");
}

?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template name="formatDate">
        <xsl:param name="dateTime" />
        <xsl:variable name="date" select="substring-before($dateTime, 'T')" />
        <xsl:value-of select="$date" />
    </xsl:template>

    <xsl:template name="forms">

        <xsl:param name="forms"/>

        <xsl:param name="isEditable"/>

        <xsl:for-each select="$forms">
            <xsl:variable name="formID" select="@id" />

            <xsl:element name="tr">
                <xsl:attribute name="class">form-instance-row</xsl:attribute>
                <xsl:element name="th" >
                    <xsl:attribute name="class">form-title</xsl:attribute>
                    <xsl:value-of select="@name" />
                </xsl:element>
                <xsl:if test="$isEditable = 'true'">
                <xsl:element name="td" >
                    <xsl:element name="a" >
                      <xsl:attribute name="class">add-instance</xsl:attribute>
                      <xsl:choose>
                          <xsl:when test="formInstances[@existingInstances > 0]">
                              <xsl:attribute name="href"><?php echo $base_path?>form/<?php echo $context?>/<xsl:value-of select="$formID"/>/<?php echo $facilityName?>?parentInstanceId=none&amp;instanceId=<xsl:value-of select="formInstances/instance[1]/@instanceId"/></xsl:attribute>
                              Edit
                          </xsl:when>
                          <xsl:otherwise>
                              <xsl:attribute name="href"><?php echo $base_path?>form/<?php echo $context?>/<xsl:value-of select="$formID"/>/<?php echo $facilityName?>?parentInstanceId=none</xsl:attribute>
                              New
                          </xsl:otherwise>
                      </xsl:choose>
                  </xsl:element>
                </xsl:element>
                </xsl:if>

            </xsl:element>

        </xsl:for-each>

    </xsl:template>

    <xsl:template match="/">
        <?php if(!empty($message)):?>
        <xsl:element name="div">
            <xsl:attribute name="class">message</xsl:attribute>
            <?php echo $message?>
        </xsl:element>
        <?php else: ?>
            <xsl:for-each select="modules/module">
                <xsl:element name="div">
                    <xsl:attribute name="class">module</xsl:attribute>
                    <xsl:element name="div">
                      <xsl:attribute name="class">module-block-header</xsl:attribute>
                      <xsl:element name="span">
                          <xsl:attribute name="class">module-title</xsl:attribute>
                          <xsl:value-of select="@name" />
                      </xsl:element>
                      <xsl:if test="@status = 'submitted'">
                        <xsl:element name="span">
                            <xsl:element name="span">
                                Submission date:
                            </xsl:element>
                            <xsl:element name="span">
                                <xsl:call-template name="formatDate">
                                    <xsl:with-param name="dateTime" select="@dateModified" />
                                </xsl:call-template>
                            </xsl:element>
                        </xsl:element>
                      </xsl:if>
                    </xsl:element>
                    <xsl:element name="table">
                        <xsl:attribute name="class">forms-table</xsl:attribute>
                        <xsl:call-template name="forms">
                            <xsl:with-param name="forms" select="form" />
                            <xsl:with-param name="isEditable" select="@isEditable" />
                        </xsl:call-template>
                    </xsl:element>
                </xsl:element>
            </xsl:for-each>
        <?php endif;?>
    </xsl:template>
</xsl:stylesheet>



