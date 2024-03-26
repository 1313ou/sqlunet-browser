<?xml version="1.0" encoding="UTF-8"?><!--
  ~ Copyright (c) 2023. Bernard Bou
  -->

<!-- LINK 2 HTML - (C) 2014 Author: Bernard Bou -->
<xsl:transform version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8" doctype-system="about:legacy-compat" />

	<xsl:variable name="imagedir" select='"./images/"' />

	<xsl:template match="/">
		<HTML lang="en">
			<HEAD>
				<TITLE>
					<xsl:value-of select="//concepts/@name" />
				</TITLE>
				<LINK rel="stylesheet" type="text/css" href="style.css" />
			</HEAD>
			<BODY>
				<H1>
					<xsl:value-of select="//concepts/@name" />
				</H1>
				<TABLE class="concepts">
					<xsl:apply-templates select="//concept" />
				</TABLE>
				<BR />
			</BODY>
		</HTML>
	</xsl:template>

	<xsl:template match="concept">
		<TR class="concept" name="concept">
			<xsl:attribute name="id">
				<xsl:value-of select="./@id" />
			</xsl:attribute>
			<TD>
				<TABLE class="expand">

					<TR class="expand">
						<TD class="expand">
							<TABLE class="expand">
								<TR>
									<TD class="headerimage" colspan="1">
										<IMG>
											<xsl:attribute name="src">
												<xsl:value-of select="$imagedir" />
												<xsl:value-of select="./img/@src" />
											</xsl:attribute>
											<xsl:attribute name="alt">
												<xsl:value-of select="./@name" />
											</xsl:attribute>
										</IMG>
									</TD>
									<TD class="headertitle">
										<SPAN class="conceptname">
											<xsl:value-of select="./@name" />
										</SPAN>
									</TD>
									<xsl:if test="./a">
										<TD class="headerlink">
											<xsl:apply-templates select="./a" />
										</TD>
									</xsl:if>
								</TR>
							</TABLE>
						</TD>
					</TR>

					<TR class="expand">
						<TD class="expand">
							<xsl:apply-templates select="./description" />
						</TD>
					</TR>

					<xsl:if test="./items">
						<TR class="expand">
							<TD class="expand">
								<xsl:apply-templates select="./items" />
							</TD>
						</TR>
					</xsl:if>

					<xsl:if test="./examples">
						<TR class="expand">
							<TD class="expand">
								<xsl:apply-templates select="./examples" />
							</TD>
						</TR>
					</xsl:if>

				</TABLE>
			</TD>
		</TR>
	</xsl:template>

	<xsl:template match="description">
		<DIV class="description">
			<xsl:copy-of select="text()" />
		</DIV>
	</xsl:template>

	<xsl:template match="items">
		<DIV class="enumeration">
			<UL class="enumerationlist">
				<xsl:apply-templates select="./item" />
			</UL>
		</DIV>
	</xsl:template>

	<xsl:template match="item">
		<LI class="enumerationitem">
			<xsl:apply-templates select="*|@*|text()" />
		</LI>
	</xsl:template>

	<xsl:template match="examples">
		<DIV class="examples">
			<xsl:if test="./context">
				<DIV class="context">In the context of the<xsl:apply-templates select="./context" />:
				</DIV>
			</xsl:if>
			<TABLE class="exampletable">
				<xsl:apply-templates select="./example" />
			</TABLE>
		</DIV>
	</xsl:template>

	<xsl:template match="example">
		<TR>
			<TD class="example">
				<xsl:copy-of select="./text()" />
			</TD>
		</TR>
	</xsl:template>

	<xsl:template match="name">
		<SPAN class="keyword">
			<xsl:apply-templates />
		</SPAN>
	</xsl:template>

	<xsl:template match="a">
		<A>
			<xsl:attribute name="href">
				<xsl:value-of select="@href" />
			</xsl:attribute>
			<IMG src="images/link.png" />
		</A>
	</xsl:template>

	<xsl:template match="@*">
		<xsl:attribute name="{name()}">
			<xsl:value-of select="." />
		</xsl:attribute>
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of select="normalize-space()" />
	</xsl:template>

</xsl:transform>

