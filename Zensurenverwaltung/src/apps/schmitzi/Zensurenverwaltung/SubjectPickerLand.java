package apps.schmitzi.Zensurenverwaltung;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
	
	@Override
	public void onCreate(Bundle savedInstanceState){
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
		SubjectPicker.marksBase = openOrCreateDatabase(SubjectPicker.DATABASE, MODE_PRIVATE, null);
		Cursor c = SubjectPicker.marksBase.rawQuery("SELECT short, name FROM subjects ORDER BY short", null);
		if (c.getCount() > 0){
			c.moveToFirst();
			do{
				intent = new Intent("apps.schmitzi.Zensurenverwaltung.SHOW_SUBJECT", Uri.parse("content://" + c.getString(1)));
				spec = tabHost.newTabSpec(c.getString(0)).setIndicator(createTabView(tabHost.getContext(),c.getString(0))).setContent(intent);
				Log.v("Zensurenverwaltung", String.valueOf(spec == null));
				tabHost.addTab(spec);
			  }while (c.moveToNext());
		} else {
			intent = new Intent(this, NoSubjectsActivity.class);
			spec = tabHost.newTabSpec("").setIndicator("").setContent(intent);
			tabHost.addTab(spec);
		}
		//tabHost.setCurrentTab(0);
		SubjectPicker.marksBase.close();
	}

	private View createTabView(Context context, String string) {
		View v = LayoutInflater.from(context).inflate(R.layout.tab, null);
		TextView t = (TextView) v.findViewById(R.id.tabText);
		t.setText(string);
		return v;
	}

}
