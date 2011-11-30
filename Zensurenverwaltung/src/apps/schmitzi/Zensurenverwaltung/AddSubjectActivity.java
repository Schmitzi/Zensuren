package apps.schmitzi.Zensurenverwaltung;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addsubject);
		btnOK = (Button) findViewById(R.id.btnAddSubjOK);
		btnCancel = (Button) findViewById(R.id.btnAddSubjCancel);
		edtLong = (EditText) findViewById(R.id.edtSubjectLong);
		edtShort = (EditText) findViewById(R.id.edtSubjectShort);
		btnOK.setOnClickListener(new btnOKListener());
		btnCancel.setOnClickListener(new btnCancelListener());
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
			SubjectPicker.marksBase = this.openOrCreateDatabase(SubjectPicker.DATABASE, MODE_PRIVATE , null);
			String name = edtLong.getText().toString();
			Spinner spnType = (Spinner) findViewById(R.id.TypeSpinner);
			if (SubjectPicker.marksBase.rawQuery("SELECT _id FROM subjects WHERE name = '"+edtLong.getText().toString()+"';", null).getCount() == 0){
				SubjectPicker.marksBase.execSQL("INSERT INTO subjects ( name, short, type )" +
						"VALUES ( '" + name + "', '" + edtShort.getText().toString() + "', " +
						Integer.toString(spnType.getSelectedItemPosition()) + " );" );
				name = name.replace(" ", "_");
				SubjectPicker.marksBase.execSQL("CREATE TABLE " + name + "(date DATE, mark SMALLINT, klausur BOOLEAN);");
				SubjectPicker.marksBase.close();
				setResult(RESULT_OK);
				finish();
			} else SubjectPicker.marksBase.close();
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
