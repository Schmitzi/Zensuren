package apps.schmitzi.Zensurenverwaltung;

import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;

public class MarkType implements Parcelable {

	private Paint highlightPaint;
	private String name;
	private int id;
	
	public Paint getPaint() {
		return highlightPaint;
	
	}
	
	public void setColor(int color){
		highlightPaint.setColor(color);
	}
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public MarkType(int color, String name, int id) {
		highlightPaint = new Paint();
		highlightPaint.setStyle(Paint.Style.STROKE);
		highlightPaint.setColor(color);
		this.name = name;
		this.id = id;
	}
	
	public boolean equals(MarkType otherMarkType){
		 return highlightPaint.getColor() == otherMarkType.highlightPaint.getColor() && name == otherMarkType.name;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(highlightPaint.getColor());
		dest.writeString(name);
		dest.writeInt(id);
	}
	
	public static final Parcelable.Creator<MarkType> CREATOR = new Parcelable.Creator<MarkType>() {
		public MarkType createFromParcel(Parcel in){
			return new MarkType(in.readInt(), in.readString(), in.readInt());
		}

		public MarkType[] newArray(int size) {
			return new MarkType[size];
		}
	};
}
