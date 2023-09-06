<?xml version="1.0" encoding="UTF-8"?><!--
  ~ Copyright (c) 2023. Bernard Bou
  -->

<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:vn="http://org.sqlunet/vn">
	<xsl:output method="xml" indent="yes" encoding="UTF-8" />
	<xsl:strip-space elements="label" />

	<xsl:template match="/">
		<DIV id="verbnet">
			<xsl:apply-templates select="//vn:verbnet" />
		</DIV>
	</xsl:template>

	<xsl:template match="vn:verbnet">
		<xsl:choose>
			<xsl:when test="count(./vn:vnclass)=0">
				<SPAN class="treejunction">
					<IMG class="treepix" src="images/closed.png" />
				</SPAN>
				<IMG class="dataimg" src="images/xnet/verbnet.png" />
				<SPAN class="domain">
					<xsl:text><![CDATA[verbnet]]></xsl:text>
				</SPAN>
				<SPAN class="error" id="default">
					<xsl:text><![CDATA[∅ data]]></xsl:text>
				</SPAN>
			</xsl:when>
			<xsl:otherwise>
				<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
					<IMG class="treepix" src="images/open.png" />
				</SPAN>
				<IMG class="dataimg" src="images/xnet/verbnet.png" />
				<SPAN class="domain">
					<xsl:text><![CDATA[verbnet]]></xsl:text>
				</SPAN>
				<OL style="display: block;">
					<xsl:apply-templates select="./vn:vnclass" />
				</OL>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="vn:vnclass">
		<!-- indented -->
		<LI class="treeitem treepanel vnclass block1">
			<!-- link -->
			<xsl:variable name="ref">
				<xsl:value-of select="./@classid" />
			</xsl:variable>
			<A class="pointer">
				<xsl:attribute name="href">
					<xsl:value-of select="concat('query?vnclassid=',$ref)" />
				</xsl:attribute>
				<IMG class="dataimg" src="images/pointer.png" />
			</A>
			<!-- data image -->
			<IMG class="dataimg" src="images/xnet/roleclass.png" />
			<!-- label -->
			<SPAN class="vnclasslabel">
				<SPAN class="vnclassname">
					<xsl:value-of select="./@name" />
				</SPAN>
			</SPAN>
			<SPAN class="definition">
				<xsl:value-of select=".//vn:sense/vn:synset/vn:definition/text()" />
			</SPAN>
		</LI>
	</xsl:template>

</xsl:transform>
