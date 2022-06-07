package org.sqlunet.speak;

import androidx.annotation.NonNull;

public class LexUnit extends Pronunciation
{
	final public String word;

	public LexUnit(final String word, final String ipa, final String variety)
	{
		super(ipa, variety);
		this.word = word;
	}

	@NonNull
	@Override
	public String toString()
	{
		return String.format("%s %s", word, super.toString());
	}
}
