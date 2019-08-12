/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.donate;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;

import org.sqlunet.donate.billing.BillingConstants;
import org.sqlunet.donate.billing.BillingManager;

import java.util.List;

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

	// E V E N T S

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy);

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
		Log.d(TAG,"onBillingClientSetupFinished()");
	}

	@Override
	public void onConsumeFinished(final String token, final int result)
	{
		Log.d(TAG,"onConsumeFinished()");
	}

	@Override
	public void onPurchasesUpdated(@Nullable final List<Purchase> purchases)
	{
		Log.d(TAG,"onPurchasesUpdated()");
	}

	// B U Y

	public void onBuy1(@SuppressWarnings("UnusedParameters") View v)
	{
		if (this.billingManager != null)
		{
			this.billingManager.initiatePurchaseFlow(BillingConstants.SKU_DONATE1, BillingClient.SkuType.INAPP);
		}
	}

	public void onBuy2(@SuppressWarnings("UnusedParameters") View v)
	{
		if (this.billingManager != null)
		{
			this.billingManager.initiatePurchaseFlow(BillingConstants.SKU_DONATE2, BillingClient.SkuType.INAPP);
		}
	}

	public void onBuy3(@SuppressWarnings("UnusedParameters") View v)
	{
		if (this.billingManager != null)
		{
			this.billingManager.initiatePurchaseFlow(BillingConstants.SKU_DONATE3, BillingClient.SkuType.INAPP);
		}
	}

	public void onBuy4(@SuppressWarnings("UnusedParameters") View v)
	{
		if (this.billingManager != null)
		{
			this.billingManager.initiatePurchaseFlow(BillingConstants.SKU_DONATE4, BillingClient.SkuType.INAPP);
		}
	}

	public void onBuy5(@SuppressWarnings("UnusedParameters") View v)
	{
		if (this.billingManager != null)
		{
			this.billingManager.initiatePurchaseFlow(BillingConstants.SKU_DONATE5, BillingClient.SkuType.INAPP);
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
