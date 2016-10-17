package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Layer
 *
 * @author Bernard Bou
 */
class FnLayer
{
	public final long layerId;

	public final String layerType;

	public final int rank;

	public final List<FnLabel> labels;

	private FnLayer(final long layerId, final String layerType, final int rank, final List<FnLabel> labels)
	{
		super();
		this.layerId = layerId;
		this.layerType = layerType;
		this.rank = rank;
		this.labels = labels;
	}

	public static List<FnLayer> makeFromAnnoSet(final SQLiteDatabase connection, final long annoSetId)
	{
		List<FnLayer> result = null;
		FnLayerQueryFromAnnoSetCommand query = null;
		try
		{
			query = new FnLayerQueryFromAnnoSetCommand(connection, annoSetId);
			query.execute();

			while (query.next())
			{
				final long layerId = query.getLayerId();
				final String layerType = query.getLayerType();
				final int rank = query.getRank();
				final List<FnLabel> labels = query.getLabels();
				if (result == null)
				{
					result = new ArrayList<>();
				}
				result.add(new FnLayer(layerId, layerType, rank, labels));
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

	public static List<FnLayer> makeFromSentence(final SQLiteDatabase connection, final long sentenceId)
	{
		List<FnLayer> result = null;
		FnLayerQueryFromSentenceCommand query = null;
		try
		{
			query = new FnLayerQueryFromSentenceCommand(connection, sentenceId);
			query.execute();

			while (query.next())
			{
				final long layerId = query.getLayerId();
				final String layerType = query.getLayerType();
				final int rank = query.getRank();
				final List<FnLabel> labels = query.getLabels();
				if (result == null)
				{
					result = new ArrayList<>();
				}
				result.add(new FnLayer(layerId, layerType, rank, labels));
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
