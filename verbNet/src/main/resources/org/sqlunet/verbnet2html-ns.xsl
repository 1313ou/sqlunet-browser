<?xml version="1.0" encoding="UTF-8"?><!--
  ~ Copyright (c) 2023. Bernard Bou
  -->

<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:vn="http://org.sqlunet/vn">
	<xsl:output method="xml" indent="yes" encoding="UTF-8" />
	<xsl:strip-space elements="label" />

	<xsl:template match="/">
		<DIV id="verbnet">
			<xsl:apply-templates select="//vn:verbnet" />
		</DIV>
	</xsl:template>

	<xsl:template match="vn:verbnet">
		<xsl:choose>
			<!-- no role or frame -->
			<xsl:when test="count(.//vn:themrole)=0 and count(.//vn:frame)=0">
				<SPAN class="treejunction">
					<IMG class="treepix" src="images/closed.png" />
				</SPAN>
				<!-- data image -->
				<IMG class="dataimg" src="images/verbnet/verbnet.png" />
				<SPAN class="domain">
					<xsl:text><![CDATA[verbnet]]></xsl:text>
				</SPAN>
				<SPAN class="error" id="default">
					<xsl:text><![CDATA[∅ data]]></xsl:text>
				</SPAN>
			</xsl:when>
			<!-- one or more role or frame -->
			<xsl:otherwise>
				<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
					<IMG class="treepix" src="images/open.png" />
				</SPAN>
				<!-- data image -->
				<IMG class="dataimg" src="images/verbnet/verbnet.png" />
				<SPAN class="domain">
					<xsl:text><![CDATA[verbnet]]></xsl:text>
				</SPAN>
				<UL style="display: block;">
					<xsl:apply-templates select="./vn:vnclass" />
				</UL>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="vn:vnclass">
		<!-- indented -->
		<LI class="treeitem treepanel vnclass block1">
			<!-- tree handle -->
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png" />
			</SPAN>
			<!-- data image -->
			<IMG class="dataimg" src="images/xnet/roleclass.png" />
			<!-- label -->
			<SPAN class="vnclasslabel">
				<SPAN class="vnclassname">
					<xsl:value-of select="./@name" />
				</SPAN>
			</SPAN>
			<!-- collapsible content -->
			<UL style="display: block;">
				<xsl:apply-templates select="./vn:themroles" />
				<xsl:apply-templates select="./vn:frames" />
			</UL>
		</LI>
	</xsl:template>

	<xsl:template match="vn:themroles">
		<!-- indented -->
		<LI class="treeitem treepanel vnthemroleset">
			<!-- tree handle -->
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png" />
			</SPAN>
			<!-- data image -->
			<IMG class="dataimg" src="images/xnet/roles.png" />
			<!-- label -->
			<SPAN class="vnthemrolesetlabel">
				<xsl:value-of select="count(./vn:themrole)" />
				<xsl:text><![CDATA[ thematic role(s)]]></xsl:text>
			</SPAN>
			<!-- collapsible content -->
			<UL style="display: block;">
				<xsl:apply-templates select="./vn:themrole" />
			</UL>
		</LI>
	</xsl:template>

	<xsl:template match="vn:themrole">
		<!-- indented -->
		<LI class="treeitem treepanel vnthemrole">
			<!-- data image -->
			<IMG class="dataimg" src="images/xnet/role.png" />
			<!-- label -->
			<SPAN class="vnthemrolelabel">
				<SPAN class="vnthemrolename">
					<xsl:value-of select="./@type" />
				</SPAN>
				<xsl:if test="./@synset='true'">
					<IMG class="dataimg" src="images/verbnet/synsetspecific.png" />
				</xsl:if>
				<xsl:apply-templates select="./vn:restrs" />
			</SPAN>
		</LI>
	</xsl:template>

	<xsl:template match="vn:frames">
		<!-- indented -->
		<LI class="treeitem treepanel vnframeset">
			<!-- tree handle -->
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png" />
			</SPAN>
			<!-- data image -->
			<IMG class="dataimg" src="images/verbnet/vnframe.png" />
			<!-- label -->
			<SPAN class="vnframesetlabel">
				<xsl:value-of select="count(./vn:frame)" />
				<xsl:text><![CDATA[ frame(s)]]></xsl:text>
			</SPAN>
			<!-- collapsible content -->
			<UL style="display: block;">
				<xsl:apply-templates select="./vn:frame" />
			</UL>
		</LI>
	</xsl:template>

	<xsl:template match="vn:frame">
		<!-- indented -->
		<LI class="treeitem treepanel vnframe block3">
			<!-- tree handle -->
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png" />
			</SPAN>
			<!-- data image -->
			<IMG class="dataimg" src="images/verbnet/vnframe.png" />
			<!-- label -->
			<SPAN class="vnframelabel">
				<xsl:apply-templates select="./vn:description" />
				<!-- <xsl:text><![CDATA[#]]></xsl:text> -->
				<!-- <xsl:value-of select="./@id" /> -->
				<xsl:if test="./@synset='true'">
					<IMG class="dataimg" src="images/verbnet/synsetspecific.png" />
				</xsl:if>
			</SPAN>
			<!-- collapsible content -->
			<UL>
				<!-- indented -->
				<LI class="vnframesynsem">
					<!-- tree handle -->
					<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
						<IMG class="treepix" src="images/open.png" />
					</SPAN>
					<!-- data image -->
					<IMG class="dataimg" src="images/verbnet/syntax.png" />
					<!-- label -->
					<SPAN class="vnframesynsemlabel">
						<xsl:text><![CDATA[syntax]]></xsl:text>
					</SPAN>
					<!-- collapsible content -->
					<UL style="display: block;">
						<xsl:apply-templates select="./vn:syntax" />
					</UL>
				</LI>
				<!-- indented -->
				<LI class="vnframesynsem">
					<!-- tree handle -->
					<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
						<IMG class="treepix" src="images/open.png" />
					</SPAN>
					<!-- data image -->
					<IMG class="dataimg" src="images/verbnet/semantics.png" />
					<!-- label -->
					<SPAN class="vnframesynsemlabel">
						<xsl:text><![CDATA[semantics]]></xsl:text>
					</SPAN>
					<!-- collapsible content -->
					<UL style="display: block;">
						<xsl:apply-templates select="./vn:semantics" />
					</UL>
				</LI>
				<xsl:apply-templates select="./vn:examples" />
			</UL>
		</LI>
	</xsl:template>

	<xsl:template match="vn:description">
		<!-- not indented -->
		<!-- label -->
		<SPAN class="vnframename">
			<xsl:value-of select="./@primary" />
		</SPAN>
		<xsl:text><![CDATA[/]]></xsl:text>
		<SPAN class="vnframename2">
			<xsl:value-of select="./@secondary" />
		</SPAN>
	</xsl:template>

	<xsl:template match="vn:syntax">
		<!-- indented -->
		<LI class="treeitem treepanel vnsyntax">
			<xsl:apply-templates select="./vn:synitem" />
		</LI>
	</xsl:template>

	<xsl:template match="vn:semantics">
		<!-- indented -->
		<LI class="treeitem treepanel vnsemantics">
			<xsl:apply-templates select="./vn:semitem" />
		</LI>
	</xsl:template>

	<xsl:template match="vn:synitem">
		<!-- not indented -->
		<!-- data image -->
		<DIV>
			<IMG class="dataimg" src="images/verbnet/syntax.png" />
			<xsl:apply-templates select="vn:cat" />
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:apply-templates select="vn:value" />
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:apply-templates select="vn:restrs" />
		</DIV>
	</xsl:template>

	<xsl:template match="vn:cat">
		<SPAN class="vncat">
			<xsl:value-of select="./@value" />
		</SPAN>
	</xsl:template>

	<xsl:template match="vn:value">
		<SPAN class="vnthemrolename">
			<xsl:value-of select="./@value" />
		</SPAN>
	</xsl:template>

	<xsl:template match="vn:restrs">
		<IMG class="dataimg" src="images/verbnet/restr.png" />
		<SPAN class="vnrestrs">
			<xsl:value-of select="./@value" />
		</SPAN>
	</xsl:template>

	<xsl:template match="vn:semitem">
		<DIV>
			<IMG class="dataimg" src="images/verbnet/semantics.png" />
			<xsl:apply-templates select="rel" />
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:text><![CDATA[(]]></xsl:text>
			<xsl:apply-templates select="arg" />
			<xsl:text><![CDATA[)]]></xsl:text>
		</DIV>
	</xsl:template>

	<xsl:template match="vn:rel">
		<SPAN class="vnrel">
			<xsl:value-of select="./@value" />
		</SPAN>
	</xsl:template>

	<xsl:template match="vn:arg">
		<!-- <SPAN class="vnarg"> -->
		<!-- <xsl:value-of select="./@argtype" /> -->
		<!-- </SPAN> -->
		<!-- <xsl:text><![CDATA[ ]]></xsl:text> -->
		<xsl:if test="./@argtype &gt; 1">
			<xsl:text><![CDATA[, ]]></xsl:text>
		</xsl:if>
		<xsl:element name="span">
			<xsl:attribute name="class">
				<xsl:choose>
					<xsl:when test="starts-with(./@value,'event:')">vnargevent</xsl:when>
					<xsl:when test="starts-with(./@value,'specific:')">vnargspecific</xsl:when>
					<xsl:when test="starts-with(./@value,'constant:')">vnargconst</xsl:when>
					<xsl:otherwise>vnargval</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:value-of select="./@value" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="vn:examples">
		<xsl:apply-templates select="./vn:example" />
	</xsl:template>

	<xsl:template match="vn:example">
		<LI class="treeitem treepanel vnexample">
			<IMG class="dataimg" src="images/xnet/sample.png" />
			<SPAN class="vnexampletext">
				<xsl:apply-templates select="./text()" />
			</SPAN>
		</LI>
	</xsl:template>

	<xsl:template match="vn:text">
		<DIV>
			<IMG class="dataimg" src="images/xnet/definition.png" />
			<SPAN class="vntext">
				<xsl:apply-templates select="./text()" />
			</SPAN>
		</DIV>
	</xsl:template>

</xsl:transform>
