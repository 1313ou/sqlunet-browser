<?xml version="1.0" encoding="UTF-8"?><!--
  ~ Copyright (c) 2023. Bernard Bou
  -->

<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" indent="yes" encoding="UTF-8" />
	<xsl:strip-space elements="label" />

	<xsl:template match="/">
		<DIV id="propbank">
			<xsl:apply-templates select="//propbank" />
		</DIV>
	</xsl:template>

	<xsl:template match="propbank">
		<xsl:choose>
			<xsl:when test="count(./roleset)=0">
				<SPAN class="treejunction">
					<IMG class="treepix" src="images/closed.png" />
				</SPAN>
				<!-- data image -->
				<IMG class="dataimg" src="images/propbank/propbank.png" />
				<SPAN class="domain">
					<xsl:text><![CDATA[propbank]]></xsl:text>
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
				<IMG class="dataimg" src="images/propbank/propbank.png" />
				<SPAN class="domain">
					<xsl:text><![CDATA[propbank]]></xsl:text>
				</SPAN>
				<UL style="display: block;">
					<xsl:apply-templates select="./roleset" />
				</UL>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="roleset">
		<!-- indented -->
		<LI class="treeitem treepanel pbroleset block1">
			<!-- tree handle -->
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png" />
			</SPAN>
			<!-- data image -->
			<IMG class="dataimg" src="images/xnet/roleclass.png" />
			<!-- label -->
			<SPAN class="pbrolesetlabel">
				<SPAN class="pbrolesetname">
					<xsl:value-of select="./@name" />
				</SPAN>
				<IMG class="dataimg" src="images/xnet/definition.png" />
				<SPAN class="pbrolesettext">
					<xsl:value-of select="./text()" />
				</SPAN>
				<!--
				<xsl:text><![CDATA[ rolesetid=]]></xsl:text>
				<xsl:value-of select="./@rolesetid"/>
				-->
				<xsl:text><![CDATA[ num=]]></xsl:text>
				<xsl:value-of select="./@num" />
				<xsl:text><![CDATA[ head=]]></xsl:text>
				<xsl:value-of select="./@head" />
			</SPAN>
			<!-- collapsible content -->
			<UL style="display: block;">
				<!-- non indented -->
				<xsl:apply-templates select="./role" />
				<xsl:apply-templates select="./example" />
			</UL>
		</LI>
	</xsl:template>

	<xsl:template match="role">
		<!-- indented -->
		<LI class="treeitem treepanel pbrole">
			<!-- data image -->
			<IMG class="dataimg" src="images/xnet/role.png" />
			<!-- label -->
			<SPAN class="pbrolelabel">
				<!-- <xsl:text><![CDATA[role]]></xsl:text> -->
				<!-- <xsl:text><![CDATA[ #]]></xsl:text> -->
				<!-- <xsl:value-of select="./@argtype" /> -->
				<!-- <![CDATA[ ]]> -->
				<SPAN class="pbrolename">
					<xsl:value-of select="./text()" />
				</SPAN>
				<xsl:if test="./@vnrole != ''">
					<xsl:text><![CDATA[-]]></xsl:text>
					<SPAN class="pbvnrolename">
						<xsl:value-of select="./@vnrole" />
					</SPAN>
				</xsl:if>
			</SPAN>
		</LI>
	</xsl:template>

	<xsl:template match="example">
		<!-- indented -->
		<LI class="treeitem treepanel pbexample">
			<!-- tree handle -->
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png" />
			</SPAN>
			<!-- data image -->
			<IMG class="dataimg" src="images/xnet/sample.png" />
			<!-- label -->
			<SPAN class="pbexamplelabel">
				<xsl:text><![CDATA[example]]></xsl:text>
				<xsl:text><![CDATA[ id=]]></xsl:text>
				<xsl:value-of select="./@exampleid" />
			</SPAN>
			<!-- collapsible content -->
			<UL style="display: block;">
				<LI>
					<DIV class="pbexampletext">
						<xsl:value-of select="./text()" />
					</DIV>
					<xsl:apply-templates select="./rel" />
					<xsl:apply-templates select="./arg" />
				</LI>
			</UL>
		</LI>
	</xsl:template>

	<xsl:template match="rel">
		<!-- non indented -->
		<DIV class="pbrel">
			<!-- data image -->
			<IMG class="dataimg" src="images/xnet/relation.png" />
			<!-- label -->
			<SPAN class="pbrellabel">
				<SPAN class="pbrelname">
					<xsl:value-of select="./text()" />
				</SPAN>
			</SPAN>
		</DIV>
	</xsl:template>

	<xsl:template match="arg">
		<!-- non indented -->
		<DIV class="pbarg">
			<!-- data image -->
			<IMG class="dataimg" src="images/xnet/role.png" />
			<!-- label -->
			<SPAN class="pbarglabel">
				<SPAN class="pbargname">
					<xsl:value-of select="./@descr" />
				</SPAN>
				<xsl:if test="./@vnrole != ''">
					<xsl:text><![CDATA[-]]></xsl:text>
					<SPAN class="pbargvnrole">
						<xsl:value-of select="./@vnrole" />
					</SPAN>
				</xsl:if>
			</SPAN>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<SPAN class="pbargtext">
				<xsl:value-of select="./text()" />
			</SPAN>
		</DIV>
	</xsl:template>

</xsl:transform>
