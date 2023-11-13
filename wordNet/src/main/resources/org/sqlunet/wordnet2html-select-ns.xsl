<?xml version="1.0" encoding="UTF-8"?><!--
  ~ Copyright (c) 2023. Bernard Bou
  -->

<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:wn="http://org.sqlunet/wn">
	<xsl:output method="xml" indent="yes" encoding="UTF-8" />
	<xsl:strip-space elements="label" />

	<xsl:template match="/">
		<DIV id="wordnet">
			<xsl:apply-templates select="//wn:wordnet" />
		</DIV>
	</xsl:template>

	<xsl:template match="wn:wordnet">
		<xsl:choose>
			<xsl:when test="count(.//wn:synset)=0">
				<SPAN class="treejunction">
					<IMG class="treepix" src="images/closed.png" />
				</SPAN>
				<IMG class="dataimg" src="images/wordnet/wordnet.png" />
				<SPAN class="domain">
					<xsl:text><![CDATA[wordnet]]></xsl:text>
				</SPAN>
				<SPAN class="error" id="default">
					<xsl:text><![CDATA[∅ data]]></xsl:text>
				</SPAN>
			</xsl:when>
			<xsl:otherwise>
				<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
					<IMG class="treepix" src="images/open.png" />
				</SPAN>
				<IMG class="dataimg" src="images/wordnet/wordnet.png" />
				<SPAN class="domain">
					<xsl:text><![CDATA[wordnet]]></xsl:text>
				</SPAN>
				<OL style="display: block;">
					<xsl:apply-templates select="./wn:pos" />
				</OL>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="wn:pos">
		<LI class="treeitem treepanel block1">
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png" />
			</SPAN>
			<IMG class="dataimg" src="images/xnet/pos.png" />
			<SPAN class="wnpos">
				<xsl:value-of select="./@name" />
			</SPAN>
			<xsl:if test="count(./wn:domain)&gt;0">
				<OL style="display: block;">
					<xsl:apply-templates select="./wn:domain" />
				</OL>
			</xsl:if>
		</LI>
	</xsl:template>

	<xsl:template match="wn:domain">
		<LI class="treeitem">
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png" />
			</SPAN>
			<IMG class="dataimg" src="images/wordnet/domain.png" />
			<SPAN class="wndomain">
				<xsl:value-of select="./@name" />
			</SPAN>
			<xsl:if test="count(./wn:sense)&gt;0">
				<OL style="display: block;">
					<xsl:apply-templates select="./wn:sense" />
				</OL>
			</xsl:if>
		</LI>
	</xsl:template>

	<xsl:template match="wn:sense">
		<LI class="treeitem treepanel block2">
			<!-- relation -->
			<xsl:variable name="ref">
				<xsl:value-of select="./wn:synset/@synsetid" />
			</xsl:variable>
			<A class="pointer">
				<xsl:attribute name="href">
					<xsl:value-of select="concat('query?synsetid=',$ref)" />
				</xsl:attribute>
				<IMG class="dataimg" src="images/pointer.png" />
			</A>
			<IMG class="dataimg" src="images/wordnet/synset.png" />
			<SPAN class="wnsense">
				<xsl:text><![CDATA[sense]]></xsl:text>
				<xsl:choose>
					<xsl:when test='./@number'>
						<xsl:text><![CDATA[[]]></xsl:text>
						<xsl:value-of select="./@number" />
						<xsl:text><![CDATA[]]]></xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text><![CDATA[(]]></xsl:text>
						<xsl:value-of select="./@wordid" />
						<xsl:text><![CDATA[,]]></xsl:text>
						<xsl:value-of select="./@synsetid" />
						<xsl:text><![CDATA[)]]></xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</SPAN>
			<xsl:apply-templates select="./wn:synset" />
		</LI>
	</xsl:template>

	<xsl:template match="wn:synset">
		<SPAN class="definition wndefinition">
			<xsl:apply-templates select="./wn:definition" />
		</SPAN>
	</xsl:template>

	<xsl:template match="wn:definition">
		<xsl:apply-templates select="text()" />
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of select="normalize-space()" />
	</xsl:template>

</xsl:transform>
