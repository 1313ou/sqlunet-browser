<?xml version="1.0" encoding="UTF-8"?><!--
  ~ Copyright (c) 2023. Bernard Bou
  -->

<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="UTF-8" indent="yes" method="xml" />
	<xsl:strip-space elements="label" />

	<xsl:template match="/">
		<DIV id="bnc">
			<xsl:apply-templates select="./bnc" />
		</DIV>
	</xsl:template>

	<xsl:template match="bnc">
		<xsl:choose>
			<xsl:when test="count(./bncdata)=0">
				<SPAN class="treejunction">
					<IMG class="treepix" src="images/closed.png" />
				</SPAN>
				<!-- data image -->
				<IMG class="dataimg" src="images/xnet/bnc.png" />
				<SPAN class="domain">
					<xsl:text><![CDATA[bnc]]></xsl:text>
				</SPAN>
				<SPAN class="error" id="default">
					<xsl:text><![CDATA[∅ data]]></xsl:text>
				</SPAN>
			</xsl:when>
			<xsl:otherwise>
				<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
					<IMG class="treepix" src="images/open.png" />
				</SPAN>
				<IMG class="dataimg" src="images/xnet/bnc.png" />
				<SPAN class="domain">
					<xsl:text><![CDATA[bnc]]></xsl:text>
				</SPAN>
				<OL style="display: block;">
					<xsl:apply-templates select="./bncdata" />
				</OL>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="bncdata">
		<!-- indent -->
		<LI class="treeitem treepanel bncdata block3">
			<IMG class="dataimg" src="images/xnet/pos.png" />
			<SPAN class="bncpos">
				<xsl:value-of select="./@pos" />
			</SPAN>

			<DIV class="bncgen">
				<DIV class="bncsection">
					<xsl:text><![CDATA[general]]></xsl:text>
				</DIV>
				<DIV class="bncfreq">
					frequency
					<xsl:apply-templates select="./freq" />
					<SPAN class="bncunit">
						<xsl:text><![CDATA[ (per million)]]></xsl:text>
					</SPAN>
				</DIV>
				<DIV class="bncrange">
					range
					<xsl:apply-templates select="./range" />
				</DIV>
				<DIV class="bncdisp">
					dispersion
					<xsl:apply-templates select="./disp" />
				</DIV>
			</DIV>

			<xsl:if test="count(./spokenfreq)>0 or count(./writtenfreq)>0">
				<DIV class="bncspwr">
					<IMG class="dataimg" src="images/bnc/spwr.png" />
					<TABLE class="bnctable">
						<TR>
							<TH class="bnctablecell">
								<DIV class="bncsection">
									<xsl:text><![CDATA[spoken]]></xsl:text>
								</DIV>
							</TH>
							<TH class="bnctablecell">
								<DIV class="bncsection">
									<xsl:text><![CDATA[written]]></xsl:text>
								</DIV>
							</TH>
						</TR>
						<TR>
							<TD class="bnctablecell">
								<DIV class="bncfreq">
									frequency
									<xsl:apply-templates select="./spokenfreq" />
									<SPAN class="bncunit">
										<xsl:text><![CDATA[ (per million)]]></xsl:text>
									</SPAN>
								</DIV>
								<DIV class="bncrange">
									range
									<xsl:apply-templates select="./spokenrange" />
								</DIV>
								<DIV class="bncdisp">
									dispersion
									<xsl:apply-templates select="./spokendisp" />
								</DIV>
							</TD>
							<TD class="bnctablecell">
								<DIV class="bncfreq">
									frequency
									<xsl:apply-templates select="./writtenfreq" />
									<SPAN class="bncunit">
										<xsl:text><![CDATA[ (per million)]]></xsl:text>
									</SPAN>
								</DIV>
								<DIV class="bncrange">
									range
									<xsl:apply-templates select="./writtenrange" />
								</DIV>
								<DIV class="bncdisp">
									dispersion
									<xsl:apply-templates select="./writtendisp" />
								</DIV>
							</TD>
						</TR>
					</TABLE>
				</DIV>
			</xsl:if>

			<xsl:if test="count(./convfreq)>0 or count(./taskfreq)>0">
				<DIV class="bncconvtask">
					<IMG class="dataimg" src="images/bnc/convtask.png" />
					<TABLE class="bnctable">
						<TR>
							<TH class="bnctablecell">
								<DIV class="bncsection">
									<xsl:text><![CDATA[conversation-oriented]]></xsl:text>
								</DIV>
							</TH>
							<TH class="bnctablecell">
								<DIV class="bncsection">
									<xsl:text><![CDATA[task-oriented]]></xsl:text>
								</DIV>
							</TH>
						</TR>
						<TR>
							<TD class="bnctablecell">
								<DIV class="bncfreq">
									frequency
									<xsl:apply-templates select="./convfreq" />
									<SPAN class="bncunit">
										<xsl:text><![CDATA[ (per million)]]></xsl:text>
									</SPAN>
								</DIV>
								<DIV class="bncrange">
									range
									<xsl:apply-templates select="./convrange" />
								</DIV>
								<DIV class="bncdisp">
									dispersion
									<xsl:apply-templates select="./convdisp" />
								</DIV>
							</TD>
							<TD class="bnctablecell">
								<DIV class="bncfreq">
									frequency
									<xsl:apply-templates select="./taskfreq" />
									<SPAN class="bncunit">
										<xsl:text><![CDATA[ (per million)]]></xsl:text>
									</SPAN>
								</DIV>
								<DIV class="bncrange">
									range
									<xsl:apply-templates select="./taskrange" />
								</DIV>
								<DIV class="bncdisp">
									dispersion
									<xsl:apply-templates select="./taskdisp" />
								</DIV>
							</TD>
						</TR>
					</TABLE>
				</DIV>
			</xsl:if>

			<xsl:if test="count(./imagfreq)>0 or count(./inffreq)>0">
				<DIV class="bncimaginf">
					<IMG class="dataimg" src="images/bnc/imaginf.png" />
					<TABLE class="bnctable">
						<TR>
							<TH class="bnctablecell">
								<DIV class="bncsection">
									<xsl:text><![CDATA[imaginative]]></xsl:text>
								</DIV>
							</TH>
							<TH class="bnctablecell">
								<DIV class="bncsection">
									<xsl:text><![CDATA[informative]]></xsl:text>
								</DIV>
							</TH>
						</TR>
						<TR>
							<TD class="bnctablecell">
								<DIV class="bncfreq">
									frequency
									<xsl:apply-templates select="./imagfreq" />
									<SPAN class="bncunit">
										<xsl:text><![CDATA[ (per million)]]></xsl:text>
									</SPAN>
								</DIV>
								<DIV class="bncrange">
									range
									<xsl:apply-templates select="./imagrange" />
								</DIV>
								<DIV class="bncdisp">
									dispersion
									<xsl:apply-templates select="./imagdisp" />
								</DIV>
							</TD>
							<TD class="bnctablecell">
								<DIV class="bncfreq">
									frequency
									<xsl:apply-templates select="./inffreq" />
									<SPAN class="bncunit">
										<xsl:text><![CDATA[ (per million)]]></xsl:text>
									</SPAN>
								</DIV>
								<DIV class="bncrange">
									range
									<xsl:apply-templates select="./infrange" />
								</DIV>
								<DIV class="bncdisp">
									dispersion
									<xsl:apply-templates select="./infdisp" />
								</DIV>
							</TD>
						</TR>
					</TABLE>
				</DIV>
			</xsl:if>

		</LI>
	</xsl:template>
</xsl:transform>
