<?xml version="1.0" encoding="UTF-8"?>
<!-- Propbank to HTML Client-side 2015/05/15 (C) 2015 Author: Bernard Bou -->

<xsl:transform version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" encoding="UTF-8" />
	<xsl:strip-space elements="label" />

	<xsl:template match="/">
		<DIV id="propbank">
			<xsl:apply-templates select=".//propbank" />
		</DIV>
	</xsl:template>

	<xsl:template match="propbank">
		<xsl:choose>
			<xsl:when test="count(./roleset)=0">
				<SPAN class="treejunction">
					<IMG class="treepix" src="images/closed.png" />
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
					<IMG class="treepix" src="images/open.png" />
				</SPAN>
				<SPAN class="domain">
					<xsl:text>propbank</xsl:text>
				</SPAN>
				<OL style="display: block;">
					<xsl:apply-templates select="./roleset" />
				</OL>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="roleset">
		<!-- indented -->
		<LI class="treeitem treepanel pbroleset block1">
			<!-- link -->
			<xsl:variable name="ref">
				<xsl:value-of select="./@rolesetid" />
			</xsl:variable>
			<A class="pointer">
				<xsl:attribute name="href">
          			<xsl:value-of select="concat('query?pbrolesetid=',$ref)" />
        		</xsl:attribute>
				<IMG class="dataimg" src="images/pointer.png" />
<!-- 				<xsl:value-of select="$ref" /> -->
			</A>
			<!-- data image -->
			<IMG class="dataimg" src="images/propbank/roles.png" />
			<!-- label -->
			<SPAN class="pbrolesetlabel">
				<SPAN class="pbrolesetname">
					<xsl:value-of select="./@name" />
				</SPAN>
				<!--
				<xsl:text> rolesetid=</xsl:text>
				<xsl:value-of select="./@rolesetid" />
				<xsl:text> num=</xsl:text>
				<xsl:value-of select="./@num" />
				<xsl:text> head=</xsl:text>
				<xsl:value-of select="./@head" />
				-->
			</SPAN>
			<SPAN class="definition">
				<xsl:value-of select="./text()" />
			</SPAN>
		</LI>
	</xsl:template>

</xsl:transform>
	