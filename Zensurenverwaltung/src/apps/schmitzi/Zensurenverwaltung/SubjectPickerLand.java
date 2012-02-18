package apps.schmitzi.Zensurenverwaltung;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TextView;

public class SubjectPickerLand extends TabActivity {
	SQLiteDatabase base;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		setTheme(android.R.style.Theme_Light);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_land);
	//	initializeTabHost();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		if(display.getRotation() == Surface.ROTATION_0)
			finish();
		else {
			initializeTabHost();
			
		}
	}
	private void initializeTabHost() {
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;	
		Intent intent;
		tabHost.setCurrentTab(0);
		tabHost.clearAllTabs();
		tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
		base = openOrCreateDatabase(SubjectPicker.DATABASE, MODE_PRIVATE, null);
		Cursor c = base.query("subjects", new String[]{"short", "name"}, null, null, null, null, "short");
		if (c.getCount() > 0){
			c.moveToFirst();
			do{
				intent = new Intent(this, SubjectShower.class);
				intent.putExtra("apps.schmitzi.Zensurenverwaltung.subject", c.getString(1));
				spec = tabHost.newTabSpec(c.getString(0)).setIndicator(createTabView(tabHost.getContext(),c.getString(0))).setContent(intent);
				Log.v("Zensurenverwaltung", String.valueOf(spec == null));
				tabHost.addTab(spec);
			  }while (c.moveToNext());
		} else {
			intent = new Intent(this, NoSubjectsActivity.class);
			spec = tabHost.newTabSpec("").setIndicator("").setContent(intent);
			tabHost.addTab(spec);
		}
		base.close();
	}

	private View createTabView(Context context, String string) {
		View v = LayoutInflater.from(context).inflate(R.layout.tab, null);
		TextView t = (TextView) v.findViewById(R.id.tabText);
		t.setText(string);
		return v;
	}

}
