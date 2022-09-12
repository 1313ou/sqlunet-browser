/*
 * Copyright (c) 2022. Bernard Bou
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

import com.android.billingclient.api.Purchase;
import com.bbou.donate.billing.BillingManager;
import com.bbou.donate.billing.Products;

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
public class DonateActivity extends AppCompatActivity implements BillingManager.BillingListener
{
	static private final String TAG = "DonateA";

	static private final int[] BUTTON_IDS = {R.id.buyButton1, R.id.buyButton2, R.id.buyButton3, R.id.buyButton4, R.id.buyButton5};

	static private final int[] DRAWABLE_IDS = {R.drawable.ic_donate1, R.drawable.ic_donate2, R.drawable.ic_donate3, R.drawable.ic_donate4, R.drawable.ic_donate5};

	/**
	 * Adapter to in-app billing
	 */
	private BillingManager billingManager;

	/**
	 * Product id to buttons
	 */
	@NonNull
	private final Map<String, ImageButton> buttonsByProductId = new HashMap<>();

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

		// init product ids from resources
		Products.init(this);
		final String[] inappProducts = Products.getInappProducts();
		assert inappProducts != null;
		final int n = inappProducts.length;

		// image buttons
		final ImageButton[] buttons = new ImageButton[n];
		for (int i = 0; i < n; i++)
		{
			// buttons
			buttons[i] = findViewById(BUTTON_IDS[i]);
			buttons[i].setOnClickListener((button) -> {

				int tag = Integer.parseInt((String) button.getTag());
				Log.d(TAG, "clicked " + tag);
				donate(inappProducts[tag]);
			});
			buttons[i].setOnLongClickListener((button) -> {

				int tag = Integer.parseInt((String) button.getTag());
				Log.d(TAG, "long clicked " + tag);
				final String productId = inappProducts[tag];
				final StringBuilder sb = new StringBuilder();
				final Purchase purchase = this.billingManager == null ? null : this.billingManager.productIdToPurchase(productId);
				if (purchase != null)
				{
					Log.i(TAG, purchase.toString());
					sb.append("Order ID: ");
					sb.append(purchase.getOrderId());
					sb.append('\n');
					sb.append("Products: ");
					for (String productId2 : purchase.getProducts())
					{
						sb.append(productId2);
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
					sb.append("ProductId: ");
					sb.append(productId);
				}
				inform(sb.toString());
				return true;
			});
		}

		// image buttons per product
		this.buttonsByProductId.clear();
		for (int i = 0; i < n; i++)
		{
			this.buttonsByProductId.put(Products.getInappProducts()[i], buttons[i]);
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
	public void onPurchaseFinished(final Purchase purchase)
	{
		Log.d(TAG, "New purchase " + purchase.toString());
		for (String productId : purchase.getProducts())
		{
			update(productId, true);
		}
	}

	@Override
	synchronized public void onPurchaseList(@Nullable final List<Purchase> updatedPurchases)
	{
		Log.d(TAG, "onPurchaseList()");

		// reset all buttons and overlays
		final String[] productIds = Products.getInappProducts();
		if (productIds != null)
		{
			for (String productId : productIds)
			{
				update(productId, false);
			}
		}

		// update buttons and overlays with purchases
		if (updatedPurchases != null)
		{
			Log.d(TAG, "Purchase count " + updatedPurchases.size());

			// set buttons and overlays
			for (Purchase purchase : updatedPurchases)
			{
				Log.d(TAG, "Owned " + purchase.toString());
				for (String productId : purchase.getProducts())
				{
					update(productId, true);
				}
			}
		}
		else
		{
			Log.d(TAG, "Null purchase list");
		}
	}

	// C O N S U M E

	public void consumeAll()
	{
		if (this.billingManager != null)
		{
			this.billingManager.consumeAll();
		}
	}

	@Override
	public void onConsumeFinished(final Purchase purchase)
	{
		Log.d(TAG, "onConsumeFinished() " + purchase);
		if (purchase != null)
		{
			for (String productId : purchase.getProducts())
			{
				update(productId, false);
			}
		}
	}

	public void onConsume(View v)
	{
		Log.d(TAG, "onConsume()");
		consumeAll();
	}

	// D O N A T E

	private void donate(final String productId)
	{
		if (this.billingManager != null)
		{
			this.billingManager.initiatePurchaseFlow(productId);
		}
	}

	// H E L P E R

	private void update(final String productId, boolean isOwned)
	{
		final ImageButton imageButton = this.buttonsByProductId.get(productId);
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
	public boolean onCreateOptionsMenu(@NonNull final Menu menu)
	{
		// inflate the menu; this adds items to the type bar if it is present.
		getMenuInflater().inflate(R.menu.donate, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull final MenuItem item)
	{
		int id = item.getItemId();
		if (id == R.id.action_donate_refresh && this.billingManager != null)
		{
			this.billingManager.queryPurchases();
			return true;
		}
		return false;
	}
}
