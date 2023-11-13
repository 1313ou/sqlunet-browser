<?xml version="1.0" encoding="UTF-8"?><!--
  ~ Copyright (c) 2023. Bernard Bou
  -->

<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="UTF-8" indent="yes" method="xml" />
	<xsl:strip-space elements="label" />

	<xsl:template match="/">
		<DIV id="syntagnet">
			<xsl:apply-templates select="//syntagnet" />
		</DIV>
	</xsl:template>

	<xsl:template match="syntagnet">
		<xsl:choose>
			<xsl:when test="count(./collocation)=0">
				<SPAN class="treejunction">
					<IMG class="treepix" src="images/closed.png" />
				</SPAN>
				<!-- data image -->
				<IMG class="dataimg" src="images/syntagnet/syntagnet.png" />
				<SPAN class="domain">
					<xsl:text><![CDATA[syntagnet]]></xsl:text>
				</SPAN>
				<SPAN class="error" id="default">
					<xsl:text><![CDATA[∅ data]]></xsl:text>
				</SPAN>
			</xsl:when>
			<xsl:otherwise>
				<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
					<IMG class="treepix" src="images/open.png" />
				</SPAN>
				<!-- data image -->
				<IMG class="dataimg" src="images/syntagnet/syntagnet.png" />
				<SPAN class="domain">
					<xsl:text><![CDATA[syntagnet]]></xsl:text>
				</SPAN>
				<UL style="display: block;">
					<xsl:apply-templates select="./collocation" />
				</UL>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="collocation">
		<!-- indented -->
		<LI class="treeitem treepanel sncollocation block1">
			<!-- tree handle -->
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png" />
			</SPAN>
			<!-- data image -->
			<IMG class="dataimg" src="images/syntagnet/collocation.png" />
			<!-- label -->
			<SPAN class="sncollocationlabel">
				<SPAN class="sncollocationname">
					<xsl:value-of select="./word[@which='1']/text()" />
					<xsl:text> </xsl:text>
					<xsl:value-of select="./word[@which='2']/text()" />
				</SPAN>
			</SPAN>
			<UL style="display: block;">
				<LI class="block2">
					<IMG class="dataimg" src="images/xnet/definition.png" />
					<SPAN class="sncollocationtext">
						<xsl:value-of select="./synset[@which='1']/text()" />
					</SPAN>
					<BR />
					<IMG class="dataimg" src="images/xnet/definition.png" />
					<SPAN class="sncollocationtext">
						<xsl:value-of select="./synset[@which='2']/text()" />
					</SPAN>
					<!--
					<xsl:text><![CDATA[ collocationid=]]></xsl:text>
					<xsl:value-of select="./@collocationid"/>
					-->
					<!--
					<BR/>
					<xsl:text><![CDATA[ word1id=]]></xsl:text>
					<xsl:value-of select="./@word1id"/>
					<xsl:text><![CDATA[ word2id=]]></xsl:text>
					<xsl:value-of select="./@word2id"/>
					<xsl:text><![CDATA[ synset1id=]]></xsl:text>
					<xsl:value-of select="./@synset1id"/>
					<xsl:text><![CDATA[ synset2id=]]></xsl:text>
					<xsl:value-of select="./@synset2id"/>
					-->
				</LI>
			</UL>
		</LI>
	</xsl:template>

</xsl:transform>
