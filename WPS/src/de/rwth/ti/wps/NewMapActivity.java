package de.rwth.ti.wps;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class NewMapActivity extends Activity {
	
	private static final int CHOOSE_MAP_FILE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_map);
		
		ImageView mapView = (ImageView) findViewById(R.id.mapView);
		
		
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	public void chooseMapFile(View view) {
		Intent chooseMapIntent = new Intent();
		chooseMapIntent.setType("image/*");
		chooseMapIntent.setAction(Intent.ACTION_GET_CONTENT);
		//startActivityForResult(Intent.createChooser(chooseMapIntent, "@string/activity_new_map_choose_map"), CHOOSE_MAP_FILE);
		startActivityForResult(chooseMapIntent, CHOOSE_MAP_FILE);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CHOOSE_MAP_FILE) {
			if (resultCode == RESULT_OK) {
				Uri mapUri = data.getData();
				
				if (mapUri != null) {
		            //User had pick an image.
		            Cursor cursor = getContentResolver().query(mapUri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
		            cursor.moveToFirst();

		            //Link to the image
		            final String imageFilePath = cursor.getString(0);
		            cursor.close();
		            
		            TextView textView = (TextView) findViewById(R.id.mapPath);
		            textView.setText(imageFilePath);
		        }
			}
		}
	}

}
