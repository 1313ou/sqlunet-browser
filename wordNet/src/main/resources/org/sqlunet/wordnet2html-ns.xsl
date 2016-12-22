<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               version="1.0"
               xmlns:wn="http://org.sqlunet/wn">
	<xsl:output encoding="UTF-8" indent="yes" method="xml"/>
	<xsl:strip-space elements="label"/>

	<xsl:template match="/">
		<DIV id="wordnet">
			<xsl:apply-templates select="//wn:wordnet"/>
		</DIV>
	</xsl:template>

	<xsl:template match="wn:wordnet">
		<xsl:choose>
			<!-- no synset -->
			<xsl:when test="count(.//wn:synset)=0">
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
			<!-- one or more synset -->
			<xsl:otherwise>
				<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
					<IMG class="treepix" src="images/open.png"/>
				</SPAN>
				<SPAN class="domain">
					<xsl:text>wordnet</xsl:text>
				</SPAN>
				<UL style="display: block;">
					<xsl:apply-templates select="./wn:pos"/>
					<xsl:apply-templates select="./wn:sense"/>
				</UL>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="wn:pos">
		<LI class="treeitem treepanel block1">
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png"/>
			</SPAN>
			<IMG src="images/xnet/pos.png"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<SPAN class="wnpos">
				<xsl:apply-templates select="./@name"/>
			</SPAN>
			<xsl:if test="count(./wn:lexdomain)&gt;0">
				<UL style="display: block;">
					<xsl:apply-templates select="./wn:lexdomain"/>
				</UL>
			</xsl:if>
		</LI>
	</xsl:template>

	<xsl:template match="wn:lexdomain">
		<LI class="treeitem">
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png"/>
			</SPAN>
			<IMG src="images/xnet/domain.png"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<SPAN class="wnlexdomain">
				<xsl:apply-templates select="./@name"/>
			</SPAN>
			<xsl:if test="count(./wn:sense)&gt;0">
				<UL style="display: block;">
					<xsl:apply-templates select="./wn:sense"/>
				</UL>
			</xsl:if>
		</LI>
	</xsl:template>

	<xsl:template match="wn:sense">
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
			<xsl:if test="count(./wn:synset)&gt;0">
				<UL style="display: block;">
					<xsl:apply-templates select="./wn:word"/>
				</UL>
				<UL style="display: block;">
					<xsl:apply-templates select="./wn:synset"/>
				</UL>
			</xsl:if>
		</LI>
	</xsl:template>

	<xsl:template match="wn:synset">
		<LI class="treeitem treepanel block3">
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png"/>
			</SPAN>
			<IMG src="images/xnet/definition.png"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<SPAN class="wndefinition">
				<xsl:apply-templates select="./wn:definition"/>
			</SPAN>
			<xsl:if test="count(./wn:word)&gt;0">
				<UL style="display: block;">
					<xsl:apply-templates select="./wn:word"/>
				</UL>
			</xsl:if>
			<xsl:if test="count(./wn:sample)&gt;0">
				<UL style="display: block;">
					<xsl:apply-templates select="./wn:sample"/>
				</UL>
			</xsl:if>
			<xsl:apply-templates select="./wn:links"/>
		</LI>
	</xsl:template>

	<xsl:template match="wn:word">
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

	<xsl:template match="wn:sample">
		<LI class="treeitem">
			<IMG src="images/xnet/sample.png"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<SPAN class="wnsample">
				<xsl:apply-templates select="./text()"/>
			</SPAN>
		</LI>
	</xsl:template>

	<xsl:template match="wn:links">
		<xsl:if test="count(./wn:*)&gt;0">
			<UL style="display: block;">
				<xsl:apply-templates select="./wn:*"/>
			</UL>
		</xsl:if>
	</xsl:template>

	<!-- semantic -->
	<xsl:template
		match="wn:hypernym|wn:hyponym|wn:instance_hypernym|wn:instance_hyponym|wn:part_meronym|wn:part_holonym|wn:substance_meronym|wn:substance_holonym|wn:member_meronym|wn:member_holonym|wn:entail|wn:cause|wn:similar|wn:attribute">
		<xsl:call-template name="_link">
			<xsl:with-param name="type" select="name()"/>
			<xsl:with-param name="linkclass" select="'[sem]'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- lexical -->
	<xsl:template
		match="wn:antonym|wn:participle|wn:pertainym|wn:derivation">
		<xsl:call-template name="_link">
			<xsl:with-param name="type" select="name()"/>
			<xsl:with-param name="linkclass" select="'[lex]'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- both -->
	<xsl:template
		match="wn:also|wn:verb_group|wn:domain_category|wn:domain_member_category|wn:domain_region|wn:domain_member_region|wn:domain_usage|wn:domain_member_usage">
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
			<xsl:if test="count(./wn:synset)&gt;0">
				<UL style="display: block;">
					<xsl:apply-templates select="./wn:synset"/>
				</UL>
			</xsl:if>
			<xsl:if test="count(./wn:sense)&gt;0">
				<UL style="display: block;">
					<xsl:apply-templates select="./wn:sense"/>
				</UL>
			</xsl:if>
		</LI>
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of select="normalize-space()"/>
	</xsl:template>

</xsl:transform>
