<?xml version="1.0" encoding="UTF-8"?>
<!-- PropBank to HTML Client-side 2015/05/15 (C) 2015 Author: Bernard Bou -->

<xsl:transform version="1.0"
               xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:strip-space elements="label"/>

	<xsl:template match="/">
		<DIV id="propbank">
			<xsl:apply-templates select=".//propbank"/>
		</DIV>
	</xsl:template>

	<xsl:template match="propbank">
		<xsl:choose>
			<xsl:when test="count(./roleset)=0">
				<SPAN class="treejunction">
					<IMG class="treepix" src="images/closed.png"/>
				</SPAN>
				<SPAN class="domain">
					<xsl:text>propbank</xsl:text>
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
					<xsl:text>propbank</xsl:text>
				</SPAN>
				<OL style="display: block;">
					<xsl:apply-templates select="./roleset"/>
				</OL>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="roleset">
		<!-- indented -->
		<LI class="treeitem treepanel pbroleset block1">
			<!-- tree handle -->
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png"/>
			</SPAN>
			<!-- data image -->
			<IMG class="dataimg" src="images/propbank/roles.png"/>
			<!-- label -->
			<SPAN class="pbrolesetlabel">
				<SPAN class="pbrolesetname">
					<xsl:value-of select="./@name"/>
				</SPAN>
				<xsl:text>rolesetid=</xsl:text>
				<xsl:value-of select="./@rolesetid"/>
				<xsl:text>num=</xsl:text>
				<xsl:value-of select="./@num"/>
				<xsl:text>head=</xsl:text>
				<xsl:value-of select="./@head"/>
			</SPAN>
			<!-- collapsible content -->
			<OL style="display: block;">
				<!-- non indented -->
				<IMG class="dataimg" src="images/propbank/definition.png"/>
				<SPAN class="pbrolesettext">
					<xsl:value-of select="./text()"/>
				</SPAN>

				<xsl:apply-templates select="./role"/>
				<xsl:apply-templates select="./example"/>
			</OL>
		</LI>
	</xsl:template>

	<xsl:template match="role">
		<!-- indented -->
		<LI class="treeitem treepanel pbrole">
			<!-- data image -->
			<IMG class="dataimg" src="images/propbank/role.png"/>
			<!-- label -->
			<SPAN class="pbrolelabel">
				<!-- <xsl:text>role</xsl:text> -->
				<!-- <xsl:text> #</xsl:text> -->
				<!-- <xsl:value-of select="./@narg" /> -->
				<!-- <xsl:text> </xsl:text> -->
				<SPAN class="pbrolename">
					<xsl:value-of select="./text()"/>
				</SPAN>
				<xsl:if test="./@theta != ''">
					<xsl:text>-</xsl:text>
					<SPAN class="pbthetaname">
						<xsl:value-of select="./@theta"/>
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
				<IMG class="treepix" src="images/open.png"/>
			</SPAN>
			<!-- data image -->
			<IMG class="dataimg" src="images/propbank/sample.png"/>
			<!-- label -->
			<SPAN class="pbexamplelabel">
				<xsl:text>example</xsl:text>
				<xsl:text>id=</xsl:text>
				<xsl:value-of select="./@exampleid"/>
			</SPAN>
			<!-- collapsible content -->
			<OL style="display: block;">
				<!-- non indented -->
				<DIV class="pbexampletext">
					<xsl:value-of select="./text()"/>
				</DIV>

				<xsl:apply-templates select="./rel"/>
				<xsl:apply-templates select="./arg"/>
			</OL>
		</LI>
	</xsl:template>

	<xsl:template match="rel">
		<!-- non indented -->
		<DIV class="pbrel">
			<!-- data image -->
			<IMG class="dataimg" src="images/propbank/relation.png"/>
			<!-- label -->
			<SPAN class="pbrellabel">
				<SPAN class="pbrelname">
					<xsl:value-of select="./text()"/>
				</SPAN>
			</SPAN>
		</DIV>
	</xsl:template>

	<xsl:template match="arg">
		<!-- non indented -->
		<DIV class="pbarg">
			<!-- data image -->
			<IMG class="dataimg" src="images/propbank/role.png"/>
			<!-- label -->
			<SPAN class="pbarglabel">
				<SPAN class="pbargname">
					<xsl:value-of select="./@descr"/>
				</SPAN>
				<xsl:if test="./@theta != ''">
					<xsl:text>-</xsl:text>
					<SPAN class="pbargtheta">
						<xsl:value-of select="./@theta"/>
					</SPAN>
				</xsl:if>
				<xsl:text/>
			</SPAN>
			<SPAN class="pbargtext">
				<xsl:value-of select="./text()"/>
			</SPAN>
		</DIV>
	</xsl:template>

</xsl:transform>
	