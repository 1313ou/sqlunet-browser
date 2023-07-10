<?xml version="1.0" encoding="UTF-8"?><!-- RELaTIONS 2 HTML - (C) 2020 Author: Bernard Bou -->
<xsl:transform version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8" doctype-system="about:legacy-compat" />

	<xsl:variable name="imagedir" select='"./images/"' />

	<xsl:template match="/">
		<HTML>
			<HEAD>
				<TITLE>
					<xsl:value-of select="'Relations'" />
				</TITLE>
				<LINK rel="stylesheet" type="text/css" href="style.css" />
			</HEAD>
			<BODY>
				<TABLE class="links">
					<xsl:apply-templates select=".//link" />
				</TABLE>
				<BR />
			</BODY>
		</HTML>
	</xsl:template>

	<xsl:template match="link">
		<TR class="link" name="link">
			<xsl:attribute name="id">
				<xsl:value-of select="./@id" />
			</xsl:attribute>
			<TD>
				<TABLE>

					<TR>
						<TD class="linkimage">
							<IMG>
								<xsl:attribute name="src">
									<xsl:copy-of select="$imagedir" />
									<xsl:value-of select="./img/@src" />
								</xsl:attribute>
								<xsl:attribute name="alt">
									<xsl:value-of select="./@name" />
								</xsl:attribute>
							</IMG>
						</TD>
						<TD class="linkheader">
							<SPAN class="name">
								<xsl:value-of select="./@name" />
							</SPAN>
						</TD>
					</TR>

					<TR>
						<TD colspan="2">
							<xsl:apply-templates select="./description" />
						</TD>
					</TR>

					<TR>
						<TD colspan="2">
							<xsl:apply-templates select="./logical" />
						</TD>
					</TR>

					<TR>
						<TD colspan="2">
							<DIV class="features">

								<!-- semantic/lexical -->
								<xsl:choose>
									<xsl:when test="./@type='semantic'">
										<IMG class="featureimage">
											<xsl:attribute name="src">
												<xsl:copy-of select="$imagedir" />
												<xsl:value-of select="'semantic.png'" />
											</xsl:attribute>
											<xsl:attribute name="alt">
												<xsl:value-of select="'semantic'" />
											</xsl:attribute>
											<xsl:attribute name="title">
												<xsl:value-of select="'semantic'" />
											</xsl:attribute>
										</IMG>
									</xsl:when>
									<xsl:when test="./@type='lexical'">
										<IMG class="featureimage">
											<xsl:attribute name="src">
												<xsl:copy-of select="$imagedir" />
												<xsl:value-of select="'lexical.png'" />
											</xsl:attribute>
											<xsl:attribute name="alt">
												<xsl:value-of select="'lexical'" />
											</xsl:attribute>
											<xsl:attribute name="title">
												<xsl:value-of select="'lexical'" />
											</xsl:attribute>
										</IMG>
									</xsl:when>
									<xsl:when test="./@type='both'">
										<IMG class="featureimage">
											<xsl:attribute name="src">
												<xsl:copy-of select="$imagedir" />
												<xsl:value-of select="'semantic.png'" />
											</xsl:attribute>
											<xsl:attribute name="alt">
												<xsl:value-of select="'semantic'" />
											</xsl:attribute>
											<xsl:attribute name="title">
												<xsl:value-of select="'semantic'" />
											</xsl:attribute>
										</IMG>
										<IMG class="featureimage">
											<xsl:attribute name="src">
												<xsl:copy-of select="$imagedir" />
												<xsl:value-of select="'lexical.png'" />
											</xsl:attribute>
											<xsl:attribute name="alt">
												<xsl:value-of select="'lexical'" />
											</xsl:attribute>
											<xsl:attribute name="title">
												<xsl:value-of select="'lexical'" />
											</xsl:attribute>
										</IMG>
									</xsl:when>
								</xsl:choose>

								<!-- reflexive -->
								<xsl:if test="./@reflexive='true'">
									<IMG class="featureimage">
										<xsl:attribute name="src">
											<xsl:copy-of select="$imagedir" />
											<xsl:value-of select="'reflexive.png'" />
										</xsl:attribute>
										<xsl:attribute name="alt">
											<xsl:value-of select="'reflexive'" />
										</xsl:attribute>
										<xsl:attribute name="title">
											<xsl:value-of select="'reflexive'" />
										</xsl:attribute>
									</IMG>
								</xsl:if>

								<!-- pos -->
								<xsl:if test="./@n='true'">
									<IMG class="featureimage">
										<xsl:attribute name="src">
											<xsl:copy-of select="$imagedir" />
											<xsl:value-of select="'pos.n.png'" />
										</xsl:attribute>
										<xsl:attribute name="alt">
											<xsl:value-of select="'noun'" />
										</xsl:attribute>
										<xsl:attribute name="title">
											<xsl:value-of select="'noun'" />
										</xsl:attribute>
									</IMG>
								</xsl:if>
								<xsl:if test="./@v='true'">
									<IMG class="featureimage">
										<xsl:attribute name="src">
											<xsl:copy-of select="$imagedir" />
											<xsl:value-of select="'pos.v.png'" />
										</xsl:attribute>
										<xsl:attribute name="alt">
											<xsl:value-of select="'verb'" />
										</xsl:attribute>
										<xsl:attribute name="title">
											<xsl:value-of select="'verb'" />
										</xsl:attribute>
									</IMG>
								</xsl:if>
								<xsl:if test="./@a='true'">
									<IMG class="featureimage">
										<xsl:attribute name="src">
											<xsl:copy-of select="$imagedir" />
											<xsl:value-of select="'pos.a.png'" />
										</xsl:attribute>
										<xsl:attribute name="alt">
											<xsl:value-of select="'adj'" />
										</xsl:attribute>
										<xsl:attribute name="title">
											<xsl:value-of select="'adjective'" />
										</xsl:attribute>
									</IMG>
								</xsl:if>
								<xsl:if test="./@r='true'">
									<IMG class="featureimage">
										<xsl:attribute name="src">
											<xsl:copy-of select="$imagedir" />
											<xsl:value-of select="'pos.r.png'" />
										</xsl:attribute>
										<xsl:attribute name="alt">
											<xsl:value-of select="'adv'" />
										</xsl:attribute>
										<xsl:attribute name="title">
											<xsl:value-of select="'adverb'" />
										</xsl:attribute>
									</IMG>
								</xsl:if>
								<xsl:if test="./@s='true'">
									<IMG class="featureimage">
										<xsl:attribute name="src">
											<xsl:copy-of select="$imagedir" />
											<xsl:value-of select="'pos.s.png'" />
										</xsl:attribute>
										<xsl:attribute name="alt">
											<xsl:value-of select="'satellite'" />
										</xsl:attribute>
										<xsl:attribute name="title">
											<xsl:value-of select="'satellite adj'" />
										</xsl:attribute>
									</IMG>
								</xsl:if>

							</DIV>
						</TD>
					</TR>

					<TR>
						<TD colspan="2">
							<xsl:apply-templates select="./examples" />
						</TD>
					</TR>

				</TABLE>
			</TD>
		</TR>
	</xsl:template>

	<xsl:template match="description">
		<DIV class="description">
			<xsl:copy-of select="./span" />
		</DIV>
	</xsl:template>

	<xsl:template match="logical">
		<DIV class="logical">
			<xsl:apply-templates select="./def" />
			<xsl:apply-templates select="./cond" />
		</DIV>
	</xsl:template>

	<xsl:template match="def">
		<DIV class="def">
			<xsl:copy-of select="./span" />
			<xsl:text>iff</xsl:text>
		</DIV>
	</xsl:template>

	<xsl:template match="cond">
		<DIV class="cond">
			<xsl:text>→</xsl:text>
			<xsl:copy-of select="./span" />
		</DIV>
	</xsl:template>

	<xsl:template match="examples">
		<DIV class="examples">
			<TABLE class="exampletable">
				<xsl:apply-templates select="./example" />
			</TABLE>
		</DIV>
	</xsl:template>

	<xsl:template match="example">
		<TR>
			<TD class="from">
				<xsl:value-of select="./from/text()" />
			</TD>
			<TD class="rel">
				<IMG>
					<xsl:attribute name="src">
						<xsl:copy-of select="$imagedir" />
						<xsl:choose>
							<xsl:when test="./img">
								<xsl:value-of select="./img/@src" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="../../img/@src" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<xsl:attribute name="alt">
						<xsl:value-of select="../../@name" />
					</xsl:attribute>
				</IMG>
			</TD>
			<TD class="to">
				<xsl:value-of select="./to/text()" />
			</TD>
			<xsl:apply-templates select="./gloss" />
		</TR>
	</xsl:template>

	<xsl:template match="gloss">
		<TD class="gloss">
			<xsl:value-of select="text()" />
		</TD>
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

