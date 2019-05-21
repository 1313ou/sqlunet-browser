<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
  -->

<xsl:transform xmlns:bc="http://org.sqlunet/bc"
               xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               version="1.0">
	<xsl:output encoding="UTF-8" indent="yes" method="xml"/>
	<xsl:strip-space elements="label"/>

	<xsl:template match="/">
		<DIV id="bnc">
			<xsl:apply-templates select="./bc:bnc"/>
		</DIV>
	</xsl:template>

	<xsl:template match="bc:bnc">
		<xsl:choose>
			<xsl:when test="count(./bc:bncdata)=0">
				<SPAN class="treejunction">
					<IMG class="treepix" src="images/closed.png"/>
				</SPAN>
				<!-- data image -->
				<IMG class="dataimg" src="images/xnet/bnc.png"/>
				<SPAN class="domain">
					<xsl:text><![CDATA[bnc]]></xsl:text>
				</SPAN>
				<SPAN class="error" id="default">
					<xsl:text><![CDATA[∅ data]]></xsl:text>
				</SPAN>
			</xsl:when>
			<xsl:otherwise>
				<SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
					<IMG class="treepix" src="images/open.png"/>
				</SPAN>
				<IMG class="dataimg" src="images/xnet/bnc.png"/>
				<SPAN class="domain">
					<xsl:text><![CDATA[bnc]]></xsl:text>
				</SPAN>
				<OL style="display: block;">
					<xsl:apply-templates select="./bc:bncdata"/>
				</OL>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="bc:bncdata">
		<!-- indent -->
		<LI class="treeitem treepanel bncdata block3">
			<IMG class="dataimg" src="images/xnet/pos.png"/>
			<SPAN class="bncpos">
				<xsl:value-of select="./@pos"/>
			</SPAN>

			<DIV class="bncgen">
				<DIV class="bncsection">
					<xsl:text><![CDATA[general]]></xsl:text>
				</DIV>
				<DIV class="bncfreq">
					frequency
					<xsl:apply-templates select="./bc:freq"/>
					<SPAN class="bncunit">
						<xsl:text><![CDATA[ (per million)]]></xsl:text>
					</SPAN>
				</DIV>
				<DIV class="bncrange">
					range
					<xsl:apply-templates select="./bc:range"/>
				</DIV>
				<DIV class="bncdisp">
					dispersion
					<xsl:apply-templates select="./bc:disp"/>
				</DIV>
			</DIV>

			<xsl:if test="count(./bc:spokenfreq)>0 or count(./bc:writtenfreq)>0">
				<DIV class="bncspwr">
					<IMG class="dataimg" src="images/bnc/spwr.png"/>
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
									<xsl:apply-templates select="./bc:spokenfreq"/>
									<SPAN class="bncunit">
										<xsl:text><![CDATA[ (per million)]]></xsl:text>
									</SPAN>
								</DIV>
								<DIV class="bncrange">
									range
									<xsl:apply-templates select="./bc:spokenrange"/>
								</DIV>
								<DIV class="bncdisp">
									dispersion
									<xsl:apply-templates select="./bc:spokendisp"/>
								</DIV>
							</TD>
							<TD class="bnctablecell">
								<DIV class="bncfreq">
									frequency
									<xsl:apply-templates select="./bc:writtenfreq"/>
									<SPAN class="bncunit">
										<xsl:text><![CDATA[ (per million)]]></xsl:text>
									</SPAN>
								</DIV>
								<DIV class="bncrange">
									range
									<xsl:apply-templates select="./bc:writtenrange"/>
								</DIV>
								<DIV class="bncdisp">
									dispersion
									<xsl:apply-templates select="./bc:writtendisp"/>
								</DIV>
							</TD>
						</TR>
					</TABLE>
				</DIV>
			</xsl:if>

			<xsl:if test="count(./bc:convfreq)>0 or count(./bc:taskfreq)>0">
				<DIV class="bncconvtask">
					<IMG class="dataimg" src="images/bnc/convtask.png"/>
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
									<xsl:apply-templates select="./bc:convfreq"/>
									<SPAN class="bncunit">
										<xsl:text><![CDATA[ (per million)]]></xsl:text>
									</SPAN>
								</DIV>
								<DIV class="bncrange">
									range
									<xsl:apply-templates select="./bc:convrange"/>
								</DIV>
								<DIV class="bncdisp">
									dispersion
									<xsl:apply-templates select="./bc:convdisp"/>
								</DIV>
							</TD>
							<TD class="bnctablecell">
								<DIV class="bncfreq">
									frequency
									<xsl:apply-templates select="./bc:taskfreq"/>
									<SPAN class="bncunit">
										<xsl:text><![CDATA[ (per million)]]></xsl:text>
									</SPAN>
								</DIV>
								<DIV class="bncrange">
									range
									<xsl:apply-templates select="./bc:taskrange"/>
								</DIV>
								<DIV class="bncdisp">
									dispersion
									<xsl:apply-templates select="./bc:taskdisp"/>
								</DIV>
							</TD>
						</TR>
					</TABLE>
				</DIV>
			</xsl:if>

			<xsl:if test="count(./bc:imagfreq)>0 or count(./bc:inffreq)>0">
				<DIV class="bncimaginf">
					<IMG class="dataimg" src="images/bnc/imaginf.png"/>
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
									<xsl:apply-templates select="./bc:imagfreq"/>
									<SPAN class="bncunit">
										<xsl:text><![CDATA[ (per million)]]></xsl:text>
									</SPAN>
								</DIV>
								<DIV class="bncrange">
									range
									<xsl:apply-templates select="./bc:imagrange"/>
								</DIV>
								<DIV class="bncdisp">
									dispersion
									<xsl:apply-templates select="./bc:imagdisp"/>
								</DIV>
							</TD>
							<TD class="bnctablecell">
								<DIV class="bncfreq">
									frequency
									<xsl:apply-templates select="./bc:inffreq"/>
									<SPAN class="bncunit">
										<xsl:text><![CDATA[ (per million)]]></xsl:text>
									</SPAN>
								</DIV>
								<DIV class="bncrange">
									range
									<xsl:apply-templates select="./bc:infrange"/>
								</DIV>
								<DIV class="bncdisp">
									dispersion
									<xsl:apply-templates select="./bc:infdisp"/>
								</DIV>
							</TD>
						</TR>
					</TABLE>
				</DIV>
			</xsl:if>

		</LI>
	</xsl:template>
</xsl:transform>
