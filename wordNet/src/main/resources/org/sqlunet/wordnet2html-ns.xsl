<?xml version="1.0" encoding="UTF-8"?><!--
  ~ Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
  -->

<xsl:transform xmlns:wn="http://org.sqlunet/wn" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="UTF-8" indent="yes" method="xml" />
	<xsl:strip-space elements="label" />

	<xsl:template match="/">
		<DIV id="wordnet">
			<xsl:apply-templates select="//wn:wordnet" />
		</DIV>
	</xsl:template>

	<xsl:template match="wn:wordnet">
		<xsl:choose>
			<!-- no synset -->
			<xsl:when test="count(.//wn:synset)=0">
				<SPAN class="treejunction">
					<IMG class="treepix" src="images/closed.png" />
				</SPAN>
				<!-- data image -->
				<IMG class="dataimg" src="images/xnet/wordnet.png" />
				<SPAN class="domain">
					<xsl:text><![CDATA[wordnet]]></xsl:text>
				</SPAN>
				<SPAN class="error" id="default">
					<xsl:text><![CDATA[∅ data]]></xsl:text>
				</SPAN>
			</xsl:when>
			<!-- one or more synset -->
			<xsl:otherwise>
				<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
					<IMG class="treepix" src="images/open.png" />
				</SPAN>
				<!-- data image -->
				<IMG class="dataimg" src="images/xnet/wordnet.png" />
				<SPAN class="domain">
					<xsl:text><![CDATA[wordnet]]></xsl:text>
				</SPAN>
				<UL style="display: block;">
					<xsl:apply-templates select="./wn:word" />
					<xsl:apply-templates select="./wn:pos" />
					<xsl:apply-templates select="./wn:sense" />
					<xsl:apply-templates select="./wn:synset" />
				</UL>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="wn:word">
		<xsl:variable name="word">
			<xsl:value-of select="text()" />
		</xsl:variable>
		<LI class="treeitem">
			<IMG class="dataimg" src="images/xnet/member.png" />
			<A class="pointer">
				<xsl:attribute name="href">
					<xsl:value-of select="concat('query?wordid=',./@wordid)" />
				</xsl:attribute>
				<IMG class="dataimg" src="images/pointer.png" />
			</A>
			<!--
			<A class="pointer">
				<xsl:attribute name="href">
					<xsl:value-of select="concat('query?word=',$word)"/>
				</xsl:attribute>
				<IMG class="dataimg" src="images/pointer.png"/>
			</A>
			-->
			<SPAN class="wnword">
				<xsl:value-of select="$word" />
			</SPAN>
		</LI>
		<xsl:if test="count(./wn:sense)&gt;0">
			<UL style="display: block;">
				<xsl:apply-templates select="./wn:sense" />
			</UL>
		</xsl:if>
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
				<UL style="display: block;">
					<xsl:apply-templates select="./wn:domain" />
				</UL>
			</xsl:if>
		</LI>
	</xsl:template>

	<xsl:template match="wn:domain">
		<LI class="treeitem">
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png" />
			</SPAN>
			<IMG class="dataimg" src="images/xnet/domain.png" />
			<SPAN class="wndomain">
				<xsl:value-of select="./@name" />
			</SPAN>
			<xsl:if test="count(./wn:sense)&gt;0">
				<UL style="display: block;">
					<xsl:apply-templates select="./wn:sense" />
				</UL>
			</xsl:if>
		</LI>
	</xsl:template>

	<xsl:template match="wn:sense">
		<LI class="treeitem treepanel block2">
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png" />
			</SPAN>
			<IMG class="dataimg" src="images/xnet/synset.png" />
			<SPAN class="wnsense">
				<xsl:text><![CDATA[sense]]></xsl:text>
				<xsl:if test='./@number'>
					<xsl:text><![CDATA[[]]></xsl:text>
					<xsl:value-of select="./@number" />
					<xsl:text><![CDATA[]]]></xsl:text>
				</xsl:if>
				<!--
				<xsl:if test='./@wordid and ./@synsetid'>
					<xsl:text><![CDATA[(]]></xsl:text>
					<xsl:value-of select="./@wordid"/>
					<xsl:text><![CDATA[,]]></xsl:text>
					<xsl:value-of select="./@synsetid"/>
					<xsl:text><![CDATA[)]]></xsl:text>
				</xsl:if>
				-->
			</SPAN>
			<xsl:if test='./@pos'>
				<IMG class="dataimg" src="images/xnet/pos.png" />
				<SPAN class="wnpos">
					<xsl:value-of select="./@pos" />
				</SPAN>
			</xsl:if>
			<xsl:if test='./@domain'>
				<IMG class="dataimg" src="images/xnet/domain.png" />
				<SPAN class="wndomain">
					<xsl:value-of select="./@domain" />
				</SPAN>
			</xsl:if>
			<xsl:if test="count(./wn:word)&gt;0">
				<UL style="display: block;">
					<xsl:apply-templates select="./wn:word" />
				</UL>
			</xsl:if>
			<xsl:if test="count(./wn:synset)&gt;0">
				<xsl:choose>
					<xsl:when test="count(./wn:synset/wn:word)&gt;0 or count(./wn:synset/wn:sample)&gt;0 or count(./wn:synset/wn:relations)&gt;0">
						<UL style="display: block;">
							<xsl:apply-templates select="./wn:synset" />
						</UL>
					</xsl:when>
					<xsl:otherwise>
						<DIV>
							<A class="pointer">
								<xsl:attribute name="href">
									<xsl:value-of select="concat('query?synsetid=',./@synsetid)" />
								</xsl:attribute>
								<IMG class="dataimg" src="images/pointer.png" />
							</A>
							<IMG class="dataimg" src="images/xnet/definition.png" />
							<SPAN class="wndefinition">
								<xsl:apply-templates select="./wn:synset/wn:definition" />
							</SPAN>
						</DIV>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
		</LI>
	</xsl:template>

	<xsl:template match="wn:synset">
		<LI class="treeitem treepanel block3">
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png" />
			</SPAN>
			<A class="pointer">
				<xsl:attribute name="href">
					<xsl:value-of select="concat('query?synsetid=',./@synsetid)" />
				</xsl:attribute>
				<IMG class="dataimg" src="images/pointer.png" />
			</A>
			<IMG class="dataimg" src="images/xnet/synset.png" />
			<IMG class="dataimg" src="images/xnet/definition.png" />
			<SPAN class="wndefinition">
				<xsl:apply-templates select="./wn:definition" />
			</SPAN>
			<xsl:if test="count(./wn:word)&gt;0">
				<UL style="display: block;">
					<xsl:apply-templates select="./wn:word" />
				</UL>
			</xsl:if>
			<xsl:if test="count(./wn:sample)&gt;0">
				<UL style="display: block;">
					<xsl:apply-templates select="./wn:sample" />
				</UL>
			</xsl:if>
			<xsl:apply-templates select="./wn:relations" />
		</LI>
	</xsl:template>

	<xsl:template match="wn:sample">
		<LI class="treeitem">
			<IMG class="dataimg" src="images/xnet/sample.png" />
			<SPAN class="wnsample">
				<xsl:apply-templates select="./text()" />
			</SPAN>
		</LI>
	</xsl:template>

	<xsl:template match="wn:relations">
		<xsl:if test="count(./wn:*)&gt;0">
			<UL style="display: block;">
				<xsl:apply-templates select="./wn:*" />
			</UL>
		</xsl:if>
	</xsl:template>

	<!-- semantic -->
	<xsl:template match="wn:hypernym|wn:hyponym|wn:instance_hypernym|wn:instance_hyponym|wn:part_meronym|wn:part_holonym|wn:substance_meronym|wn:substance_holonym|wn:member_meronym|wn:member_holonym|wn:entails|wn:is_entailed_by|wn:causes|wn:is_caused_by|wn:similar|wn:attribute">
		<xsl:call-template name="_relation">
			<xsl:with-param name="type" select="name()" />
			<xsl:with-param name="relationclass" select="'[sem]'" />
		</xsl:call-template>
	</xsl:template>

	<!-- lexical -->
	<xsl:template match="wn:antonym|wn:participle|wn:pertainym|wn:derivation">
		<xsl:call-template name="_relation">
			<xsl:with-param name="type" select="name()" />
			<xsl:with-param name="relationclass" select="'[lex]'" />
		</xsl:call-template>
	</xsl:template>

	<!-- both -->
	<xsl:template match="wn:also|wn:verb_group|wn:domain_topic|wn:domain_member_topic|wn:domain_region|wn:domain_member_region|wn:exemplifies|wn:is_exemplified_by">
		<xsl:call-template name="_relation">
			<xsl:with-param name="type" select="name()" />
			<xsl:with-param name="relationclass" select="'[both]'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="_relation">
		<xsl:param name="type" />
		<xsl:param name="relationclass" />
		<LI class="treeitem">
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png" />
			</SPAN>
			<xsl:element name="img">
				<xsl:attribute name="class">
					<xsl:value-of select="'dataimg'" />
				</xsl:attribute>
				<xsl:attribute name="src">
					<xsl:value-of select="concat('images/wordnet/',name(),'.png')" />
				</xsl:attribute>
			</xsl:element>
			<SPAN class="wnrelation">
				<xsl:value-of select="name()" />
			</SPAN>
			<![CDATA[ ]]>
			<xsl:value-of select="$relationclass" />
			<xsl:if test="count(./wn:synset)&gt;0">
				<UL style="display: block;">
					<xsl:apply-templates select="./wn:synset" />
				</UL>
			</xsl:if>
			<xsl:if test="count(./wn:sense)&gt;0">
				<UL style="display: block;">
					<xsl:apply-templates select="./wn:sense" />
				</UL>
			</xsl:if>
		</LI>
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of select="normalize-space()" />
	</xsl:template>

</xsl:transform>
