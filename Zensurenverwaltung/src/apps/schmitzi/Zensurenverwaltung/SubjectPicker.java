package apps.schmitzi.Zensurenverwaltung;

import java.util.ArrayList;
import java.util.TreeMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SubjectPicker extends Activity {
    /** Called when the activity is first created. */
	
	public static SQLiteDatabase marksBase;
	public static final String DATABASE = "Zensurenverwaltung_Data";
	private SubjectAdapter adapter;
	SharedPreferences prefs;
	final int MODE_ADD = 0, MODE_EDIT = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("Zensuren", MODE_PRIVATE);
        if (!prefs.contains("Semester 1")){
        	Intent in = new Intent("apps.schmitzi.Zensurenverwaltung.SEMESTER");
        	in.addCategory(Intent.CATEGORY_DEFAULT);
        	startActivity(in);
        }
        setContentView(R.layout.main);
        marksBase = this.openOrCreateDatabase(DATABASE, MODE_PRIVATE, null);
        marksBase.execSQL("CREATE TABLE IF NOT EXISTS subjects ( _id INTEGER AUTO_INCREMENT PRIMARY KEY," +
        				  "name varchar(100) NOT NULL, short varchar(10) NOT NULL, mean NUMERIC(4,2), type INTEGER );");
        marksBase.close();
        SharedPreferences prefs = getSharedPreferences("Zensuren", MODE_PRIVATE);
        if (!prefs.contains("Semester 1")){
        	
        }
    }
	
    @Override
	protected void onResume() {
		super.onResume();
		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		if (display.getRotation() == Surface.ROTATION_0)
			initializeListView();
		else
			startActivity(new Intent("apps.schmitzi.Zensurenverwaltung.CHANGE_TO_LANDSCAPE"));
	}

	private void initializeListView() {
		marksBase = this.openOrCreateDatabase(DATABASE, MODE_PRIVATE, null);
		Cursor c = marksBase.query("subjects", new String[] {"name, mean"}, null, null, null, null, null);
        TreeMap<String, Double> items = new TreeMap<String, Double>();
        if (c.getCount() > 0) {
        	c.moveToFirst();
        	do {
        		Double mean;
        		if (! c.isNull(1)) mean = c.getDouble(1);
        		else mean = null;
        		items.put(c.getString(0), mean);
        	} while (c.moveToNext());
        	ListView lv = (ListView) findViewById(R.id.lvSubjects);
        	adapter = new SubjectAdapter(this, R.layout.list_item, items);
        	lv.setAdapter(adapter);
        	registerForContextMenu(lv);
        	lv.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					String subject = ((TextView) arg1.findViewById(R.id.SubjectText)).getText().toString();
					Intent in = new Intent("apps.schmitzi.Zensurenverwaltung.SHOW_SUBJECT");
					in.putExtra("apps.schmitzi.Zensurenverwaltung.subject", subject);
					startActivity(in);
				}
        	});
        } else {
        	ListView lv = (ListView) findViewById(R.id.lvSubjects);
        	lv.setAdapter(new ArrayAdapter<String>(this, R.layout.empty_list, new String[] {"Keine Fächer vorhanden"}));
        }
        marksBase.close();
	}

	private class SubjectAdapter extends ArrayAdapter<String> {
    	public TreeMap<String, Double> items;
    	
    	public SubjectAdapter(Context context, int textViewResourceId, TreeMap<String, Double> items) {
    		super(context, textViewResourceId, new ArrayList<String>(items.keySet()));
    		this.items = items;
    	}
    	
    	 @Override
    	 public View getView(int position, View convertView, ViewGroup parent) {
    		 View v = convertView;
    		 if(v==null) {
    			 LayoutInflater inf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			 v = inf.inflate(R.layout.list_item, null);
    		 }
    		 String s = (String) items.keySet().toArray()[position];
    		 if (s != null) {
    			 TextView t = (TextView) v.findViewById(R.id.SubjectText);
    			 if (t != null)
    				 t.setText(s);
    			 TextView t2 = (TextView) v.findViewById(R.id.MeanText);
    			 Double mean = items.get(s);
    			 if (t2 != null && mean != null )
    				 t2.setText(Double.toString(((double) Math.round(mean * 100))/100));
    			 else if ( t2 != null)
    				 t2.setText("");
    		 }
        	 return v;
    	 }
    }

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		if (v.getId() == R.id.lvSubjects) {
			inflater.inflate(R.menu.subject_list, menu);
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
			TextView t = (TextView) info.targetView.findViewById(R.id.SubjectText);
			menu.setHeaderTitle(t.getText().toString());
		}
		
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.DeleteItem:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Wollen Sie wirklich löschen?")
				   .setCancelable(false)
				   .setPositiveButton("Ja",new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						TextView t = (TextView) info.targetView.findViewById(R.id.SubjectText);
						adapter.items.remove(t.getText().toString());
						marksBase = openOrCreateDatabase(DATABASE, MODE_PRIVATE, null);
						marksBase.execSQL("DELETE FROM subjects WHERE name ='" + t.getText().toString() +"';");
						marksBase.execSQL("DROP TABLE " + t.getText().toString().replace(" ", "_") +";");
						marksBase.close();
						initializeListView();
						dialog.dismiss();
					}
				})
				   .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
		
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						
					}
				});
			AlertDialog alert = builder.create();
			alert.show();
			return true;
		case R.id.editItem:
			TextView t = (TextView) info.targetView.findViewById(R.id.SubjectText);
			Intent in = new Intent("apps.schmitzi.Zensurenverwaltung.ADD_SUBJECT");
			in.putExtra("apps.schmitzi.Zensurenverwaltung.subject", t.getText().toString());
			in.putExtra("apps.schmitzi.Zensurenverwaltung.requestCode", MODE_EDIT);
			startActivity(in);
			return true;
		default:
			return false;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.subject_picker, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		
		Intent in;
		switch(item.getItemId()) {
			case R.id.AddSubjectMenuButton:
				in = new Intent("apps.schmitzi.Zensurenverwaltung.ADD_SUBJECT");
				in.addCategory(Intent.CATEGORY_DEFAULT);
				in.putExtra("apps.schmitzi.Zensurenverwaltung.requestCode", MODE_ADD);
				startActivity(in);
				return true;
			case R.id.PreferencesMenuButton:
				in = new Intent("apps.schmitzi.Zensurenverwaltung.PREFERENCES");
				in.addCategory(Intent.CATEGORY_DEFAULT);
				startActivity(in);
				return true;
    		default:
    			return false;
		}
	}
}