/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.donate;

import android.os.Bundle;
import android.util.Log;
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
	 * Purchases
	 */
	@Nullable
	private List<Purchase> purchases;

	/**
	 * Buttons
	 */
	@Nullable
	private FloatingActionButton[] buttons;

	/**
	 * Buttons
	 */
	@Nullable
	private ImageView[] overlays;

	/**
	 * SKU to Buttons
	 */
	@Nullable
	private Map<String, FloatingActionButton> skuToButton;

	/**
	 * SKU to Buttons
	 */
	@Nullable
	private Map<String, ImageView> skuToOverlays;

	// E V E N T S

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy);

		// buttons
		final int n = Skus.IN_APP_SKUS.length;
		this.buttons = new FloatingActionButton[n];
		this.buttons[0] = findViewById(R.id.buyButton1);
		this.buttons[1] = findViewById(R.id.buyButton2);
		this.buttons[2] = findViewById(R.id.buyButton3);
		this.buttons[3] = findViewById(R.id.buyButton4);
		this.buttons[4] = findViewById(R.id.buyButton5);

		this.overlays = new ImageView[n];
		this.overlays[0] = findViewById(R.id.overlay1);
		this.overlays[1] = findViewById(R.id.overlay2);
		this.overlays[2] = findViewById(R.id.overlay3);
		this.overlays[3] = findViewById(R.id.overlay4);
		this.overlays[4] = findViewById(R.id.overlay5);

		this.skuToButton = new HashMap<>();
		this.skuToOverlays = new HashMap<>();
		for (int i = 0; i < n; i++)
		{
			this.skuToButton.put(Skus.IN_APP_SKUS[i], this.buttons[i]);
			this.skuToOverlays.put(Skus.IN_APP_SKUS[i], this.overlays[i]);
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
	protected void onStart()
	{
		super.onStart();

		// setup adapter
		if (this.billingManager == null)
		{
			this.billingManager = new BillingManager(this, this);
		}
	}

	// L I S T E N E R

	@Override
	public void onBillingClientSetupFinished()
	{
		Log.d(TAG, "onBillingClientSetupFinished()");
	}

	@Override
	public void onPurchasesUpdated(@Nullable final List<Purchase> purchases)
	{
		Log.d(TAG, "onPurchasesUpdated()");
		this.purchases = purchases;
		if (this.purchases != null)
		{
			Log.d(TAG, "purchase list size " + this.purchases.size());
			for (Purchase purchase : this.purchases)
			{
				Log.d(TAG, "purchase " + purchase + " acknowledged=" + purchase.isAcknowledged() + " time=" + purchase.getPurchaseTime());
				final String sku = purchase.getSku();
				final ImageButton imageButton = this.skuToButton.get(sku);
				imageButton.setEnabled(false);
				final ImageView overlay = this.skuToOverlays.get(sku);
				overlay.setVisibility(View.VISIBLE);
			}
		}
		else
		{
			Log.d(TAG, "null purchase list");
		}
	}

	@Override
	public void onConsumeFinished(final String token, final int result)
	{
		Log.d(TAG, "onConsumeFinished()");
	}

	// C O N S U M E

	public void onConsume(@SuppressWarnings("UnusedParameters") View v)
	{
		if (this.billingManager != null)
		{
			for (Purchase purchase : this.purchases)
			{
				this.billingManager.consumeAsync(purchase.getPurchaseToken());
			}
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
}
