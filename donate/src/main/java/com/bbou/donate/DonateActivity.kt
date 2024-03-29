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
import android.widget.ImageButton
import androidx.annotation.DrawableRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import com.android.billingclient.api.Purchase
import com.bbou.donate.billing.BillingManager
import com.bbou.donate.billing.BillingManager.BillingListener
import com.bbou.donate.billing.Products
import com.bbou.donate.billing.Products.inappProducts
import com.bbou.donate.billing.Products.init
import java.util.Date

/**
 * Donate
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class DonateActivity : AppCompatActivity(), BillingListener {

    /**
     * Adapter to in-app billing
     */
    private var billingManager: BillingManager? = null

    /**
     * Product id to buttons
     */
    private val buttonsByProductId: MutableMap<String, ImageButton?> = HashMap()

    /**
     * Overlay drawable
     */
    private var overlay: Drawable? = null

    // L I F E C Y C L E   A N D   S E T U P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // content view
        setContentView(R.layout.activity_donate)

        // overlay drawable
        val overlay = (getDrawable(this, R.drawable.ic_overlay) as BitmapDrawable?)!!
        overlay.gravity = Gravity.TOP or Gravity.END
        this.overlay = overlay

        // init product ids from resources
        init(this)
        val inappProducts = inappProducts!!
        val n = inappProducts.size

        // image buttons
        val buttons = Array(n) {
            val button: ImageButton = findViewById(BUTTON_IDS[it])!!
            button.setOnClickListener { button2: View ->
                val tag = (button2.tag as String).toInt()
                Log.d(TAG, "clicked $tag")
                donate(inappProducts[tag])
            }
            button.setOnLongClickListener { button2: View ->
                val tag = (button2.tag as String).toInt()
                Log.d(TAG, "long clicked $tag")
                val productId = inappProducts[tag]
                val sb = StringBuilder()
                val purchase = if (billingManager == null) null else billingManager!!.productIdToPurchase(productId)
                if (purchase != null) {
                    Log.i(TAG, purchase.toString())
                    sb.append("Order ID: ")
                    sb.append(purchase.orderId)
                    sb.append('\n')
                    sb.append("Products: ")
                    for (productId2 in purchase.products) {
                        sb.append(productId2)
                        sb.append(' ')
                    }
                    sb.append('\n')
                    sb.append("Date: ")
                    sb.append(Date(purchase.purchaseTime))
                    sb.append('\n')
                    sb.append("Token: ")
                    sb.append(purchase.purchaseToken)
                    if (purchase.isAcknowledged) {
                        sb.append('\n')
                        sb.append("Acknowledged")
                    }
                } else {
                    sb.append("ProductId: ")
                    sb.append(productId)
                }
                inform(sb.toString())
                true
            }
            button
        }

        // image buttons per product
        buttonsByProductId.clear()
        for (i in 0 until n) {
            buttonsByProductId[Products.inappProducts!![i]] = buttons[i]
        }

        // consume button
        val btn = findViewById<Button>(R.id.consume)
        btn.setOnClickListener { onConsume() }

        // setup manager
        if (billingManager == null) {
            billingManager = BillingManager(this, this)
        }

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // set up the action bar
        val actionBar = supportActionBar!!
        actionBar.displayOptions = ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE
    }

    override fun onDestroy() {
        super.onDestroy()
        if (billingManager != null) {
            try {
                billingManager!!.destroy()
            } catch (ignored: Exception) {
                // ignore
            }
        }
    }

    override fun onBillingClientSetupFinished() {
        Log.d(TAG, "onBillingClientSetupFinished()")
    }

    // P U R C H A S E  L I S T E N E R

    override fun onPurchaseFinished(purchase: Purchase) {
        Log.d(TAG, "New purchase $purchase")
        for (productId in purchase.products) {
            update(productId, true)
        }
    }

    @Synchronized
    override fun onPurchaseList(purchases: List<Purchase>) {
        Log.d(TAG, "onPurchaseList()")

        // reset all buttons and overlays
        val productIds = inappProducts
        if (productIds != null) {
            for (productId in productIds) {
                update(productId, false)
            }
        }

        // update buttons and overlays with purchases
        Log.d(TAG, "Purchase count " + purchases.size)

        // set buttons and overlays
        for (purchase in purchases) {
            Log.d(TAG, "Owned $purchase")
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

    override fun onConsumeFinished(purchase: Purchase) {
        Log.d(TAG, "onConsumeFinished() $purchase")
        for (productId in purchase.products) {
            update(productId, false)
        }
    }

    private fun onConsume() {
        Log.d(TAG, "onConsume()")
        consumeAll()
    }

    // D O N A T E

   private fun donate(productId: String) {
        if (billingManager != null) {
            billingManager!!.initiatePurchaseFlow(productId)
        }
    }

    // H E L P E R

    private fun update(productId: String, isOwned: Boolean) {
        val imageButton = buttonsByProductId[productId]
        if (imageButton != null) {
            val tag = (imageButton.tag as String).toInt()
            val drawable = getDrawable(this, DRAWABLE_IDS[tag])
            if (isOwned) {
                val layers = arrayOfNulls<Drawable>(2)
                layers[0] = drawable
                layers[1] = overlay
                val layerDrawable = LayerDrawable(layers)
                imageButton.setImageDrawable(layerDrawable)
            } else {
                imageButton.setImageDrawable(drawable)
            }
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
