/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.sqlunet.browser.common.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class Main2Activity extends AppCompatActivity
{
	private AppBarConfiguration appBarConfiguration;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);

		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		/*
		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
			}
		});
		*/

		final DrawerLayout drawer = findViewById(R.id.drawer_layout);
		final NavigationView navigationView = findViewById(R.id.nav_view);
		final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

		// Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
		this.appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_search_browse, R.id.nav_search_text, R.id.nav_status, R.id.nav_setup, R.id.nav_settings).setDrawerLayout(drawer).build();
		NavigationUI.setupActionBarWithNavController(this, navController, this.appBarConfiguration);
		NavigationUI.setupWithNavController(navigationView, navController);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onSupportNavigateUp()
	{
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		return NavigationUI.navigateUp(navController, this.appBarConfiguration) || super.onSupportNavigateUp();
	}
}
