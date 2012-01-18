package apps.schmitzi.Zensurenverwaltung;

import java.sql.Date;

import android.content.SharedPreferences;
import android.database.Cursor;

class Calculator {
	Cursor marks;
	int type;
	Date[] semesterDates = new Date[4];
	
	public Calculator(Cursor c, int type, SharedPreferences prefs){
		super();
		marks = c;
		this.type = type;
		for (int i = 0; i < 4; i++){
			semesterDates[i] = Date.valueOf(prefs.getString("Semester " + String.valueOf(i + 1), ""));
		}
	}
	
	public Double calculateMean(){
		Double[] semester = new Double[4];
		double result = 0;
		double Klausuren = 0, Tests = 0;
		int numberOfKlausuren = 0, numberOfTests = 0, currentSemester = 0;
		if (marks.getCount() == 0 ) return null;
		marks.moveToFirst();
		do{
			Date date = Date.valueOf(marks.getString(0));
			if(!date.before(semesterDates[currentSemester + 1])){
				switch(type){
				case 0:
					semester[currentSemester] = Tests / numberOfTests;
					Tests = 0; numberOfTests = 0; currentSemester++;
					break;
				case 1:
					if (currentSemester < 2){
						if (numberOfKlausuren == 0) semester[currentSemester] = (Tests/numberOfTests);
						else if (numberOfTests == 0) semester[currentSemester] = Klausuren;
						else semester[currentSemester] = Klausuren * 0.25 + (Tests / numberOfTests) * 0.75;
					} else {
						semester[currentSemester] = Tests/numberOfTests;
					}
					Tests = 0; numberOfTests = 0; Klausuren = 0; numberOfKlausuren = 0; currentSemester++;
					break;
				case 2:
					if (currentSemester < 3){
						if (numberOfKlausuren == 0) semester[currentSemester] = Tests/numberOfTests;
						else if (numberOfTests == 0) semester[currentSemester] = Klausuren;
						else semester[currentSemester] = Klausuren * 0.25 + (Tests / numberOfTests) * 0.75;
					} else {
						semester[currentSemester] = Tests/numberOfTests;
					}
					Tests = 0; numberOfTests = 0; Klausuren = 0; numberOfKlausuren = 0; currentSemester++;
					break;
				case 3:
					if (currentSemester < 3){
						if (numberOfKlausuren == 0) semester[currentSemester] = Tests/numberOfTests;
						else if (numberOfTests == 0) semester[currentSemester] = Klausuren / numberOfKlausuren;
						else semester[currentSemester] = (Klausuren/ numberOfKlausuren) * 0.5 + (Tests / numberOfTests) * 0.5;
					} else {
						semester[currentSemester] = Tests / numberOfTests;
					}
					Tests = 0; numberOfTests = 0; Klausuren = 0; numberOfKlausuren = 0; currentSemester++;
					break;
				}
			}
			if (marks.getInt(2) == 0){
				Tests += marks.getDouble(1);
				numberOfTests++;
			} else {
				Klausuren += marks.getDouble(1);
				numberOfKlausuren++;
			}
		}while (marks.moveToNext());
		switch(type){
		case 0:
			semester[currentSemester] = Tests / numberOfTests;
			break;
		case 1:
			if (currentSemester < 2){
				if (numberOfKlausuren == 0) semester[currentSemester] = Tests / numberOfTests;
				else if (numberOfTests == 0) semester[currentSemester] = Klausuren / numberOfKlausuren;
				else semester[currentSemester] = Klausuren * 0.25 + (Tests / numberOfTests) * 0.75;
			} else {
				semester[currentSemester] = Tests/numberOfTests;
			}
			break;
		case 2:
			if (currentSemester < 3){
				if (numberOfKlausuren == 0) semester[currentSemester] = Tests/numberOfTests;
				else if (numberOfTests == 0) semester[currentSemester] = Klausuren / numberOfKlausuren;
				else semester[currentSemester] = Klausuren * 0.25 + (Tests / numberOfTests) * 0.75;
			} else {
				semester[currentSemester] = Tests/numberOfTests;
			}
			break;
		case 3:
			if (currentSemester < 3){
				if (numberOfKlausuren == 0) semester[currentSemester] = Tests/numberOfTests;
				else if (numberOfTests == 0) semester[currentSemester] = Klausuren / numberOfKlausuren;
				else semester[currentSemester] = (Klausuren/ numberOfKlausuren) * 0.5 + (Tests / numberOfTests) * 0.5;
			} else {
				semester[currentSemester] = Tests/numberOfTests;
			}
			break;
		}
		for (int i = 0; i<= currentSemester; i++) result += semester[i];
		result /= (currentSemester + 1);
		return result;
	}

}
