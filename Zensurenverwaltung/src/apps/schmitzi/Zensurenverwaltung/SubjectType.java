package apps.schmitzi.Zensurenverwaltung;

import java.util.ArrayList;

public class SubjectType {

	private ArrayList<Configuration> configs;
	private ArrayList<Integer> beginningSemsters;
	private String name;
	private int id;
	
	public Configuration getConfiguration(int semester){
		int index = 0;
		for (int i = 0; i < beginningSemsters.size(); i++){
			if(beginningSemsters.get(i + 1) > semester && beginningSemsters.get(i) <= semester)
				index = i;
		}
		return configs.get(index);
	}
	
	public ArrayList<Configuration> getConfigurations(){
		return configs;
	}
	
	public ArrayList<Integer> getSemesters(){
		return beginningSemsters;
	}
	
	public void addConfig(int beginningSemester, Configuration newConfiguration){
		configs.add(newConfiguration);
		beginningSemsters.add(beginningSemester);
	}
	
	public void setName(String value){
		name = value;
	}
	
	public String getName(){
		return name;
	}
	
	public void setId(int value){
		id = value;
	}
	
	public int getId(){
		return id;
	}
}
