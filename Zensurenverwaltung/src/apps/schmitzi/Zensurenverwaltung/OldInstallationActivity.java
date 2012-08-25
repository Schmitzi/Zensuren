package apps.schmitzi.Zensurenverwaltung;

import java.sql.Date;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class OldInstallationActivity extends Activity {
	public static final String LEGACY_DATABASE = "Zensurenverwaltung_DATA";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.old_installation);
		Button btnYes = (Button)findViewById(R.id.btnOK);
		btnYes.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				UpdateTask task = new UpdateTask();
				task.execute((Void)null);
				showDialog(0);
			}
		});
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			setResult(RESULT_OK);
			finish();
		}
	}
	
	public Dialog onCreateDialog(int id){
		switch (id) {
		case 0:
			ProgressDialog pDialog = new ProgressDialog(OldInstallationActivity.this, ProgressDialog.STYLE_SPINNER);
			pDialog.setTitle(R.string.copying_settings);
			pDialog.setMessage(getResources().getText(R.string.wait));
			return pDialog;

		default:
			return null;
		}
	}

	public class UpdateTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			SQLConnection connection = new SQLConnection(
					OldInstallationActivity.this);
			connection.createDatabase();
			initTypes(connection);
			SharedPreferences preferences = OldInstallationActivity.this
					.getSharedPreferences(SQLConnection.DATABASE, MODE_PRIVATE);
			Editor editor = preferences.edit();
			for (int i = 1; i < 5; i++) {
				connection.addSemester(Date.valueOf(preferences.getString(
						"Semester " + String.valueOf(i), "")));
				editor.remove("Semester " + String.valueOf(i));
			}
			editor.commit();
			SQLiteDatabase db = openOrCreateDatabase(LEGACY_DATABASE,
					MODE_PRIVATE, null);
			Cursor c = db.query("subjects", null, null, null, null, null, null);
			if (c.getCount() > 0) {
				c.moveToFirst();
				do {
					int id = 1;
					connection.addSubject(c.getString(0), c.getString(1),
							c.getInt(2));
					connection.setSubjectMean(id, c.getDouble(3));
					Cursor d = db.query(c.getString(0).replace(" ", "_"), null,
							null, null, null, null, null);
					if (d.getCount() > 0){
						d.moveToFirst();
						do{
							connection.addMark(id, c.getInt(1), Date.valueOf(c.getString(0)), c.getInt(2) + 1);
						} while(c.moveToNext());
					}
					id++;
				} while (c.moveToNext());
			}

			return null;
		}
		
		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(Void value){
			dismissDialog(0);
			setResult(RESULT_OK);
			finish();
		}

		private void initTypes(SQLConnection connection) {
			connection.addMarkType("Test", Color.WHITE);
			connection.addMarkType("Klausur", Color.rgb(255, 180, 0));
			Configuration configuration1 = new Configuration();
			MarkType test = new MarkType(Color.WHITE, "Test", 1);
			MarkType klausur = new MarkType(Color.rgb(255, 180, 0), "Klausur",
					2);
			configuration1.addMarkType(test, 1);
			SubjectType type = new SubjectType();
			type.setName("Grundkurs ohne Klausuren");
			type.addConfig(1, configuration1);
			connection.addSubjectType(type);
			Configuration configuration2 = new Configuration();
			configuration2.addMarkType(test, 3);
			configuration2.addMarkType(klausur, 1);
			type = new SubjectType();
			type.addConfig(3, configuration1);
			type.addConfig(1, configuration2);
			type.setName("Grundkurs mit Klausuren");
			connection.addSubjectType(type);
			type = new SubjectType();
			type.addConfig(1, configuration2);
			type.addConfig(4, configuration1);
			type.setName("Grundkurs mit Prüfung");
			connection.addSubjectType(type);
			configuration2 = new Configuration();
			configuration2.addMarkType(klausur, 1);
			configuration2.addMarkType(test, 1);
			type = new SubjectType();
			type.addConfig(1, configuration2);
			type.addConfig(4, configuration1);
			type.setName("Leistungskurs");
			connection.addSubjectType(type);
		}

	}

}
