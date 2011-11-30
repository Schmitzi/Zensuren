package apps.schmitzi.Zensurenverwaltung;

import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SubjectShower extends Activity {

		private String subject;
		SQLiteDatabase base;
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.subject_shower);
			Intent starter = getIntent();
			subject = starter.getData().getHost();
			setTitle(subject);
		}
		
		private void intializeListView() {
			Cursor c = base.rawQuery("SELECT * FROM " + subject.replace(" ", "_") + " ORDER BY date", null); 
			ArrayList<Test> items = new ArrayList<Test>();
			ListView lv = (ListView) findViewById(R.id.lvMarks);
			if (c.getCount() > 0)
			{
				for (int i = 0; i < c.getCount(); i++){
					Test t = new Test(c, i);
					items.add(t);
				}
				Log.v("Zensurenverwaltung", Integer.toString(items.size()));
				TestAdapter adapter = new TestAdapter(this, R.layout.test_item, items);
				lv.setAdapter(adapter);
				registerForContextMenu(lv);
				
				
			} else {
				lv.setAdapter(new ArrayAdapter<String>(this, R.layout.empty_list, new String[] {"Keine Zensuren vorhanden"}));
			}
		}
		@Override
		public void onResume()
		{
			super.onResume();
			TextView txtMean = (TextView) findViewById(R.id.Mean2Text);
			base = openOrCreateDatabase(SubjectPicker.DATABASE, MODE_PRIVATE, null);
			Cursor c = base.rawQuery("SELECT mean FROM subjects WHERE name = '"+ subject + "';", null);
			c.moveToFirst();
			if (! c.isNull(0))
				txtMean.setText(Double.toString(((double) Math.round(c.getDouble(0) * 100))/100));
			else txtMean.setText("keiner");
			intializeListView();
			base.close();
		}
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu){
			MenuInflater inflater = getMenuInflater();
			if(getParent() != null)
				inflater.inflate(R.menu.main_land, menu);
			else inflater.inflate(R.menu.subject_shower, menu);
			return true;
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item){
			Intent in;
//			if (getParent() != null)
	//		{
		//		TabActivity tab = (TabActivity) getParent();
			//	tab.getTabHost().setCurrentTab(0);
			//}
			switch(item.getItemId()){
			case R.id.AddTestMenuBtn:
				in = new Intent("apps.schmitzi.Zensurenverwaltung.ADD_TEST",getIntent().getData());
				Log.v("Zensurenverwaltung", Boolean.toString(base.isOpen()));
				startActivity(in);
				return true;
			case R.id.MainLandAddSubject:
				in = new Intent("apps.schmitzi.Zensurenverwaltung.ADD_SUBJECT");
				in.addCategory(Intent.CATEGORY_DEFAULT);
				startActivity(in);
				return true;
			case R.id.DeleteMenuBtn:
				SubjectPicker.marksBase = openOrCreateDatabase(SubjectPicker.DATABASE, MODE_PRIVATE, null);
				SubjectPicker.marksBase.execSQL("DROP TABLE " + subject.replace(" ", "_"));
				SubjectPicker.marksBase.execSQL("DELETE FROM subjects WHERE name = '" + subject + "';");
				SubjectPicker.marksBase.close();
				finish();
				return true;
			default:
				return false;
			}
		}
		public class TestAdapter extends ArrayAdapter<Test>
		{
			public ArrayList<Test> items;

			public TestAdapter(Context context, int textViewResourceId, ArrayList<Test> items) {
				super(context, textViewResourceId, items);
				this.items = items;
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				View v = convertView;
				if(v == null)
				{
					LayoutInflater inf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = inf.inflate(R.layout.test_item, null);
				}
				Date d = items.get(position).getDate();
				String s = DateFormat.getDateInstance().format(d);
				if (s!= null){
					TextView t1 = (TextView) v.findViewById(R.id.DateText);
					if (t1 != null) t1.setText(s);
					TextView t2 = (TextView) v.findViewById(R.id.MarkText);
					if (t2 != null){
						int mark = items.get(position).getMark();
						t2.setText(Integer.toString(mark));
						if (items.get(position).getType()) v.setBackgroundResource(R.drawable.klausur);
						else v.setBackgroundResource(0);
					}
				}
				
				Log.v(items.get(position).getDate().toString(), String.valueOf(items.get(position).getType()));
				return v;
			}
			
		}
}




































