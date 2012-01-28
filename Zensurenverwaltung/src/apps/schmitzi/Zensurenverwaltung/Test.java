package apps.schmitzi.Zensurenverwaltung;

import java.sql.Date;

import android.database.Cursor;

public class Test {
	private byte mark;
	private boolean type;
	private Date date;
	
	public Date getDate(){
		return date;
	}
	
	public byte getMark(){
		return mark;
	}
	
	public boolean getType(){
		return type;
	}
	
	public Test(){
		super();
		mark = 16;
		date = new Date(0);
		type = false;
	}
	
	public Test (Cursor c, int position){
		super();
		c.moveToPosition(position);
		mark = (byte) c.getInt(1);
		date = Date.valueOf(c.getString(0));
		type = c.getInt(2) == 1;
	}
}
