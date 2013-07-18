package de.rwth.ti.layouthelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import de.rwth.ti.wps.R;

public class ConfirmDialogHelper {
	private Context parentContext;
	private ConfirmDialogListener listener;

	public ConfirmDialogHelper(Context context, ConfirmDialogListener listener) {
		this.parentContext = context;
		this.listener = listener;
	}

	public void createDialog(String title, int messageId, final int id) {
		createDialog(title, parentContext.getString(messageId), id);
	}

	public void createDialog(int titleId, String message, final int id) {
		createDialog(parentContext.getString(titleId), message, id);
	}

	public void createDialog(int titleId, int messageId, final int id) {
		createDialog(parentContext.getString(titleId),
				parentContext.getString(messageId), id);
	}

	public void createDialog(String title, String message, final int id) {
		new AlertDialog.Builder(parentContext)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								ConfirmDialogHelper.this.confirmed(id);
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// Do nothing.
							}
						}).show();
	}

	public void confirmed(int id) {
		if (listener != null) {
			listener.confirmed(id);
		}
	}
}
