package apps.schmitzi.Zensurenverwaltung;

import java.util.ArrayList;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SubjectTypeListActivity extends Activity {
	
	SubjectTypeAdapter adapter;
	ArrayList<Integer> deledtedIds = new ArrayList<Integer>();
	int mode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subject_type_list);
		mode = getIntent().getIntExtra("mode", SQLConnection.MODE_EDIT);
		Button btnOK = (Button)findViewById(R.id.btnOK);
		Button btnCancel = (Button)findViewById(R.id.btnCancel);
		if(mode == SQLConnection.MODE_SETUP){
			btnCancel.setText(R.string.back);
			btnOK.setText(R.string.next);
		}
		btnOK.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				new SaveTask().execute((Void)null);
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
				
			}
		});
		
		ListView lv = (ListView)findViewById(R.id.lstSubjectTypes);
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent in = new Intent(SubjectTypeListActivity.this, SubjectTypeActivity.class);
				in.putExtra("mode", mode);
				if(position == parent.getChildCount() -1){
					in.putExtra("type", 0);
				} else
				in.putExtra("type", adapter.getItem(position).getId());
				startActivityForResult(in, 0);
			}
		});
		
		lv.setAdapter(adapter);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 0){
			new LoadTask().execute((Void)null);
		}
	}
	
	protected class LoadTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			SQLConnection connection = new SQLConnection(SubjectTypeListActivity.this);
			ArrayList<SubjectType> types = connection.getSubjectTypes();
			adapter = new SubjectTypeAdapter(SubjectTypeListActivity.this, R.layout.subject_type_item);
			adapter.addAll(types);
			adapter.add(new SubjectType());
			adapter.notifyDataSetChanged();
			return null;
		}
	}
	
	protected class SubjectTypeAdapter extends ArrayAdapter<SubjectType>{

		public SubjectTypeAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}
		
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent){
			View v = convertView;
			LayoutInflater inf = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
			if (position == getCount() - 1){
				v = inf.inflate(R.layout.add_item, null);
				return v;
			}
			v = inf.inflate(R.layout.subject_type_item, null);
			TextView txtName = (TextView)v.findViewById(R.id.txtName);
			txtName.setText(getItem(position).getName());
			ImageView img = (ImageView)v.findViewById(R.id.imgDelete);
			img.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					deledtedIds.add(getItem(position).getId());
					remove(getItem(position));
				}
			});
			return v;
			
		}
	}
	
	protected class SaveTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			SQLConnection connection = new SQLConnection(SubjectTypeListActivity.this);
			for (int i: deledtedIds){
				connection.deleteSubjectType(i);
			}
			return null;
		}
		
		protected void onPostExecute(Void result){
			setResult(RESULT_OK);
			finish();
		}
		
	}
}
