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
 *            thisXmlUrl the document url
 * @return {Document} DOMDocument result
 * @see Sarissa
 * @see http://www.sarissa.org
 */
Ajax.getXml = function(thisXmlUrl)
{
	var thisRequest = new XMLHttpRequest();
	thisRequest.open('GET', thisXmlUrl, false);
	thisRequest.send(null);
	return thisRequest.responseXML;
};

/**
 * function getProcessor() Get XSLT processor
 *
 * @memberOf Ajax
 * @type method
 * @param {String}
 *            thisXslUrl the XSLT stylesheet document url
 * @return {XSLTProcessor} XSLTProcessor
 * @see Sarissa
 * @see http://www.sarissa.org
 */
Ajax.getProcessor = function(thisXslUrl)
{
	var thisXsl = Ajax.getXml(thisXslUrl);
	var thisProcessor = new XSLTProcessor();
	thisProcessor.importStylesheet(thisXsl);
	return thisProcessor;
};

/**
 * function insertHtml() Insert HTML at node singled out by id
 *
 * @memberOf Ajax
 * @type method
 * @param {String}
 *            thisId the node's id
 * @param {String}
 *            thisXmlUrl the XML document url
 * @param {Function}
 *            onEnd the callback called when asynchronous operation completes
 * @see Sarissa
 * @see http://www.sarissa.org
 */
Ajax.insertHtml = function(thisId, thisXmlUrl, onEnd)
{
	Sarissa.updateContentFromURI(thisXmlUrl, document.getElementById(thisId), null, onEnd, false);
};

/**
 * function insertHtml() Insert HTML at node singled out by id, after applying XSL transformation described by XSL document
 *
 * @memberOf Ajax
 * @type method
 * @param {String}
 *            thisId the node's id
 * @param {String}
 *            thisXmlUrl the XML document url
 * @param {String}
 *            thisXslUrl the XSLT stylesheet document url
 * @param {Function}
 *            onEnd the callback called when asynchronous operation completes
 * @see Sarissa
 * @see http://www.sarissa.org
 */
Ajax.insertHtmlXsl = function(thisId, thisXmlUrl, thisXslUrl, onEnd)
{
	var thisProcessor = Ajax.getProcessor(thisXslUrl);
	Sarissa.updateContentFromURI(thisXmlUrl, document.getElementById(thisId), thisProcessor, onEnd, false);
};
