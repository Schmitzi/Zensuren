package apps.schmitzi.Zensurenverwaltung;

import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SubjectShower extends Activity {

		private String subject;
		SQLiteDatabase base;
		TestAdapter adapter;
		final int MODE_ADD = 0, MODE_EDIT = 1;
		Date[] semester = new Date[4];
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.subject_shower);
			Intent starter = getIntent();
			subject = starter.getData().getHost();
			setTitle(subject);
			SharedPreferences prefs = getSharedPreferences("Zensuren", MODE_PRIVATE);
			semester[0] = Date.valueOf(prefs.getString("Semester 1", ""));
			semester[1] = Date.valueOf(prefs.getString("Semester 2", ""));
			semester[2] = Date.valueOf(prefs.getString("Semester 3", ""));
			semester[3] = Date.valueOf(prefs.getString("Semester 4", ""));
		}
		
		private void initializeListView() {
			TextView txtMean = (TextView) findViewById(R.id.Mean2Text);
			base = openOrCreateDatabase(SubjectPicker.DATABASE, MODE_PRIVATE, null);
			Cursor c = base.query("subjects", new String[] {"mean"}, "name = ?", new String[]{subject}, null, null, null);
			c.moveToFirst();
			if (! c.isNull(0))
				txtMean.setText(Double.toString(((double) Math.round(c.getDouble(0) * 100))/100));
			else txtMean.setText("keiner");
			c = base.query(subject.replace(' ', '_'), null, null, null, null, null, "date"); 
			ArrayList<Test> items = new ArrayList<Test>();
			ListView lv = (ListView) findViewById(R.id.lvMarks);
			if (c.getCount() > 0)
			{
				for (int i = 0; i < c.getCount(); i++){
					Test t = new Test(c, i);
					items.add(t);
				}
				Log.v("Zensurenverwaltung", Integer.toString(items.size()));
				adapter = new TestAdapter(this, R.layout.test_item, items);
				lv.setAdapter(adapter);
				registerForContextMenu(lv);
				
				
			} else {
				lv.setAdapter(new ArrayAdapter<String>(this, R.layout.empty_list, new String[] {"Keine Zensuren vorhanden"}));
			}
			base.close();
		}
		@Override
		public void onResume()
		{
			super.onResume();
			initializeListView();;
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
			switch(item.getItemId()){
			case R.id.AddTestMenuBtn:
				in = new Intent("apps.schmitzi.Zensurenverwaltung.ADD_TEST",getIntent().getData());
				Log.v("Zensurenverwaltung", Boolean.toString(base.isOpen()));
				startActivity(in);
				return true;
			case R.id.MainLandAddSubject:
				in = new Intent("apps.schmitzi.Zensurenverwaltung.ADD_SUBJECT");
				in.addCategory(Intent.CATEGORY_DEFAULT);
				in.putExtra("apps.schmitzi.Zensurenverwaltung.requestCode", MODE_ADD);
				startActivity(in);
				return true;
			case R.id.DeleteMenuBtn:
				base = openOrCreateDatabase(SubjectPicker.DATABASE, MODE_PRIVATE, null);
				base.execSQL("DROP TABLE " + subject.replace(" ", "_"));
				base.execSQL("DELETE FROM subjects WHERE name = '" + subject + "';");
				base.close();
				finish();
				return true;
			case R.id.PrefLandButton:
				in = new Intent("apps.schmitzi.Zensurenverwaltung.PREFERENCES");
				startActivity(in);
				return true;
			case R.id.EditMenuButton:
				in = new Intent("apps.schmitzi.Zensurenverwaltung.ADD_SUBJECT");
				in.addCategory(Intent.CATEGORY_DEFAULT);
				in.putExtra("apps.schmitzi.Zensurenverwaltung.subject", subject);
				in.putExtra("apps.schmitzi.Zensurenverwaltung.requestCode", MODE_EDIT);
				startActivity(in);
				return true;
			default:
				return false;
			}
		}
		
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
			super.onCreateContextMenu(menu, v, menuInfo);
			MenuInflater inflater = getMenuInflater();
			if (v.getId() == R.id.lvMarks){
				inflater.inflate(R.menu.marks_list, menu);
				AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
			}
		}
		
		public boolean onContextItemSelected(MenuItem item) {
			final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			switch (item.getItemId()) {
			case R.id.deleteMark:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Wollen Sie wirklich löschen?")
					   .setCancelable(false)
					   .setPositiveButton("Ja",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							base = openOrCreateDatabase(SubjectPicker.DATABASE, MODE_PRIVATE, null);
							ListView lv = (ListView) findViewById(R.id.lvMarks);
							int position = lv.getPositionForView(info.targetView);
							Cursor c = base.query(subject.replace(' ', '_'), new String[] {"rowid"}, null, null, null, null, null);
							c.moveToPosition(position);
							int id = c.getInt(0);
							base.delete(subject.replace(' ', '_'), "rowid = ?", new String[] {String.valueOf(id)});
							ContentValues values = new ContentValues();
							c= base.query("subjects", new String[]{"type"}, "name = ?", new String[]{subject}, null, null, null);
							c.moveToFirst();
							int type = c.getInt(0);
							c = base.query(subject.replace(' ', '_'), null, null, null, null, null, "date");
							Double mean = new Calculator(c, type, getSharedPreferences("Zensuren", MODE_PRIVATE)).calculateMean();
							values.put("mean", mean);
							base.update("subjects", values, "name = ?", new String[] {subject});
							base.close();
							initializeListView();
							dialog.dismiss();
						}
					})
					   .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							
						}
					});
				builder.show();
				return true;
			default:
				return false;
			}
		}
		
		public class TestAdapter extends ArrayAdapter<Test>
		{
			int currentSemester = 1;
			public ArrayList<Test> items;

			public TestAdapter(Context context, int textViewResourceId, ArrayList<Test> items) {
				super(context, textViewResourceId, items);
				this.items = items;
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				View v = convertView;
				if (position == 0){
					if(v == null)
					{
						LayoutInflater inf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						v = inf.inflate(R.layout.divider, null);
					}
					TextView t = (TextView) v.findViewById(R.id.dividerText);
					t.setText("Semester 1");
					return v;
				}
				Date d = items.get(position - currentSemester).getDate();
				if (currentSemester < 4 && !d.before(semester[currentSemester])){
					if(v == null)
					{
						LayoutInflater inf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						v = inf.inflate(R.layout.divider, null);
					}
					TextView t = (TextView) v.findViewById(R.id.dividerText);
					currentSemester++;
					t.setText("Semester " + String.valueOf(currentSemester));
					return v;
				}
				if(v == null)
				{
					LayoutInflater inf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = inf.inflate(R.layout.test_item, null);
				}
				String s = DateFormat.getDateInstance().format(d);
				if (s!= null){
					TextView t1 = (TextView) v.findViewById(R.id.DateText);
					if (t1 != null) t1.setText(s);
					TextView t2 = (TextView) v.findViewById(R.id.MarkText);
					if (t2 != null){
						int mark = items.get(position - currentSemester).getMark();
						t2.setText(Integer.toString(mark));
						if (items.get(position - currentSemester).getType()) v.setBackgroundResource(R.drawable.klausur);
						else v.setBackgroundResource(0);
					}
				}
				
				Log.v(items.get(position).getDate().toString(), String.valueOf(items.get(position).getType()));
				return v;
			}
			
		}
}