<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/owner">
		<owner>
		<xsl:attribute name="userid" select="@userid"/>
			<xsl:apply-templates select="//form"/>
		</owner>
	</xsl:template>
	
    <xsl:template match="form">
		<xsl:variable name="numberInstances" select="count(instance)"/>
		<xsl:variable name="instances" as="element(instance)+">
		   <xsl:perform-sort select="instance">
		   <xsl:sort select="@index" order = "ascending"/>
		   </xsl:perform-sort>
		</xsl:variable>
        
        <xsl:variable name="indexes" as="attribute(index)+">
		   <xsl:perform-sort select="instance/@index">
		   <xsl:sort /> <!-- select="@index" order = "ascending"/> -->
		   </xsl:perform-sort>
		 </xsl:variable>        
         
		 <xsl:apply-templates select="instance">
			 <xsl:sort select="@index" order =  "ascending"/>
			 <xsl:with-param name="indexes" select="$indexes"/>
		</xsl:apply-templates>

    </xsl:template>
  
    <xsl:template match="instance" >
    <xsl:param name="indexes"/>
    <xsl:variable name="ind" select="index-of($indexes, @index)"/>
     <!-- index of <xsl:value-of select="@index"/> is :   <xsl:value-of select="$ind"/>! -->
     
		<instance>
            <xsl:attribute name="index" select="@index"/>
			<xsl:attribute name="parentInstanceIndex" select="ancestor::instance[1]/@index"/>
			<xsl:attribute name="parentFormId" select="ancestor::form[2]/@id"/>
			<xsl:attribute name="formId" select="../@id"/>
			<xsl:attribute name="processOrder" select="count(ancestor::instance) + 1"/>  
			<xsl:attribute name="nodePath" select="string-join(ancestor-or-self::instance/@index,',')"   />
     
     <xsl:apply-templates select="*[not(form) and not(instance)]"/>
        </instance>
    </xsl:template>
    
    
   <xsl:template match="node()|@*">
		<xsl:copy><xsl:apply-templates select="node()|@*"/></xsl:copy>
	</xsl:template>
</xsl:stylesheet>