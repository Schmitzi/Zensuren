package apps.schmitzi.Zensurenverwaltung;

import java.util.ArrayList;

public class Configuration {
	
	private ArrayList<MarkType> usedMarkTypes;
	private ArrayList<Integer> weights;
	
	public void addMarkType(MarkType newType, int weight){
		usedMarkTypes.add(newType);
		weights.add(weight);
	}
	
	public int getWeight(MarkType type){
		int index = usedMarkTypes.indexOf(type);
		return weights.get(index).intValue();
	}
	
	public ArrayList<MarkType> getMarkTypes(){
		return usedMarkTypes;
	}
}
