/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.xn

import android.os.Bundle
import android.os.Parcelable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import org.sqlunet.bnc.browser.BNCFragment
import org.sqlunet.browser.BaseBrowse2Fragment
import org.sqlunet.browser.R
import org.sqlunet.browser.xn.Settings.getAllPref
import org.sqlunet.browser.xn.web.WebFragment
import org.sqlunet.browser.xn.xselector.XSelectorPointer
import org.sqlunet.browser.xn.xselector.XSelectorsFragment
import org.sqlunet.framenet.browser.FrameNetFragment
import org.sqlunet.propbank.browser.PropBankFragment
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.settings.Settings
import org.sqlunet.settings.Settings.DetailViewMode
import org.sqlunet.speak.Pronunciation.Companion.pronunciations
import org.sqlunet.speak.Settings.findCountry
import org.sqlunet.speak.Settings.findVoiceFor
import org.sqlunet.speak.SpeakButton.appendClickableImage
import org.sqlunet.speak.TTS.Companion.pronounce
import org.sqlunet.style.Factories
import org.sqlunet.style.Spanner.Companion.append
import org.sqlunet.verbnet.browser.VerbNetFragment
import org.sqlunet.wordnet.browser.SenseFragment

/**
 * A fragment representing a detail
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class Browse2Fragment : BaseBrowse2Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        val alt = args != null && args.getBoolean(ARG_ALT)
        layoutId = if (alt) R.layout.fragment_browse2_multi_alt else R.layout.fragment_browse2_multi
    }

    /**
     * Search
     */
    override fun search() {
        val context = requireContext()
        if (!isAdded) {
            return
        }
        val manager = getChildFragmentManager()
        targetView!!.movementMethod = LinkMovementMethod()
        targetView!!.text = toTarget()

        // args
        val recurse = org.sqlunet.wordnet.settings.Settings.getRecursePref(context)
        val parameters = org.sqlunet.wordnet.settings.Settings.getRenderParametersPref(requireContext())
        val args = Bundle()
        args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer)
        args.putString(ProviderArgs.ARG_HINTPOS, pos)
        args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse)
        args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters)

        // detail fragment
        val mode: DetailViewMode = Settings.getDetailViewModePref(context)
        when (mode) {
            DetailViewMode.VIEW -> {
                var enable = getAllPref(context)
                if (pointer is XSelectorPointer) {
                    // sections to disable
                    var mask = 0
                    val xpointer = pointer as XSelectorPointer?
                    val group = xpointer!!.xGroup
                    when (group) {
                        XSelectorsFragment.GROUPID_WORDNET -> mask = org.sqlunet.browser.xn.Settings.ENABLE_VERBNET or org.sqlunet.browser.xn.Settings.ENABLE_PROPBANK or org.sqlunet.browser.xn.Settings.ENABLE_FRAMENET
                        XSelectorsFragment.GROUPID_VERBNET -> mask = org.sqlunet.browser.xn.Settings.ENABLE_PROPBANK or org.sqlunet.browser.xn.Settings.ENABLE_FRAMENET
                        XSelectorsFragment.GROUPID_PROPBANK -> mask = org.sqlunet.browser.xn.Settings.ENABLE_VERBNET or org.sqlunet.browser.xn.Settings.ENABLE_FRAMENET
                        XSelectorsFragment.GROUPID_FRAMENET -> mask = org.sqlunet.browser.xn.Settings.ENABLE_VERBNET or org.sqlunet.browser.xn.Settings.ENABLE_PROPBANK
                    }
                    enable = enable and mask.inv()
                }

                // transaction
                val transaction = manager.beginTransaction().setReorderingAllowed(true)

                // wordnet
                if (enable and org.sqlunet.browser.xn.Settings.ENABLE_WORDNET != 0) {
                    // var labelView = findViewById(R.id.label_wordnet)
                    // labelView.setVisibility(View.VISIBLE)
                    val senseFragment = SenseFragment()
                    senseFragment.setArguments(args)
                    senseFragment.setExpand(wordNetOnly(pointer))
                    transaction.replace(R.id.container_wordnet, senseFragment, SenseFragment.FRAGMENT_TAG)
                } else {
                    val senseFragment = manager.findFragmentByTag(SenseFragment.FRAGMENT_TAG)
                    if (senseFragment != null) {
                        transaction.remove(senseFragment)
                    }
                }

                // verbnet
                if (enable and org.sqlunet.browser.xn.Settings.ENABLE_VERBNET != 0) {
                    // var labelView = findViewById(R.id.label_verbnet)
                    // labelView.setVisibility(View.VISIBLE)
                    val verbnetFragment: Fragment = VerbNetFragment()
                    verbnetFragment.setArguments(args)
                    transaction.replace(R.id.container_verbnet, verbnetFragment, VerbNetFragment.FRAGMENT_TAG)
                } else {
                    val verbnetFragment = manager.findFragmentByTag(VerbNetFragment.FRAGMENT_TAG)
                    if (verbnetFragment != null) {
                        transaction.remove(verbnetFragment)
                    }
                }

                // propbank
                if (enable and org.sqlunet.browser.xn.Settings.ENABLE_PROPBANK != 0) {
                    // var labelView = findViewById(R.id.label_propbank)
                    // labelView.setVisibility(View.VISIBLE)
                    val propbankFragment: Fragment = PropBankFragment()
                    propbankFragment.setArguments(args)
                    transaction.replace(R.id.container_propbank, propbankFragment, PropBankFragment.FRAGMENT_TAG)
                } else {
                    val propbankFragment = manager.findFragmentByTag(PropBankFragment.FRAGMENT_TAG)
                    if (propbankFragment != null) {
                        transaction.remove(propbankFragment)
                    }
                }

                // framenet
                if (enable and org.sqlunet.browser.xn.Settings.ENABLE_FRAMENET != 0) {
                    // var labelView = findViewById(R.id.label_framenet)
                    // labelView.setVisibility(View.VISIBLE)
                    val framenetFragment: Fragment = FrameNetFragment()
                    framenetFragment.setArguments(args)
                    transaction.replace(R.id.container_framenet, framenetFragment, FrameNetFragment.FRAGMENT_TAG)
                } else {
                    val framenetFragment = manager.findFragmentByTag(FrameNetFragment.FRAGMENT_TAG)
                    if (framenetFragment != null) {
                        transaction.remove(framenetFragment)
                    }
                }

                // bnc
                if (enable and org.sqlunet.browser.xn.Settings.ENABLE_BNC != 0) {
                    // var labelView = findViewById(R.id.label_bnc)
                    // labelView.setVisibility(View.VISIBLE)
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

            DetailViewMode.WEB -> {
                // web fragment
                val webFragment: Fragment = WebFragment()
                webFragment.setArguments(args)

                // detail fragment replace
                manager.beginTransaction() 
                    .setReorderingAllowed(true) 
                    .replace(R.id.container_web, webFragment, WebFragment.FRAGMENT_TAG) 
                    .commit()
            }
        }
    }

    /**
     * Determine whether to expand
     *
     * @param pointer pointer
     * @return whether to expand
     */
    private fun wordNetOnly(pointer: Parcelable?): Boolean {
        if (pointer is XSelectorPointer) {
            return pointer.wordNetOnly()
        }
        return false
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
