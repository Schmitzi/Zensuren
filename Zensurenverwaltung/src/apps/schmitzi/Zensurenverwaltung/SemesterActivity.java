package apps.schmitzi.Zensurenverwaltung;

import java.sql.Date;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

	int oldCount;
	SemesterAdapter adapter;
	int mode;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.semester_activity);
		mode = getIntent().getIntExtra("mode", SQLConnection.MODE_EDIT);
		ListView lv = (ListView) findViewById(R.id.lvSemester);
		lv.setAdapter(adapter);
		InitializerTask t = new InitializerTask();
		t.execute((Void)null);
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position == parent.getChildCount() - 1){
					adapter.insert(new Semester(0, adapter.getItem(position - 1).getBeginning()), position);
					adapter.notifyDataSetChanged();
				}
				showDialog(position);
			}

		});

		Button btnCancel = (Button) findViewById(R.id.btnSemCancel);
		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();

			}

		});
		
		Button btnOK = (Button) findViewById(R.id.btnSemOK);
		btnOK.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				SaveTask task = new SaveTask();
				task.execute((Void)null);
			}
		});
		
		if(mode == SQLConnection.MODE_SETUP){
			btnOK.setText(R.string.next);
			btnCancel.setText(R.string.back);
		}
	}

	public Dialog onCreateDialog(final int id) {
		OnDateSetListener callback = new OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				adapter.getItem(id).setBeginning(new Date(year, monthOfYear, dayOfMonth));
				adapter.notifyDataSetChanged();
			}
		};
		Date d = adapter.getItem(id).getBeginning();
		
		return new DatePickerDialog(SemesterActivity.this, callback,
				d.getYear(), d.getMonth(), d.getDate()); 
	}

	public class SemesterAdapter extends ArrayAdapter<Semester> {

		public SemesterAdapter(Context context, int textViewResourceId,
				List<Semester> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			LayoutInflater inf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (position == getCount() - 1) {
				v = inf.inflate(R.layout.add_item, null);
				return v;
			}
			v = inf.inflate(R.layout.pref_item, null);
			String s = String.valueOf(position + 1);
			if (s != null) {
				TextView t1 = (TextView) v.findViewById(R.id.PrefTitle);
				if (t1 != null)
					t1.setText(s);
				TextView t2 = (TextView) v.findViewById(R.id.PrefDescription);
				if (t2 != null)
					t2.setText(getItem(position).getBeginning().toString());
			}
			return v;
		}

	}
	
	public class SaveTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			SQLConnection connection = new SQLConnection(SemesterActivity.this);
			ListView lv = (ListView)findViewById(R.id.lvSemester);
			SemesterAdapter ad = (SemesterAdapter)lv.getAdapter();
			int newCount = ad.getCount();
			if (newCount < oldCount){
				for (int i = 0; i < newCount; i++){
					connection.editSemester(i + 1, ad.getItem(i).getBeginning());
				}
				for (int i = newCount; i < oldCount; i++){
					connection.deleteSemester(i + 1);
				}
			} else {
				for (int i = 0; i < oldCount; i++){
					connection.editSemester(i + 1, ad.getItem(i).getBeginning());
				}
				for (int i = oldCount; i < newCount; i++){
					connection.addSemester(ad.getItem(i).getBeginning());
				}
			}
			return null;
		}
		protected void onPostExecute(Void result){
			if (mode == SQLConnection.MODE_SETUP){
				Intent in = new Intent(SemesterActivity.this, MarkBordersActivity.class);
				in.putExtra("mode", mode);
				startActivityForResult(in, 0);
			} else
				finish();
		}
	}
	
	public class InitializerTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			SQLConnection connection = new SQLConnection(SemesterActivity.this);
			adapter.addAll(connection.getSemesters());
			adapter.add(new Semester(0, new Date(3000, 1,1)));
			adapter.notifyDataSetChanged();
			return null;
		}
		
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}
}
