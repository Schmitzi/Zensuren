package apps.schmitzi.Zensurenverwaltung;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddSubjectActivity extends Activity {
	Button btnOK;
	Button btnCancel;
	EditText edtLong;
	EditText edtShort;
	Spinner spnType;
	SQLiteDatabase base;
	int mode;
	String subject;
	final int MODE_ADD = 0, MODE_EDIT = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addsubject);
		mode = getIntent().getIntExtra("apps.schmitzi.Zensurenverwaltung.requestCode", 0);
		btnOK = (Button) findViewById(R.id.btnAddSubjOK);
		btnCancel = (Button) findViewById(R.id.btnAddSubjCancel);
		spnType = (Spinner) findViewById(R.id.TypeSpinner);
		edtLong = (EditText) findViewById(R.id.edtSubjectLong);
		edtShort = (EditText) findViewById(R.id.edtSubjectShort);
		btnOK.setOnClickListener(new btnOKListener());
		btnCancel.setOnClickListener(new btnCancelListener());
		if (mode == MODE_EDIT){
			subject = getIntent().getStringExtra("apps.schmitzi.Zensurenverwaltung.subject");
			setTitle(subject + " bearbeiten");
			edtLong.setText(subject);
			edtLong.setEnabled(false);
			base = openOrCreateDatabase(SubjectPicker.DATABASE, MODE_PRIVATE, null);
			Cursor c = base.query("subjects", new String[]{"short", "type"}, "name = ?", new String[]{subject}, null, null, null);
			c.moveToFirst();
			edtShort.setText(c.getString(0));
			edtShort.setEnabled(false);
			spnType.setSelection(c.getInt(1));
		}
	}
	public class btnOKListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			onbtnOKClick(v);
			
		}
		
	}
	public class btnCancelListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			onbtnCancelClick(v);
			
		}
		
	}
	public void onbtnOKClick(View v) {
		if(!(edtLong.getText().toString().equals("")) && !(edtShort.getText().toString().equals("")) )
		{
			base = this.openOrCreateDatabase(SubjectPicker.DATABASE, MODE_PRIVATE , null);
			String name = edtLong.getText().toString();
			ContentValues values;
			if (mode == MODE_ADD){
				if (base.query("subjects", new String[]{"_id"}, "name = ?", new String[]{name}, null, null, null).getCount() == 0){
					values = new ContentValues();
					values.put("name", name);
					values.put("short", edtShort.getText().toString());
					values.put("type", Integer.toString(spnType.getSelectedItemPosition()));
					base.insert("subjects", null, values);
					name = name.replace(" ", "_");
					base.execSQL("CREATE TABLE " + name + "(date DATE, mark SMALLINT, klausur BOOLEAN);");
					base.close();
					finish();
				} else base.close();
			} else {
				Cursor c = base.query("subjects", new String[]{"rowid"}, "name = ?", new String[]{subject}, null, null, null);
				c.moveToFirst();
				int id = c.getInt(0);
				values = new ContentValues();
				values.put("type", Integer.toString(spnType.getSelectedItemPosition()));
				base.update("subjects", values, "rowid = ?", new String[]{String.valueOf(id)});
				base.close();
				finish();
			}
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Eines der beiden Felder wurde nicht ausgefüllt. Dies ist zwingend notwendig.");
			builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       });
			builder.create().show();
		}
	}
	public void onbtnCancelClick(View v) {
		setResult(RESULT_CANCELED);
		finish();
	}
}
