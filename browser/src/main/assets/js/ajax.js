/**
 * Ajax
 * @author Bernard Bou <bbou@ac-toulouse.fr>
 * @requires Sarissa
 */

/**
 * Object Ajax() Ajax is a utility class. Provides "static" methods for XML handling
 *
 * @class
 * @constructor
 * @super Object
 * @type constructor
 * @memberOf Ajax
 * @see Sarissa
 * @see http://www.sarissa.org
 */
function Ajax()
{
};

/**
 * function getXml() Get XML document
 *
 * @memberOf Ajax
 * @type method
 * @param {String}
 *            xmlUrl the document url
 * @return {Document} DOMDocument result
 * @see Sarissa
 * @see http://www.sarissa.org
 */
Ajax.getXml = function(xmlUrl)
{
	var request = new XMLHttpRequest();
	request.open('GET', xmlUrl, false);
	request.send(null);
	return request.responseXML;
};

/**
 * function getProcessor() Get XSLT processor
 *
 * @memberOf Ajax
 * @type method
 * @param {String}
 *            xslUrl the XSLT stylesheet document url
 * @return {XSLTProcessor} XSLTProcessor
 * @see Sarissa
 * @see http://www.sarissa.org
 */
Ajax.getProcessor = function(xslUrl)
{
	var xsl = Ajax.getXml(xslUrl);
	var processor = new XSLTProcessor();
	processor.importStylesheet(xsl);
	return processor;
};

/**
 * function insertHtml() Insert HTML at node singled out by id
 *
 * @memberOf Ajax
 * @type method
 * @param {String}
 *            id the node's id
 * @param {String}
 *            xmlUrl the XML document url
 * @param {Function}
 *            onEnd the callback called when asynchronous operation completes
 * @see Sarissa
 * @see http://www.sarissa.org
 */
Ajax.insertHtml = function(id, xmlUrl, onEnd)
{
	Sarissa.updateContentFromURI(xmlUrl, document.getElementById(id), null, onEnd, false);
};

/**
 * function insertHtml() Insert HTML at node singled out by id, after applying XSL transformation described by XSL document
 *
 * @memberOf Ajax
 * @type method
 * @param {String}
 *            id the node's id
 * @param {String}
 *            xmlUrl the XML document url
 * @param {String}
 *            xslUrl the XSLT stylesheet document url
 * @param {Function}
 *            onEnd the callback called when asynchronous operation completes
 * @see Sarissa
 * @see http://www.sarissa.org
 */
Ajax.insertHtmlXsl = function(id, xmlUrl, xslUrl, onEnd)
{
	var processor = Ajax.getProcessor(xslUrl);
	Sarissa.updateContentFromURI(xmlUrl, document.getElementById(id), processor, onEnd, false);
};
