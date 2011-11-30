package apps.schmitzi.Zensurenverwaltung;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class NoSubjectsActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.no_subjects);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.subject_picker, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		
		switch(item.getItemId()) {
			case R.id.AddSubjectMenuButton:
				Intent in = new Intent("apps.schmitzi.Zensurenverwaltung.ADD_SUBJECT");
				in.addCategory(Intent.CATEGORY_DEFAULT);
				startActivity(in);
				return true;
    		default:
    			return false;
		}
	}
}
