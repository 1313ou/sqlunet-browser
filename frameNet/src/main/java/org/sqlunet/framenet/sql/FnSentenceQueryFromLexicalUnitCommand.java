package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * FrameNet sentence query command from lex unit
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnSentenceQueryFromLexicalUnitCommand extends DBQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	private static final String QUERY = SqLiteDialect.FrameNetSentencesFromLexicalUnitQuery;

	/**
	 * Constructor
	 *
	 * @param connection database connection
	 * @param luId       target lex unit id
	 */
	@SuppressWarnings("boxing")
	public FnSentenceQueryFromLexicalUnitCommand(final SQLiteDatabase connection, final long luId)
	{
		super(connection, FnSentenceQueryFromLexicalUnitCommand.QUERY);
		setParams(luId);
	}

	/**
	 * Get the governor id from the result set
	 *
	 * @return the governor id from the result set
	 */
	public long getSentenceId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get the text from the result set
	 *
	 * @return the text from the result set
	 */
	public String getText()
	{
		return this.cursor.getString(1);
	}
}