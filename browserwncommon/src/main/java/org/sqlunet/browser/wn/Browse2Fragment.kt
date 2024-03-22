/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.wn

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import org.sqlunet.bnc.browser.BNCFragment
import org.sqlunet.browser.BaseBrowse2Fragment
import org.sqlunet.browser.wn.Settings.getAllPref
import org.sqlunet.browser.wn.lib.R
import org.sqlunet.browser.wn.web.WebFragment
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.settings.Settings
import org.sqlunet.speak.Pronunciation.Companion.pronunciations
import org.sqlunet.speak.Settings.findCountry
import org.sqlunet.speak.Settings.findVoiceFor
import org.sqlunet.speak.SpeakButton.appendClickableImage
import org.sqlunet.speak.TTS.Companion.pronounce
import org.sqlunet.style.Factories
import org.sqlunet.style.Spanner.Companion.append
import org.sqlunet.wordnet.browser.SenseFragment

/**
 * A fragment representing a detail
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class Browse2Fragment : BaseBrowse2Fragment() {

    /**
     * Search
     */
    override fun search() {
        val context = requireContext()
        if (!isAdded) {
            return
        }
        val manager = getChildFragmentManager()
        assert(targetView != null)
        targetView!!.movementMethod = LinkMovementMethod()
        targetView!!.text = toTarget()

        // parameters
        val recurse = org.sqlunet.wordnet.settings.Settings.getRecursePref(context)
        val parameters = org.sqlunet.wordnet.settings.Settings.getRenderParametersPref(requireContext())
        val args = Bundle()
        args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer)
        args.putString(ProviderArgs.ARG_HINTWORD, word)
        args.putString(ProviderArgs.ARG_HINTCASED, cased)
        args.putString(ProviderArgs.ARG_HINTPRONUNCIATION, pronunciation)
        args.putString(ProviderArgs.ARG_HINTPOS, pos)
        args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse)
        args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters)

        // detail fragment
        val mode: Settings.DetailViewMode = Settings.getDetailViewModePref(context)
        when (mode) {
            Settings.DetailViewMode.VIEW -> {

                // transaction
                val transaction = manager.beginTransaction().setReorderingAllowed(true)
                val enable = getAllPref(context)

                // wordnet
                if (enable and org.sqlunet.browser.wn.Settings.ENABLE_WORDNET != 0) {
                    // val labelView: View = findViewById(R.id.label_wordnet);
                    // labelView.setVisibility(View.VISIBLE);
                    val senseFragment = SenseFragment()
                    senseFragment.setArguments(args)
                    senseFragment.setExpand(false)
                    transaction.replace(R.id.container_wordnet, senseFragment, SenseFragment.FRAGMENT_TAG)
                } else {
                    val senseFragment = manager.findFragmentByTag(SenseFragment.FRAGMENT_TAG)
                    if (senseFragment != null) {
                        transaction.remove(senseFragment)
                    }
                }

                // bnc
                if (enable and org.sqlunet.browser.wn.Settings.ENABLE_BNC != 0) {
                    // val labelView: View = findViewById(R.id.label_bnc)
                    // labelView.visibility = View.VISIBLE;
                    val bncFragment: Fragment = BNCFragment()
                    bncFragment.setArguments(args)
                    transaction.replace(R.id.container_bnc, bncFragment, BNCFragment.FRAGMENT_TAG)
                } else {
                    val bncFragment = manager.findFragmentByTag(BNCFragment.FRAGMENT_TAG)
                    if (bncFragment != null) {
                        transaction.remove(bncFragment)
                    }
                }
                transaction.commit()
            }

            Settings.DetailViewMode.WEB -> {
                // web fragment
                val webFragment: Fragment = WebFragment()
                webFragment.setArguments(args)

                // detail fragment replace
                manager.beginTransaction() //
                    .setReorderingAllowed(true) //
                    .replace(R.id.container_web, webFragment, WebFragment.FRAGMENT_TAG) //
                    .commit()
            }
        }
    }

    /**
     * Search target info
     *
     * @return styled string
     */
    private fun toTarget(): CharSequence {
        val sb = SpannableStringBuilder()
        if (cased != null) {
            sb.append(' ')
            append(sb, cased, 0, Factories.casedFactory)
        } else {
            append(sb, word, 0, Factories.wordFactory)
        }
        if (pos != null) {
            sb.append(' ')
            append(sb, pos, 0, Factories.posFactory)
        }
        if (pronunciation != null) {
            val pronunciations = pronunciations(pronunciation)
            for (p in pronunciations!!) {
                val label = p.toString()
                val ipa = p.ipa
                val country = if (p.variety == null) findCountry(requireContext()) else p.variety
                sb.append('\n')
                appendClickableImage(sb, org.sqlunet.speak.R.drawable.ic_speak_button, label, {
                    Log.d("Speak", "")
                    pronounce(requireContext(), word!!, ipa, country, findVoiceFor(country, requireContext()))
                }, requireContext())
            }
        }
        return sb
    }

    companion object {
        const val ARG_ALT = "alt_arg"
    }
}
