package apps.schmitzi.Zensurenverwaltung;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class SubjectTypeActivity extends Activity {

	ConfigurationAdapter adapter;
	int id, mode;
	ArrayList<String> names = new ArrayList<String>();
	ArrayList<MarkType> markTypes;

	final String HEADER = "Header_item_string", ADD_MT = "Add_item_mt_string",
			ADD_CONF = "Add_item_conf_string";
	final int ERROR = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subject_type);
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		Button btnOK = (Button) findViewById(R.id.btnOK);
		btnOK.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				GetDataTask task = new GetDataTask();
				task.execute((Void) null);
			}
		});

		ListView lv = (ListView) findViewById(R.id.lstMarkTypes);
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parentView, View view,
					int position, long id) {
				if (adapter.getItem(position).getName() == ADD_MT) {
					adapter.insert(new BasicNameValuePair(names.get(0), ""),
							position);
				}
				if (adapter.getItem(position).getName() == ADD_CONF) {
					adapter.insert(new BasicNameValuePair(HEADER, ""), position);
					adapter.insert(new BasicNameValuePair(ADD_MT, ""),
							position + 1);
				}
			}
		});
	}

	protected class ConfigurationAdapter extends
			ArrayAdapter<BasicNameValuePair> {

		public ConfigurationAdapter(Context context, int resource,
				int textViewResourceId, List<BasicNameValuePair> objects) {
			super(context, resource, textViewResourceId, objects);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View v = convertView;
			LayoutInflater inf = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			BasicNameValuePair b = getItem(position);
			if (b.getName() == HEADER) {
				v = inf.inflate(R.layout.configuration_header, null);
				EditText edt = (EditText) v.findViewById(R.id.edtSem);
				edt.setText(b.getValue());
				ImageView img = (ImageView) v.findViewById(R.id.imgDelete);
				img.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						remove(getItem(position));
						while (getItem(position).getName() != HEADER
								&& position < getCount()) {
							remove(getItem(position));
						}
						notifyDataSetChanged();
					}
				});
				return v;
			}
			if (b.getName() == ADD_MT) {
				v = inf.inflate(R.layout.add_item, null);
				TextView t = (TextView) v.findViewById(R.id.addText);
				t.setText(getResources().getString(R.string.mark_type)
						+ t.getText().toString());
				return v;
			}
			if (b.getName() == ADD_CONF) {
				v = inf.inflate(R.layout.add_item, null);
				TextView t = (TextView) v.findViewById(R.id.addText);
				t.setText(getResources().getString(R.string.configuration)
						+ t.getText().toString());
				return v;
			}
			v = inf.inflate(R.layout.configuration_item, null);
			Spinner spn = (Spinner) v.findViewById(R.id.spnMarkType);
			ArrayList<String> names = new ArrayList<String>();
			if (spn != null) {
				spn.setAdapter(new ArrayAdapter<String>(
						SubjectTypeActivity.this,
						android.R.layout.simple_list_item_1, names));
				spn.setSelection(names.indexOf(b.getName()));
			}
			EditText edt = (EditText) findViewById(R.id.edtWeight);
			if (edt != null) {
				edt.setText(b.getValue());
			}
			ImageView img = (ImageView) v.findViewById(R.id.imgDelete);
			img.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					remove(getItem(position));
				}
			});
			return v;
		}
	}

	protected class InitTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			SQLConnection connection = new SQLConnection(
					SubjectTypeActivity.this);
			markTypes = connection.getMarkTypes();
			for (MarkType m : markTypes)
				names.add(m.getName());
			SubjectType type = new SubjectType();
			mode = getIntent().getIntExtra("mode", SQLConnection.MODE_EDIT);
			if (mode == SQLConnection.MODE_EDIT) {
				id = getIntent().getIntExtra("type", 0);
				if (id != 0) {
					type = connection.getSubjectType(id);
				}
				for (int i = 0; i < type.getConfigurations().size(); i++) {
					adapter.add(new BasicNameValuePair(HEADER, type
							.getSemesters().get(i).toString()));
					Configuration conf = type.getConfigurations().get(i);
					for (MarkType m : conf.getMarkTypes())
						adapter.add(new BasicNameValuePair(m.getName(), String
								.valueOf(conf.getWeight(m))));
					adapter.add(new BasicNameValuePair(ADD_MT, ""));
				}
			}
			adapter.add(new BasicNameValuePair(ADD_CONF, ""));
			adapter.notifyDataSetChanged();
			return null;
		}
	}

	protected class GetDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			SQLConnection connection = new SQLConnection(
					SubjectTypeActivity.this);
			ListView lv = (ListView) findViewById(R.id.lstMarkTypes);
			Configuration currentConfiguration = new Configuration();
			SubjectType subjectType = new SubjectType();
			subjectType.setId(id);
			int beginningSemester = 0;
			for (int i = 0; i < lv.getChildCount(); i++) {
				BasicNameValuePair b = adapter.getItem(i);
				if (b.getName() == ADD_CONF || b.getName() == ADD_MT)
					continue;
				View v = lv.getChildAt(i);
				if (b.getName() == HEADER) {
					if (i != 0)
						subjectType.addConfig(beginningSemester,
								currentConfiguration);
					EditText edt = (EditText) v.findViewById(R.id.edtSem);
					beginningSemester = Integer.valueOf(edt.getText()
							.toString());
					currentConfiguration = new Configuration();
				} else {
					Spinner spn = (Spinner) v.findViewById(R.id.spnMarkType);
					MarkType mt = connection.getMarkType(spn
							.getSelectedItemPosition() + 1);
					EditText edtW = (EditText) v.findViewById(R.id.edtWeight);
					currentConfiguration.addMarkType(mt,
							Integer.valueOf(edtW.getText().toString()));
				}
			}
			ArrayList<Integer> sems = subjectType.getSemesters();
			for (int i = 0; i < sems.size(); i++) {
				for (int j = i + 1; j < sems.size(); j++) {
					if (sems.get(i) == sems.get(j)) {
						cancel(true);
						return null;
					}
				}
			}
			EditText edt = (EditText)findViewById(R.id.edtName);
			subjectType.setName(edt.getText().toString());
			if (id != 0)
				connection.editSubjectType(subjectType);
			else
				connection.addSubjectType(subjectType);
			return null;
		}
	}

	protected void onPostExecute(Void result) {
		setResult(RESULT_OK);
		finish();
	}

	protected void onCancelled() {
		showDialog(ERROR);
	}

	protected Dialog onCreateDialog(int requestCode) {
		Dialog resultDialog = null;
		if (requestCode == ERROR) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setCancelable(false);
			b.setPositiveButton(getResources().getString(R.string.OK),
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							dismissDialog(ERROR);

						}
					});
			b.setTitle(R.string.double_conf);
			b.setMessage(R.string.double_conf_text);
			resultDialog = b.create();
		}
		return resultDialog;
	}
}
