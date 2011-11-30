package apps.schmitzi.Zensurenverwaltung;

import java.sql.Date;
import java.text.DateFormat;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;

public class AddTestActivity extends Activity {

	int mark;
	Date date;
	String subject;
	int type;
	SQLiteDatabase base;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_test);
		subject = getIntent().getData().getHost();
		setTitle("Neue Zensur in " + subject);
		mark = 0;
		java.util.Date tempDate = new java.util.Date();
		date = new Date( tempDate.getYear(), tempDate.getMonth(), tempDate.getDate());
		initializebtnDate();
		initializebtnMark();
		base = openOrCreateDatabase(SubjectPicker.DATABASE, MODE_PRIVATE, null);
		final CheckBox chkKlausur = (CheckBox) findViewById(R.id.KlausurCheckBox);
		Cursor c = base.rawQuery("SELECT type FROM subjects WHERE name = ?;", new String[]{subject});
		c.moveToFirst();
		type = c.getInt(0);
		if (type == 0) chkKlausur.setEnabled(false);
		Button btnOK = (Button) findViewById(R.id.AddTestButton);
		btnOK.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				int klausur;
				if(chkKlausur.isChecked()) klausur = 1;
				else klausur = 0;
				base.execSQL("INSERT INTO " + subject.replace(" ", "_") + " ( date, mark, klausur ) "+
							 "VALUES ( '" + date.toString() + "', " + Integer.toString(mark) + ", " + 
							 Integer.toString(klausur) + " );");
				double mean;
				Cursor c = base.rawQuery("SELECT mean FROM subjects WHERE name = ?;", new String[] {subject});
				c.moveToFirst();
				if (c.isNull(0)) mean = mark;
				else {
					Cursor d = base.rawQuery("SELECT mark FROM " + subject.replace(" ", "_") + " WHERE klausur = 0", null);
					Cursor e = base.rawQuery("SELECT mark FROM " + subject.replace(" ", "_") + " WHERE klausur = 1", null);
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
				base.execSQL("UPDATE subjects SET mean = " + Double.toString(mean) + " WHERE name = '" + subject + "';");
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

	private void initializebtnMark() {
		final Button btnMark = (Button) findViewById(R.id.MarkButton);
		btnMark.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final Dialog dialog = new Dialog(AddTestActivity.this);
				dialog.setContentView(R.layout.numpicker_dialog);
				dialog.setTitle("Punkte");
				final NumberPicker numPick = (NumberPicker) dialog.findViewById(R.id.NumPicker);
				numPick.setRange(0, 15);
				Button btnOK = (Button) dialog.findViewById(R.id.MarkOKButton);
				btnOK.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick (View arg0){
						mark = numPick.getCurrent();
						btnMark.setText(Integer.toString(mark));
						dialog.dismiss();
					}
				});
				Button btnCancel = (Button) dialog.findViewById(R.id.MarkCancelButton);
				btnCancel.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0){
						dialog.dismiss();
					}
				});
				dialog.setCancelable(false);
				dialog.show();
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
}


