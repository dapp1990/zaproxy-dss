<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="1.0"
  >
  <xsl:output method="html"/> 
 
  <xsl:template match="/OWASPZAPReport">
<html>
<head>
<!-- ZAP: rebrand -->
<title>ZAP Scanning Report</title>

</head>

<body text="#000000">
<!-- ZAP: rebrand -->
<p><strong>ZAP Scanning Report</strong></p>

<p>
<xsl:apply-templates select="text()"/>
</p>

<p><strong>Summary of Alerts</strong></p>
<table width="45%" border="0">
  <tr bgcolor="#666666"> 
    <td width="45%" height="24"><strong><font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif">Risk 
      Level</font></strong></td>
    <td width="55%" align="center"><strong><font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif">Number 
      of Alerts</font></strong></td>
  </tr>
  <tr bgcolor="#e8e8e8"> 
    <td><font size="2" face="Arial, Helvetica, sans-serif"><a href="#high">High</a></font></td>
    <td align="center"><font size="2" face="Arial, Helvetica, sans-serif">
    <xsl:value-of select="count(descendant::alertitem[riskcode='3'])"/>
    </font></td>
  </tr>
  <tr bgcolor="#e8e8e8"> 
    <td><font size="2" face="Arial, Helvetica, sans-serif"><a href="#medium">Medium</a></font></td>
    <td align="center"><font size="2" face="Arial, Helvetica, sans-serif">
    <xsl:value-of select="count(descendant::alertitem[riskcode='2'])"/>
    </font></td>
  </tr>
    <tr bgcolor="#e8e8e8"> 
    <td><font size="2" face="Arial, Helvetica, sans-serif"><a href="#low">Low</a></font></td>
    <td align="center"><font size="2" face="Arial, Helvetica, sans-serif">
    <xsl:value-of select="count(descendant::alertitem[riskcode='1'])"/>
    </font></td>
  </tr>
    <tr bgcolor="#e8e8e8"> 
    <td><font size="2" face="Arial, Helvetica, sans-serif"><a href="#info">Informational</a></font></td>
    <td align="center"><font size="2" face="Arial, Helvetica, sans-serif">
    <xsl:value-of select="count(descendant::alertitem[riskcode='0'])"/>
    </font></td>
  </tr>
</table>
<p></p>

<xsl:apply-templates select="descendant::imagestatistics">
  
</xsl:apply-templates>

<p><strong>Alert Detail</strong></p>

<xsl:apply-templates select="descendant::alertitem">
  <xsl:sort order="descending" data-type="number" select="riskcode"/>
  <xsl:sort order="descending" data-type="number" select="confidence"/>
</xsl:apply-templates>

</body>
</html>
</xsl:template>

  <xsl:template match="imagestatistics">
    <p><strong>Image statistics of site <xsl:value-of select="site"/></strong></p>

  <table border="0">
  <tr bgcolor="#666666"><td colspan="2"><strong><font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif">Image type statistics</font></strong></td></tr>
  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>jpeg</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
      <p><xsl:value-of select="filetypes/jpeg"/></p>
    </font></td>
  </tr>

  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>png</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
      <p><xsl:value-of select="filetypes/png"/></p>
    </font></td>
  </tr>

  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>gif</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
      <p><xsl:value-of select="filetypes/gif"/></p>
    </font></td>
  </tr>

  </table>

  <table border="0">
    <tr bgcolor="#666666"><td colspan="2"><strong><font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif">File size statistics</font></strong></td></tr>
  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Minimun</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
      <p><xsl:value-of select="filesize/min"/></p>
    </font></td>
  </tr>

  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Minimun URL</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
      <p><xsl:value-of select="filesize/minurl"/></p>
    </font></td>
  </tr>

  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Maximum</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
      <p><xsl:value-of select="filesize/max"/></p>
    </font></td>
  </tr>

  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Maximum URL</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
      <p><xsl:value-of select="filesize/maxurl"/></p>
    </font></td>
  </tr>

<tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Medium</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
      <p><xsl:value-of select="filesize/med"/></p>
    </font></td>
  </tr>


<tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Average</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
      <p><xsl:value-of select="filesize/avg"/></p>
    </font></td>
  </tr>
  </table>

    <table border="0">
      <tr bgcolor="#666666"><td colspan="2"><strong><font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif">Image width statistics</font></strong></td></tr>
  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Minimun</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
      <p><xsl:value-of select="filewidth/min"/></p>
    </font></td>
  </tr>

  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Minimun URL</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
      <p><xsl:value-of select="filewidth/minurl"/></p>
    </font></td>
  </tr>

  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Maximum</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
      <p><xsl:value-of select="filewidth/max"/></p>
    </font></td>
  </tr>

  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Maximum URL</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
      <p><xsl:value-of select="filewidth/maxurl"/></p>
    </font></td>
  </tr>

<tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Medium</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
      <p><xsl:value-of select="filewidth/med"/></p>
    </font></td>
  </tr>


<tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Average</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
      <p><xsl:value-of select="filewidth/avg"/></p>
    </font></td>
  </tr>
  </table>

    <table border="0">
      <tr bgcolor="#666666"><td colspan="2"><strong><font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif">Image height statistics</font></strong></td></tr>
  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Minimun</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
      <p><xsl:value-of select="fileheight/min"/></p>
    </font></td>
  </tr>

  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Minimun URL</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
      <p><xsl:value-of select="fileheight/minurl"/></p>
    </font></td>
  </tr>

  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Maximum</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
      <p><xsl:value-of select="fileheight/max"/></p>
    </font></td>
  </tr>

  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Maximum URL</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
      <p><xsl:value-of select="fileheight/maxurl"/></p>
    </font></td>
  </tr>

<tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Medium</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
      <p><xsl:value-of select="fileheight/med"/></p>
    </font></td>
  </tr>


<tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Average</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
      <p><xsl:value-of select="fileheight/avg"/></p>
    </font></td>
  </tr>
  </table>

</xsl:template>

  <!-- Top Level Heading -->
  <xsl:template match="alertitem">
<p></p>
<table width="100%" border="0">
<xsl:apply-templates select="text()|name|desc|uri|param|attack|evidence|instances|count|otherinfo|solution|reference|cweid|wascid|p|br|wbr|ul|li"/>
</table>
  </xsl:template>

  <xsl:template match="name[following-sibling::riskcode='3']">
  <tr bgcolor="red" height="24">	
    <td width="20%" valign="top"><strong><font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif">
    <a name="high"/>
    <xsl:value-of select="following-sibling::riskdesc"/>
    </font></strong></td>
    <td width="80%"><strong><font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif">
      <xsl:apply-templates select="text()"/>
</font></strong></td>
  </tr>
  </xsl:template>

  <xsl:template match="name[following-sibling::riskcode='2']">
  <!-- ZAP: Changed the medium colour to orange -->
  <tr bgcolor="orange" height="24">	
    <td width="20%" valign="top"><strong><font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif">
    <a name="medium"/>
    <xsl:value-of select="following-sibling::riskdesc"/>
	</font></strong></td>
    <td width="80%"><strong><font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif">
      <xsl:apply-templates select="text()"/>
</font></strong></td>
  </tr>

  </xsl:template>
  <xsl:template match="name[following-sibling::riskcode='1']">
  <!-- ZAP: Changed the low colour to yellow -->
  <tr bgcolor="yellow" height="24">
    <a name="low"/>
    <td width="20%" valign="top"><strong><font color="#000000" size="2" face="Arial, Helvetica, sans-serif">
    <xsl:value-of select="following-sibling::riskdesc"/>
	</font></strong></td>
    <td width="80%"><strong><font color="#000000" size="2" face="Arial, Helvetica, sans-serif">
      <xsl:apply-templates select="text()"/>
</font></strong></td>
  </tr>
  </xsl:template>
  
  <xsl:template match="name[following-sibling::riskcode='0']">
  <tr bgcolor="blue" height="24">	
    <td width="20%" valign="top"><strong><font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif">
    <a name="info"/>
    <xsl:value-of select="following-sibling::riskdesc"/>
	</font></strong></td>
    <td width="80%"><strong><font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif">
      <xsl:apply-templates select="text()"/>
</font></strong></td>
  </tr>
  </xsl:template>

  <xsl:template match="desc">
  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Description</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
    <xsl:apply-templates select="text()|*"/>
    </font></td>
  </tr>
  <TR vAlign="top"> 
    <TD colspan="2"> </TD>
  </TR>
  
  </xsl:template>

  <xsl:template match="uri">
  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><blockquote><font size="2" face="Arial, Helvetica, sans-serif"><p>URL</p></font></blockquote></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
    <p><xsl:apply-templates select="text()|*"/></p>
    </font></td>
  </tr>
  </xsl:template>

  <xsl:template match="param">
  <xsl:if test="text() !=''">
  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><blockquote><font size="2" face="Arial, Helvetica, sans-serif"><p>Parameter</p></font></blockquote></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
    <p><xsl:apply-templates select="text()|*"/></p>
    </font></td>
  </tr>
  </xsl:if>
  </xsl:template>

<xsl:template match="attack">
  <xsl:if test="text() !=''">
  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><blockquote><font size="2" face="Arial, Helvetica, sans-serif"><p>Attack</p></font></blockquote></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
    <p><xsl:apply-templates select="text()|*"/></p>
    </font></td>
  </tr>
  </xsl:if>
  </xsl:template>

  <xsl:template match="evidence">
  <xsl:if test="text() !=''">
  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><blockquote><font size="2" face="Arial, Helvetica, sans-serif"><p>Evidence</p></font></blockquote></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
    <p><xsl:apply-templates select="text()|*"/></p>
    </font></td>
  </tr>
  </xsl:if>
  </xsl:template>

  <xsl:template match="instances/instance/uri">
  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><blockquote><font size="2" face="Arial, Helvetica, sans-serif"><p>URL</p></font></blockquote></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
    <p><xsl:apply-templates select="text()|*"/></p>
    </font></td>
  </tr>
  </xsl:template>
 
  <xsl:template match="instances/instance/param">
  <xsl:if test="text() !=''">
  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><blockquote><font size="2" face="Arial, Helvetica, sans-serif"><p>&#160;&#160;&#160;&#160;Parameter</p></font></blockquote></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
	<p><xsl:apply-templates select="text()|*"/></p>
    </font></td>
  </tr>
  </xsl:if>
  </xsl:template>

  <xsl:template match="instances/instance/attack">
  <xsl:if test="text() !=''">
  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><blockquote><font size="2" face="Arial, Helvetica, sans-serif"><p>&#160;&#160;&#160;&#160;Attack</p></font></blockquote></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
	<p><xsl:apply-templates select="text()|*"/></p>
    </font></td>
  </tr>
  </xsl:if>
  </xsl:template>

  <xsl:template match="instances/instance/evidence">
  <xsl:if test="text() !=''">
  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><blockquote><font size="2" face="Arial, Helvetica, sans-serif"><p>&#160;&#160;&#160;&#160;Evidence</p></font></blockquote></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
    <p><xsl:apply-templates select="text()|*"/></p>
    </font></td>
  </tr>
  </xsl:if>
  </xsl:template>

  <xsl:template match="count">
  <xsl:if test="text() !=''">
  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Instances</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
    <p><xsl:apply-templates select="text()|*"/></p>
    </font></td>
  </tr>
  </xsl:if>
  </xsl:template>

  <xsl:template match="otherinfo">
  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Other information</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
    <p><xsl:apply-templates select="text()|*"/></p>
    </font></td>
  </tr>

  <TR vAlign="top"> 
    <TD colspan="2"> </TD>
  </TR>
  </xsl:template>

  <xsl:template match="solution">
  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Solution</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
	<xsl:apply-templates select="text()|*"/>
	</font></td>
  </tr>
  </xsl:template>

  <xsl:template match="reference">
  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>Reference</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
	<xsl:apply-templates select="text()|*"/>
    </font></td>
  </tr>
  </xsl:template>
  
  <xsl:template match="cweid">
  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>CWE Id</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
	<p><xsl:apply-templates select="text()|*"/></p>
    </font></td>
  </tr>
  </xsl:template>
  
  <xsl:template match="wascid">
  <tr bgcolor="#e8e8e8" valign="top"> 
    <td width="20%"><font size="2" face="Arial, Helvetica, sans-serif"><p>WASC Id</p></font></td>
    <td width="80%">
    <font size="2" face="Arial, Helvetica, sans-serif">
	<p><xsl:apply-templates select="text()|*"/></p>
    </font></td>
  </tr>
  </xsl:template>
  
  <xsl:template match="p">
  <p align="justify">
  <xsl:apply-templates select="text()|*"/>
  </p>
  </xsl:template> 

  <xsl:template match="br">
  <br/>
  <xsl:apply-templates/>
  </xsl:template> 

  <xsl:template match="ul">
  <ul>
  <xsl:apply-templates select="text()|*"/>
  </ul>
  </xsl:template> 

  <xsl:template match="li">
  <li>
  <xsl:apply-templates select="text()|*"/>
  </li>
  </xsl:template> 
  
  <xsl:template match="wbr">
  <wbr/>
  <xsl:apply-templates/>
  </xsl:template> 

</xsl:stylesheet>