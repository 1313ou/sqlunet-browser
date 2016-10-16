package org.sqlunet.propbank.sql;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

/**
 * Example attached to a PropBank roleset
 *
 * @author Bernard Bou
 */
class PbExample
{
	/**
	 * Example id
	 */
	public final long theExampleId;

	/**
	 * Text
	 */
	public final String theText;

	/**
	 * Relation
	 */
	public final PbRel theRel;

	/**
	 * Arguments
	 */
	public final List<PbArg> theArgs;

	/**
	 * Aspect
	 */
	public final String theAspect;

	/**
	 * Form
	 */
	public final String theForm;

	/**
	 * Tense
	 */
	public final String theTense;

	/**
	 * Voice
	 */
	public final String theVoice;

	/**
	 * Person
	 */
	public final String thePerson;

	/**
	 * Constructor
	 *
	 * @param thisExampleId
	 *            is the example id
	 * @param thisText
	 *            is the text of the example
	 * @param thisRel
	 *            is the relation
	 * @param theseArguments
	 *            is the list of arguments
	 */
	private PbExample(final long thisExampleId, final String thisText, final PbRel thisRel, final List<PbArg> theseArguments, final String thisAspect, final String thisForm, final String thisTense, final String thisVoice, final String thisPerson)
	{
		this.theExampleId = thisExampleId;
		this.theText = thisText;
		this.theRel = thisRel;
		this.theArgs = theseArguments;
		this.theAspect = thisAspect;
		this.theForm = thisForm;
		this.theTense = thisTense;
		this.theVoice = thisVoice;
		this.thePerson = thisPerson;
	}

	/**
	 * Make a list of examples from query built from relationsetid
	 *
	 * @param thisConnection
	 *            is the database connection
	 * @return list of PropBank examples
	 */
	static public List<PbExample> make(final SQLiteDatabase thisConnection, final long thisRelationSetId)
	{
		final List<PbExample> thisResult = new ArrayList<>();
		PbExampleQueryCommand thisQuery = null;

		try
		{
			thisQuery = new PbExampleQueryCommand(thisConnection, thisRelationSetId);
			thisQuery.execute();

			while (thisQuery.next())
			{
				// data from resultset

				// example
				final long thisExampleId = thisQuery.getExampleId();
				final String thisText = thisQuery.getText();
				final PbRel thisRel = thisQuery.getRel();
				final List<PbArg> theseArguments = thisQuery.getArgs();
				final String thisAspect = thisQuery.getAspect();
				final String thisForm = thisQuery.getForm();
				final String thisTense = thisQuery.getTense();
				final String thisVoice = thisQuery.getVoice();
				final String thisPerson = thisQuery.getPerson();

				thisResult.add(new PbExample(thisExampleId, thisText, thisRel, theseArguments, thisAspect, thisForm, thisTense, thisVoice, thisPerson));
			}
		}
		finally
		{
			if (thisQuery != null)
			{
				thisQuery.release();
			}
		}
		return thisResult;
	}
}
