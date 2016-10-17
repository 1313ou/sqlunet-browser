<?xml version="1.0" encoding="UTF-8"?>
<!-- VerbNet to HTML Client-side 2015/05/15 (C) 2015 Author: Bernard Bou -->

<xsl:transform version="1.0"
               xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:strip-space elements="label"/>

	<xsl:template match="/">
		<DIV id="verbnet">
			<xsl:apply-templates select=".//verbnet"/>
		</DIV>
	</xsl:template>

	<xsl:template match="verbnet">
		<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
			<IMG class="treepix" src="images/open.png"/>
		</SPAN>
		<SPAN class="domain">
			<xsl:text>verbnet</xsl:text>
		</SPAN>
		<OL style="display: block;">
			<xsl:apply-templates select=".//vnclass"/>
		</OL>
	</xsl:template>

	<xsl:template match="sense">
		<LI class="treeitem treepanel block2">
			<IMG class="dataimg" src="images/wordnet/synset.png"/>
			<SPAN class="wnsense">
				<xsl:text>sense</xsl:text>
				<xsl:choose>
					<xsl:when test='./@number'>
						<xsl:text>[</xsl:text>
						<xsl:apply-templates select="./@number"/>
						<xsl:text>]</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>(</xsl:text>
						<xsl:apply-templates select="./@word-id"/>
						<xsl:text>,</xsl:text>
						<xsl:apply-templates select="./@synset-id"/>
						<xsl:text>)</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</SPAN>
			<xsl:apply-templates select="./synset"/>
			<xsl:apply-templates select="./vnclass"/>
		</LI>
	</xsl:template>

	<xsl:template match="synset">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:apply-templates select="./definition"/>
		<xsl:apply-templates select="text()"/>
	</xsl:template>

	<xsl:template match="definition">
		<SPAN class="definition">
			<xsl:apply-templates select="text()"/>
		</SPAN>
	</xsl:template>

	<xsl:template match="vnclass">
		<!-- indented -->
		<LI class="treeitem treepanel vnclass block1">
			<!-- link -->
			<xsl:variable name="ref">
				<xsl:value-of select="./@classid"/>
			</xsl:variable>
			<A class="pointer">
				<xsl:attribute name="href">
					<xsl:value-of select="concat('query?vnclassid=',$ref)"/>
				</xsl:attribute>
				<IMG class="dataimg" src="images/pointer.png"/>
				<!-- 				<xsl:value-of select="$ref" /> -->
			</A>
			<!-- data image -->
			<IMG class="dataimg" src="images/verbnet/vnclass.png"/>
			<!-- label -->
			<SPAN class="vnclasslabel">
				<SPAN class="vnclassname">
					<xsl:value-of select="./@name"/>
				</SPAN>
			</SPAN>
			<SPAN class="definition">
				<xsl:value-of select="../synset/definition/text()"/>
			</SPAN>
		</LI>
	</xsl:template>

</xsl:transform>
