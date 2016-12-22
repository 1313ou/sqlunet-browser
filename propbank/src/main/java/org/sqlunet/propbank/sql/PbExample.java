package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Examples attached to a PropBank role set
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class PbExample
{
	/**
	 * Example id
	 */
	public final long exampleId;

	/**
	 * Text
	 */
	public final String text;

	/**
	 * Relation
	 */
	public final String rel;

	/**
	 * Arguments
	 */
	public final List<PbArg> args;

	/**
	 * Aspect
	 */
	public final String aspect;

	/**
	 * Form
	 */
	public final String form;

	/**
	 * Tense
	 */
	public final String tense;

	/**
	 * Voice
	 */
	public final String voice;

	/**
	 * Person
	 */
	public final String person;

	/**
	 * Constructor
	 *
	 * @param exampleId is the example id
	 * @param text      is the text of the example
	 * @param rel       is the relation
	 * @param args      is the list of arguments
	 */
	private PbExample(final long exampleId, final String text, final String rel, final List<PbArg> args, final String aspect, final String form, final String tense, final String voice, final String person)
	{
		this.exampleId = exampleId;
		this.text = text;
		this.rel = rel;
		this.args = args;
		this.aspect = aspect;
		this.form = form;
		this.tense = tense;
		this.voice = voice;
		this.person = person;
	}

	/**
	 * Make a list of examples from query built from roleSet id
	 *
	 * @param connection connection
	 * @return list of PropBank examples
	 */
	static public List<PbExample> make(final SQLiteDatabase connection, final long roleSetId)
	{
		final List<PbExample> result = new ArrayList<>();
		PbExampleQueryFromRoleSetId query = null;

		try
		{
			query = new PbExampleQueryFromRoleSetId(connection, roleSetId);
			query.execute();

			while (query.next())
			{
				// data from resultset
				final long exampleId = query.getExampleId();
				final String text = query.getText();
				final String rel = query.getRel();
				final List<PbArg> args = query.getArgs();
				final String aspect = query.getAspect();
				final String form = query.getForm();
				final String tense = query.getTense();
				final String voice = query.getVoice();
				final String person = query.getPerson();

				result.add(new PbExample(exampleId, text, rel, args, aspect, form, tense, voice, person));
			}
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return result;
	}
}
