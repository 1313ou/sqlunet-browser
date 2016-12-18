<?xml version="1.0" encoding="UTF-8"?>
<!-- FrameNet to HTML Client-side 2015/05/15 (C) 2015 Author: Bernard Bou -->

<xsl:transform version="1.0"
               xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:strip-space elements="label"/>

	<xsl:template match="/">
		<DIV id="framenet">
			<xsl:apply-templates select="./framenet"/>
		</DIV>
	</xsl:template>

	<xsl:template match="framenet">
		<xsl:choose>
			<xsl:when test="count(.//frame)=0">
				<SPAN class="treejunction">
					<IMG class="treepix" src="images/closed.png"/>
				</SPAN>
				<SPAN class="domain">
					<xsl:text>framenet (s)</xsl:text>
				</SPAN>
				<SPAN class="error" id="default">
					<xsl:text>∅ data</xsl:text>
				</SPAN>
			</xsl:when>
			<xsl:otherwise>
				<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
					<IMG class="treepix" src="images/open.png"/>
				</SPAN>
				<SPAN class="domain">
					<xsl:text>framenet</xsl:text>
				</SPAN>
				<OL style="display: block;">
					<xsl:apply-templates select="./frame"/>
					<xsl:apply-templates select="./lexunit"/>
				</OL>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="lexunit">
		<!-- indent -->
		<LI class="treeitem treepanel fnlexunit block1">
			<!-- link -->
			<xsl:variable name="ref">
				<xsl:value-of select="./@luid"/>
			</xsl:variable>
			<A class="pointer">
				<xsl:attribute name="href">
					<xsl:value-of select="concat('query?fnluid=',$ref)"/>
				</xsl:attribute>
				<IMG class="dataimg" src="images/pointer.png"/>
				<!-- 				<xsl:value-of select="$ref" /> -->
			</A>
			<!-- data image -->
			<IMG class="dataimg" src="images/framenet/lexunit.png"/>
			<!-- lexunit label -->
			<SPAN class="fnlexunitlabel">
				<SPAN class="fnlexunitname">
					<xsl:value-of select="./@name"/>
				</SPAN>
			</SPAN>

			<!-- data image -->
			<IMG class="dataimg" src="images/framenet/frame.png"/>
			<!-- frame label -->
			<SPAN class="fnframelabel">
				<SPAN class="fnframename">
					<xsl:value-of select="./frame/@name"/>
				</SPAN>
			</SPAN>
			<!-- lexunit -->
			<SPAN class="definition">
				<xsl:value-of select="./text()"/>
			</SPAN>
		</LI>
	</xsl:template>

	<xsl:template match="framedefinition">
		<!-- non-indent -->
		<IMG class="dataimg" src="images/propbank/definition.png"/>
		<SPAN class="fnframedefinition">
			<xsl:apply-templates select="./child::node()"/>
		</SPAN>
	</xsl:template>

	<!-- fen|t|ment|em|ex - outside <ex> -->
	<!-- fex|t|ment|gov|x|m|target - within <ex> -->

	<xsl:template match="t">
		<SPAN class="fntarget">
			<xsl:value-of select="./text()"/>
		</SPAN>
	</xsl:template>

	<xsl:template match="fen">
		<SPAN class="fnfen">
			<xsl:value-of select="./text()"/>
		</SPAN>
	</xsl:template>

	<xsl:template match="fex">
		<SPAN class="fnfex">
			<xsl:value-of select="./text()"/>
		</SPAN>
	</xsl:template>

	<xsl:template match="ment">
		<SPAN class="fnment">
			<xsl:value-of select="./text()"/>
		</SPAN>
	</xsl:template>

	<xsl:template match="gov">
		<SPAN class="fngov">
			<xsl:value-of select="./text()"/>
		</SPAN>
	</xsl:template>

	<xsl:template match="x">
		<SPAN class="fnx">
			<xsl:value-of select="./text()"/>
		</SPAN>
	</xsl:template>

	<xsl:template match="m">
		<SPAN class="fnm">
			<xsl:value-of select="./text()"/>
		</SPAN>
	</xsl:template>

</xsl:transform>
	