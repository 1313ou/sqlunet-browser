<?xml version="1.0" encoding="UTF-8"?>

<!-- RELATIONS 2 HTML - (C) 2020 Author: Bernard Bou -->
<xsl:transform version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:import href='relation2html.xsl'/>
  <xsl:output method="html" indent="yes" encoding="UTF-8" doctype-system="about:legacy-compat"/>

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
        <DIV class="header">
          <IMG class="menu" src="images/menu.png" alt="menu" onclick="javascript:menu();"/>
          <SPAN class="title">Relations</SPAN>
        </DIV>
        <TABLE class="full-width">
          <TR class="full-width">
            <TD class='totop'>
              <DIV id='toc' class="toc">
                <TABLE>
                  <xsl:call-template name="build_toc">
                    <xsl:with-param name="title"/>
                  </xsl:call-template>
                </TABLE>
              </DIV>
            </TD>
            <TD class="totop full-width">
              <DIV class="main full-width">
                <TABLE class="full-width">
                  <xsl:apply-templates select=".//relation"/>
                </TABLE>
              </DIV>
            </TD>
          </TR>
        </TABLE>
      </BODY>
    </HTML>
  </xsl:template>

  <xsl:template name="build_toc">
    <xsl:param name="title"/>
    <xsl:for-each select=".//relation">
      <TR class='selector'>
        <!-- @formatter:off --><xsl:attribute name="onclick">javascript:select('<xsl:value-of select="./@id"/>');</xsl:attribute><!-- @formatter:on -->
        <TD>
          <IMG>
            <xsl:attribute name="src">
              <xsl:copy-of select="$imagedir"/>
              <xsl:value-of select="./img/@src"/>
            </xsl:attribute>
            <xsl:attribute name="alt">
              <xsl:value-of select="./@name"/>
            </xsl:attribute>
          </IMG>
        </TD>
        <TD>
          <xsl:value-of select="./@name"/>
        </TD>
      </TR>
    </xsl:for-each>
  </xsl:template>

</xsl:transform>

