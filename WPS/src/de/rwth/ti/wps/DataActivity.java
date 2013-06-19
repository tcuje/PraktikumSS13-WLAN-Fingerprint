package de.rwth.ti.wps;


import de.rwth.ti.db.Building;
import de.rwth.ti.db.Floor;
import de.rwth.ti.db.MeasurePoint;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class DataActivity extends SuperActivity 
implements OnClickListener 
{ 
	

	RadioGroup radiogroup;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data);
		// Show the Up button in the action bar.
		
	
	}
	
	public void onRadioButtonClicked(View view) {

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        
        switch(view.getId()) {
        case R.id.dataAPButton:
            if (checked){
                Toast.makeText(this, "You've selected: AP", Toast.LENGTH_LONG).show();
                onClick(view);
                }
            break;
        case R.id.dataFloorButton:
            if (checked){
                Toast.makeText(this, "You've selected: floor", Toast.LENGTH_LONG).show();
            onClick(view);
            }
            break;
        case R.id.dataMSButton:
            if (checked){
            	Intent launchDelete = new Intent(DataActivity.this, DataDeleteActivity.class);
            	startActivity(launchDelete);
                }
            break;
        case R.id.dataBuildingButton:
            if (checked){
            	Intent launchPopup = new Intent(DataActivity.this, DataActivityPopup.class);
            	startActivity(launchPopup);
            		
                }
            break;
        }
           }
	
	public void onClick(View view) {
					
			// FIXME GUI get real data from gui
			
			}
	
		
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		
		switch (item.getItemId()) {
	
		//case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			//NavUtils.navigateUpFromSameTask(this);
			//return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
