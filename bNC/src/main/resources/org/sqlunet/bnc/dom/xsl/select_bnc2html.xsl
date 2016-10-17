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
    </xsl:template>

</xsl:transform>
