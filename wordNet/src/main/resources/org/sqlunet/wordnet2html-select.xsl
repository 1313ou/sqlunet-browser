<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               version="1.0">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:strip-space elements="label"/>

	<xsl:template match="/">
		<DIV id="wordnet">
			<xsl:apply-templates select="//wordnet"/>
		</DIV>
	</xsl:template>

	<xsl:template match="wordnet">
		<xsl:choose>
			<xsl:when test="count(.//synset)=0">
				<SPAN class="treejunction">
					<IMG class="treepix" src="images/closed.png"/>
				</SPAN>
				<IMG class="dataimg" src="images/xnet/wordnet.png"/>
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
				<IMG class="dataimg" src="images/xnet/wordnet.png"/>
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
			<IMG class="dataimg" src="images/xnet/pos.png"/>
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
			<IMG class="dataimg" src="images/xnet/domain.png"/>
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
			<!-- link -->
			<xsl:variable name="ref">
				<xsl:value-of select="./synset/@synsetid"/>
			</xsl:variable>
			<A class="pointer">
				<xsl:attribute name="href">
					<xsl:value-of select="concat('query?synsetid=',$ref)"/>
				</xsl:attribute>
				<IMG class="dataimg" src="images/pointer.png"/>
			</A>
			<IMG class="dataimg" src="images/xnet/synset.png"/>
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
			<xsl:apply-templates select="./synset"/>
		</LI>
	</xsl:template>

	<xsl:template match="synset">
		<SPAN class="definition wndefinition">
			<xsl:apply-templates select="./definition"/>
		</SPAN>
	</xsl:template>

	<xsl:template match="definition">
		<xsl:apply-templates select="text()"/>
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of select="normalize-space()"/>
	</xsl:template>

</xsl:transform>
