<?php
/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

global $user;
global $base_path;
$mpath = $base_path . drupal_get_path('module', 'cacure_mgmt');

$context = cure_contexts_get_active_context();
$path = arg();

$formId = $path[2];
$instanceId = '';
$parentInstanceId = 'none';
if (!empty($_GET['parentInstanceId'])) {
    $parentInstanceId = $_GET['parentInstanceId'];
}
if (!empty($_GET['instanceId'])) {
    $instanceId = $_GET['instanceId'];
}
$groupName = '';
if (!empty($path[3])) {
    $groupName = $path[3];
} else {
    $groupName = $user->name;
}
$groupName = urlencode($groupName);
?>
<!--<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:ajx="http://www.ajaxforms.net/1906/ajx" xmlns:xforms="http://www.w3.org/1902/xforms" xmlns:ev="http://www.w3.org/1901/xml-events" xmlns:xsi="http://www.w3.org/1901/XMLSchema-instance" xmlns:xsd="http://www.w3.org/1901/XMLSchema" version="1.0">-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template name="formInstances">
        <xsl:param name="formID" />
        <xsl:param name="formName" />
        <xsl:param name="instances" />
        <xsl:param name="childForms" />

        <xsl:for-each select="$instances">
            <xsl:variable name="instanceId" select="@instanceId"/>
            <xsl:variable name="childFormsRenderContent">
                <xsl:if test="$childForms != ''">
                    <xsl:for-each select="$childForms">
                        <xsl:call-template name="moduleForm">
                            <xsl:with-param name="form" select="current()"/>
                            <xsl:with-param name="parentInstanceId" select="$instanceId"/>
                        </xsl:call-template>
                    </xsl:for-each>
                </xsl:if>
            </xsl:variable>

            <xsl:if test="not(@visible) or @visible = 'true'">
                <xsl:element name="div">
                    <xsl:attribute name="class">instance-group</xsl:attribute>
                    <xsl:choose>
                        <xsl:when test="$childFormsRenderContent != ''">
                            <xsl:element name="a" >
                                <xsl:attribute name="id">link_<xsl:value-of select="$formID"/>_<xsl:value-of select="@instanceId"/></xsl:attribute>
                                <xsl:attribute name="href">#<xsl:value-of select="$formID"/>_<xsl:value-of select="@instanceId"/></xsl:attribute>

                                <xsl:choose>
                                    <xsl:when test="$formID = '<?php print $formId ?>' and @instanceId = '<?php echo $instanceId;?>'">
                                        <xsl:attribute name="class">instance-tab-expand-active</xsl:attribute>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:attribute name="class">instance-tab-expand</xsl:attribute>
                                    </xsl:otherwise>
                                </xsl:choose>

                            </xsl:element>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:element name="span" >
                                <xsl:attribute name="class">instance-tab-empty</xsl:attribute>
                            </xsl:element>
                        </xsl:otherwise>
                    </xsl:choose>

                    <xsl:element name="a" >

                        <xsl:choose>
                            <xsl:when test="@parentInstanceId">
                                <xsl:attribute name="href"><?php echo $base_path?>form/<?php echo $context?>/<xsl:value-of select="$formID"/>/<?php echo $groupName?>?parentInstanceId=<xsl:value-of select="@parentInstanceId"/>&amp;instanceId=<xsl:value-of select="@instanceId"/></xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="href"><?php echo $base_path?>form/<?php echo $context?>/<xsl:value-of select="$formID"/>/<?php echo $groupName?>?parentInstanceId=none&amp;instanceId=<xsl:value-of select="@instanceId"/></xsl:attribute>
                            </xsl:otherwise>
                        </xsl:choose>

                        <xsl:choose>
                            <xsl:when test="$formID = '<?php print $formId ?>' and @instanceId = '<?php echo $instanceId;?>'">
                                <xsl:attribute name="class">instance-link-active <xsl:value-of select="@status"/></xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="class">instance-link <xsl:value-of select="@status"/></xsl:attribute>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:element name="span">
                            <xsl:value-of select="$formName" />
                        </xsl:element>

                    </xsl:element>

                    <xsl:if test="$childForms != ''">
                        <xsl:element name="div">
                            <xsl:attribute name="class">instance-tab</xsl:attribute>
                            <xsl:attribute name="id"><xsl:value-of select="$formID"/>_<xsl:value-of select="@instanceId"/></xsl:attribute>
                            <xsl:for-each select="$childForms">
                                <xsl:call-template name="moduleForm">
                                    <xsl:with-param name="form" select="current()"/>
                                    <xsl:with-param name="parentInstanceId" select="$instanceId"/>
                                </xsl:call-template>
                            </xsl:for-each>
                        </xsl:element>
                    </xsl:if>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="formatDate">
        <xsl:param name="dateTime" />
        <xsl:variable name="date" select="substring-before($dateTime, 'T')" />
        <xsl:value-of select="$date" />
    </xsl:template>


    <xsl:template name="moduleForm">

        <xsl:param name="form"/>

        <xsl:param name="parentInstanceId" select="'none'"/>


        <xsl:variable name="formID" select="$form/@id" />
        <xsl:variable name="formName" select="$form/@name" />
        <xsl:variable name="childForms" select="$form/form" />
        <xsl:variable name="formInstances" select="$form/formInstances"/>
        <xsl:variable name="maxInstances" select="$formInstances/@maxInstances" />
        <xsl:variable name="renderable" select="($parentInstanceId = 'none') or not($formInstances/availableParentInstances) or ($formInstances/instance[@parentInstanceId = $parentInstanceId]) or ($formInstances/availableParentInstances/parentInstance[@instanceId=$parentInstanceId])" />
        <xsl:variable name="existingInstances" select="$formInstances/@existingInstances" />
        <xsl:variable name="canHaveNewInstances" select="(($parentInstanceId = 'none') and ($maxInstances > $existingInstances)) or ($formInstances/availableParentInstances/parentInstance[@instanceId=$parentInstanceId] and ($maxInstances > count($formInstances/instance[@parentInstanceId = $parentInstanceId]))) or (($parentInstanceId != 'none') and not($formInstances/availableParentInstances) and ($maxInstances > count($formInstances/instance[@parentInstanceId = $parentInstanceId])))" />

        <xsl:if test="$renderable = 'true'">
            <xsl:if test="($existingInstances > 0) or ($canHaveNewInstances = 'true')">
                <xsl:element name="p" >
                    <xsl:choose>
                        <xsl:when test="($formID = '<?php print $formId; ?>') and ($parentInstanceId = '<?php print $parentInstanceId; ?>')">
                            <xsl:attribute name="class">form-title-active</xsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="class">form-title</xsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:value-of select="$formName"/>

                    <xsl:if test="$canHaveNewInstances">
                        <xsl:element name="a" >
                            <xsl:attribute name="title">Add new instance</xsl:attribute>
                            <xsl:attribute name="class">add-instance-link</xsl:attribute>
                            <xsl:attribute name="href"><?php echo $base_path?>form/<?php echo $context?>/<xsl:value-of select="$formID"/>/<?php echo $groupName?>?parentInstanceId=<xsl:value-of select="$parentInstanceId"/></xsl:attribute>
                        </xsl:element>
                    </xsl:if>
                </xsl:element>

                <xsl:if test="$existingInstances > 0">
                    <xsl:choose>
                        <xsl:when test="$parentInstanceId != 'none'">
                            <xsl:call-template name="formInstances">
                                <xsl:with-param name="formID" select="$formID"/>
                                <xsl:with-param name="formName" select="$formName"/>
                                <xsl:with-param name="instances" select="formInstances/instance[@parentInstanceId = $parentInstanceId]" />
                                <xsl:with-param name="childForms" select="$childForms"/>
                            </xsl:call-template>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:call-template name="formInstances">
                                <xsl:with-param name="formID" select="$formID"/>
                                <xsl:with-param name="formName" select="$formName"/>
                                <xsl:with-param name="instances" select="formInstances/instance"/>
                                <xsl:with-param name="childForms" select="$childForms"/>
                            </xsl:call-template>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:if>
            </xsl:if>
        </xsl:if>
    </xsl:template>

    <xsl:template name="moduleFlatForm">
        <xsl:param name="form"/>

        <xsl:variable name="formID" select="$form/@id" />
        <xsl:variable name="formName" select="$form/@name" />
        <xsl:variable name="formInstances" select="$form/formInstances"/>
        <xsl:variable name="existingInstances" select="$formInstances/@existingInstances" />

        <xsl:if test="not(@visible) or @visible = 'true'">
            <xsl:element name="div">
                <xsl:element name="a" >
                    <xsl:choose>
                        <xsl:when test="$existingInstances > 0">
                            <xsl:attribute name="class">instance-link <xsl:value-of select="$formInstances/instance/@status"/></xsl:attribute>
                            <xsl:variable name="instanceId" select="$formInstances/instance/@instanceId"/>
                            <xsl:attribute name="href"><?php echo $base_path?>form/<?php echo $context?>/<xsl:value-of select="$formID"/>/<?php echo $groupName?>?instanceId=<xsl:value-of select="$instanceId"/></xsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="class">instance-link <xsl:value-of select="@status"/></xsl:attribute>
                            <xsl:attribute name="href"><?php echo $base_path?>form/<?php echo $context?>/<xsl:value-of select="$formID"/>/<?php echo $groupName?></xsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:element name="span">
                        <xsl:value-of select="$formName" />
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template match="/">
        <?php if(isset($arg['message'])):?>
            <xsl:element name="div">
                <xsl:attribute name="class">message</xsl:attribute>
                <?php echo $arg['message']?>
            </xsl:element>
        <?php else:?>
            <xsl:element name="div">
                <xsl:attribute name="class">health-element</xsl:attribute>
                <xsl:element name="div">
                    <xsl:attribute name="class">module-element-list</xsl:attribute>

                    <xsl:for-each select="modules/module">
                        <xsl:variable name="isFlat" select="@isFlat" />

                        <xsl:element name="div">
                            <xsl:attribute name="class">module-element</xsl:attribute>
                            <xsl:element name="div">
                                <xsl:attribute name="class">form-element-list</xsl:attribute>
                                <xsl:choose>
                                    <xsl:when test="$isFlat = 'false'">
                                        <xsl:for-each select="form">
                                            <xsl:call-template name="moduleForm">
                                                <xsl:with-param name="form" select="current()"/>
                                            </xsl:call-template>
                                        </xsl:for-each>
                                    </xsl:when>
                                    <xsl:otherwise>
                                         <xsl:for-each select="form">
                                            <xsl:call-template name="moduleFlatForm">
                                                <xsl:with-param name="form" select="current()"/>
                                            </xsl:call-template>
                                        </xsl:for-each>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:element>
                        </xsl:element>
                    </xsl:for-each>

                </xsl:element>
            </xsl:element>
        <?php endif;?>
    </xsl:template>
</xsl:stylesheet>

