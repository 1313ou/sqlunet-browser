/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.xn;

import org.sqlunet.settings.Settings;
import org.sqlunet.style.Colors;

public class Application extends android.app.Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		setAllColorsFromResources();
		Settings.initialize(this);
	}

	public void setAllColorsFromResources()
	{
		Colors.setColorsFromResources(this);
		org.sqlunet.predicatematrix.style.Colors.setColorsFromResources(this);
		org.sqlunet.wordnet.style.Colors.setColorsFromResources(this);
		org.sqlunet.framenet.style.Colors.setColorsFromResources(this);
		org.sqlunet.verbnet.style.Colors.setColorsFromResources(this);
		org.sqlunet.propbank.style.Colors.setColorsFromResources(this);
		org.sqlunet.bnc.style.Colors.setColorsFromResources(this);
	}
}
