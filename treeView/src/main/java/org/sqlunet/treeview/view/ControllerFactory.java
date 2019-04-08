package org.sqlunet.treeview.view;

import android.content.Context;

import org.sqlunet.treeview.control.Controller;
import org.sqlunet.treeview.model.TreeNode;

import java.lang.reflect.Constructor;

public class ControllerFactory
{
	static public void addController(final TreeNode node, final Context context)
	{
		if (node.getController() == null)
		{
			final Controller controller = makeController(node.controllerClass, context);
			node.setController(controller);
		}

		for (TreeNode child : node.getChildren())
		{
			addController(child, context);
		}
	}

	static public Controller makeController(final Class<?> controllerClass, final Context context)
	{
		try
		{
			final Constructor ctor = controllerClass.getDeclaredConstructor(Context.class);
			return (Controller) ctor.newInstance(context);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Could not instantiate class " + controllerClass);
		}
	}
}
