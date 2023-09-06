<?xml version="1.0" encoding="UTF-8"?><!--
  ~ Copyright (c) 2023. Bernard Bou
  -->

<!-- RELATIONS 2 HTML - (C) 2020 Author: Bernard Bou -->
<xsl:transform version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8" doctype-system="about:legacy-compat" />
	<xsl:import href='link2html.xsl' />

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
		var maindoc=document;
		var all=maindoc.getElementsByName('link');
		for(i=0;i<all.length;i++)
		{
			var e=all[i];
			e.style.visibility='collapse';
		}
		var e=maindoc.getElementById(tag);
		e.style.visibility='visible';
}
function menu()
{
		var maindoc=document;
		var toc=maindoc.getElementById('toc');
		if((toc.style.display != 'inline'))
			toc.style.display='inline';
		else
			toc.style.display='none';
}
]]>
					</xsl:text>
				</SCRIPT>
			</HEAD>
			<BODY>
				<DIV class="header">
					<IMG class="menu" src="./images/menu.png" alt="menu" oncLick="javascript:menu()" />
					<SPAN class="title">Relations</SPAN>
				</DIV>
				<TABLE>
					<TR>
						<TD style='vertical-align: top;'>
							<DIV id='toc' class="toc">
								<TABLE>
									<xsl:call-template name="build_toc">
										<xsl:with-param name="title" />
									</xsl:call-template>
								</TABLE>
							</DIV>
						</TD>
						<TD style='vertical-align: top;'>
							<DIV class="main">
								<TABLE>
									<xsl:apply-templates select=".//link" />
								</TABLE>
							</DIV>
						</TD>
					</TR>
				</TABLE>
			</BODY>
		</HTML>
	</xsl:template>

	<xsl:template name="build_toc">
		<xsl:param name="title" />
		<xsl:for-each select=".//link">
			<TR class='selector'>
				<xsl:attribute name="onclick">javascript:select('<xsl:value-of select="./@id" />');
				</xsl:attribute>
				<TD>
					<IMG>
						<xsl:attribute name="src">
							<xsl:copy-of select="$imagedir" />
							<xsl:value-of select="./img/@src" />
						</xsl:attribute>
						<xsl:attribute name="alt">
							<xsl:value-of select="./@name" />
						</xsl:attribute>
					</IMG>
				</TD>
				<TD>
					<xsl:value-of select="./@name" />
				</TD>
			</TR>
		</xsl:for-each>
	</xsl:template>

</xsl:transform>

