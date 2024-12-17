<?xml version="1.0" encoding="UTF-8"?>

<!-- RELATIONS 2 HTML - (C) 2020 Author: Bernard Bou -->
<xsl:transform version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes" encoding="UTF-8" doctype-system="about:legacy-compat"/>

  <xsl:variable name="imagedir" select='"images/"'/>

  <xsl:template match="/">
    <HTML lang="en">
      <HEAD>
        <TITLE>
          <xsl:value-of select="'Relations'"/>
        </TITLE>
        <LINK rel="stylesheet" type="text/css" href="relations.css"/>
      </HEAD>
      <BODY>
        <TABLE class="relations full-width">
          <xsl:apply-templates select=".//relation"/>
        </TABLE>
        <BR/>
      </BODY>
    </HTML>
  </xsl:template>

  <xsl:template match="relation">
    <TR class="relation full-width" name="relation">
      <xsl:attribute name="id">
        <xsl:value-of select="./@id"/>
      </xsl:attribute>
      <TD class="full-width">
        <TABLE class="full-width">
          <!-- HEADER -->
          <TR class="full-width">
            <TD colspan="2">
              <DIV class="relationheader">
                <IMG class="relationimage">
                  <xsl:attribute name="src">
                    <xsl:copy-of select="$imagedir"/>
                    <xsl:value-of select="./img/@src"/>
                  </xsl:attribute>
                  <xsl:attribute name="alt">
                    <xsl:value-of select="./@name"/>
                  </xsl:attribute>
                </IMG>
                <SPAN class="relationname"><xsl:value-of select="./@name"/></SPAN>
              </DIV>
            </TD>
          </TR>
          <!-- DESCRIPTION -->
          <TR class="full-width">
            <TD colspan="2">
              <xsl:apply-templates select="./description"/>
            </TD>
          </TR>
          <!-- LOGICAL -->
          <TR>
            <TD colspan="2">
              <xsl:apply-templates select="./logical"/>
            </TD>
          </TR>
          <!-- FEATURES -->
          <TR>
            <TD class="colfeatures">
              <DIV class="features">

                <!-- semantic/lexical -->
                <xsl:choose>
                  <xsl:when test="./@type='semantic'">
                    <IMG class="featureimage">
                      <xsl:attribute name="src">
                        <xsl:copy-of select="$imagedir"/>
                        <xsl:text>feature_semantic.png</xsl:text>
                      </xsl:attribute>
                      <xsl:attribute name="alt">
                        <xsl:text>semantic</xsl:text>
                      </xsl:attribute>
                      <xsl:attribute name="title">
                        <xsl:text>semantic</xsl:text>
                      </xsl:attribute>
                    </IMG>
                  </xsl:when>
                  <xsl:when test="./@type='lexical'">
                    <IMG class="featureimage">
                      <xsl:attribute name="src">
                        <xsl:copy-of select="$imagedir"/>
                        <xsl:text>feature_lexical.png</xsl:text>
                      </xsl:attribute>
                      <xsl:attribute name="alt">
                        <xsl:text>lexical</xsl:text>
                      </xsl:attribute>
                      <xsl:attribute name="title">
                        <xsl:text>lexical</xsl:text>
                      </xsl:attribute>
                    </IMG>
                  </xsl:when>
                  <xsl:when test="./@type='both'">
                    <IMG class="featureimage">
                      <xsl:attribute name="src">
                        <xsl:copy-of select="$imagedir"/>
                        <xsl:text>feature_semantic.png</xsl:text>
                      </xsl:attribute>
                      <xsl:attribute name="alt">
                        <xsl:text>semantic</xsl:text>
                      </xsl:attribute>
                      <xsl:attribute name="title">
                        <xsl:text>semantic</xsl:text>
                      </xsl:attribute>
                    </IMG>
                    <IMG class="featureimage">
                      <xsl:attribute name="src">
                        <xsl:copy-of select="$imagedir"/>
                        <xsl:text>feature_lexical.png</xsl:text>
                      </xsl:attribute>
                      <xsl:attribute name="alt">
                        <xsl:text>lexical</xsl:text>
                      </xsl:attribute>
                      <xsl:attribute name="title">
                        <xsl:text>lexical</xsl:text>
                      </xsl:attribute>
                    </IMG>
                  </xsl:when>
                </xsl:choose>

                <!-- symmetric -->
                <xsl:if test="./@symmetric='true'">
                  <IMG class="featureimage">
                    <xsl:attribute name="src">
                      <xsl:copy-of select="$imagedir"/>
                      <xsl:text>feature_symmetric.png</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="alt">
                      <xsl:text>symmetric</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                      <xsl:text>symmetric</xsl:text>
                    </xsl:attribute>
                  </IMG>
                </xsl:if>

                <!-- pos -->
                <xsl:if test="./@n='true'">
                  <IMG class="featureimage">
                    <xsl:attribute name="src">
                      <xsl:copy-of select="$imagedir"/>
                      <xsl:value-of select="'pos_n.png'"/>
                    </xsl:attribute>
                    <xsl:attribute name="alt">
                      <xsl:value-of select="'noun'"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                      <xsl:value-of select="'noun'"/>
                    </xsl:attribute>
                  </IMG>
                </xsl:if>
                <xsl:if test="./@v='true'">
                  <IMG class="featureimage">
                    <xsl:attribute name="src">
                      <xsl:copy-of select="$imagedir"/>
                      <xsl:value-of select="'pos_v.png'"/>
                    </xsl:attribute>
                    <xsl:attribute name="alt">
                      <xsl:value-of select="'verb'"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                      <xsl:value-of select="'verb'"/>
                    </xsl:attribute>
                  </IMG>
                </xsl:if>
                <xsl:if test="./@a='true'">
                  <IMG class="featureimage">
                    <xsl:attribute name="src">
                      <xsl:copy-of select="$imagedir"/>
                      <xsl:value-of select="'pos_a.png'"/>
                    </xsl:attribute>
                    <xsl:attribute name="alt">
                      <xsl:value-of select="'adj'"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                      <xsl:value-of select="'adjective'"/>
                    </xsl:attribute>
                  </IMG>
                </xsl:if>
                <xsl:if test="./@r='true'">
                  <IMG class="featureimage">
                    <xsl:attribute name="src">
                      <xsl:copy-of select="$imagedir"/>
                      <xsl:value-of select="'pos_r.png'"/>
                    </xsl:attribute>
                    <xsl:attribute name="alt">
                      <xsl:value-of select="'adv'"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                      <xsl:value-of select="'adverb'"/>
                    </xsl:attribute>
                  </IMG>
                </xsl:if>
                <xsl:if test="./@s='true'">
                  <IMG class="featureimage">
                    <xsl:attribute name="src">
                      <xsl:copy-of select="$imagedir"/>
                      <xsl:value-of select="'pos_s.png'"/>
                    </xsl:attribute>
                    <xsl:attribute name="alt">
                      <xsl:value-of select="'satellite'"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                      <xsl:value-of select="'satellite adj'"/>
                    </xsl:attribute>
                  </IMG>
                </xsl:if>

              </DIV>
            </TD>
            <TD class="colother">
              <TABLE class="toright">
                <TR>
                  <TD>
                    <DIV class="inverse">
                      <xsl:if test="./@inverse">
                        <xsl:variable name="inverse">
                          <xsl:value-of select="./@inverse"/>
                        </xsl:variable>
                        <xsl:text>inverse: </xsl:text>
                        <A>
                          <xsl:attribute name="href">
                            <xsl:value-of select="$inverse"/>
                            <xsl:text>.html</xsl:text>
                          </xsl:attribute>
                          <xsl:value-of select="$inverse"/>
                        </A>
                        <xsl:text> </xsl:text>
                        <IMG class="otherimage">
                          <xsl:attribute name="src">
                            <xsl:copy-of select="$imagedir"/>
                            <xsl:call-template name="get-image">
                              <xsl:with-param name="tag">
                                <xsl:value-of select="$inverse"/>
                              </xsl:with-param>
                              <xsl:with-param name="scope"/>
                            </xsl:call-template>
                          </xsl:attribute>
                        </IMG>
                      </xsl:if>
                    </DIV>
                  </TD>
                </TR>
                <TR>
                  <TD>
                    <DIV class="see">
                      <xsl:apply-templates select="./see"/>
                    </DIV>
                  </TD>
                </TR>
              </TABLE>
            </TD>
          </TR>
          <!-- SPACE -->
          <TR>
            <TD colspan="2"/>
          </TR>
          <!-- EXAMPLES -->
          <TR>
            <TD colspan="2">
              <DIV class="examples">
                <TABLE class="exampletable">
                  <xsl:apply-templates select="./guides"/>
                  <xsl:apply-templates select="./examples"/>
                </TABLE>
              </DIV>
            </TD>
          </TR>
          <!-- PHRASES -->
          <TR>
            <TD colspan="2">
              <DIV class="phrases">
                <TABLE class="phrasetable">
                  <xsl:apply-templates select="./phrases"/>
                </TABLE>
              </DIV>
            </TD>
          </TR>
        </TABLE>
      </TD>
    </TR>
  </xsl:template>

  <xsl:template match="description">
    <DIV class="description">
      <xsl:copy-of select="./span"/>
    </DIV>
  </xsl:template>

  <xsl:template match="logical">
    <DIV class="logical">
      <xsl:apply-templates select="./def"/>
      <xsl:apply-templates select="./cond"/>
    </DIV>
  </xsl:template>

  <xsl:template match="def">
    <DIV class="def">
      <xsl:copy-of select="./span"/>
      <xsl:text> iff</xsl:text>
    </DIV>
  </xsl:template>

  <xsl:template match="cond">
    <DIV class="cond">
      <xsl:text>→</xsl:text>
      <xsl:copy-of select="./span"/>
    </DIV>
  </xsl:template>

  <xsl:template match="see">
    <DIV class="see">
      <xsl:text> </xsl:text>
      <xsl:value-of select="./text()"/>
      <xsl:text> </xsl:text>
      <xsl:variable name="tag">
        <xsl:value-of select="./@tag"/>
      </xsl:variable>
      <xsl:text>see: </xsl:text>
      <xsl:call-template name="make-image-with-link">
        <xsl:with-param name="tag">
          <xsl:value-of select="$tag"/>
        </xsl:with-param>
      </xsl:call-template>
    </DIV>
  </xsl:template>

  <xsl:template match="examples">
    <xsl:apply-templates select="./example"/>
  </xsl:template>

  <xsl:template match="example">
    <TR>
      <TD class="from">
        <xsl:value-of select="./from/text()"/>
      </TD>
      <TD class="rel">
        <IMG>
          <xsl:attribute name="src">
            <xsl:copy-of select="$imagedir"/>
            <xsl:choose>
              <xsl:when test="./img">
                <xsl:value-of select="./img/@src"/>
              </xsl:when>
              <xsl:when test="./@scope">
                <xsl:call-template name="get-image">
                  <xsl:with-param name="tag">
                    <xsl:value-of select="../../@id"/>
                  </xsl:with-param>
                  <xsl:with-param name="scope">
                    <xsl:value-of select="./@scope"/>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="../../img/@src"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
          <xsl:attribute name="alt">
            <xsl:value-of select="../../@name"/>
          </xsl:attribute>
        </IMG>
      </TD>
      <TD class="to">
        <xsl:value-of select="./to/text()"/>
      </TD>
      <xsl:apply-templates select="./gloss"/>
    </TR>
  </xsl:template>

  <xsl:template match="guides">
    <xsl:apply-templates select="./guide"/>
  </xsl:template>

  <xsl:template match="guide">
    <TR>
      <!-- @formatter:off -->
      <TD class="fromguide"><xsl:value-of select="./from/text()"/></TD>
      <TD/>
      <TD class="toguide"><xsl:value-of select="./to/text()"/></TD>
      <xsl:apply-templates select="./gloss"/>
      <!-- @formatter:on -->
    </TR>
  </xsl:template>

  <xsl:template match="phrases">
    <xsl:apply-templates select="./phrase"/>
  </xsl:template>

  <xsl:template match="phrase">
    <xsl:variable name="relationphrase">
      <xsl:copy-of select="./*"/>
    </xsl:variable>
    <xsl:variable name="scope">
      <xsl:value-of select="./@scope"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$scope != ''">
        <xsl:for-each select="../../examples/example[@scope=$scope]">
          <TR>
            <!-- @formatter:off -->
            <TD class='fromphrase'><xsl:value-of select="./from"/></TD>
            <TD class='relphrase'><xsl:copy-of select="$relationphrase"/></TD>
            <TD class='tophrase'><xsl:value-of select="./to"/></TD>
            <!-- @formatter:on -->
          </TR>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:for-each select="../../examples/example">
          <TR>
            <TD class='fromphrase'>
              <xsl:value-of select="./from"/>
            </TD>
            <TD class='phrase'>
              <xsl:value-of select="$relationphrase"/>
            </TD>
            <TD class='tophrase'>
              <xsl:value-of select="./to"/>
            </TD>
          </TR>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="gloss">
    <TD class="gloss">
      <xsl:value-of select="text()"/>
    </TD>
  </xsl:template>

  <xsl:template name="get-image">
    <xsl:param name="tag"/>
    <xsl:param name="scope"/>
    <xsl:value-of select="$tag"/>
    <xsl:if test="$scope">
      <xsl:text>_</xsl:text>
      <xsl:value-of select="$scope"/>
    </xsl:if>
    <xsl:text>.png</xsl:text>
  </xsl:template>

  <!--
    <xsl:call-template name="get-image">
      <xsl:with-param name="tag">
        <xsl:value-of select="$tag"/>
      </xsl:with-param>
      <xsl:with-param name="scope">
        <xsl:value-of select="$scope"/>
      </xsl:with-param>
    </xsl:call-template>
  -->

  <xsl:template name="make-image">
    <xsl:param name="tag"/>
    <IMG class="inline-image">
      <xsl:attribute name="src">
        <xsl:text>images/</xsl:text>
        <xsl:value-of select="$tag"/>
        <xsl:text>.png</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="alt">
        <xsl:value-of select="$tag"/>
      </xsl:attribute>
    </IMG>
  </xsl:template>

  <xsl:template name="make-image-with-link">
    <xsl:param name="tag"/>
    <A>
      <xsl:attribute name="href">
        <xsl:value-of select="$tag"/>
        <xsl:text>.html</xsl:text>
      </xsl:attribute>
      <xsl:value-of select="$tag"/>
    </A>
    <xsl:text> </xsl:text>
    <xsl:call-template name="make-image">
      <xsl:with-param name="tag">
        <xsl:value-of select="$tag"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="@*">
    <xsl:attribute name="{name()}">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template match="text()">
    <xsl:value-of select="normalize-space()"/>
  </xsl:template>

</xsl:transform>

