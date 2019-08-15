/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.donate;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
//import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.bbou.donate.billing.BillingManager;
import com.bbou.donate.billing.Skus;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
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

	static private final int[] BUTTON_IDS = {R.id.buyButton1, R.id.buyButton2, R.id.buyButton3, R.id.buyButton4, R.id.buyButton5};

	static private final int[] OVERLAY_IDS = {R.id.overlay1, R.id.overlay2, R.id.overlay3, R.id.overlay4, R.id.overlay5};

	/**
	 * Adapter to in-app billing
	 */
	private BillingManager billingManager;

	/**
	 * Purchases per token
	 */
	@NonNull
	private final Map<String, Purchase> tokenToPurchase = new HashMap<>();

	/**
	 * Purchases per sku
	 */
	@NonNull
	private final Map<String, Purchase> skuToPurchase = new HashMap<>();

	/*
	 * SKU to Buttons
	 */
	/*
	@NonNull
	private final Map<String, FloatingActionButton> skuToButton = new HashMap<>();
	*/

	/**
	 * SKU to Buttons
	 */
	@NonNull
	private final Map<String, ImageView> skuToOverlay = new HashMap<>();

	// L I F E C Y C L E   A N D   S E T U P

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy);

		final int n = Skus.INAPP_SKUS.length;
		/*
		final int n1 = BUTTON_IDS.length;
		final int n2 = OVERLAY_IDS.length;
		assert n1 == n && n2 == n;
		*/

		final FloatingActionButton[] buttons = new FloatingActionButton[n];
		final ImageView[] overlays = new ImageView[n];

		for (int i = 0; i < n; i++)
		{
			// buttons
			buttons[i] = findViewById(BUTTON_IDS[i]);
			buttons[i].setOnClickListener((v) -> {
				int tag = Integer.parseInt((String) v.getTag());
				Log.d(TAG, "clicked " + tag);
				buy(Skus.INAPP_SKUS[tag]);
			});
			buttons[i].setOnLongClickListener((v) -> {
				int tag = Integer.parseInt((String) v.getTag());
				Log.d(TAG, "long clicked " + tag);
				final String sku = Skus.INAPP_SKUS[tag];
				final Purchase purchase = this.skuToPurchase.get(sku);
				if (purchase != null)
				{
					Log.i(TAG, purchase.toString());
					final StringBuilder sb = new StringBuilder();
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
					inform(sb.toString());
				}
				return true;
			});

			// overlays
			overlays[i] = findViewById(OVERLAY_IDS[i]);
		}

		// sku maps
		// this.skuToButton.clear();
		this.skuToOverlay.clear();
		for (int i = 0; i < n; i++)
		{
			// this.skuToButton.put(Skus.INAPP_SKUS[i], buttons[i]);
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
		this.skuToPurchase.clear();
		this.tokenToPurchase.clear();

		// reset all buttons and overlays
		for (String sku : Skus.INAPP_SKUS)
		{
			update(sku, false);
		}

		// update buttons and overlays with purchases
		if (updatedPurchases != null)
		{
			Log.d(TAG, "purchase list size " + this.tokenToPurchase.size());

			// build data
			for (Purchase purchase : updatedPurchases)
			{
				this.tokenToPurchase.put(purchase.getPurchaseToken(), purchase);
				this.skuToPurchase.put(purchase.getSku(), purchase);
			}

			// set buttons and overlays
			for (Purchase purchase : this.tokenToPurchase.values())
			{
				Log.d(TAG, purchase.toString());
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
		//final ImageButton imageButton = this.skuToButton.get(sku);
		//assert imageButton != null;
		//imageButton.setEnabled(!isOwned);

		final ImageView overlay = this.skuToOverlay.get(sku);
		assert overlay != null;
		overlay.setVisibility(isOwned ? View.VISIBLE : View.INVISIBLE);
	}

	// C O N S U M E

	public void onConsume(@SuppressWarnings("UnusedParameters") View v)
	{
		if (this.billingManager != null)
		{
			for (Purchase purchase : this.tokenToPurchase.values())
			{
				this.billingManager.consume(purchase.getPurchaseToken());
			}
		}
	}

	@Override
	public void onConsumeFinished(final String token, final int result)
	{
		Log.d(TAG, "onConsumeFinished() " + token);
		final Purchase purchase = this.tokenToPurchase.get(token);
		if (purchase != null)
		{
			final String sku = purchase.getSku();
			update(sku, false);
		}
	}

	// B U Y

	private void buy(final String sku)
	{
		if (this.billingManager != null)
		{
			this.billingManager.initiatePurchaseFlow(sku, BillingClient.SkuType.INAPP);
		}
	}

	private void inform(@NonNull final String message)
	{
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.title_donate);
		alert.setMessage(message);
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
