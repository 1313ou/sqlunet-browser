<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               version="1.0"
               xmlns:pb="http://org.sqlunet/pb">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:strip-space elements="label"/>

	<xsl:template match="/">
		<DIV id="propbank">
			<xsl:apply-templates select="//pb:propbank"/>
		</DIV>
	</xsl:template>

	<xsl:template match="pb:propbank">
		<xsl:choose>
			<xsl:when test="count(./pb:roleset)=0">
				<SPAN class="treejunction">
					<IMG class="treepix" src="images/closed.png"/>
				</SPAN>
				<!-- data image -->
				<IMG class="dataimg" src="images/xnet/propbank.png"/>
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
				<!-- data image -->
				<IMG class="dataimg" src="images/xnet/propbank.png"/>
				<SPAN class="domain">
					<xsl:text>propbank</xsl:text>
				</SPAN>
				<UL style="display: block;">
					<xsl:apply-templates select="./pb:roleset"/>
				</UL>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="pb:roleset">
		<!-- indented -->
		<LI class="treeitem treepanel pbroleset block1">
			<!-- tree handle -->
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png"/>
			</SPAN>
			<!-- data image -->
			<IMG class="dataimg" src="images/xnet/roleclass.png"/>
			<!-- label -->
			<SPAN class="pbrolesetlabel">
				<SPAN class="pbrolesetname">
					<xsl:value-of select="./@name"/>
				</SPAN>
				<IMG class="dataimg" src="images/xnet/definition.png"/>
				<SPAN class="pbrolesettext">
					<xsl:value-of select="./text()"/>
				</SPAN>
				<xsl:text> rolesetid=</xsl:text>
				<xsl:value-of select="./@rolesetid"/>
				<xsl:text> num=</xsl:text>
				<xsl:value-of select="./@num"/>
				<xsl:text> head=</xsl:text>
				<xsl:value-of select="./@head"/>
			</SPAN>
			<!-- collapsible content -->
			<UL style="display: block;">
				<!-- non indented -->
				<xsl:apply-templates select="./pb:role"/>
				<xsl:apply-templates select="./pb:example"/>
			</UL>
		</LI>
	</xsl:template>

	<xsl:template match="pb:role">
		<!-- indented -->
		<LI class="treeitem treepanel pbrole">
			<!-- data image -->
			<IMG class="dataimg" src="images/xnet/role.png"/>
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

	<xsl:template match="pb:example">
		<!-- indented -->
		<LI class="treeitem treepanel pbexample">
			<!-- tree handle -->
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png"/>
			</SPAN>
			<!-- data image -->
			<IMG class="dataimg" src="images/xnet/sample.png"/>
			<!-- label -->
			<SPAN class="pbexamplelabel">
				<xsl:text>example</xsl:text>
				<xsl:text> id=</xsl:text>
				<xsl:value-of select="./@exampleid"/>
			</SPAN>
			<!-- collapsible content -->
			<UL style="display: block;">
				<LI>
					<DIV class="pbexampletext">
						<xsl:value-of select="./text()"/>
					</DIV>
					<xsl:apply-templates select="./pb:rel"/>
					<xsl:apply-templates select="./pb:arg"/>
				</LI>
			</UL>
		</LI>
	</xsl:template>

	<xsl:template match="pb:rel">
		<!-- non indented -->
		<DIV class="pbrel">
			<!-- data image -->
			<IMG class="dataimg" src="images/xnet/relation.png"/>
			<!-- label -->
			<SPAN class="pbrellabel">
				<SPAN class="pbrelname">
					<xsl:value-of select="./text()"/>
				</SPAN>
			</SPAN>
		</DIV>
	</xsl:template>

	<xsl:template match="pb:arg">
		<!-- non indented -->
		<DIV class="pbarg">
			<!-- data image -->
			<IMG class="dataimg" src="images/xnet/role.png"/>
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
	
