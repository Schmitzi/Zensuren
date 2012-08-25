package apps.schmitzi.Zensurenverwaltung;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MarkBordersActivity extends Activity {
	SharedPreferences preferences = getSharedPreferences(
			SQLConnection.DATABASE, MODE_PRIVATE);

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mark_borders);
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		Button btnOK = (Button) findViewById(R.id.btnOK);
		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();

			}
		});
		final int mode = getIntent().getIntExtra("mode",
				SQLConnection.MODE_EDIT);
		if (mode == SQLConnection.MODE_SETUP) {
			btnCancel.setText(R.string.back);
			btnOK.setText(R.string.next);
		}
		btnOK.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				EditText edtBest = (EditText) findViewById(R.id.edtMaxMark);
				EditText edtWorst = (EditText) findViewById(R.id.edtMinMark);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt("best",
						Integer.valueOf(edtBest.getText().toString()));
				editor.putInt("worst",
						Integer.valueOf(edtWorst.getText().toString()));
				editor.commit();
				if (mode == SQLConnection.MODE_SETUP) {
					Intent in = new Intent(MarkBordersActivity.this,
							MarkTypeActivity.class);
					in.putExtra("mode", mode);
					startActivityForResult(in, 0);
				} else
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
