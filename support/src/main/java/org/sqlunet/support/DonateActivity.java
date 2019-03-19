package org.sqlunet.support;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.sqlunet.support.util.IabHelper;

/**
 * Donate
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DonateActivity extends AppCompatActivity implements IABAdapter.IABListener
{
	static private final String TAG = "IAB MainActivity";

	/**
	 * Adapter to in-app billing
	 */
	private IABAdapter iabAdapter;

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
		if (this.iabAdapter != null)
		{
			try
			{
				this.iabAdapter.onDestroy();
			}
			catch (Exception e)
			{
				//
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.d(TAG, "onActivityResult" + " request=" + requestCode + " result=" + resultCode + " intent=" + data);
		if (this.iabAdapter != null && !this.iabAdapter.onActivityResult(requestCode, resultCode, data))
		{
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		// setup adapter
		if (this.iabAdapter == null)
		{
			this.iabAdapter = new IABAdapter();
			this.iabAdapter.onCreate(this, this);
		}
	}

	// L I S T E N E R

	@Override
	public void onStart(final Op op)
	{
		//
	}

	@Override
	public void onFinish(boolean result, @NonNull final Op op)
	{
		switch (op)
		{
			case INVENTORY:
				break;

			case BUY:
				break;

			case SETUP:
			default:
				break;
		}
	}

	// B U Y

	public void onBuy1(@SuppressWarnings("UnusedParameters") View v)
	{
		if (this.iabAdapter != null)
		{
			try
			{
				this.iabAdapter.buy(IABAdapter.SKU_DONATE1);
			}
			catch (IabHelper.IabAsyncInProgressException e)
			{
				warn(e);
			}
		}
	}

	public void onBuy2(@SuppressWarnings("UnusedParameters") View v)
	{
		if (this.iabAdapter != null)
		{
			try
			{
				this.iabAdapter.buy(IABAdapter.SKU_DONATE2);
			}
			catch (IabHelper.IabAsyncInProgressException e)
			{
				warn(e);
			}
		}
	}

	public void onBuy3(@SuppressWarnings("UnusedParameters") View v)
	{
		if (this.iabAdapter != null)
		{
			try
			{
				this.iabAdapter.buy(IABAdapter.SKU_DONATE3);
			}
			catch (IabHelper.IabAsyncInProgressException e)
			{
				warn(e);
			}
		}
	}

	public void onBuy4(@SuppressWarnings("UnusedParameters") View v)
	{
		if (this.iabAdapter != null)
		{
			try
			{
				this.iabAdapter.buy(IABAdapter.SKU_DONATE4);
			}
			catch (IabHelper.IabAsyncInProgressException e)
			{
				warn(e);
			}
		}
	}

	public void onBuy5(@SuppressWarnings("UnusedParameters") View v)
	{
		if (this.iabAdapter != null)
		{
			try
			{
				this.iabAdapter.buy(IABAdapter.SKU_DONATE5);
			}
			catch (IabHelper.IabAsyncInProgressException e)
			{
				warn(e);
			}
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
