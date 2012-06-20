package apps.schmitzi.Zensurenverwaltung;

import android.graphics.Paint;

public class MarkType {

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
}
