<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="1.0" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:wn="http://org.sqlunet/wn"
>
	<xsl:output method="html" indent="yes" encoding="UTF-8"/>
	<xsl:strip-space elements="label"/>

	<xsl:template match="/">
		<HTML>
		<HEAD>
		<LINK rel='stylesheet' type='text/css' href='css/style.css' />
		<LINK rel='stylesheet' type='text/css' href='css/tree.css"' />
		<LINK rel='stylesheet' type='text/css' href='css/wordnet.css' />
		<LINK rel='stylesheet' type='text/css' href='css/verbnet.css' />
		<LINK rel='stylesheet' type='text/css' href='css/propbank.css' />
		<LINK rel='stylesheet' type='text/css' href='css/framenet.css' />
		<LINK rel='stylesheet' type='text/css' href='css/bnc.css' />
		<SCRIPT type='text/javascript' src='js/tree.js'/>
		<SCRIPT type='text/javascript' src='js/sarissa.js'/>
		<SCRIPT type='text/javascript' src='js/ajax.js'/>
		<SCRIPT type='text/javascript' src='js/wordnet.js'/>
		<SCRIPT type='text/javascript' src='js/verbnet.js'/>
		<SCRIPT type='text/javascript' src='js/propbank.js'/>
		<SCRIPT type='text/javascript' src='js/framenet.js'/>
		<SCRIPT type='text/javascript' src='js/bnc.js'/>
		</HEAD>

		<BODY>
		<xsl:apply-templates/>
		</BODY>
		</HTML>
	</xsl:template>

	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>

</xsl:transform>
