/**
 * Tree
 * @author Bernard Bou <bbou@ac-toulouse.fr>
 */

/**
 * Object Tree() Tree is a utility class. Provides "static" methods for HTML tree handling
 *
 * @class
 * @constructor
 * @super Object
 * @type constructor
 * @memberOf Tree
 */
function Tree()
{
};

/**
 * function toggle() Toggle tree node state (collapsed/expanded)
 *
 * @memberOf Tree
 * @type method
 * @param {Element}
 *            thisElement
 *            <OL>/<TABLE>
 *            element to expand
 */
Tree.toggle = function(thisElement)
{
	thisDisplay = null;
	thisSubElement = thisElement.nextSibling;

	// peep first subelement to see state
	while (thisSubElement != null)
	{
		if (thisSubElement.nodeName == "OL" || thisSubElement.nodeName == "ol")
		{
			if (Tree.isVisible(thisSubElement))
			{
				thisDisplay = false;
				Tree.setImage(thisElement, 'images/closed.png');
			}
			else
			{
				thisDisplay = true;
				Tree.setImage(thisElement, 'images/open.png');
			}
			break;
		}
		thisSubElement = thisSubElement.nextSibling;
	}

	// hide/show this subelement and next
	while (thisSubElement != null)
	{
		if (thisSubElement.nodeName == "OL" || thisSubElement.nodeName == "ol")
		{
			Tree.display(thisSubElement, thisDisplay);
		}
		thisSubElement = thisSubElement.nextSibling;
	}
};

/**
 * function expand() Expand tree node given its id
 *
 * @memberOf Tree
 * @type method
 * @param {String}
 *            thisElementId id of
 *            <OL>/<TABLE>
 *            element to expand
 */
Tree.expand = function(thisElementId)
{
	var thisElement = document.getElementById(thisElementId);
	Tree.setImage(thisElement, 'images/open.png');

	// show this OL subelement and next
	thisSubElement = thisElement.nextSibling;
	while (thisSubElement != null)
	{
		if (thisSubElement.nodeName == "OL" || thisSubElement.nodeName == "ol" || 
			thisSubElement.nodeName == "TABLE" || thisSubElement.nodeName == "table")
		{
			Tree.display(thisSubElement, true);
		}
		thisSubElement = thisSubElement.nextSibling;
	}
};

/**
 * function collapseAll() Collapse tree
 *
 * @memberOf Tree
 * @type method
 * @param {Array}
 *            theseTags list of element tags to collapse
 */
Tree.collapseAll = function(theseTags)
{
	for ( var i = 0; i < tags.length; i++)
	{
		var theseElements = document.getElementsByTagName(theseTags[i]);
		for ( var j = 0; j < theseElements.length; j++)
			Tree.display(theseElements[j], false);
		thisRoot = document.getElementById("root");
		Tree.display(thisRoot, true);
	}
};

/**
 * function expandAll() Expand tree
 *
 * @memberOf Tree
 * @type method
 * @param {Array}
 *            theseTags list of element tags to expand
 */
Tree.expandAll = function(theseTags)
{
	for ( var i = 0; i < tags.length; i++)
	{
		var theseElements = document.getElementsByTagName(theseTags[i]);
		for ( var j = 0; j < theseElements.length; j++)
			Tree.display(theseElements[j], true);
		thisRoot = document.getElementById("root");
		Tree.display(thisRoot, true);
	}
};

/**
 * function display() Display/Hide tree node
 *
 * @memberOf Tree
 * @type method
 * @param {Element}
 *            thisElement element
 * @param {boolean}
 *            visible whether to make visible or not
 */
Tree.display = function(thisElement, visible)
{
	thisElement.style.display = visible ? "block" : "none";
};

/**
 * function isVisible() Get tree node visible state
 *
 * @memberOf Tree
 * @type method
 * @param {Element}
 *            thisElement element
 * @return {boolean} whether element visible or not
 */
Tree.isVisible = function(thisElement)
{
	return thisElement.style.display == "block";
};

/**
 * function setImage() Set tree node image
 *
 * @memberOf Tree
 * @type method
 * @param {Element}
 *            thisParentElement element's parent node
 * @param {String}
 *            thisImage image's src
 */
Tree.setImage = function(thisParentElement, thisImage)
{
	thisElement = thisParentElement.firstChild;
	for (; thisElement != null; thisElement = thisElement.nextSibling)
		if (thisElement.nodeName == "IMG" || thisElement.nodeName == "img")
		{
			thisElement.src = thisImage;
			break;
		}
};
