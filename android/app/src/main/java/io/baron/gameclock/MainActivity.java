package io.baron.gameclock;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.*;


public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		TimerCollection.instance = new TimerCollection(this);

		getFragmentManager()
			.beginTransaction()
			.add(R.id.activity, new GameTimerListFragment())
			.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
			.addToBackStack(null)
			.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_new:
				getFragmentManager()
					.beginTransaction()
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.addToBackStack(null)
					.add(R.id.activity, new NewGameTimerFragment())
					.commit();

				return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
