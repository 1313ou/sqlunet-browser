/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.donate;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.bbou.donate.billing.BillingManager;
import com.bbou.donate.billing.Skus;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Donate
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DonateActivity extends AppCompatActivity implements BillingManager.BillingUpdatesListener
{
	static private final String TAG = "DonateA";

	static private final int[] BUTTON_IDS = {R.id.buyButton1, R.id.buyButton2, R.id.buyButton3, R.id.buyButton4, R.id.buyButton5};

	static private final int[] DRAWABLE_IDS = {R.drawable.ic_donate1, R.drawable.ic_donate2, R.drawable.ic_donate3, R.drawable.ic_donate4, R.drawable.ic_donate5};

	/**
	 * Adapter to in-app billing
	 */
	private BillingManager billingManager;

	/**
	 * Purchases per sku
	 */
	@NonNull
	private final Map<String, Purchase> skuToPurchase = new HashMap<>();

	/**
	 * SKU to Buttons
	 */
	@NonNull
	private final Map<String, ImageButton> skuToButton = new HashMap<>();

	/**
	 * Overlay drawable
	 */
	private Drawable overlay;

	// L I F E C Y C L E   A N D   S E T U P

	@SuppressWarnings("ConstantConditions")
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content view
		setContentView(R.layout.activity_donate);

		// overlay drawable
		final BitmapDrawable overlay = (BitmapDrawable) getDrawable(this, R.drawable.ic_overlay);
		overlay.setGravity(Gravity.TOP | Gravity.END);
		this.overlay = overlay;

		// init skus from resources
		Skus.init(this);
		final String[] inappSkus = Skus.getInappSkus();
		final int n = inappSkus.length;
		if (BuildConfig.DEBUG)
		{
			final int n1 = BUTTON_IDS.length;
			final int n2 = DRAWABLE_IDS.length;
			if (n1 != n || n2 != n)
			{
				throw new RuntimeException("");
			}
		}

		// image buttons
		final ImageButton[] buttons = new ImageButton[n];
		for (int i = 0; i < n; i++)
		{
			// buttons
			buttons[i] = findViewById(BUTTON_IDS[i]);
			buttons[i].setOnClickListener((button) -> {

				int tag = Integer.parseInt((String) button.getTag());
				Log.d(TAG, "clicked " + tag);
				donate(inappSkus[tag]);
			});
			buttons[i].setOnLongClickListener((button) -> {

				int tag = Integer.parseInt((String) button.getTag());
				Log.d(TAG, "long clicked " + tag);
				final String sku = inappSkus[tag];
				final StringBuilder sb = new StringBuilder();
				final Purchase purchase = this.skuToPurchase.get(sku);
				if (purchase != null)
				{
					Log.i(TAG, purchase.toString());
					sb.append("Order ID: ");
					sb.append(purchase.getOrderId());
					sb.append('\n');
					sb.append("SKU: ");
					sb.append(purchase.getSku());
					sb.append('\n');
					sb.append("Date: ");
					sb.append(new Date(purchase.getPurchaseTime()));
					sb.append('\n');
					sb.append("Token: ");
					sb.append(purchase.getPurchaseToken());
					if (purchase.isAcknowledged())
					{
						sb.append('\n');
						sb.append("Acknowledged");
					}
				}
				else
				{
					sb.append("SKU: ");
					sb.append(sku);
				}
				inform(sb.toString());
				return true;
			});
		}

		// image buttons per sku
		this.skuToButton.clear();
		for (int i = 0; i < n; i++)
		{
			this.skuToButton.put(Skus.getInappSkus()[i], buttons[i]);
		}

		// setup manager
		if (this.billingManager == null)
		{
			this.billingManager = new BillingManager(this, this);
		}

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// set up the action bar
		final ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		if (this.billingManager != null)
		{
			try
			{
				this.billingManager.destroy();
			}
			catch (Exception e)
			{
				//
			}
		}
	}

	@Override
	public void onBillingClientSetupFinished()
	{
		Log.d(TAG, "onBillingClientSetupFinished()");
	}

	// P U R C H A S E  L I S T E N E R

	@Override
	public void onPurchasesUpdated(@Nullable final List<Purchase> updatedPurchases)
	{
		Log.d(TAG, "onPurchasesUpdated()");

		// reset data
		this.skuToPurchase.clear();

		// reset all buttons and overlays
		for (String sku : Skus.getInappSkus())
		{
			update(sku, false);
		}

		// update buttons and overlays with purchases
		if (updatedPurchases != null)
		{
			Log.d(TAG, "Purchase count " + updatedPurchases.size());

			// build data
			for (Purchase purchase : updatedPurchases)
			{
				this.skuToPurchase.put(purchase.getSku(), purchase);
			}

			// set buttons and overlays
			for (Purchase purchase : this.skuToPurchase.values())
			{
				Log.d(TAG, "Update " + purchase.toString());
				final String sku = purchase.getSku();
				update(sku, true);
			}
		}
		else
		{
			Log.d(TAG, "Null purchase list");
		}
	}

	private void update(final String sku, boolean isOwned)
	{
		final ImageButton imageButton = this.skuToButton.get(sku);
		if (imageButton != null)
		{
			int tag = Integer.parseInt((String) imageButton.getTag());
			final Drawable drawable = getDrawable(this, DRAWABLE_IDS[tag]);

			if (isOwned)
			{
				final Drawable[] layers = new Drawable[2];
				layers[0] = drawable;
				layers[1] = this.overlay;
				final LayerDrawable layerDrawable = new LayerDrawable(layers);
				imageButton.setImageDrawable(layerDrawable);
			}
			else
			{
				imageButton.setImageDrawable(drawable);
			}
		}
	}

	// C O N S U M E

	public void onConsume(@SuppressWarnings("UnusedParameters") View v)
	{
		if (this.billingManager != null)
		{
			for (Purchase purchase : this.skuToPurchase.values())
			{
				this.billingManager.consume(purchase);
			}
		}
	}

	@Override
	public void onConsumeFinished(final String token, final int result)
	{
		Log.d(TAG, "onConsumeFinished() " + token);
		if (this.billingManager != null)
		{
			// this.billingManager.queryPurchases();

			for (Purchase purchase : this.skuToPurchase.values())
			{
				if (purchase.getPurchaseToken().equals(token))
				{
					final String sku = purchase.getSku();
					this.skuToPurchase.remove(sku);
					update(sku, false);
					break;
				}
			}
		}
	}

	// D O N A T E

	private void donate(final String sku)
	{
		if (this.billingManager != null)
		{
			this.billingManager.initiatePurchaseFlow(sku, BillingClient.SkuType.INAPP);
		}
	}

	// H E L P E R

	@SuppressWarnings("deprecation")
	static public Drawable getDrawable(@NonNull final Context context, @DrawableRes int drawableRes)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			return context.getResources().getDrawable(drawableRes, context.getTheme());
		}
		else
		{
			//noinspection deprecation
			return context.getResources().getDrawable(drawableRes);
		}
	}

	private void inform(@NonNull final String message)
	{
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.title_donate);
		alert.setMessage(message);
		alert.show();
	}

	// M E N U

	@SuppressWarnings("SameReturnValue")
	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		// inflate the menu; this adds items to the type bar if it is present.
		getMenuInflater().inflate(R.menu.donate, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull final MenuItem item)
	{
		if (item.getItemId() == R.id.action_donate_refresh && this.billingManager != null)
		{
			this.billingManager.queryPurchases();
			return true;
		}
		return false;
	}
}
