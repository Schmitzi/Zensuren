package apps.schmitzi.Zensurenverwaltung;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SetupActivity extends Activity {

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup_start);
		Button btnCancel = (Button)findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		Button btnOK = (Button) findViewById(R.id.btnOK);
		btnOK.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				SharedPreferences preferences = getSharedPreferences(SQLConnection.DATABASE, MODE_PRIVATE);
				Intent intent;
				if(preferences.contains("Semester 1")){
					intent = new Intent(SetupActivity.this, OldInstallationActivity.class);
					startActivityForResult(intent, 0);
				}
				
			}
		});
	}
}
