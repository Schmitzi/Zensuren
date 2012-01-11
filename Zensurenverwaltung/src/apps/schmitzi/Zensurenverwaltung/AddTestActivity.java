package apps.schmitzi.Zensurenverwaltung;

import java.sql.Date;
import java.text.DateFormat;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;

public class AddTestActivity extends Activity {

	Date date;
	int mark;
	String subject;
	int type;
	SQLiteDatabase base;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_test);
		subject = getIntent().getData().getHost();
		setTitle("Neue Zensur in " + subject);
		java.util.Date tempDate = new java.util.Date();
		date = new Date( tempDate.getYear(), tempDate.getMonth(), tempDate.getDate());
		initializebtnDate();
		base = openOrCreateDatabase(SubjectPicker.DATABASE, MODE_PRIVATE, null);
		final CheckBox chkKlausur = (CheckBox) findViewById(R.id.KlausurCheckBox);
		Cursor c = base.query("subjects", new String[] {"type"}, "name = ?", new String[] {subject}, null, null, null);
		c.moveToFirst();
		type = c.getInt(0);
		if (type == 0) chkKlausur.setEnabled(false);
		Button btnOK = (Button) findViewById(R.id.AddTestButton);
		btnOK.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Spinner spnMark = (Spinner)findViewById(R.id.spnMark);
				mark = spnMark.getSelectedItemPosition();
				boolean klausur;
				klausur = chkKlausur.isChecked();
				ContentValues values = new ContentValues();
				values.put("date", date.toString());
				values.put("mark", mark);
				values.put("klausur", klausur);
				base.insert(subject.replace(' ', '_'), null, values);
				double mean = calculateMean();
				ContentValues values2 = new ContentValues();
				values2.put("mean", mean);
				base.update("subjects", values2, "name = ?", new String[] {subject});
				base.close();
				finish();
			}
		});
		Button btnCancel = (Button) findViewById(R.id.CancelTestButton);
		btnCancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				finish();
			}
		});
	}


	private void initializebtnDate() {
		Button btnDate = (Button) findViewById(R.id.DateButton);
		String dayOfWeek;
		switch(date.getDay()){
		case 1:
			dayOfWeek = "Montag";
			break;
		case 2:
			dayOfWeek = "Dienstag";
			break;
		case 3:
			dayOfWeek = "Mittwoch";
			break;
		case 4:
			dayOfWeek = "Donnerstag";
			break;
		case 5:
			dayOfWeek = "Freitag";
			break;
		case 6:
			dayOfWeek = "Samstag";
			break;
		default:
			dayOfWeek = "Sonntag";
			break;
		}
		btnDate.setText(dayOfWeek + ", " + DateFormat.getDateInstance().format(date));
		btnDate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				showDialog(0);
			}
		});
	}
	
	@Override
	public Dialog onCreateDialog(int id){
		if (id == 0){
			OnDateSetListener callback = new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int month, int day) {
					date.setYear(year - 1900);
					date.setMonth(month);
					date.setDate(day);
					initializebtnDate();
				}
			};
			return new DatePickerDialog(this, callback, date.getYear() + 1900, date.getMonth(), date.getDate());
		} else return null;
	}

	double calculateMean() {
		double mean;
		Cursor c = base.query("subjects", new String[]{"mean"}, "name = ?", new String[]{subject}, null, null, null);
		c.moveToFirst();
		if (c.isNull(0)) mean = mark;
		else {
			Cursor d = base.query(subject.replace(' ', '_'), new String[] {"mark"}, "klausur = 0", null, null, null, null);
			Cursor e = base.query(subject.replace(' ', '_'), new String[] {"mark"}, "klausur = 1", null, null, null, null);
			d.moveToFirst(); e.moveToFirst();
			double meanD = 0, meanE = 0;
			try {
				do {
					meanD += d.getInt(0);
				} while (d.moveToNext());
				meanD = meanD / d.getCount() * 0.25 * (4 - type);
			} catch (CursorIndexOutOfBoundsException ex) {
				meanD = 0;
			}
			try {
				do {
					meanE += e.getInt(0);
				} while (e.moveToNext());
				meanE = meanE / e.getCount() * 0.25 * type;
			} catch (CursorIndexOutOfBoundsException ex) {
				meanE = 0;
			}
			if (d.getCount() == 0) meanD = meanE / type * (4 - type);
			if (e.getCount() == 0) meanE = meanD * type / (4 - type);
			mean = meanD + meanE;
		}
		return mean;
	}
}


