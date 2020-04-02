<?xml version="1.0" encoding="UTF-8"?>
<!-- RELATION 2 HTML SELECTORS - (C) 2020 Author: Bernard Bou -->
<xsl:transform version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8" doctype-system="about:legacy-compat" />

	<xsl:variable name="imagedir" select='"./images/"' />

	<xsl:template match="/">
		<HTML>
			<HEAD>
				<TITLE>
					<xsl:value-of select="'Relations'" />
				</TITLE>
				<LINK rel="stylesheet" type="text/css" href="style.css" />
				<SCRIPT type="text/javascript">
					<xsl:text disable-output-escaping="yes"><![CDATA[
function select(tag)
{
		var iframe=parent.document.getElementById('main');
		var maindoc=iframe.contentWindow.document;
		var all=maindoc.getElementsByName('link');
		for(i=0;i<all.length;i++)
		{
			var e=all[i];
			e.style.visibility="collapse";
		}
		var e=maindoc.getElementById(tag);
		e.style.visibility="visible";
}
]]>
</xsl:text>
				</SCRIPT>
			</HEAD>
			<BODY>
				<TABLE>
					<xsl:apply-templates select=".//link" />
				</TABLE>
			</BODY>
		</HTML>
	</xsl:template>

	<xsl:template match="link">
		<TR class='selector'>
			<xsl:attribute name="onclick">javascript:select('<xsl:value-of select="./@id" />');</xsl:attribute>
			<TD style='vertical-align: top;'>
				<IMG class="linkimage">
					<xsl:attribute name="src"><xsl:copy-of select="$imagedir" /><xsl:value-of select="./img/@src" /></xsl:attribute>
					<xsl:attribute name="alt"><xsl:value-of select="./@name" /></xsl:attribute>
				</IMG>
				<SPAN class="selectorname">
					<xsl:value-of select="./@name" />
				</SPAN>
			</TD>
		</TR>
	</xsl:template>

</xsl:transform>

