package org.sqlunet.framenet.sql;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

class FnLayer
{
	public final long layerId;

	public final String layerType;

	public final int rank;

	public final List<FnLabel> labels;

	private FnLayer(final long thisLayerId, final String thisLayerType, final int thisRank, final List<FnLabel> theseLabels)
	{
		super();
		this.layerId = thisLayerId;
		this.layerType = thisLayerType;
		this.rank = thisRank;
		this.labels = theseLabels;
	}

	public static List<FnLayer> makeFromAnnoSet(final SQLiteDatabase thisConnection, final long thisAnnoSetId)
	{
		List<FnLayer> thisResult = null;
		FnLayerQueryFromAnnoSetCommand thisQuery = null;
		try
		{
			thisQuery = new FnLayerQueryFromAnnoSetCommand(thisConnection, thisAnnoSetId);
			thisQuery.execute();

			while (thisQuery.next())
			{
				final long thisLayerId = thisQuery.getLayerId();
				final String thisLayerType = thisQuery.getLayerType();
				final int thisRank = thisQuery.getRank();
				final List<FnLabel> theseLabels = thisQuery.getLabels();
				if (thisResult == null)
				{
					thisResult = new ArrayList<>();
				}
				thisResult.add(new FnLayer(thisLayerId, thisLayerType, thisRank, theseLabels));
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
	
	public static List<FnLayer> makeFromSentence(final SQLiteDatabase thisConnection, final long thisSentenceId)
	{
		List<FnLayer> thisResult = null;
		FnLayerQueryFromSentenceCommand thisQuery = null;
		try
		{
			thisQuery = new FnLayerQueryFromSentenceCommand(thisConnection, thisSentenceId);
			thisQuery.execute();

			while (thisQuery.next())
			{
				final long thisLayerId = thisQuery.getLayerId();
				final String thisLayerType = thisQuery.getLayerType();
				final int thisRank = thisQuery.getRank();
				final List<FnLabel> theseLabels = thisQuery.getLabels();
				if (thisResult == null)
				{
					thisResult = new ArrayList<>();
				}
				thisResult.add(new FnLayer(thisLayerId, thisLayerType, thisRank, theseLabels));
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
