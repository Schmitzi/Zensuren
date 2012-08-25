package apps.schmitzi.Zensurenverwaltung;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ImageButton;
import android.widget.ListView;
import apps.schmitzi.Zensurenverwaltung.AddSubjectActivity.btnOKListener;

public class MarkTypeActivity extends Activity {
	SQLConnection connection = new SQLConnection(this);
	MarkTypeAdapter adapter = new MarkTypeAdapter(this, 0,
			new ArrayList<MarkType>());
	ArrayList<MarkType> originalList = new ArrayList<MarkType>();
	ArrayList<Integer> deletedList = new ArrayList<Integer>();
	int mode;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mark_type);
		InitializerTask task = new InitializerTask();
		task.execute((Void) null);
		Button btnOK = (Button) findViewById(R.id.btnOK);
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		mode = getIntent().getIntExtra("mode", SQLConnection.MODE_EDIT);
		if (mode == SQLConnection.MODE_SETUP) {
			btnOK.setText(getResources().getString(R.string.next));
			btnCancel.setText(getResources().getString(R.string.back));
		}
		btnOK.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				SaveTask task2 = new SaveTask();
				task2.execute(mode == SQLConnection.MODE_SETUP);
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	}

	protected Dialog onCreateDialog(int id) {
		if (id == 0) {
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setCancelable(false);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setTitle(R.string.saving);
			progressDialog.setMessage(getResources().getString(R.string.wait));
			return progressDialog;
		}
		return null;
	}

	private class MarkTypeAdapter extends ArrayAdapter<MarkType> {

		public MarkTypeAdapter(Context context, int textViewResourceId,
				List<MarkType> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View v = convertView;
			LayoutInflater inf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (position == getCount() - 1) {
				v = inf.inflate(R.layout.add_item, null);
				return v;
			}
			v = inf.inflate(R.layout.mark_type_item, null);
			final MarkType current = getItem(position);
			EditText edt = (EditText) v.findViewById(R.id.edtName);
			if (edt != null) {
				edt.setText(current.getName());
			}
			ImageButton btn = (ImageButton) v.findViewById(R.id.btnHighlight);
			if (btn != null) {
				btn.setBackgroundColor(current.getPaint().getColor());
				btn.setOnClickListener(new OnClickListener() {
					ImageButton btn;

					public void setColor(int color) {
						btn.setBackgroundColor(color);
					}

					public void onClick(View v) {
						btn = (ImageButton) v;
						AmbilWarnaDialog dlg = new AmbilWarnaDialog(
								MarkTypeActivity.this, current.getPaint()
										.getColor(),
								new AmbilWarnaDialog.OnAmbilWarnaListener() {

									public void onOk(AmbilWarnaDialog dialog,
											int color) {
										setColor(color);

									}

									public void onCancel(AmbilWarnaDialog dialog) {
										// TODO Auto-generated method stub

									}
								});
						dlg.show();
					}
				});
			}
			Button btnDel = (Button) v.findViewById(R.id.btnDelete);
			if (btnDel != null) {
				btnDel.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						adapter.remove(adapter.getItem(position));
						deletedList.remove(position);
						adapter.notifyDataSetChanged();

					}
				});
			}
			return v;

		}

	}

	private class InitializerTask extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(Void... params) {
			ListView lv = (ListView) findViewById(R.id.lstMarkTypes);
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if (position == parent.getCount() - 1) {
						adapter.insert(new MarkType(Color.WHITE, "", 0),
								position);
						adapter.notifyDataSetChanged();
					}

				}

			});
			originalList = connection.getMarkTypes();
			adapter.addAll((Collection<MarkType>) originalList.clone());
			adapter.add(new MarkType(Color.TRANSPARENT, "Dummy", 0));
			adapter.notifyDataSetChanged();
			return null;
		}

	}

	private class SaveTask extends AsyncTask<Boolean, Void, Void> {

		@Override
		protected Void doInBackground(Boolean... params) {
			SQLConnection connection = new SQLConnection(MarkTypeActivity.this);
			ListView lv = (ListView) findViewById(R.id.lstMarkTypes);
			String name;
			int color;
			View v;
			if (params[0]) {
				for (int i = 0; i < lv.getChildCount(); i++) {
					v = lv.getChildAt(i);
					name = ((EditText) v.findViewById(R.id.edtName)).getText()
							.toString();
					color = ((ImageButton) v.findViewById(R.id.btnHighlight))
							.getSolidColor();
					connection.addMarkType(name, color);
				}
			} else {
				for (int i : deletedList) {
					connection.deleteMarkType(originalList.get(i).getId());
					originalList.remove(i);
				}
				ArrayList<MarkType> newList = new ArrayList<MarkType>();
				for (int i = 0; i < lv.getChildCount() - 1; i++) {
					name = ((EditText) lv.getChildAt(i).findViewById(
							R.id.edtName)).getText().toString();
					color = ((ImageButton) lv.getChildAt(i).findViewById(
							R.id.btnHighlight)).getSolidColor();
					MarkType temp = new MarkType(color, name, adapter
							.getItem(i).getId());
					newList.add(temp);
				}

				//Neue Typen eintragen. Wenn die ID ungleich 0 ist, ist der Typ schon vorhanden und wird bearbeitet.
				for (int i = 0; i < originalList.size(); i++) {
					if (!originalList.get(i).equals(newList.get(i))) {
						MarkType n = newList.get(i);
						if (n.getId() != 0)
							connection.editMarkType(newList.get(i).getId(),
									n.getName(), n.getPaint().getColor());
						else
							connection.addMarkType(newList.get(i).getName(),
									newList.get(i).getPaint().getColor());
					}
				}
				for (int i = originalList.size(); i < newList.size(); i++) {
					MarkType n = newList.get(i);
					if (n.getId() != 0)
						connection.editMarkType(newList.get(i).getId(),
								n.getName(), n.getPaint().getColor());
					else
						connection.addMarkType(newList.get(i).getName(),
								newList.get(i).getPaint().getColor());
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			dismissDialog(0);
			if (mode == SQLConnection.MODE_EDIT)
				finish();
			else {
				Intent in = new Intent(MarkTypeActivity.this, SubjectTypeListActivity.class);
				in.putExtra("mode", mode);
				startActivityForResult(in, 0);
			}
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}
}
