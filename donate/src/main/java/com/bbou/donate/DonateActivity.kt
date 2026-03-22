/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.donate

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.annotation.DrawableRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import com.android.billingclient.api.Purchase
import com.bbou.donate.billing.BillingManager
import com.bbou.donate.billing.BillingManager.BillingListener
import com.bbou.donate.billing.Products.inappProducts
import com.bbou.donate.billing.Products.init
import com.google.android.material.button.MaterialButton
import org.sqlunet.browser.BaseActivity
import java.util.Date
import org.sqlunet.core.R as CoreR

/**
 * Donate
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class DonateActivity : BaseActivity(), BillingListener {

    /**
     * Adapter to in-app billing
     */
    private var billingManager: BillingManager? = null

    /**
     * Product id to buttons
     */
    private val buttonsByProductId: MutableMap<String, MaterialButton> = HashMap()

    // L I F E C Y C L E   A N D   S E T U P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // content view
        setContentView(R.layout.activity_donate)

        // init product ids from resources
        init(this)
        val n = inappProducts.size

        // image buttons
        val buttons = Array(n) {
            val button: MaterialButton = findViewById(BUTTON_IDS[it])!!
            // click: donate
            button.setOnClickListener { button2: View ->
                val tag = (button2.tag as String).toInt()
                Log.d(TAG, "clicked $tag")
                donate(inappProducts[tag])
            }
            // long click: info
            button.setOnLongClickListener { button2: View ->
                val tag = (button2.tag as String).toInt()
                Log.d(TAG, "long clicked $tag")
                val productId = inappProducts[tag]
                val sb = StringBuilder()
                    .apply {
                        val purchase = if (billingManager == null) null else billingManager!!.productIdToPurchase(productId)
                        if (purchase != null) {
                            Log.i(TAG, purchase.toString())
                            append("Order ID: ")
                            append(purchase.orderId)
                            append('\n')
                            append("Products: ")
                            for (productId2 in purchase.products) {
                                append(productId2)
                                append(' ')
                            }
                            append('\n')
                            append("Date: ")
                            append(Date(purchase.purchaseTime))
                            append('\n')
                            append("Token: ")
                            append(purchase.purchaseToken)
                            if (purchase.isAcknowledged) {
                                append('\n')
                                append("Acknowledged")
                            }
                        } else {
                            append("ProductId: ")
                            append(productId)
                        }
                    }
                inform(sb.toString())
                true
            }
            button
        }

        // image buttons per product
        buttonsByProductId.clear()
        for (i in 0 until n) {
            buttonsByProductId[inappProducts[i]] = buttons[i]
        }

        // consume button
        findViewById<Button>(R.id.consume).setOnClickListener { onConsume() }

        // setup manager
        if (billingManager == null) {
            billingManager = BillingManager(this, this)
        }

        // toolbar
        val toolbar = findViewById<Toolbar>(CoreR.id.toolbar)
        setSupportActionBar(toolbar)

        // set up the action bar
        val actionBar = supportActionBar!!
        actionBar.displayOptions = ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE
    }

    override fun onResume() {
        super.onResume()
        billingManager?.queryPurchases()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (billingManager != null) {
            try {
                billingManager!!.destroy()
            } catch (_: Exception) {
                // ignore
            }
        }
    }

    // S E T U P   C O M P L E T E   L I S T E N E R

    override fun onBillingClientSetupFinished() {
        Log.d(TAG, "onBillingClientSetupFinished()")
        // Trigger initial query as soon as connection is ready
        billingManager?.queryPurchases()
    }

    // P U R C H A S E  L I S T E N E R

    override fun onPurchaseList(purchases: List<Purchase>) {
        Log.d(TAG, "onPurchaseList() count=${purchases.size}")
        runOnUiThread {
            // reset all buttons and overlays
            for (productId in inappProducts) {
                update(productId, false)
            }

            // set buttons and overlays
            for (purchase in purchases) {
                Log.d(TAG, "Owned $purchase")
                for (productId in purchase.products) {
                    update(productId, true)
                }
            }
        }
    }

    override fun onPurchaseFinished(purchase: Purchase) {
        Log.d(TAG, "New purchase $purchase")
        runOnUiThread {
            for (productId in purchase.products) {
                update(productId, true)
            }
        }
    }

    // C O N S U M E

    private fun consumeAll() {
        if (billingManager != null) {
            billingManager!!.consumeAll()
        }
    }

    private fun onConsume() {
        Log.d(TAG, "onConsume()")
        if (billingManager != null) {
            billingManager!!.consumeAll()
        }
    }

    override fun onConsumeFinished(purchase: Purchase) {
        Log.d(TAG, "onConsumeFinished() $purchase")
        runOnUiThread {
            for (productId in purchase.products) {
                update(productId, false)
            }
        }
    }

    // D O N A T E

    private fun donate(productId: String) {
        if (billingManager != null) {
            billingManager!!.initiatePurchaseFlow(productId)
        }
    }

    // H E L P E R

    private fun update(productId: String, isOwned: Boolean) {
        val button = buttonsByProductId[productId] ?: return
        val tag = (button.tag as String).toIntOrNull() ?: return
        val baseDrawable = getDrawable(this, DRAWABLE_IDS[tag]) ?: return
        
        if (isOwned) {
            // Fresh overlay instance to avoid sharing between buttons
            val overlayDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_overlay)?.mutate()
            if (overlayDrawable is BitmapDrawable) {
                overlayDrawable.gravity = Gravity.TOP or Gravity.END
            }
            
            val layerDrawable = LayerDrawable(arrayOf(baseDrawable, overlayDrawable!!))
            button.icon = layerDrawable
        } else {
            button.icon = baseDrawable
        }
    }

    private fun inform(message: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.title_donate)
            .setMessage(message)
            .show()
    }

    // M E N U

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // inflate the menu; this adds items to the type bar if it is present.
        menuInflater.inflate(R.menu.donate, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_donate_refresh && billingManager != null) {
            billingManager!!.queryPurchases()
            return true
        }
        return false
    }

    companion object {

        private const val TAG = "DonateA"
        private val BUTTON_IDS = intArrayOf(R.id.buyButton1, R.id.buyButton2, R.id.buyButton3, R.id.buyButton4, R.id.buyButton5)
        private val DRAWABLE_IDS = intArrayOf(R.drawable.ic_donate1, R.drawable.ic_donate2, R.drawable.ic_donate3, R.drawable.ic_donate4, R.drawable.ic_donate5)
        private fun getDrawable(context: Context, @DrawableRes resId: Int): Drawable? {
            return AppCompatResources.getDrawable(context, resId)
        }
    }
}
