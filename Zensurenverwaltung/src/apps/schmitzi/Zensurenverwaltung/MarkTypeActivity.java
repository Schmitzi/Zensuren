package apps.schmitzi.Zensurenverwaltung;

import java.util.ArrayList;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

import android.app.Activity;
import android.content.Context;
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

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mark_type);
		InitializerTask task = new InitializerTask();
		task.execute((Void)null);
		Button btnOK = (Button)findViewById(R.id.btnOK);
		
	}

	private class MarkTypeAdapter extends ArrayAdapter<MarkType> {

		public MarkTypeAdapter(Context context, int textViewResourceId,
				List<MarkType> objects) {
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
			return v;

		}

	}
	
	private class InitializerTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			ListView lv = (ListView) findViewById(R.id.lstMarkTypes);
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if (position == parent.getCount() - 1) {
						adapter.insert(new MarkType(Color.WHITE, "", 0), position);
						adapter.notifyDataSetChanged();
					}

				}

			});
			
			adapter.addAll(connection.getMarkTypes());
			adapter.add(new MarkType(Color.TRANSPARENT, "Dummy", 0));
			adapter.notifyDataSetChanged();
			return null;
		}
		
	}

}
