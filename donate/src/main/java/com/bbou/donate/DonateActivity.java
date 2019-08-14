/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.donate;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.bbou.donate.billing.BillingManager;
import com.bbou.donate.billing.Skus;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	/**
	 * Adapter to in-app billing
	 */
	private BillingManager billingManager;

	/**
	 * Purchases per token
	 */
	@Nullable
	private final Map<String, Purchase> purchases = new HashMap<>();

	/**
	 * SKU to Buttons
	 */
	@Nullable
	private final Map<String, FloatingActionButton> skuToButton = new HashMap<>();

	/**
	 * SKU to Buttons
	 */
	@Nullable
	private final Map<String, ImageView> skuToOverlay = new HashMap<>();

	// L I F E C Y C L E   A N D   S E T U P

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy);

		final int n = Skus.INAPP_SKUS.length;

		// buttons
		final FloatingActionButton[] buttons = new FloatingActionButton[n];
		buttons[0] = findViewById(R.id.buyButton1);
		buttons[1] = findViewById(R.id.buyButton2);
		buttons[2] = findViewById(R.id.buyButton3);
		buttons[3] = findViewById(R.id.buyButton4);
		buttons[4] = findViewById(R.id.buyButton5);

		// overlays
		final ImageView[] overlays = new ImageView[n];
		overlays[0] = findViewById(R.id.overlay1);
		overlays[1] = findViewById(R.id.overlay2);
		overlays[2] = findViewById(R.id.overlay3);
		overlays[3] = findViewById(R.id.overlay4);
		overlays[4] = findViewById(R.id.overlay5);

		// sku maps
		assert this.skuToButton != null;
		this.skuToButton.clear();
		assert this.skuToOverlay != null;
		this.skuToOverlay.clear();
		for (int i = 0; i < n; i++)
		{
			this.skuToButton.put(Skus.INAPP_SKUS[i], buttons[i]);
			this.skuToOverlay.put(Skus.INAPP_SKUS[i], overlays[i]);
		}

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// set up the action bar
		final ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

		// setup manager
		if (this.billingManager == null)
		{
			this.billingManager = new BillingManager(this, this);
		}
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
		this.purchases.clear();

		// reset buttons and overlays
		for (String sku : Skus.INAPP_SKUS)
		{
			update(sku, false);
		}

		if (updatedPurchases != null)
		{
			Log.d(TAG, "purchase list size " + this.purchases.size());

			// build data
			for (Purchase purchase : updatedPurchases)
			{
				this.purchases.put(purchase.getPurchaseToken(), purchase);
			}

			// set buttons and overlays
			for (Purchase purchase : this.purchases.values())
			{
				Log.d(TAG, purchase + " acknowledged=" + purchase.isAcknowledged() + " time=" + purchase.getPurchaseTime());
				final String sku = purchase.getSku();
				update(sku, true);
			}
		}
		else
		{
			Log.d(TAG, "null purchase list");
		}
	}

	private void update(final String sku, boolean isOwned)
	{
		final ImageButton imageButton = this.skuToButton.get(sku);
		assert imageButton != null;
		final ImageView overlay = this.skuToOverlay.get(sku);
		assert overlay != null;

		imageButton.setEnabled(!isOwned);
		overlay.setVisibility(isOwned ? View.VISIBLE : View.INVISIBLE);
	}

	// C O N S U M E

	public void onConsume(@SuppressWarnings("UnusedParameters") View v)
	{
		if (this.billingManager != null)
		{
			for (Purchase purchase : this.purchases.values())
			{
				this.billingManager.consume(purchase.getPurchaseToken());
			}
		}
	}

	@Override
	public void onConsumeFinished(final String token, final int result)
	{
		Log.d(TAG, "onConsumeFinished() " + token);
		final Purchase purchase = this.purchases.get(token);
		if (purchase != null)
		{
			final String sku = purchase.getSku();
			update(sku, false);
		}
	}

	// B U Y

	public void onBuy1(@SuppressWarnings("UnusedParameters") View v)
	{
		if (this.billingManager != null)
		{
			this.billingManager.initiatePurchaseFlow(Skus.SKU_DONATE1, BillingClient.SkuType.INAPP);
		}
	}

	public void onBuy2(@SuppressWarnings("UnusedParameters") View v)
	{
		if (this.billingManager != null)
		{
			this.billingManager.initiatePurchaseFlow(Skus.SKU_DONATE2, BillingClient.SkuType.INAPP);
		}
	}

	public void onBuy3(@SuppressWarnings("UnusedParameters") View v)
	{
		if (this.billingManager != null)
		{
			this.billingManager.initiatePurchaseFlow(Skus.SKU_DONATE3, BillingClient.SkuType.INAPP);
		}
	}

	public void onBuy4(@SuppressWarnings("UnusedParameters") View v)
	{
		if (this.billingManager != null)
		{
			this.billingManager.initiatePurchaseFlow(Skus.SKU_DONATE4, BillingClient.SkuType.INAPP);
		}
	}

	public void onBuy5(@SuppressWarnings("UnusedParameters") View v)
	{
		if (this.billingManager != null)
		{
			this.billingManager.initiatePurchaseFlow(Skus.SKU_DONATE5, BillingClient.SkuType.INAPP);
		}
	}

	private void warn(@NonNull final Exception e)
	{
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.title_donate);
		alert.setMessage(e.getMessage());
		alert.show();
	}

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
