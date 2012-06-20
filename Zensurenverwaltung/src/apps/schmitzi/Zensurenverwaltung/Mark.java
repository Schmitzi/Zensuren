package apps.schmitzi.Zensurenverwaltung;

import java.sql.Date;

public class Mark {
	private int mark;
	private int type;
	private Date date;
	private int id;
	
	public Date getDate(){
		return date;
	}
	
	public int getMark(){
		return mark;
	}
	
	public void setMark(int value){
		mark = value;
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int value){
		id = value;
	}
	
	public int getType(){
		return type;
	}
	
	public Mark( Date date, int mark, int type, int id){
		this.mark = mark;
		this.type = type;
		this.date = date;
		this.id = id;
	}
}
