package de.rwth.ti.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.os.Environment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChooseFileDialog {

	private String m_sdcardDirectory = "";
	private Context m_context;
	private TextView m_titleView;

	private String m_dir = "";
	private List<String> m_files = null;
	private ChosenDirectoryListener m_chosenDirectoryListener = null;
	private ArrayAdapter<String> m_listAdapter = null;

	// ////////////////////////////////////////////////////
	// Callback interface for selected directory
	// ////////////////////////////////////////////////////
	public interface ChosenDirectoryListener {
		public void onChosenDir(String chosenDir);
	}

	public ChooseFileDialog(Context context,
			ChosenDirectoryListener chosenDirectoryListener) {
		m_context = context;
		m_sdcardDirectory = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		m_chosenDirectoryListener = chosenDirectoryListener;

		try {
			m_sdcardDirectory = new File(m_sdcardDirectory).getCanonicalPath();
		} catch (IOException ioe) {
		}
	}

	// //////////////////////////////////////////////////////////////////////////////
	// chooseDirectory(String dir) - load directory chooser dialog for initial
	// input 'dir' directory
	// //////////////////////////////////////////////////////////////////////////////

	public void chooseDirectory(String dir) {
		File dirFile = new File(dir);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			dir = m_sdcardDirectory;
		}

		try {
			dir = new File(dir).getCanonicalPath();
		} catch (IOException ioe) {
			return;
		}

		m_dir = dir;
		m_files = getFiles(dir);

		class DirectoryOnClickListener implements
				DialogInterface.OnClickListener {
			public void onClick(DialogInterface dialog, int item) {
				// Navigate into the sub-directory
				m_dir += File.separator
						+ ((AlertDialog) dialog).getListView().getAdapter()
								.getItem(item);
				updateDirectory();
			}
		}

		AlertDialog.Builder dialogBuilder = createDirectoryChooserDialog(dir,
				m_files, new DirectoryOnClickListener());

		dialogBuilder.setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Current directory chosen
				if (m_chosenDirectoryListener != null) {
					// Call registered listener supplied with the chosen
					// directory
					m_chosenDirectoryListener.onChosenDir(m_dir);
				}
			}
		}).setNegativeButton("Cancel", null);

		final AlertDialog dirsDialog = dialogBuilder.create();

		dirsDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					// Back button pressed
					if (m_dir.equals(m_sdcardDirectory)) {
						// The very top level directory, do nothing
						return false;
					} else {
						// Navigate back to an upper directory
						m_dir = new File(m_dir).getParent();
						updateDirectory();
					}

					return true;
				} else {
					return false;
				}
			}
		});

		// Show directory chooser dialog
		dirsDialog.show();
	}

	private List<String> getFiles(String dir) {
		List<String> files = new ArrayList<String>();
		try {
			File dirFile = new File(dir);
			if (!dirFile.exists() || !dirFile.isFile()) {
				return files;
			}

			for (File file : dirFile.listFiles()) {
				if (file.isFile()) {
					files.add(file.getName());
				}
			}
		} catch (Exception e) {
		}

		Collections.sort(files, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});

		return files;
	}

	private AlertDialog.Builder createDirectoryChooserDialog(String title,
			List<String> listItems,
			DialogInterface.OnClickListener onClickListener) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(m_context);

		// Create custom view for AlertDialog title containing
		// current directory TextView and possible 'New folder' button.
		// Current directory TextView allows long directory path to be wrapped
		// to multiple lines.
		LinearLayout titleLayout = new LinearLayout(m_context);
		titleLayout.setOrientation(LinearLayout.VERTICAL);

		m_titleView = new TextView(m_context);
		m_titleView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		m_titleView.setTextAppearance(m_context,
				android.R.style.TextAppearance_Large);
		m_titleView.setTextColor(m_context.getResources().getColor(
				android.R.color.white));
		m_titleView.setGravity(Gravity.CENTER_VERTICAL
				| Gravity.CENTER_HORIZONTAL);
		m_titleView.setText(title);

		titleLayout.addView(m_titleView);

		dialogBuilder.setCustomTitle(titleLayout);

		m_listAdapter = createListAdapter(listItems);

		dialogBuilder.setSingleChoiceItems(m_listAdapter, -1, onClickListener);
		dialogBuilder.setCancelable(false);

		return dialogBuilder;
	}

	private void updateDirectory() {
		m_files.clear();
		m_files.addAll(getFiles(m_dir));
		m_titleView.setText(m_dir);
		m_listAdapter.notifyDataSetChanged();
	}

	private ArrayAdapter<String> createListAdapter(List<String> items) {
		return new ArrayAdapter<String>(m_context,
				android.R.layout.select_dialog_item, android.R.id.text1, items) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);

				if (v instanceof TextView) {
					// Enable list item (directory) text wrapping
					TextView tv = (TextView) v;
					tv.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
					tv.setEllipsize(null);
				}
				return v;
			}
		};
	}
}
