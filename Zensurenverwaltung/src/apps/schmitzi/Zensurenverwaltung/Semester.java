package apps.schmitzi.Zensurenverwaltung;

import java.sql.Date;

public class Semester {

	private Date beginning;
	private int id;
	
	public Semester(int id, Date beginning){
		this.beginning = beginning;
		this.id = id;
	}
	
	public Date getBeginning(){
		return beginning;
	}
	
	public void setBeginning(Date value){
		beginning = value;
	}
	
	public int getId(){
		return id;
	}
}
