<?xml version="1.0" encoding="UTF-8"?>
<!-- WordNet to HTML Client-side 2015/05/15 (C) 2015 Author: Bernard Bou -->

<xsl:transform version="1.0"
               xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:strip-space elements="label"/>

	<xsl:template match="/">
		<DIV id="wordnet">
			<xsl:apply-templates select=".//wordnet"/>
		</DIV>
	</xsl:template>

	<xsl:template match="wordnet">
		<xsl:choose>
			<xsl:when test="count(.//synset)=0 and count(.//frame)=0">
				<SPAN class="treejunction">
					<IMG class="treepix" src="images/closed.png"/>
				</SPAN>
				<SPAN class="domain">
					<xsl:text>wordnet</xsl:text>
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
					<xsl:text>wordnet</xsl:text>
				</SPAN>
				<OL style="display: block;">
					<xsl:apply-templates select="./pos"/>
				</OL>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="pos">
		<LI class="treeitem treepanel block1">
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png"/>
			</SPAN>
			<IMG src="images/wordnet/pos.png"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<SPAN class="wnpos">
				<xsl:apply-templates select="./@name"/>
			</SPAN>
			<xsl:if test="count(./lexdomain)&gt;0">
				<OL style="display: block;">
					<xsl:apply-templates select="./lexdomain"/>
				</OL>
			</xsl:if>
		</LI>
	</xsl:template>

	<xsl:template match="lexdomain">
		<LI class="treeitem">
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png"/>
			</SPAN>
			<IMG src="images/wordnet/lexdomain.png"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<SPAN class="wnlexdomain">
				<xsl:apply-templates select="./@name"/>
			</SPAN>
			<xsl:if test="count(./sense)&gt;0">
				<OL style="display: block;">
					<xsl:apply-templates select="./sense"/>
				</OL>
			</xsl:if>
		</LI>
	</xsl:template>

	<xsl:template match="sense">
		<LI class="treeitem treepanel block2">
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png"/>
			</SPAN>
			<IMG src="images/wordnet/synset.png"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
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
			<xsl:if test="count(./synset)&gt;0">
				<OL style="display: block;">
					<xsl:apply-templates select="./synset"/>
				</OL>
			</xsl:if>
			<xsl:apply-templates select="./links"/>
		</LI>
	</xsl:template>

	<xsl:template match="synset">
		<LI class="treeitem treepanel block3">
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png"/>
			</SPAN>
			<IMG src="images/wordnet/definition.png"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<SPAN class="wndefinition">
				<xsl:apply-templates select="./definition"/>
			</SPAN>
			<xsl:if test="count(./word)&gt;0">
				<OL style="display: block;">
					<xsl:apply-templates select="./word"/>
				</OL>
			</xsl:if>
			<xsl:if test="count(./sample)&gt;0">
				<OL style="display: block;">
					<xsl:apply-templates select="./sample"/>
				</OL>
			</xsl:if>
			<xsl:apply-templates select="text()"/>
		</LI>
	</xsl:template>

	<xsl:template match="sample">
		<LI class="treeitem">
			<IMG src="images/wordnet/sample.png"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<SPAN class="wnsample">
				<xsl:apply-templates select="./text()"/>
			</SPAN>
		</LI>
	</xsl:template>

	<xsl:template match="word">
		<xsl:variable name="word">
			<xsl:value-of select="text()"/>
		</xsl:variable>
		<LI class="treeitem">
			<xsl:element name="img">
				<xsl:attribute name="src">images/wordnet/member.png</xsl:attribute>
			</xsl:element>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<A class="wnword">
				<xsl:attribute name="href">
					<xsl:value-of select="concat('query?word=',$word)"/>
				</xsl:attribute>
				<xsl:value-of select="$word"/>
			</A>
		</LI>
	</xsl:template>

	<xsl:template match="links">
		<xsl:if test="count(./*)&gt;0">
			<OL style="display: block;">
				<xsl:apply-templates select="./*"/>
			</OL>
		</xsl:if>
	</xsl:template>

	<xsl:template
		match="hypernym|hyponym|instance_hypernym|instance_hyponym|part_meronym|part_holonym|substance_meronym|substance_holonym|member_meronym|member_holonym|antonym|entail|cause">
		<xsl:call-template name="_link">
			<xsl:with-param name="type" select="name()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template
		match="similar|derivation|pertainym|also|attribute|verb_group|pertainym|domain_category|domain_member_category|domain_region|domain_member_region|domain_usage|domain_member_usage">
		<xsl:call-template name="_link">
			<xsl:with-param name="type" select="name()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="_link">
		<xsl:param name="type"/>
		<LI class="treeitem">
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG src="images/open.png"/>
			</SPAN>
			<xsl:element name="img">
				<xsl:attribute name="src">
					<xsl:value-of select="concat('images/wordnet/',name(),'.png')"/>
				</xsl:attribute>
			</xsl:element>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<SPAN class="wnlink">
				<xsl:value-of select="name()"/>
			</SPAN>
			<xsl:if test="count(./synset)&gt;0">
				<OL style="display: block;">
					<xsl:apply-templates select="./synset"/>
				</OL>
			</xsl:if>
			<xsl:if test="count(./*[name() = $type])&gt;0">
				<OL style="display: block;">
					<xsl:apply-templates select="./*[name() = $type]"/>
				</OL>
			</xsl:if>
		</LI>
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of select="normalize-space()"/>
	</xsl:template>

</xsl:transform>
