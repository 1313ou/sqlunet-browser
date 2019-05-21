<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
  -->

<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               version="1.0">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:strip-space elements="label"/>

	<xsl:template match="/">
		<DIV id="framenet">
			<xsl:apply-templates select="//framenet"/>
		</DIV>
	</xsl:template>

	<xsl:template match="framenet">
		<xsl:choose>
			<xsl:when test="count(./lexunit)=0 and count(./frame)=0 and count(./sentence)=0">
				<SPAN class="treejunction">
					<IMG class="treepix" src="images/closed.png"/>
				</SPAN>
				<!-- data image -->
				<IMG class="dataimg" src="images/xnet/framenet.png"/>
				<SPAN class="domain">
					<xsl:text><![CDATA[framenet]]></xsl:text>
				</SPAN>
				<SPAN class="error" id="default">
					<xsl:text><![CDATA[∅ data]]></xsl:text>
				</SPAN>
			</xsl:when>
			<xsl:otherwise>
				<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
					<IMG class="treepix" src="images/open.png"/>
				</SPAN>
				<!-- data image -->
				<IMG class="dataimg" src="images/xnet/framenet.png"/>
				<SPAN class="domain">
					<xsl:text><![CDATA[framenet]]></xsl:text>
				</SPAN>
				<UL style="display: block;">
					<xsl:apply-templates select="./frame"/>
					<xsl:apply-templates select="./lexunit"/>
					<xsl:apply-templates select="./sentence"/>
				</UL>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="lexunit">
		<!-- indent -->
		<LI class="treeitem treepanel fnlexunit block1">
			<!-- tree handle -->
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png"/>
			</SPAN>
			<!-- pointer -->
			<A class="pointer">
				<xsl:attribute name="href">
					<xsl:value-of select="concat('query?fnluid=',./@luid)"/>
				</xsl:attribute>
				<IMG class="dataimg" src="images/pointer.png"/>
			</A>
			<!-- data image -->
			<IMG class="dataimg" src="images/xnet/member.png"/>
			<!-- label -->
			<SPAN class="fnlexunitlabel">
				<SPAN class="fnlexunitname">
					<xsl:value-of select="./@name"/>
				</SPAN>
				<!-- data image -->
				<IMG class="dataimg" src="images/xnet/definition.png"/>
				<SPAN class="fnlexunitdefinition">
					<xsl:value-of select="./text()"/>
				</SPAN>
			</SPAN>
			<!-- collapsible content -->
			<UL style="display: block;">
				<xsl:apply-templates select="./frame"/>
				<xsl:apply-templates select="./sentences"/>
			</UL>
		</LI>
	</xsl:template>

	<xsl:template match="frame">
		<!-- indent -->
		<LI class="treeitem treepanel fnframe block2">
			<!-- tree handle -->
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png"/>
			</SPAN>
			<!-- data image -->
			<IMG class="dataimg" src="images/xnet/roleclass.png"/>
			<!-- label -->
			<SPAN class="fnframelabel">
				<SPAN class="fnframename">
					<xsl:value-of select="./@name"/>
				</SPAN>
			</SPAN>
			<!-- collapsible content -->
			<UL style="display: block;">
				<LI>
					<DIV class="fnframetext">
						<xsl:apply-templates select="./framedefinition"/>
					</DIV>
					<UL style="display: block;">
						<xsl:apply-templates select="./related"/>
						<xsl:apply-templates select="./lexunit"/>
						<xsl:apply-templates select="./fe"/>
					</UL>
				</LI>
			</UL>
		</LI>
	</xsl:template>

	<xsl:template match="fe">
		<!-- indent -->
		<LI class="treeitem treepanel fnfe block3">
			<!-- tree handle -->
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png"/>
			</SPAN>
			<!-- data image -->
			<xsl:choose>
				<xsl:when test="@type='Core'">
					<IMG class="dataimg" src="images/xnet/rolex.png"/>
					<!-- label -->
				</xsl:when>
				<xsl:otherwise>
					<!-- data image -->
					<IMG class="dataimg" src="images/xnet/role.png"/>
				</xsl:otherwise>
			</xsl:choose>
			<!-- label -->
			<SPAN class="fnfelabel">
				<SPAN class="fnfename">
					<xsl:value-of select="./@name"/>
				</SPAN>
				<![CDATA[ ]]>
				<xsl:if test="./@semtype != ''">
					<SPAN class="fnfesemtype">
						<xsl:value-of select="./@semtype"/>
					</SPAN>
					<![CDATA[ ]]>
				</xsl:if>
				<xsl:if test="./@type != ''">
					<xsl:text><![CDATA[ type=]]></xsl:text>
					<xsl:value-of select="./@type"/>
				</xsl:if>
				<xsl:if test="./@coreset != ''">
					<xsl:text><![CDATA[ coreset=]]></xsl:text>
					<xsl:value-of select="./@coreset"/>
				</xsl:if>
			</SPAN>
			<!-- collapsible content -->
			<UL style="display: block;">
				<LI>
					<DIV class="fnfetext">
						<xsl:apply-templates select="./fedefinition"/>
					</DIV>
				</LI>
			</UL>
		</LI>
	</xsl:template>

	<xsl:template match="related">
		<DIV class="fnrelated">
			<SPAN class="fnrelatedrelation">
				<xsl:value-of select="./@relation"/>
			</SPAN>
			<SPAN class="fnrelatedname">
				<xsl:value-of select="./@frame"/>
			</SPAN>
		</DIV>
	</xsl:template>

	<xsl:template match="framedefinition">
		<!-- non-indent -->
		<DIV class="fnframedefinition">
			<xsl:apply-templates select="./child::node()"/>
		</DIV>
	</xsl:template>

	<xsl:template match="fedefinition">
		<!-- non-indent -->
		<DIV class="fnfedefinition">
			<xsl:apply-templates select="./child::node()"/>
		</DIV>
	</xsl:template>

	<!-- fen|t|ment|em|ex - outside <ex> -->
	<!-- fex|t|ment|gov|x|m|target - within <ex> -->

	<xsl:template match="ex">
		<DIV class="fnex">
			<IMG class="dataimg" src="images/xnet/sample.png"/>
			<xsl:apply-templates select="./child::node()"/>
		</DIV>
	</xsl:template>

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

	<xsl:template match="sentences">
		<!-- collapsible content -->
		<LI>
			<UL style="display: block;">
				<xsl:apply-templates select="./sentence"/>
			</UL>
		</LI>
	</xsl:template>

	<xsl:template match="sentence">
		<!-- indent -->
		<LI class="treeitem treepanel fnsentence">
			<!-- tree handle -->
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png"/>
			</SPAN>
			<!-- pointer -->
			<A class="pointer">
				<xsl:attribute name="href">
					<xsl:value-of select="concat('query?fnsentenceid=',./@sentenceid)"/>
				</xsl:attribute>
				<IMG class="dataimg" src="images/pointer.png"/>
			</A>
			<!-- data image -->
			<IMG class="dataimg" src="images/xnet/sentence.png"/>
			<!-- label -->
			<SPAN class="fnsentencelabel">
				<xsl:text><![CDATA[sentence #]]></xsl:text>
				<xsl:value-of select="./@num"/>
				<xsl:text><![CDATA[ id=]]></xsl:text>
				<xsl:value-of select="./@sentenceid"/>
				<!-- <xsl:text><![CDATA[ anno=]]></xsl:text> -->
				<!-- <xsl:value-of select="./@annosetids" /> -->
			</SPAN>
			<!-- collapsible content -->
			<UL style="display: block;">
				<LI>
					<SPAN class="fnsentencetext">
						<xsl:value-of select="./text()"/>
					</SPAN>
					<UL style="display: block;">
						<xsl:apply-templates select=".//layer"/>
					</UL>
				</LI>
			</UL>
		</LI>
	</xsl:template>

	<xsl:template match="layer">
		<xsl:variable name="senttext">
			<xsl:value-of select="../../text()"/>
		</xsl:variable>
		<LI class="treeitem treepanel fnlayer">
			<SPAN class="fnlayertype">
				<xsl:value-of select="./@type"/>
			</SPAN>
			<UL style="display: block;">
				<xsl:apply-templates select=".//label"/>
			</UL>
		</LI>
	</xsl:template>

	<xsl:template match="label">
		<xsl:variable name="senttext">
			<xsl:value-of select="../../../text()"/>
		</xsl:variable>
		<xsl:variable name="start">
			<xsl:value-of select="./@from + 1"/>
		</xsl:variable>
		<xsl:variable name="len">
			<xsl:value-of select="./@to - ./@from + 1"/>
		</xsl:variable>
		<LI class="treeitem treepanel fnlabel">
			<SPAN class="fnlabelname">
				<xsl:value-of select="./@label"/>
			</SPAN>
			<![CDATA[ ]]>
			<SPAN class="fnlabelvalue">
				<xsl:value-of select="substring($senttext, $start, $len)"/>
			</SPAN>
		</LI>
	</xsl:template>

</xsl:transform>
