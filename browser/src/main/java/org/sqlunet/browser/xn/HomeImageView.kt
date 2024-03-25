/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.xn

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.text.Html
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.snackbar.Snackbar
import org.sqlunet.browser.R
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.sqrt

internal class HomeImageView : AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun init() {
        setOnTouchListener { v: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_UP -> v.performClick()
                MotionEvent.ACTION_DOWN -> {}
                else -> {}
            }
            var x = event.x
            var y = event.y
            Log.i(TAG, "TOUCH x=$x y=$y")
            val rect = Rect()
            rect.left = left
            rect.top = top
            rect.bottom = bottom
            rect.right = right
            x += rect.left.toFloat()
            y += rect.top.toFloat()
            val w = rect.width().toFloat()
            val h = rect.height().toFloat()
            val r = (min(w.toDouble(), h.toDouble()) / 2).toFloat()
            val cx = rect.centerX().toFloat()
            val cy = rect.centerY().toFloat()
            val d = distance(cx, cy, x, y)
            val a = arg(cx, cy, x, y)

            // Log.i(TAG, "image rect=" + rect)
            // Log.i(TAG, "center " + cx + "," + cy)
            // Log.i(TAG, "dist=" + d)
            // Log.i(TAG, "arg=" + a)
            var i = 0
            while (i < rings.size) {
                val f = rings[i]
                // Log.i("RING" + i, "dmax=" + (r * f));
                if (d < r * f) {
                    break
                }
                i++
            }
            val ring = i
            Log.d(TAG, "ring=$ring")
            i = 0
            while (i < pies.size) {
                val s = pies[i]
                // Log.i("SECTOR" + i, "smax=" + s);
                if (a < s) {
                    break
                }
                i++
            }
            val pie = i % 3
            Log.d(TAG, "pie=$pie")
            var messageId = 0
            when (ring) {
                0 -> messageId = R.string.wordnet_blurb
                1 -> when (pie) {
                    0 -> messageId = R.string.propbank_blurb
                    1 -> messageId = R.string.framenet_blurb
                    2 -> messageId = R.string.verbnet_blurb
                }

                2 -> messageId = R.string.predicatematrix_blurb
            }
            if (messageId != 0) {
                val context = context
                val message = context.getString(messageId)
                val html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(message, 0) else Html.fromHtml(message)
                //Log.i("Pick ", message);

//				var toast = Toast.makeText(getContext(), html, Toast.LENGTH_LONG);
//				var view = toast.getView();
//				if (view != null)
//				{
//					view.setBackgroundResource(android.R.drawable.toast_frame);
//					var textView = view.findViewById(android.R.id.message);
//					textView.setBackgroundColor(Color.TRANSPARENT);
//				}
//				toast.show();
                val snackbar = Snackbar.make(v, html, Snackbar.LENGTH_LONG)
                // View snackBarView = snackbar.getView();
                // snackBarView.setBackgroundColor(context.getResources().getColor(R.color.secondaryColor));
                snackbar.show()
            }
            false
        }
    }

    override fun performClick(): Boolean {
        // Calls the super implementation, which generates an AccessibilityEvent and calls the onClick() listener on the view, if any
        super.performClick()
        // Handle the action for the custom click here
        return true
    }

    companion object {
        private const val TAG = "HomeImageView"
        private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
            val dx = x2 - x1
            val dy = y2 - y1
            return sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        }

        private fun arg(x1: Float, y1: Float, x2: Float, y2: Float): Double {
            val dx = x2 - x1
            val dy = y2 - y1
            val a = atan2(dy.toDouble(), dx.toDouble())
            // return a;
            return if (a < 0) 2 * Math.PI + a else a
        }

        /**
         * Rings
         */
        private val rings = floatArrayOf(0.370f, 0.795f, 1f, Float.MAX_VALUE)

        /* Pies */
        private val pies = doubleArrayOf(Math.PI / 6f, Math.PI * 5f / 6f, Math.PI * 3f / 2f, Math.PI * 2f, Double.MAX_VALUE)
    }
}
