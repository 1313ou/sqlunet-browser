/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.donate;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;

/**
 * Donate
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("WeakerAccess")
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
	private final Map<String, Purchase> productToPurchase = new HashMap<>();

	/**
	 * SKU to Buttons
	 */
	@NonNull
	private final Map<String, ImageButton> productToButton = new HashMap<>();

	/**
	 * Overlay drawable
	 */
	@Nullable
	private Drawable overlay;

	// L I F E C Y C L E   A N D   S E T U P

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content view
		setContentView(R.layout.activity_donate);

		// overlay drawable
		final BitmapDrawable overlay = (BitmapDrawable) getDrawable(this, R.drawable.ic_overlay);
		assert overlay != null;
		overlay.setGravity(Gravity.TOP | Gravity.END);
		this.overlay = overlay;

		// init skus from resources
		Skus.init(this);
		final String[] inappSkus = Skus.getInappSkus();
		assert inappSkus != null;
		final int n = inappSkus.length;

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
				final Purchase purchase = this.productToPurchase.get(sku);
				if (purchase != null)
				{
					Log.i(TAG, purchase.toString());
					sb.append("Order ID: ");
					sb.append(purchase.getOrderId());
					sb.append('\n');
					sb.append("SKUs: ");
					for (String sku2 : purchase.getProducts())
					{
						sb.append(sku2);
						sb.append(' ');
					}
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
		this.productToButton.clear();
		for (int i = 0; i < n; i++)
		{
			this.productToButton.put(Skus.getInappSkus()[i], buttons[i]);
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
		this.productToPurchase.clear();

		// reset all buttons and overlays
		final String[] products = Skus.getInappSkus();
		if (products != null)
		{
			for (String sku : products)
			{
				update(sku, false);
			}
		}

		// update buttons and overlays with purchases
		if (updatedPurchases != null)
		{
			Log.d(TAG, "Purchase count " + updatedPurchases.size());

			// build data
			for (Purchase purchase : updatedPurchases)
			{
				for (String sku : purchase.getProducts())
				{
					this.productToPurchase.put(sku, purchase);
				}
			}

			// set buttons and overlays
			for (Purchase purchase : this.productToPurchase.values())
			{
				Log.d(TAG, "Update " + purchase.toString());
				for (String sku : purchase.getProducts())
				{
					update(sku, true);
				}
			}
		}
		else
		{
			Log.d(TAG, "Null purchase list");
		}
	}

	private void update(final String productId, boolean isOwned)
	{
		final ImageButton imageButton = this.productToButton.get(productId);
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
			for (Purchase purchase : this.productToPurchase.values())
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

			for (Purchase purchase : this.productToPurchase.values())
			{
				if (purchase.getPurchaseToken().equals(token))
				{
					for (String productId : purchase.getProducts())
					{
						this.productToPurchase.remove(productId);
						update(productId, false);
					}
					break;
				}
			}
		}
	}

	// D O N A T E

	private void donate(final String productId)
	{
		if (this.billingManager != null)
		{
			this.billingManager.initiatePurchaseFlow(productId, BillingClient.ProductType.INAPP);
		}
	}

	// H E L P E R

	@Nullable
	private static Drawable getDrawable(@NonNull final Context context, @DrawableRes int resId)
	{
		return AppCompatResources.getDrawable(context, resId);
	}

	private void inform(@NonNull final String message)
	{
		new AlertDialog.Builder(this) //
				.setTitle(R.string.title_donate) //
				.setMessage(message) //
				.show();
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
