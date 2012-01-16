package apps.schmitzi.Zensurenverwaltung;

import java.text.DateFormat;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SemesterActivity extends ListActivity {
	
	String[] semester = new String[4];
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		SharedPreferences prefs = getSharedPreferences("Zensuren", MODE_PRIVATE);
		for (int i = 0; i < 4; i++){
			semester[i] = prefs.getString("Semester " + String.valueOf(i + 1),
					DateFormat.getDateInstance().format(new java.util.Date()));
		}
		this.setListAdapter(new PrefAdapter(this, R.layout.pref_item,
							new String[] {"Semester 1", "Semester 2", "Semester 3", "Semester 4"}, semester));
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
