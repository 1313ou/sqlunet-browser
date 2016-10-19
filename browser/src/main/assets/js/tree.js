/**
 * Tree
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a> <bbou@ac-toulouse.fr>
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
 *            element
 *            <OL>/<TABLE>
 *            element to expand
 */
Tree.toggle = function(element)
{
	display = null;
	subElement = element.nextSibling;

	// peep first subelement to see state
	while (subElement != null)
	{
		if (subElement.nodeName == "OL" || subElement.nodeName == "ol")
		{
			if (Tree.isVisible(subElement))
			{
				display = false;
				Tree.setImage(element, 'images/closed.png');
			}
			else
			{
				display = true;
				Tree.setImage(element, 'images/open.png');
			}
			break;
		}
		subElement = subElement.nextSibling;
	}

	// hide/show this subelement and next
	while (subElement != null)
	{
		if (subElement.nodeName == "OL" || subElement.nodeName == "ol")
		{
			Tree.display(subElement, display);
		}
		subElement = subElement.nextSibling;
	}
};

/**
 * function expand() Expand tree node given its id
 *
 * @memberOf Tree
 * @type method
 * @param {String}
 *            elementId id of
 *            <OL>/<TABLE>
 *            element to expand
 */
Tree.expand = function(elementId)
{
	var element = document.getElementById(elementId);
	Tree.setImage(element, 'images/open.png');

	// show this OL subelement and next
	subElement = element.nextSibling;
	while (subElement != null)
	{
		if (subElement.nodeName == "OL" || subElement.nodeName == "ol" ||
			subElement.nodeName == "TABLE" || subElement.nodeName == "table")
		{
			Tree.display(subElement, true);
		}
		subElement = subElement.nextSibling;
	}
};

/**
 * function collapseAll() Collapse tree
 *
 * @memberOf Tree
 * @type method
 * @param {Array}
 *            tags list of element tags to collapse
 */
Tree.collapseAll = function(tags)
{
	for ( var i = 0; i < tags.length; i++)
	{
		var elements = document.getElementsByTagName(tags[i]);
		for ( var j = 0; j < elements.length; j++)
			Tree.display(elements[j], false);
		root = document.getElementById("root");
		Tree.display(root, true);
	}
};

/**
 * function expandAll() Expand tree
 *
 * @memberOf Tree
 * @type method
 * @param {Array}
 *            tags list of element tags to expand
 */
Tree.expandAll = function(tags)
{
	for ( var i = 0; i < tags.length; i++)
	{
		var elements = document.getElementsByTagName(tags[i]);
		for ( var j = 0; j < elements.length; j++)
			Tree.display(elements[j], true);
		root = document.getElementById("root");
		Tree.display(root, true);
	}
};

/**
 * function display() Display/Hide tree node
 *
 * @memberOf Tree
 * @type method
 * @param {Element}
 *            element element
 * @param {boolean}
 *            visible whether to make visible or not
 */
Tree.display = function(element, visible)
{
	element.style.display = visible ? "block" : "none";
};

/**
 * function isVisible() Get tree node visible state
 *
 * @memberOf Tree
 * @type method
 * @param {Element}
 *            element element
 * @return {boolean} whether element visible or not
 */
Tree.isVisible = function(element)
{
	return element.style.display == "block";
};

/**
 * function setImage() Set tree node image
 *
 * @memberOf Tree
 * @type method
 * @param {Element}
 *            parentElement element's parent node
 * @param {String}
 *            image image's src
 */
Tree.setImage = function(parentElement, image)
{
	element = parentElement.firstChild;
	for (; element != null; element = element.nextSibling)
		if (element.nodeName == "IMG" || element.nodeName == "img")
		{
			element.src = image;
			break;
		}
};
