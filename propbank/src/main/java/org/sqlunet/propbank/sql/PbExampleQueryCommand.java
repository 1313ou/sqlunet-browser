package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * PropBank example query command
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class PbExampleQueryCommand extends DBQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	private static final String QUERY = SqLiteDialect.PropBankExamplesQuery;

	/**
	 * Constructor
	 *
	 * @param connection is the database connection
	 * @param roleSetId  roleset id to build query from
	 */
	@SuppressWarnings("boxing")
	public PbExampleQueryCommand(final SQLiteDatabase connection, final long roleSetId)
	{
		super(connection, PbExampleQueryCommand.QUERY);
		setParams(roleSetId);
	}

	/**
	 * Get example id from cursor
	 *
	 * @return example id
	 */
	public long getExampleId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get text from cursor
	 *
	 * @return text
	 */
	public String getText()
	{
		return this.cursor.getString(1);
	}

	/**
	 * Get rel from cursor
	 *
	 * @return rel
	 */
	public String getRel()
	{
		return this.cursor.getString(2);
	}

	/**
	 * Get args from cursor
	 *
	 * @return args
	 */
	public List<PbArg> getArgs()
	{
		final String concatArg = this.cursor.getString(3);
		if (concatArg == null)
		{
			return null;
		}
		final List<PbArg> args = new ArrayList<>();
		for (final String arg : concatArg.split("\\|")) //$NON-NLS-1$
		{
			final String[] argFields = arg.split("~"); //$NON-NLS-1$
			args.add(new PbArg(argFields));
		}
		return args;
	}

	/**
	 * Get aspect from cursor
	 *
	 * @return aspect
	 */
	public String getAspect()
	{
		return this.cursor.getString(4);
	}

	/**
	 * Get form from cursor
	 *
	 * @return form
	 */
	public String getForm()
	{
		return this.cursor.getString(5);
	}

	/**
	 * Get tense from cursor
	 *
	 * @return tense
	 */
	public String getTense()
	{
		return this.cursor.getString(6);
	}

	/**
	 * Get voice from cursor
	 *
	 * @return voice
	 */
	public String getVoice()
	{
		return this.cursor.getString(7);
	}

	/**
	 * Get person from cursor
	 *
	 * @return person
	 */
	public String getPerson()
	{
		return this.cursor.getString(8);
	}
}
