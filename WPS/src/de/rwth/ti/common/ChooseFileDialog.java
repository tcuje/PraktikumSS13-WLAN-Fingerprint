package de.rwth.ti.common;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import de.rwth.ti.wps.R;

public class ChooseFileDialog {

	private Context context;
	private String sdcardDirectory = "";
	private ChosenFileListener chosenFileListener = null;
	private TextView titleView;
	private String dir = "";
	private int numDirs = 0;
	private List<String> entries = null;
	private ArrayAdapter<String> listAdapter = null;
	private AlertDialog dirsDialog;
	private FilenameFilter filter;

	// Callback interface for selected directory
	public interface ChosenFileListener {

		public void onChosenFile(String chosenFile);
	}

	public ChooseFileDialog(Context context,
			ChosenFileListener chosenFileListener, final String fileSuffix) {
		this.context = context;
		sdcardDirectory = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		this.chosenFileListener = chosenFileListener;
		this.filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(fileSuffix)) {
					return true;
				}
				return false;
			}
		};

		try {
			sdcardDirectory = new File(sdcardDirectory).getCanonicalPath();
		} catch (IOException ioe) {
		}
	}

	public void chooseDirectory(String dirName) {
		File dirFile = new File(dirName);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			dirName = sdcardDirectory;
		}

		try {
			dirName = new File(dirName).getCanonicalPath();
		} catch (IOException ioe) {
			return;
		}

		dir = dirName;
		List<String> dirs = getDirs(dirName);
		numDirs = dirs.size();
		entries = dirs;
		List<String> files = getFiles(dirName);
		entries.addAll(files);

		class DirectoryOnClickListener implements
				DialogInterface.OnClickListener {

			public void onClick(DialogInterface dialog, int item) {
				dir += File.separator
						+ ((AlertDialog) dialog).getListView().getAdapter()
								.getItem(item);
				if (item < numDirs) {
					// Navigate into the sub-directory
					updateDirectory();
				} else {
					// Call registered listener supplied with the chosen file
					chosenFileListener.onChosenFile(dir);
					dirsDialog.dismiss();
				}
			}
		}

		AlertDialog.Builder dialogBuilder = createDirectoryChooserDialog(
				dirName, entries, new DirectoryOnClickListener());
		dialogBuilder.setNegativeButton(R.string.cancel, null);

		dirsDialog = dialogBuilder.create();
		dirsDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					// Back button pressed
					if (dir.equals(sdcardDirectory)) {
						// The very top level directory, do nothing
						return false;
					} else {
						// Navigate back to an upper directory
						String parent = new File(dir).getParent();
						if (parent != null) {
							dir = parent;
						}
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

	private List<String> getDirs(String dir) {
		List<String> dirs = new ArrayList<String>();
		try {
			File dirFile = new File(dir);
			if (!dirFile.exists() || !dirFile.isDirectory()) {
				return dirs;
			}
			for (File file : dirFile.listFiles()) {
				if (file.isDirectory() && file.isHidden() == false) {
					dirs.add(file.getName());
				}
			}
		} catch (Exception ex) {
		}
		Collections.sort(dirs, FileComparator.INSTANCE);
		return dirs;
	}

	private List<String> getFiles(String dir) {
		List<String> files = new ArrayList<String>();
		try {
			File dirFile = new File(dir);
			if (!dirFile.exists() || !dirFile.isDirectory()) {
				return files;
			}
			for (File file : dirFile.listFiles(filter)) {
				if (file.isFile()) {
					files.add(file.getName());
				}
			}
		} catch (Exception e) {
		}
		Collections.sort(files, FileComparator.INSTANCE);
		return files;
	}

	private AlertDialog.Builder createDirectoryChooserDialog(String title,
			List<String> listItems,
			DialogInterface.OnClickListener onClickListener) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

		// Create custom view for AlertDialog title containing
		// current directory TextView and possible 'New folder' button.
		// Current directory TextView allows long directory path to be wrapped
		// to multiple lines.
		LinearLayout titleLayout = new LinearLayout(context);
		titleLayout.setOrientation(LinearLayout.VERTICAL);

		titleView = new TextView(context);
		titleView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		titleView.setTextAppearance(context,
				android.R.style.TextAppearance_Large);
		titleView.setGravity(Gravity.CENTER_VERTICAL
				| Gravity.CENTER_HORIZONTAL);
		titleView.setText(title);
		titleLayout.addView(titleView);
		dialogBuilder.setCustomTitle(titleLayout);
		listAdapter = createListAdapter(listItems);
		dialogBuilder.setSingleChoiceItems(listAdapter, -1, onClickListener);
		return dialogBuilder;
	}

	private void updateDirectory() {
		entries.clear();
		List<String> dirs = getDirs(dir);
		numDirs = dirs.size();
		entries.addAll(dirs);
		List<String> files = getFiles(dir);
		entries.addAll(files);
		titleView.setText(dir);
		listAdapter.notifyDataSetChanged();
	}

	private ArrayAdapter<String> createListAdapter(List<String> items) {
		return new ArrayAdapter<String>(context,
				android.R.layout.select_dialog_item, android.R.id.text1, items) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				if (v instanceof TextView) {
					// Enable list item (directory) text wrapping
					TextView tv = (TextView) v;
					tv.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
					tv.setEllipsize(null);
					if (position < numDirs) {
						// append directory marker
						tv.append("/");
					}
				}
				return v;
			}
		};
	}

	private static class FileComparator implements Comparator<String> {

		public static final FileComparator INSTANCE = new FileComparator();

		@Override
		public int compare(String o1, String o2) {
			String l1 = o1.toLowerCase(Constants.LOCALE);
			String l2 = o2.toLowerCase(Constants.LOCALE);
			return l1.compareTo(l2);
		}

	}
}
