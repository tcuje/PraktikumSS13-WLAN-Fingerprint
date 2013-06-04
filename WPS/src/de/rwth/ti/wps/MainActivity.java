package de.rwth.ti.wps;

import java.io.File;

import android.os.Bundle;
import android.widget.Toast;
import de.rwth.ti.common.Constants;

/**
 * This is the main activity class
 * 
 */
public class MainActivity extends SuperActivity {

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// create app sd directory
		File sdDir = new File(Constants.SD_APP_DIR);
		if (sdDir.exists() == false) {
			if (sdDir.mkdirs() == false) {
				Toast.makeText(this, R.string.error_sd_dir, Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	/** Called when the activity is first created or restarted */
	@Override
	public void onStart() {
		super.onStart();
	}

	/** Called when the activity is finishing or being destroyed by the system */
	@Override
	public void onStop() {
		super.onStop();
	}

}
