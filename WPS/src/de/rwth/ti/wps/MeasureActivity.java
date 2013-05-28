package de.rwth.ti.wps;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MeasureActivity extends SuperActivity {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measure);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		/*
		 * if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
		 * getActionBar().setSelectedNavigationItem(
		 * savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM)); }
		 */
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		/*
		 * outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
		 * .getSelectedNavigationIndex());
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.action_measure: // intent = new Intent(this,
									// MeasureActivity.class);
			break;
		// case android.R.id.home:
		// This ID represents the Home or Up button. In the case of this
		// activity, the Up button is shown. Use NavUtils to allow users
		// to navigate up one level in the application structure. For
		// more details, see the Navigation pattern on Android Design:
		//
		// http://developer.android.com/design/patterns/navigation.html#up-vs-back
		//
		// NavUtils.navigateUpFromSameTask(this);
		// break;
		default:
			return super.onOptionsItemSelected(item);
		}

		return true;
	}

	/*
	 * @Override public boolean onNavigationItemSelected(int position, long id)
	 * { // When the given dropdown item is selected, show its contents in the
	 * // container view. //TextView textView = (TextView)
	 * findViewById(R.id.testView); //textView.setText("Geb�ude " +
	 * Integer.toString(position));
	 * 
	 * return true; }
	 */
}
