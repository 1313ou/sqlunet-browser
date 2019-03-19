package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Layer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnLayer
{
	/**
	 * Layer id
	 */
	public final long layerId;

	/**
	 * Layer type
	 */
	public final String layerType;

	/**
	 * Layer rank
	 */
	public final int rank;

	/**
	 * AnnoSet id
	 */
	public final long annoSetId;

	/**
	 * Labels
	 */
	public final List<FnLabel> labels;

	/**
	 * Constructor
	 *
	 * @param layerId   layer id
	 * @param layerType layer type
	 * @param annoSetId annoSetId
	 * @param rank      layer rank
	 * @param labels    labels
	 */
	private FnLayer(final long layerId, final String layerType, final long annoSetId, final int rank, final List<FnLabel> labels)
	{
		super();
		this.layerId = layerId;
		this.layerType = layerType;
		this.annoSetId = annoSetId;
		this.rank = rank;
		this.labels = labels;
	}

	/**
	 * From-sentence factory
	 *
	 * @param connection connection
	 * @param sentenceId sentence id
	 * @return layers
	 */
	@Nullable
	static public List<FnLayer> makeFromSentence(final SQLiteDatabase connection, final long sentenceId)
	{
		List<FnLayer> result = null;
		FnLayerQueryFromSentenceId query = null;
		try
		{
			query = new FnLayerQueryFromSentenceId(connection, sentenceId);
			query.execute();

			while (query.next())
			{
				final long layerId = query.getLayerId();
				final String layerType = query.getLayerType();
				final int rank = query.getRank();
				final long annoSetId = query.getAnnoSetId();
				final List<FnLabel> labels = query.getLabels();
				if (result == null)
				{
					result = new ArrayList<>();
				}
				result.add(new FnLayer(layerId, layerType, annoSetId, rank, labels));
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

	/**
	 * From-annoSet factory
	 *
	 * @param connection connection
	 * @param annoSetId  annoset id
	 * @return layers
	 */
	@Nullable
	static public List<FnLayer> makeFromAnnoSet(final SQLiteDatabase connection, final long annoSetId)
	{
		List<FnLayer> result = null;
		FnLayerQueryFromAnnoSetId query = null;
		try
		{
			query = new FnLayerQueryFromAnnoSetId(connection, annoSetId);
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
				result.add(new FnLayer(layerId, layerType, annoSetId, rank, labels));
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
