<?xml version="1.0" encoding="UTF-8"?>
<!-- BNC to HTML Client-side 2015/05/15 (C) 2015 Author: Bernard Bou -->

<xsl:transform version="1.0"
               xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:strip-space elements="label"/>

	<xsl:template match="/">
		<DIV id="bnc">
			<xsl:apply-templates select="./bnc"/>
		</DIV>
	</xsl:template>

	<xsl:template match="bnc">
		<xsl:choose>
			<xsl:when test="count(./bncdata)=0">
				<SPAN class="treejunction">
					<IMG class="treepix" src="images/closed.png"/>
				</SPAN>
				<SPAN class="domain">
					<xsl:text>bnc</xsl:text>
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
					<xsl:text>bnc</xsl:text>
				</SPAN>
				<OL style="display: block;">
					<xsl:apply-templates select="./bncdata"/>
				</OL>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="bncdata">
		<!-- indent -->
		<LI class="treeitem treepanel bncdata block3">
			<SPAN class="bncpos">
				<xsl:value-of select="./@pos"/>
			</SPAN>
			<SPAN class="bncid">
				<xsl:value-of select="./@id"/>
			</SPAN>

			<DIV class="bncgen">
				<DIV class="bncsection">general</DIV>
				<DIV class="bncfreq">
					frequency
					<xsl:apply-templates select="./freq"/>
					<SPAN class="bncunit">(per million)</SPAN>
				</DIV>
				<DIV class="bncrange">
					range
					<xsl:apply-templates select="./range"/>
				</DIV>
				<DIV class="bncdisp">
					dispersion
					<xsl:apply-templates select="./disp"/>
				</DIV>
			</DIV>

			<xsl:if test="count(./spokenfreq)>0 or count(./writtenfreq)>0">
				<DIV class="bncspwr">
					<TABLE class="bnctable">
						<TR>
							<TH class="bnctablecell">
								<DIV class="bncsection">spoken</DIV>
							</TH>
							<TH class="bnctablecell">
								<DIV class="bncsection">written</DIV>
							</TH>
						</TR>
						<TR>
							<TD class="bnctablecell">
								<DIV class="bncfreq">
									frequency
									<xsl:apply-templates select="./spokenfreq"/>
									<SPAN class="bncunit">(per million)</SPAN>
								</DIV>
								<DIV class="bncrange">
									range
									<xsl:apply-templates select="./spokenrange"/>
								</DIV>
								<DIV class="bncdisp">
									dispersion
									<xsl:apply-templates select="./spokendisp"/>
								</DIV>
							</TD>
							<TD class="bnctablecell">
								<DIV class="bncfreq">
									frequency
									<xsl:apply-templates select="./writtenfreq"/>
									<SPAN class="bncunit">(per million)</SPAN>
								</DIV>
								<DIV class="bncrange">
									range
									<xsl:apply-templates select="./writtenrange"/>
								</DIV>
								<DIV class="bncdisp">
									dispersion
									<xsl:apply-templates select="./writtendisp"/>
								</DIV>
							</TD>
						</TR>
					</TABLE>
				</DIV>
			</xsl:if>

			<xsl:if test="count(./convfreq)>0 or count(./taskfreq)>0">
				<DIV class="bncconvtask">
					<TABLE class="bnctable">
						<TR>
							<TH class="bnctablecell">
								<DIV class="bncsection">conversation-oriented</DIV>
							</TH>
							<TH class="bnctablecell">
								<DIV class="bncsection">task-oriented</DIV>
							</TH>
						</TR>
						<TR>
							<TD class="bnctablecell">
								<DIV class="bncfreq">
									frequency
									<xsl:apply-templates select="./convfreq"/>
									<SPAN class="bncunit">(per million)</SPAN>
								</DIV>
								<DIV class="bncrange">
									range
									<xsl:apply-templates select="./convrange"/>
								</DIV>
								<DIV class="bncdisp">
									dispersion
									<xsl:apply-templates select="./convdisp"/>
								</DIV>
							</TD>
							<TD class="bnctablecell">
								<DIV class="bncfreq">
									frequency
									<xsl:apply-templates select="./taskfreq"/>
									<SPAN class="bncunit">(per million)</SPAN>
								</DIV>
								<DIV class="bncrange">
									range
									<xsl:apply-templates select="./taskrange"/>
								</DIV>
								<DIV class="bncdisp">
									dispersion
									<xsl:apply-templates select="./taskdisp"/>
								</DIV>
							</TD>
						</TR>
					</TABLE>
				</DIV>
			</xsl:if>

			<xsl:if test="count(./imagfreq)>0 or count(./inffreq)>0">
				<DIV class="bncimaginf">
					<TABLE class="bnctable">
						<TR>
							<TH class="bnctablecell">
								<DIV class="bncsection">imaginative</DIV>
							</TH>
							<TH class="bnctablecell">
								<DIV class="bncsection">informative</DIV>
							</TH>
						</TR>
						<TR>
							<TD class="bnctablecell">
								<DIV class="bncfreq">
									frequency
									<xsl:apply-templates select="./imagfreq"/>
									<SPAN class="bncunit">(per million)</SPAN>
								</DIV>
								<DIV class="bncrange">
									range
									<xsl:apply-templates select="./imagrange"/>
								</DIV>
								<DIV class="bncdisp">
									dispersion
									<xsl:apply-templates select="./imagdisp"/>
								</DIV>
							</TD>
							<TD class="bnctablecell">
								<DIV class="bncfreq">
									frequency
									<xsl:apply-templates select="./inffreq"/>
									<SPAN class="bncunit">(per million)</SPAN>
								</DIV>
								<DIV class="bncrange">
									range
									<xsl:apply-templates select="./infrange"/>
								</DIV>
								<DIV class="bncdisp">
									dispersion
									<xsl:apply-templates select="./infdisp"/>
								</DIV>
							</TD>
						</TR>
					</TABLE>
				</DIV>
			</xsl:if>

		</LI>
	</xsl:template>
</xsl:transform>
