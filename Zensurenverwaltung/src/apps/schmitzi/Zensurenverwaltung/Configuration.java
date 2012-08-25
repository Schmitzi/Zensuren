package apps.schmitzi.Zensurenverwaltung;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Configuration implements Parcelable {

	private ArrayList<MarkType> usedMarkTypes;
	private ArrayList<Integer> weights;

	public void addMarkType(MarkType newType, int weight) {
		usedMarkTypes.add(newType);
		weights.add(weight);
	}

	public int getWeight(MarkType type) {
		int index = usedMarkTypes.indexOf(type);
		return weights.get(index).intValue();
	}

	public ArrayList<MarkType> getMarkTypes() {
		return usedMarkTypes;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeTypedList(usedMarkTypes);
		dest.writeList(weights);

	}

	public static final Parcelable.Creator<Configuration> CREATOR = new Parcelable.Creator<Configuration>() {

		public Configuration createFromParcel(Parcel in) {
			Configuration conf = new Configuration();
			ArrayList<MarkType> markTypes = new ArrayList<MarkType>();
			in.readTypedList(markTypes, null);
			ArrayList<Integer> weights = new ArrayList<Integer>();
			in.readList(weights, null);
			conf.usedMarkTypes.addAll(markTypes);
			conf.weights.addAll(weights);
			return conf;
		}

		public Configuration[] newArray(int size) {
			return new Configuration[size];
		}
	};
}
