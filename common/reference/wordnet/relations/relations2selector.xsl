<?xml version="1.0" encoding="UTF-8"?><!--
  ~ Copyright (c) 2023. Bernard Bou
  -->

<!-- RELATION 2 HTML SELECTORS - (C) 2020 Author: Bernard Bou -->
<xsl:transform version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes" encoding="UTF-8" doctype-system="about:legacy-compat"/>

  <xsl:variable name="imagedir" select='"images/"'/>

  <xsl:template match="/">
    <HTML lang="en">
      <HEAD>
        <TITLE>
          <xsl:value-of select="'Relations'"/>
        </TITLE>
        <LINK rel="stylesheet" type="text/css" href="relations.css"/>
        <SCRIPT src="relations.js"/>
      </HEAD>
      <BODY>
        <TABLE>
          <xsl:apply-templates select=".//relation"/>
        </TABLE>
      </BODY>
    </HTML>
  </xsl:template>

  <xsl:template match="relation">
    <TR class='selector'>
      <!-- @formatter:off --><xsl:attribute name="onclick">javascript:select('<xsl:value-of select="./@id"/>');</xsl:attribute><!-- @formatter:on -->
      <TD class='totop;'>
        <IMG class="relationimage">
          <xsl:attribute name="src">
            <xsl:copy-of select="$imagedir"/>
            <xsl:value-of select="./img/@src"/>
          </xsl:attribute>
          <xsl:attribute name="alt">
            <xsl:value-of select="./@name"/>
          </xsl:attribute>
        </IMG>
        <SPAN class="selectorname">
          <xsl:value-of select="./@name"/>
        </SPAN>
      </TD>
    </TR>
  </xsl:template>

</xsl:transform>

