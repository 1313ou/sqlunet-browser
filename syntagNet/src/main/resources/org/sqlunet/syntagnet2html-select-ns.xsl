<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
  -->

<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               version="1.0"
               xmlns:sn="http://org.sqlunet/sn">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:strip-space elements="label"/>

	<xsl:template match="/">
		<DIV id="syntagnet">
			<xsl:apply-templates select="//sn:syntagnet"/>
		</DIV>
	</xsl:template>

	<xsl:template match="sn:syntagnet">
		<xsl:choose>
			<xsl:when test="count(./sn:collocation)=0">
				<SPAN class="treejunction">
					<IMG class="treepix" src="images/closed.png"/>
				</SPAN>
				<IMG class="dataimg" src="images/xnet/syntagnet.png"/>
				<SPAN class="domain">
					<xsl:text><![CDATA[syntagnet]]></xsl:text>
				</SPAN>
				<SPAN class="error" id="default">
					<xsl:text><![CDATA[∅ data]]></xsl:text>
				</SPAN>
			</xsl:when>
			<xsl:otherwise>
				<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
					<IMG class="treepix" src="images/open.png"/>
				</SPAN>
				<IMG class="dataimg" src="images/xnet/syntagnet.png"/>
				<SPAN class="domain">
					<xsl:text><![CDATA[syntagnet]]></xsl:text>
				</SPAN>
				<OL style="display: block;">
					<xsl:apply-templates select="./sn:collocation"/>
				</OL>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="sn:collocation">
		<!-- indented -->
		<LI class="treeitem treepanel sncollocation block1">
			<!-- link -->
			<xsl:variable name="ref">
				<xsl:value-of select="./@collocationid"/>
			</xsl:variable>
			<A class="pointer">
				<xsl:attribute name="href">
					<xsl:value-of select="concat('query?sncollocationid=',$ref)"/>
				</xsl:attribute>
				<IMG class="dataimg" src="images/pointer.png"/>
			</A>
			<!-- data image -->
			<IMG class="dataimg" src="images/xnet/roleclass.png"/>
			<!-- label -->
			<SPAN class="sncollocationlabel">
				<SPAN class="sncollocationname">
					<xsl:value-of select="./@name"/>
				</SPAN>
			</SPAN>
			<SPAN class="definition">
				<xsl:value-of select="./text()"/>
			</SPAN>
		</LI>
	</xsl:template>

</xsl:transform>
	
