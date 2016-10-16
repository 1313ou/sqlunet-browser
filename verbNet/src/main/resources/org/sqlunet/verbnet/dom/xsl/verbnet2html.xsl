<?xml version="1.0" encoding="UTF-8"?>
<!-- VerbNet to HTML Client-side 2015/05/15 (C) 2015 Author: Bernard Bou -->

<xsl:transform version="1.0"
               xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    <xsl:strip-space elements="label"/>

    <xsl:template match="/">
        <DIV id="verbnet">
            <xsl:apply-templates select=".//verbnet"/>
        </DIV>
    </xsl:template>

    <xsl:template match="verbnet">
        <xsl:choose>
            <xsl:when test="count(.//themrole)=0 and count(.//frame)=0">
                <SPAN class="treejunction">
                    <IMG class="treepix" src="images/closed.png"/>
                </SPAN>
                <SPAN class="domain">
                    <xsl:text>verbnet</xsl:text>
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
                    <xsl:text>verbnet</xsl:text>
                </SPAN>
                <OL style="display: block;">
                    <xsl:apply-templates select="./vnclass"/>
                </OL>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="vnclass">
        <!-- indented -->
        <LI class="treeitem treepanel vnclass block1">
            <!-- tree handle -->
            <SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
                <IMG class="treepix" src="images/open.png"/>
            </SPAN>
            <!-- data image -->
            <IMG class="dataimg" src="images/verbnet/vnclass.png"/>
            <!-- label -->
            <SPAN class="vnclasslabel">
                <SPAN class="vnclassname">
                    <xsl:value-of select="./@name"/>
                </SPAN>
            </SPAN>
            <!-- collapsible content -->
            <OL style="display: block;">
                <xsl:apply-templates select="./themroles"/>
                <xsl:apply-templates select="./frames"/>
            </OL>
        </LI>
    </xsl:template>

    <xsl:template match="themroles">
        <!-- indented -->
        <LI class="treeitem treepanel vnthemroleset">
            <!-- tree handle -->
            <SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
                <IMG class="treepix" src="images/open.png"/>
            </SPAN>
            <!-- data image -->
            <IMG class="dataimg" src="images/verbnet/roles.png"/>
            <!-- label -->
            <SPAN class="vnthemrolesetlabel">
                <xsl:value-of select="count(./themrole)"/>
                <xsl:text>thematic roles</xsl:text>
            </SPAN>
            <!-- collapsible content -->
            <OL style="display: block;">
                <xsl:apply-templates select="./themrole"/>
            </OL>
        </LI>
    </xsl:template>

    <xsl:template match="themrole">
        <!-- indented -->
        <LI class="treeitem treepanel vnthemrole">
            <!-- data image -->
            <IMG class="dataimg" src="images/verbnet/role.png"/>
            <!-- label -->
            <SPAN class="vnthemrolelabel">
                <SPAN class="vnthemrolename">
                    <xsl:apply-templates select="./@type"/>
                </SPAN>
                <xsl:if test="./@synset='true'">
                    <IMG class="dataimg" src="images/verbnet/synsetspecific.png"/>
                </xsl:if>
                <xsl:apply-templates select="./restrs"/>
            </SPAN>
        </LI>
    </xsl:template>

    <xsl:template match="frames">
        <!-- indented -->
        <LI class="treeitem treepanel vnframeset">
            <!-- tree handle -->
            <SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
                <IMG class="treepix" src="images/open.png"/>
            </SPAN>
            <!-- data image -->
            <IMG class="dataimg" src="images/verbnet/vnframe.png"/>
            <!-- label -->
            <SPAN class="vnframesetlabel">
                <xsl:value-of select="count(./frame)"/>
                <xsl:text>frames</xsl:text>
            </SPAN>
            <!-- collapsible content -->
            <OL style="display: block;">
                <xsl:apply-templates select="./frame"/>
            </OL>
        </LI>
    </xsl:template>

    <xsl:template match="frame">
        <!-- indented -->
        <LI class="treeitem treepanel vnframe block3">
            <!-- tree handle -->
            <SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
                <IMG class="treepix" src="images/open.png"/>
            </SPAN>
            <!-- data image -->
            <IMG class="dataimg" src="images/verbnet/vnframe.png"/>
            <!-- label -->
            <SPAN class="vnframelabel">
                <xsl:apply-templates select="description"/>
                <!-- <xsl:text>#</xsl:text> -->
                <!-- <xsl:apply-templates select="./@id" /> -->
                <xsl:if test="./@synset='true'">
                    <IMG class="dataimg" src="images/verbnet/synsetspecific.png"/>
                </xsl:if>
            </SPAN>
            <!-- collapsible content -->
            <OL>
                <!-- indented -->
                <LI class="vnframesynsem">
                    <!-- tree handle -->
                    <SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
                        <IMG class="treepix" src="images/open.png"/>
                    </SPAN>
                    <!-- data image -->
                    <IMG class="dataimg" src="images/verbnet/syntax.png"/>
                    <!-- label -->
                    <SPAN class="vnframesynsemlabel">
                        <xsl:text>syntax</xsl:text>
                    </SPAN>
                    <!-- collapsible content -->
                    <OL style="display: block;">
                        <xsl:apply-templates select="syntax"/>
                    </OL>
                </LI>
                <!-- indented -->
                <LI class="vnframesynsem">
                    <!-- tree handle -->
                    <SPAN class="treejunction" onclick="javascript:Tree.toggle(this);">
                        <IMG class="treepix" src="images/open.png"/>
                    </SPAN>
                    <!-- data image -->
                    <IMG class="dataimg" src="images/verbnet/semantics.png"/>
                    <!-- label -->
                    <SPAN class="vnframesynsemlabel">
                        <xsl:text>semantics</xsl:text>
                    </SPAN>
                    <!-- collapsible content -->
                    <OL style="display: block;">
                        <xsl:apply-templates select="semantics"/>
                    </OL>
                </LI>
                <xsl:apply-templates select="examples"/>
            </OL>
        </LI>
    </xsl:template>

    <xsl:template match="description">
        <!-- not indented -->
        <!-- label -->
        <SPAN class="vnframename">
            <xsl:apply-templates select="./@primary"/>
        </SPAN>
        <xsl:text>/</xsl:text>
        <SPAN class="vnframename2">
            <xsl:apply-templates select="./@secondary"/>
        </SPAN>
        <!-- <xsl:text><![CDATA[ ]]></xsl:text> -->
        <!-- <xsl:apply-templates select="./@descriptionNumber" /> -->
        <!-- <xsl:text><![CDATA[ ]]></xsl:text> -->
        <!-- <xsl:apply-templates select="./@xtag" /> -->
        <!-- <xsl:text><![CDATA[ ]]></xsl:text> -->
        <!-- <xsl:apply-templates select="./@vnclass" /> -->
    </xsl:template>

    <xsl:template match="syntax">
        <!-- indented -->
        <LI class="treeitem treepanel vnsyntax">
            <xsl:apply-templates select="./synitem"/>
        </LI>
    </xsl:template>

    <xsl:template match="semantics">
        <!-- indented -->
        <LI class="treeitem treepanel vnsemantics">
            <xsl:apply-templates select="./semitem"/>
        </LI>
    </xsl:template>

    <xsl:template match="synitem">
        <!-- not indented -->
        <!-- data image -->
        <DIV>
            <IMG class="dataimg" src="images/verbnet/item.png"/>
            <xsl:apply-templates select="cat"/>
            <xsl:text></xsl:text>
            <xsl:apply-templates select="value"/>
            <xsl:text></xsl:text>
            <xsl:apply-templates select="restrs"/>
        </DIV>
    </xsl:template>

    <xsl:template match="cat">
        <SPAN class="vncat">
            <xsl:value-of select="./@value"/>
        </SPAN>
    </xsl:template>

    <xsl:template match="value">
        <SPAN class="vnthemrolename">
            <xsl:value-of select="./@value"/>
        </SPAN>
    </xsl:template>

    <xsl:template match="restrs">
        <xsl:text><![CDATA[ ]]></xsl:text>
        <IMG class="dataimg" src="images/verbnet/restr.png"/>
        <SPAN class="vnrestrs">
            <xsl:value-of select="./@value"/>
        </SPAN>
    </xsl:template>

    <xsl:template match="semitem">
        <DIV>
            <IMG class="dataimg" src="images/verbnet/item.png"/>
            <xsl:text></xsl:text>
            <xsl:apply-templates select="rel"/>
            <xsl:text>(</xsl:text>
            <xsl:apply-templates select="arg"/>
            <xsl:text>)</xsl:text>
        </DIV>
    </xsl:template>

    <xsl:template match="rel">
        <SPAN class="vnrel">
            <xsl:value-of select="./@value"/>
        </SPAN>
    </xsl:template>

    <xsl:template match="arg">
        <!-- <SPAN class="vnarg"> -->
        <!-- <xsl:value-of select="./@narg" /> -->
        <!-- </SPAN> -->
        <!-- <xsl:text> </xsl:text> -->
        <xsl:if test="./@narg &gt; 1">
            <xsl:text>,</xsl:text>
        </xsl:if>
        <xsl:element name="span">
            <xsl:attribute name="class">
                <xsl:choose>
                    <xsl:when test="starts-with(./@value,'event:')">vnargevent</xsl:when>
                    <xsl:when test="starts-with(./@value,'specific:')">vnargspecific</xsl:when>
                    <xsl:when test="starts-with(./@value,'constant:')">vnargconst</xsl:when>
                    <xsl:otherwise>vnargval</xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:value-of select="./@value"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="examples">
        <xsl:apply-templates select="example"/>
    </xsl:template>

    <xsl:template match="example">
        <LI class="treeitem treepanel vnexample">
            <IMG class="dataimg" src="images/verbnet/sample.png"/>
            <SPAN class="vnexampletext">
                <xsl:apply-templates select="./text()"/>
            </SPAN>
        </LI>
    </xsl:template>

    <xsl:template match="text">
        <DIV>
            <IMG class="dataimg" src="images/definition.png"/>
            <SPAN class="vntext">
                <xsl:apply-templates select="./text()"/>
            </SPAN>
        </DIV>
    </xsl:template>

</xsl:transform>
