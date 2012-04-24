package apps.schmitzi.Zensurenverwaltung;

public class Subject {

	private int id, type;
	private String name, shortName;
	private Double mean;
	public Subject(int id, String name, String shortName, int type, Double mean) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.shortName = shortName;
		this.mean = mean;
	}
	
	public int getId(){
		return id;
	}
	
	public int getType(){
		return type;
	}
	
	public  String getName() {
		return name;
	}
	
	public String getShortName(){
		return shortName;
	}
	
	public Double getMean() {
		return mean;
	}
}
