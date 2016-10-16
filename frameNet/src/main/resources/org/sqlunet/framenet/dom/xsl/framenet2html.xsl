<?xml version="1.0" encoding="UTF-8"?>
<!-- FrameNet to HTML Client-side 2015/05/15 (C) 2015 Author: Bernard Bou -->

<xsl:transform version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" encoding="UTF-8" />
	<xsl:strip-space elements="label" />

	<xsl:template match="/">
		<DIV id="framenet">
			<xsl:apply-templates select=".//framenet" />
		</DIV>
	</xsl:template>

	<xsl:template match="framenet">
		<xsl:choose>
			<xsl:when test="count(./lexunit)=0 and count(./frame)=0">
				<SPAN class="treejunction">
					<IMG class="treepix" src="images/closed.png" />
				</SPAN>
				<SPAN class="domain">
					<xsl:text>framenet</xsl:text>
				</SPAN>
				<SPAN class="error" id="default">
					<xsl:text>∅ data</xsl:text>
				</SPAN>
			</xsl:when>
			<xsl:otherwise>
				<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
					<IMG class="treepix" src="images/open.png" />
				</SPAN>
				<SPAN class="domain">
					<xsl:text>framenet</xsl:text>
				</SPAN>
				<OL style="display: block;">
					<xsl:apply-templates select="./frame" />
					<xsl:apply-templates select="./lexunit" />
				</OL>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="lexunit">
		<!-- indent -->
		<LI class="treeitem treepanel fnlexunit block1">
			<!-- tree handle -->
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png" />
			</SPAN>
			<!-- data image -->
			<IMG class="dataimg" src="images/framenet/lexunit.png" />
			<!-- label -->
			<SPAN class="fnlexunitlabel">
				<SPAN class="fnlexunitname">
					<xsl:value-of select="./@name" />
				</SPAN>
				<SPAN class="fnlexunitdefinition">
					<xsl:value-of select="./text()" />
				</SPAN>
			</SPAN>
			<!-- collapsible content -->
			<OL style="display: block;">
				<xsl:apply-templates select="./frame" />
				<xsl:apply-templates select="./sentences" />
			</OL>
		</LI>
	</xsl:template>

	<xsl:template match="frame">
		<!-- indent -->
		<LI class="treeitem treepanel fnframe block2">
			<!-- tree handle -->
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png" />
			</SPAN>
			<!-- data image -->
			<IMG class="dataimg" src="images/framenet/roles.png" />
			<!-- label -->
			<SPAN class="fnframelabel">
				<SPAN class="fnframename">
					<xsl:value-of select="./@name" />
				</SPAN>
			</SPAN>
			<!-- collapsible content -->
			<OL style="display: block;">
				<SPAN class="fnframetext">
					<xsl:apply-templates select="./framedefinition" />
				</SPAN>
				<OL style="display: block;">
					<xsl:apply-templates select="./related" />
					<xsl:apply-templates select="./lexunit" />
					<xsl:apply-templates select="./fe" />
				</OL>
			</OL>
		</LI>
	</xsl:template>

	<xsl:template match="fe">
		<!-- indent -->
		<LI class="treeitem treepanel fnfe block3">
			<!-- tree handle -->
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png" />
			</SPAN>
			<xsl:choose>
				<xsl:when test="@coreset!=''">
					<!-- data image -->
					<IMG class="dataimg" src="images/framenet/role.png" />
					<!-- label -->
					<SPAN class="fnfelabel">
						<SPAN class="fnfename">
							<xsl:value-of select="./@name" />
						</SPAN>
						<xsl:text> </xsl:text>
						<xsl:if test="./@semtype != ''">
							<SPAN class="fnfesemtype">
								<xsl:value-of select="./@semtype" />
							</SPAN>
							<xsl:text> </xsl:text>
						</xsl:if>
						<xsl:if test="./@type != ''">
							<xsl:text> type=</xsl:text>
							<xsl:value-of select="./@type" />
						</xsl:if>
						<xsl:if test="./@coreset != ''">
							<xsl:text> coreset=</xsl:text>
							<xsl:value-of select="./@coreset" />
						</xsl:if>
					</SPAN>
				</xsl:when>
				<xsl:otherwise>
					<!-- data image -->
					<IMG class="dataimg" src="images/framenet/role2.png" />
					<!-- label -->
					<SPAN class="fnfelabel">
						<SPAN class="fnfename">
							<xsl:value-of select="./@name" />
						</SPAN>
						<xsl:text> </xsl:text>
						<xsl:if test="./@semtype != ''">
							<SPAN class="fnfesemtype">
								<xsl:value-of select="./@semtype" />
							</SPAN>
							<xsl:text> </xsl:text>
						</xsl:if>
						<xsl:if test="./@type != ''">
							<xsl:text> type=</xsl:text>
							<xsl:value-of select="./@type" />
						</xsl:if>
					</SPAN>
				</xsl:otherwise>
			</xsl:choose>
			<!-- collapsible content -->
			<OL style="display: block;">
				<!-- non-indented -->
				<SPAN class="fnfetext">
					<xsl:apply-templates select="./fedefinition" />
				</SPAN>
			</OL>
		</LI>
	</xsl:template>

	<xsl:template match="related">
		<DIV class="fnrelated">
			<SPAN class="fnrelatedrelation">
				<xsl:value-of select="./@relation" />
			</SPAN>
			<SPAN class="fnrelatedname">
				<xsl:value-of select="./@frame" />
			</SPAN>
		</DIV>
	</xsl:template>

	<xsl:template match="sentences">
		<!-- collapsible content -->
		<OL style="display: block;">
			<xsl:apply-templates select="./sentence" />
		</OL>
	</xsl:template>

	<xsl:template match="sentence">
		<!-- indent -->
		<LI class="treeitem treepanel fnsentence">
			<!-- tree handle -->
			<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
				<IMG class="treepix" src="images/open.png" />
			</SPAN>
			<!-- data image -->
			<IMG class="dataimg" src="images/framenet/sample.png" />
			<!-- label -->
			<SPAN class="fnsentencelabel">
				<xsl:text>sentence #</xsl:text>
				<xsl:value-of select="./@num" />
				<xsl:text> id=</xsl:text>
				<xsl:value-of select="./@sentenceid" />
				<!-- <xsl:text> anno=</xsl:text> -->
				<!-- <xsl:value-of select="./@annosetids" /> -->
			</SPAN>
			<!-- collapsible content -->
			<OL style="display: block;">
				<SPAN class="fnsentencetext">
					<xsl:value-of select="./text()" />
				</SPAN>
			</OL>
		</LI>
	</xsl:template>

	<xsl:template match="framedefinition">
		<!-- non-indent -->
		<SPAN class="fnframedefinition">
			<xsl:apply-templates select="./child::node()" />
		</SPAN>
	</xsl:template>

	<xsl:template match="fedefinition">
		<!-- non-indent -->
		<SPAN class="fnfedefinition">
			<xsl:apply-templates select="./child::node()" />
		</SPAN>
	</xsl:template>

	<!-- fen|t|ment|em|ex - outside <ex> -->
	<!-- fex|t|ment|gov|x|m|target - within <ex> -->

	<xsl:template match="ex">
		<DIV class="fnex">
			<IMG class="dataimg" src="images/framenet/sample.png" />
			<xsl:apply-templates select="./child::node()" />
		</DIV>
	</xsl:template>

	<xsl:template match="t">
		<SPAN class="fntarget">
			<xsl:value-of select="./text()" />
		</SPAN>
	</xsl:template>

	<xsl:template match="fen">
		<SPAN class="fnfen">
			<xsl:value-of select="./text()" />
		</SPAN>
	</xsl:template>

	<xsl:template match="fex">
		<SPAN class="fnfex">
			<xsl:value-of select="./text()" />
		</SPAN>
	</xsl:template>

	<xsl:template match="ment">
		<SPAN class="fnment">
			<xsl:value-of select="./text()" />
		</SPAN>
	</xsl:template>

	<xsl:template match="gov">
		<SPAN class="fngov">
			<xsl:value-of select="./text()" />
		</SPAN>
	</xsl:template>

	<xsl:template match="x">
		<SPAN class="fnx">
			<xsl:value-of select="./text()" />
		</SPAN>
	</xsl:template>

	<xsl:template match="m">
		<SPAN class="fnm">
			<xsl:value-of select="./text()" />
		</SPAN>
	</xsl:template>

</xsl:transform>
	