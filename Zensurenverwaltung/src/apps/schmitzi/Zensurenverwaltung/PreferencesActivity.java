package apps.schmitzi.Zensurenverwaltung;

import java.sql.Date;
import java.util.GregorianCalendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PreferencesActivity extends ListActivity {
	Date[] semester = new Date[5];
	SharedPreferences sprefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		String[] prefs = getResources().getStringArray(R.array.Preferences);
		String[] descriptions = getResources().getStringArray(R.array.PrefDescriptions);
		setListAdapter(new PrefAdapter(this, R.layout.pref_item, prefs, descriptions));
		ListView lv = getListView();
		sprefs = getSharedPreferences("Zensuren", MODE_PRIVATE);
		semester[0] = Date.valueOf(sprefs.getString("Semester 1", ""));
		semester[1] = Date.valueOf(sprefs.getString("Semester 2", ""));
		semester[2] = Date.valueOf(sprefs.getString("Semester 3", ""));
		semester[3] = Date.valueOf(sprefs.getString("Semester 4", ""));
		semester[4] = new Date(3000, 1, 1);
		lv.setOnItemClickListener(new OnItemClickListener(){
			

		public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				switch(position){
				case 0:
					Intent in = new Intent(PreferencesActivity.this, SemesterActivity.class);
					in.addCategory(Intent.CATEGORY_DEFAULT);
					startActivity(in);
					break;
				case 1:
					String[] items = {"alle Semester", "aktuelles Semester"};
					AlertDialog.Builder builder = new AlertDialog.Builder(PreferencesActivity.this);
					builder.setTitle("Zeitraum");
					builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							
						}
					});
					builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							SharedPreferences.Editor ed = sprefs.edit();
							ed.putInt("ViewMode", which);
							ed.commit();
							SQLiteDatabase base = openOrCreateDatabase(SubjectPicker.DATABASE, MODE_PRIVATE, null);
							new UpdateMeansTask(base, semester).execute(which);
							dialog.dismiss();
							showDialog(0);
						}
					});
					builder.show();
				}
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id){
		switch(id){
		case 0:
			return ProgressDialog.show(this, "Update der Datenbank", "Bitte warten...");
		default:
			return null;
		}
	}
	public class PrefAdapter extends ArrayAdapter<String> {

		String[] names, descriptions;
		
		public PrefAdapter(Context context, int textViewResourceId, String[] names, String[] descriptions) {
			super(context, textViewResourceId, names);
			this.names = names;
			this.descriptions = descriptions;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			View v = convertView;
			if(v==null) {
				 LayoutInflater inf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				 v = inf.inflate(R.layout.pref_item, null);
			 }
			Log.v("Zensuren1", names[position]);
			String s = names[position];
			if (s != null){
				TextView t1 = (TextView) v.findViewById(R.id.PrefTitle);
				if(t1 != null)
					t1.setText(s);
				TextView t2 = (TextView) v.findViewById(R.id.PrefDescription);
				if (t2 != null)
					t2.setText(descriptions[position]);
			}
			return v;
		}

	}
	public class UpdateMeansTask extends AsyncTask<Integer, Integer, Void> {

		SQLiteDatabase base;
		Date[] semester;
		
		public UpdateMeansTask(SQLiteDatabase data, Date[] semesters){
			base = data;
			semester = semesters;
		}
		@Override
		protected Void doInBackground(Integer... params) {
			Cursor subjects, marks;
			if(params[0] == 1){
				Date now = new Date(new GregorianCalendar().get(GregorianCalendar.YEAR) - 1900,new GregorianCalendar().get(GregorianCalendar.MONTH),
								    new GregorianCalendar().get(GregorianCalendar.DAY_OF_MONTH));
				Log.v("Zensuren", now.toLocaleString());
				int i = 0;
				if (now.before(semester[1])) i = 0;
				else if (now.before(semester[2])) i = 1;
				else if (now.before(semester[3])) i = 2;
				else i = 3;
				Log.v("Zensuren", Integer.toString(i));
				subjects = base.query("subjects", new String[]{"name", "type"}, null, null, null, null, null);
				if (subjects.getCount() == 0){
					base.close();
					return null;
				}
				subjects.moveToFirst();
				do{
					marks = base.query(subjects.getString(0).replace(' ', '_'), null,
							"(date >= ?) AND (date < ?)", new String[]{semester[i].toString(), semester[i + 1].toString()}, null, null, "date");
					Double mean = new Calculator(marks, subjects.getInt(1), semester).calculateMean();
					ContentValues values = new ContentValues();
					values.put("mean", mean);
					base.update("subjects", values, "name = ?", new String[]{subjects.getString(0)});
				}while (subjects.moveToNext());
			} else {
				subjects = base.query("subjects", new String[]{"name", "type"}, null, null, null, null, null);
				if (subjects.getCount() == 0){
					base.close();
					return null;
				}
				subjects.moveToFirst();
				do{
					marks = base.query(subjects.getString(0).replace(' ', '_'), null, null, null, null, null, "date");
					Double mean = new Calculator(marks, subjects.getInt(1), semester).calculateMean();
					ContentValues values = new ContentValues();
					values.put("mean", mean);
					base.update("subjects", values, "name = ?", new String[]{subjects.getString(0)});
				} while (subjects.moveToNext());
			}
			base.close();
			return null;
		}
		
		protected void onPostExecute(Void result){
			removeDialog(0);
		}

	}
}
