/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.preference

import android.os.Parcel
import android.os.Parcelable
import androidx.preference.Preference

/**
 * Saved state
 */
internal class StringSavedState : Preference.BaseSavedState {

    /**
     * The value
     */
    var value: String? = null

    /**
     * Constructor from superstate
     *
     * @param superState superstate
     */
    constructor(superState: Parcelable?) : super(superState)

    /**
     * Constructor from parcel
     *
     * @param parcel source parcel
     */
    constructor(parcel: Parcel) : super(parcel) {

        // get the preference's value
        value = parcel.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)

        // write the preference's value
        dest.writeString(value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        @Suppress("unused")
        @JvmField
        val CREATOR = object : Parcelable.Creator<StringSavedState> {

            override fun createFromParcel(parcel: Parcel): StringSavedState {
                return StringSavedState(parcel)
            }

            override fun newArray(size: Int): Array<StringSavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}
