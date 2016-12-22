<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               version="1.0"
               xmlns="http://org.sqlunet/wn">
	<xsl:output encoding="UTF-8" indent="yes" method="xml"/>
	<xsl:strip-space elements="label"/>

	<xsl:template match="/">
		<DIV id="wordnet">
			<xsl:apply-templates select="//wordnet"/>
		</DIV>
	</xsl:template>

	<xsl:template match="wordnet">
		<xsl:choose>
			<!-- no synset -->
			<xsl:when test="count(.//synset)=0">
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
			<!-- one of more synset -->
			<xsl:otherwise>
				<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
					<IMG class="treepix" src="images/open.png"/>
				</SPAN>
				<SPAN class="domain">
					<xsl:text>wordnet</xsl:text>
				</SPAN>
				<UL style="display: block;">
					<xsl:apply-templates select="./pos"/>
					<xsl:apply-templates select="./sense"/>
				</UL>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="pos">
		<LI class="treeitem treepanel block1">
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png"/>
			</SPAN>
			<IMG src="images/xnet/pos.png"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<SPAN class="wnpos">
				<xsl:apply-templates select="./@name"/>
			</SPAN>
			<xsl:if test="count(./lexdomain)&gt;0">
				<UL style="display: block;">
					<xsl:apply-templates select="./lexdomain"/>
				</UL>
			</xsl:if>
		</LI>
	</xsl:template>

	<xsl:template match="lexdomain">
		<LI class="treeitem">
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png"/>
			</SPAN>
			<IMG src="images/xnet/domain.png"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<SPAN class="wnlexdomain">
				<xsl:apply-templates select="./@name"/>
			</SPAN>
			<xsl:if test="count(./sense)&gt;0">
				<UL style="display: block;">
					<xsl:apply-templates select="./sense"/>
				</UL>
			</xsl:if>
		</LI>
	</xsl:template>

	<xsl:template match="sense">
		<LI class="treeitem treepanel block2">
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png"/>
			</SPAN>
			<IMG src="images/xnet/synset.png"/>
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
						<xsl:apply-templates select="./@wordid"/>
						<xsl:text>,</xsl:text>
						<xsl:apply-templates select="./@synsetid"/>
						<xsl:text>)</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</SPAN>
			<xsl:if test="count(./synset)&gt;0">
				<UL style="display: block;">
					<xsl:apply-templates select="./word"/>
				</UL>
				<UL style="display: block;">
					<xsl:apply-templates select="./synset"/>
				</UL>
			</xsl:if>
		</LI>
	</xsl:template>

	<xsl:template match="synset">
		<LI class="treeitem treepanel block3">
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png"/>
			</SPAN>
			<IMG src="images/xnet/definition.png"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<SPAN class="wndefinition">
				<xsl:apply-templates select="./definition"/>
			</SPAN>
			<xsl:if test="count(./word)&gt;0">
				<UL style="display: block;">
					<xsl:apply-templates select="./word"/>
				</UL>
			</xsl:if>
			<xsl:if test="count(./sample)&gt;0">
				<UL style="display: block;">
					<xsl:apply-templates select="./sample"/>
				</UL>
			</xsl:if>
			<xsl:apply-templates select="./links"/>
		</LI>
	</xsl:template>

	<xsl:template match="word">
		<xsl:variable name="word">
			<xsl:value-of select="text()"/>
		</xsl:variable>
		<LI class="treeitem">
			<xsl:element name="img">
				<xsl:attribute name="src">images/xnet/member.png</xsl:attribute>
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

	<xsl:template match="sample">
		<LI class="treeitem">
			<IMG src="images/xnet/sample.png"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<SPAN class="wnsample">
				<xsl:apply-templates select="./text()"/>
			</SPAN>
		</LI>
	</xsl:template>

	<xsl:template match="links">
		<xsl:if test="count(./*)&gt;0">
			<UL style="display: block;">
				<xsl:apply-templates select="./*"/>
			</UL>
		</xsl:if>
	</xsl:template>

	<!-- semantic -->
	<xsl:template
		match="hypernym|hyponym|instance_hypernym|instance_hyponym|part_meronym|part_holonym|substance_meronym|substance_holonym|member_meronym|member_holonym|entail|cause|similar|attribute">
		<xsl:call-template name="_link">
			<xsl:with-param name="type" select="name()"/>
			<xsl:with-param name="linkclass" select="'[sem]'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- lexical -->
	<xsl:template
		match="antonym|participle|pertainym|derivation">
		<xsl:call-template name="_link">
			<xsl:with-param name="type" select="name()"/>
			<xsl:with-param name="linkclass" select="'[lex]'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- both -->
	<xsl:template
		match="also|verb_group|domain_category|domain_member_category|domain_region|domain_member_region|domain_usage|domain_member_usage">
		<xsl:call-template name="_link">
			<xsl:with-param name="type" select="name()"/>
			<xsl:with-param name="linkclass" select="'[both]'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="_link">
		<xsl:param name="type"/>
		<xsl:param name="linkclass"/>
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
				<xsl:text><![CDATA[ ]]></xsl:text>
				<xsl:value-of select="concat('* ',$linkclass)"/>
			</SPAN>
			<xsl:if test="count(./synset)&gt;0">
				<UL style="display: block;">
					<xsl:apply-templates select="./synset"/>
				</UL>
			</xsl:if>
			<xsl:if test="count(./sense)&gt;0">
				<UL style="display: block;">
					<xsl:apply-templates select="./sense"/>
				</UL>
			</xsl:if>
		</LI>
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of select="normalize-space()"/>
	</xsl:template>

</xsl:transform>
