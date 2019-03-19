package org.sqlunet.wordnet;

import android.os.Parcel;
import androidx.annotation.NonNull;

import org.sqlunet.HasWordId;
import org.sqlunet.Pointer;

/**
 * Parcelable word
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class WordPointer extends Pointer implements HasWordId
{
	/**
	 * Static field used to regenerate object, individually or as arrays
	 */
	static public final Creator<WordPointer> CREATOR = new Creator<WordPointer>()
	{
		@Override
		public WordPointer createFromParcel(@NonNull final Parcel parcel)
		{
			return new WordPointer(parcel);
		}

		@Override
		public WordPointer[] newArray(final int size)
		{
			return new WordPointer[size];
		}
	};

	/**
	 * Constructor from parcel, reads back fields IN THE ORDER they were written
	 */
	private WordPointer(@NonNull final Parcel parcel)
	{
		super(parcel);
	}

	/**
	 * Constructor
	 *
	 * @param wordId word id
	 */
	public WordPointer(final long wordId)
	{
		super(wordId);
	}

	@Override
	public long getWordId()
	{
		return this.id;
	}

	@NonNull
	@Override
	public String toString()
	{
		return "wordid=" + this.id;
	}
}
