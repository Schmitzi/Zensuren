package apps.schmitzi.Zensurenverwaltung;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;

public class IntervalActivity extends Activity {

	int mode = getIntent().getIntExtra("mode", SQLConnection.MODE_EDIT);

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.interval);
		Button btnOK = (Button) findViewById(R.id.btnOK);
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		if (mode == SQLConnection.MODE_EDIT) {
			btnOK.setText(getResources().getString(R.string.OK));
			btnCancel.setText(getResources().getString(R.string.cancel));
		}
		btnOK.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				SharedPreferences preferences = getSharedPreferences(
						SQLConnection.DATABASE, MODE_PRIVATE);
				Editor editor = preferences.edit();
				RadioButton rdb = (RadioButton) findViewById(R.id.rdbSemesters);
				editor.putBoolean(getResources().getString(R.string.semesters),
						rdb.isChecked());
				editor.commit();
				if (mode == SQLConnection.MODE_SETUP) {
					Intent in;
					if (rdb.isChecked())
						in = new Intent(IntervalActivity.this,
								SemesterActivity.class);
					else
						in = new Intent(IntervalActivity.this,
								MarkBordersActivity.class);
					in.putExtra("mode", mode);
					startActivityForResult(in, 0);
				} else
					finish();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();

			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}
}
