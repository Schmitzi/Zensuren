package apps.schmitzi.Zensurenverwaltung;

import java.sql.Date;
import java.text.DateFormat;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

public class SemesterActivity extends Activity {
	
	String[] semester = new String[4];
	SharedPreferences prefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.semester_activity);
		prefs = getSharedPreferences("Zensuren", MODE_PRIVATE);
		if (prefs.contains("Semester 1")){
			for (int i = 0; i < 4; i++){
				semester[i] = DateFormat.getDateInstance().format(Date.valueOf(prefs.getString("Semester " + String.valueOf(i + 1), "0")));
			}
		} else {
			for (int i = 0; i < 4; i++){
				semester[i] = DateFormat.getDateInstance().format(new java.util.Date());
			}
		}
		ListView lv = (ListView) findViewById(R.id.lvSemester);
		lv.setAdapter(new PrefAdapter(this, R.layout.pref_item,
							new String[] {"Semester 1", "Semester 2", "Semester 3", "Semester 4"}, semester));
		lv.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				showDialog(position);
			}
			
		});
		
		Button btnCancel = (Button) findViewById(R.id.btnSemCancel);
		if (prefs.contains("Semester 1")){
			btnCancel.setOnClickListener(new OnClickListener(){

				public void onClick(View v) {
					finish();

				}
			
			});
		} else {
			btnCancel.setEnabled(false);
		}
		
		Button btnOK = (Button) findViewById(R.id.btnSemOK);
		btnOK.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				SharedPreferences.Editor ed = prefs.edit();
				for (int i = 0; i < 4; i++){
					Date d = new Date(Integer.valueOf(semester[i].substring(6, 10)) - 1900,
									  Integer.valueOf(semester[i].substring(3, 5)) - 1, Integer.valueOf(semester[i].substring(0, 2)));
					ed.putString("Semester " + String.valueOf(i + 1) , d.toString());
				}
				ed.commit();
				finish();
			}
		});
	}

	public Dialog onCreateDialog(final int id){
		OnDateSetListener callback = new OnDateSetListener(){

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				semester[id] = DateFormat.getDateInstance().format(new Date(year - 1900, monthOfYear, dayOfMonth));
				ListView lv = (ListView)findViewById(R.id.lvSemester);
				lv.setAdapter(new PrefAdapter(SemesterActivity.this, R.layout.pref_item,
						new String[] {"Semester 1", "Semester 2", "Semester 3", "Semester 4"}, semester));
				
			}			
		};
		return new DatePickerDialog(SemesterActivity.this, callback,Integer.valueOf(semester[id].substring(6, 10)),
				Integer.valueOf(semester[id].substring(3, 5)) - 1, Integer.valueOf(semester[id].substring(0, 2)));
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

}
