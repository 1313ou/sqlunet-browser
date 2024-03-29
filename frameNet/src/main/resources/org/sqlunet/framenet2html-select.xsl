<?xml version="1.0" encoding="UTF-8"?><!--
  ~ Copyright (c) 2023. Bernard Bou
  -->

<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" indent="yes" encoding="UTF-8" />
	<xsl:strip-space elements="label" />

	<xsl:template match="/">
		<DIV id="framenet">
			<xsl:apply-templates select="//framenet" />
		</DIV>
	</xsl:template>

	<xsl:template match="framenet">
		<xsl:choose>
			<xsl:when test="count(.//frame)=0">
				<SPAN class="treejunction">
					<IMG class="treepix" src="images/closed.png" />
				</SPAN>
				<IMG class="dataimg" src="images/framenet/framenet.png" />
				<SPAN class="domain">
					<xsl:text><![CDATA[framenet]]></xsl:text>
				</SPAN>
				<SPAN class="error" id="default">
					<xsl:text><![CDATA[∅ data]]></xsl:text>
				</SPAN>
			</xsl:when>
			<xsl:otherwise>
				<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
					<IMG class="treepix" src="images/open.png" />
				</SPAN>
				<IMG class="dataimg" src="images/framenet/framenet.png" />
				<SPAN class="domain">
					<xsl:text><![CDATA[framenet]]></xsl:text>
				</SPAN>
				<OL style="display: block;">
					<xsl:apply-templates select="./lexunit" />
					<xsl:apply-templates select="./frame" />
				</OL>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="frame">
		<!-- indent -->
		<LI class="treeitem treepanel fnlexunit block1">
			<!-- link -->
			<xsl:variable name="ref">
				<xsl:value-of select="./@frameid" />
			</xsl:variable>
			<A class="pointer">
				<xsl:attribute name="href">
					<xsl:value-of select="concat('query?fnframeid=',$ref)" />
				</xsl:attribute>
				<IMG class="dataimg" src="images/pointer.png" />
			</A>
			<!-- data image -->
			<IMG class="dataimg" src="images/xnet/roleclass.png" />
			<!-- frame label -->
			<SPAN class="fnframelabel">
				<SPAN class="fnframename">
					<xsl:value-of select="./@name" />
				</SPAN>
			</SPAN>
		</LI>
	</xsl:template>

	<xsl:template match="lexunit">
		<!-- indent -->
		<LI class="treeitem treepanel fnlexunit block1">
			<!-- link -->
			<xsl:variable name="ref">
				<xsl:value-of select="./@luid" />
			</xsl:variable>
			<A class="pointer">
				<xsl:attribute name="href">
					<xsl:value-of select="concat('query?fnluid=',$ref)" />
				</xsl:attribute>
				<IMG class="dataimg" src="images/pointer.png" />
			</A>
			<!-- data image -->
			<IMG class="dataimg" src="images/xnet/member.png" />
			<!-- lexunit label -->
			<SPAN class="fnlexunitlabel">
				<SPAN class="fnlexunitname">
					<xsl:value-of select="./@name" />
				</SPAN>
			</SPAN>

			<!-- data image -->
			<IMG class="dataimg" src="images/xnet/roleclass.png" />
			<!-- frame label -->
			<SPAN class="fnframelabel">
				<SPAN class="fnframename">
					<xsl:value-of select="./frame/@name" />
				</SPAN>
			</SPAN>
			<!-- lexunit -->
			<SPAN class="definition">
				<xsl:value-of select="./text()" />
			</SPAN>
		</LI>
	</xsl:template>

</xsl:transform>
