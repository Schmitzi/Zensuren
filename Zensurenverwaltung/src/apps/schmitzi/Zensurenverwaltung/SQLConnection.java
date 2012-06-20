package apps.schmitzi.Zensurenverwaltung;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

import android.R.integer;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Klasse, die zur Vereinfachung des Datenbankzugriffs benutzt wird.
 * 
 * @author daniel
 * 
 */
public class SQLConnection {

	public static final String DATABASE = "Zensuren",
			SUBJECT_TABLE = "SUBJECT", SUBJECT_TYPE_TABLE = "SUBJECT_TYPE",
			MARK_TYPE_TABLE = "MARK_TYPE", MARK_TABLE = "MARK",
			TYPE_RELATION_TABLE = "SUBJT_USES_MARKT",
			SEMESTER_TABLE = "SEMESTER";
	public static final int MODE_PRIVATE = Activity.MODE_PRIVATE;
	private Activity activity;

	/**
	 * Erstellt eine neue Instanz, die die angegeben Activity als Kontext
	 * verwendet
	 * 
	 * @param context
	 *            Die als Kontext zu verwendende Activity
	 */
	public SQLConnection(Activity context) {
		activity = context;
	}

	/**
	 * Erzeugt ein neues Date-Objekt mit dem aktuellen Datum
	 * 
	 * @return Das aktuelle Datum
	 */
	public Date getToday() {
		Calendar cal = GregorianCalendar.getInstance();
		return new Date(cal.get(Calendar.YEAR) - 1900, cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH));
	}

	/**
	 * Fügr ein Fach der Datenbank hinzu
	 * 
	 * @param name
	 *            Der Name des neuen Fachs
	 * @param shortName
	 *            Die Abkürzung des neuen Fachs
	 * @param type
	 *            Der Typ des neuen Fachs
	 */
	public void addSubject(String name, String shortName, int type) {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("short", shortName);
		values.put("type", type);
		db.insert(SUBJECT_TABLE, null, values);
		db.close();
	}

	/**
	 * Bearbeitet ein Fach
	 * 
	 * @param id
	 *            Die rowid des Fachs
	 * @param name
	 *            Der neue Name des Fachs
	 * @param shortName
	 *            Die neue Abkürzung des Fachs
	 * @param type
	 *            Der neue Typ des Fachs
	 */
	public void editSubject(int id, String name, String shortName, int type) {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("short", shortName);
		values.put("type", type);
		String[] whereArgs = new String[] { String.valueOf(id) };
		db.update(SUBJECT_TABLE, values, "rowid = ?", whereArgs);
		db.close();
	}

	/**
	 * Ruft alle vorhandenen Fächer ab
	 * 
	 * @return Eine Liste aller eingetragenen Fächer
	 */
	public ArrayList<Subject> getAllSubjects() {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		Cursor c = db.query(SUBJECT_TABLE, new String[] { "rowid", "*" }, null,
				null, null, null, null);
		ArrayList<Subject> result = new ArrayList<Subject>();
		db.close();
		if (c.getCount() == 0)
			return result;
		c.moveToFirst();
		do {
			Double mean;
			if (c.isNull(4))
				mean = null;
			else
				mean = c.getDouble(4);
			Subject s = new Subject(c.getInt(0), c.getString(1),
					c.getString(2), c.getInt(3), mean);
			result.add(s);
		} while (c.moveToNext());

		return result;
	}

	/**
	 * Ruft ein Fach ab
	 * 
	 * @param id
	 *            Die rowid des gewünschten Fachs
	 * @return
	 */
	public Subject getSubject(int id) {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		Cursor c = db.query(SUBJECT_TABLE, new String[] { "rowid", "*" },
				"rowid = ?", new String[] { String.valueOf(id) }, null, null,
				null);
		db.close();
		if (c.getCount() == 0)
			return null;
		c.moveToFirst();
		Double mean;
		if (c.isNull(4))
			mean = null;
		else
			mean = c.getDouble(4);
		return new Subject(c.getInt(0), c.getString(1), c.getString(2),
				c.getInt(3), mean);
	}

	/**
	 * Legt den Durchschnitt eines Fachs fest
	 * 
	 * @param id
	 *            Die rowid des Fachs
	 * @param mean
	 *            Der neue Durchschnitt
	 */
	public void setSubjectMean(int id, Double mean) {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		ContentValues values = new ContentValues();
		values.put("mean", mean);
		String[] whereArgs = new String[] { String.valueOf(id) };
		db.update(SUBJECT_TABLE, values, "rowid = ?", whereArgs);
		db.close();
	}

	/**
	 * Löscht ein Fach
	 * 
	 * @param id
	 *            Die rowid des Fachs
	 */
	public void deleteSubject(int id) {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		String[] whereArgs = new String[] { String.valueOf(id) };
		db.delete(SUBJECT_TABLE, "rowid = ?", whereArgs);
		db.delete(MARK_TABLE, "subject = ?", whereArgs);
		db.close();
	}

	/**
	 * Fügt eine Zensur hinzu
	 * 
	 * @param subject
	 *            Das Fach, in dem die Zensur erhalten wurde
	 * @param mark
	 *            Der Wert der neuen Zensur
	 * @param date
	 *            Das Datum, an dem die Zensur erhalten wurde
	 * @param type
	 *            Der Typ der Zensur
	 */
	public void addMark(int subject, int mark, Date date, int type) {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		ContentValues values = new ContentValues();
		values.put("subject", subject);
		values.put("mark", mark);
		values.put("date", date.toString());
		values.put("type", type);
		db.insert(MARK_TABLE, null, values);
		db.close();
	}

	/**
	 * Bearbeitet eine Zensur
	 * 
	 * @param id
	 *            Die rowid der zu bearbeitenden Zensur
	 * @param mark
	 *            Der neue Wert der Zensur
	 * @param date
	 *            Das neue Datum der Zensur
	 * @param type
	 *            Der neue Typ der Zensur
	 */
	public void editMark(int id, int mark, Date date, int type) {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		ContentValues values = new ContentValues();
		values.put("mark", mark);
		values.put("date", date.toString());
		values.put("type", type);
		String[] whereArgs = new String[] { String.valueOf(id) };
		db.update(MARK_TABLE, values, "rowid = ?", whereArgs);
		db.close();
	}

	/**
	 * Löscht eine Zensur
	 * 
	 * @param id
	 *            Die rowid der zu löschenden Zensur
	 */
	public void deleteMark(int id) {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		String[] whereArgs = new String[] { String.valueOf(id) };
		db.delete(MARK_TABLE, "rowid = ?", whereArgs);
		db.close();
	}

	/**
	 * Ruft die Zensur mit der gegebenen rowid ab
	 * 
	 * @param id
	 *            Die rowid der Zensur
	 * @return Ein Mark-Objekt, das die Zensur darstellt
	 */
	public Mark getMark(int id) {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		String[] selectionArgs = new String[] { String.valueOf(id) };
		Cursor c = db.query(MARK_TABLE, new String[] { "rowid", "*" },
				"rowid = ?", selectionArgs, null, null, null);
		db.close();
		if (c.getCount() == 0)
			return null;
		c.moveToFirst();
		return new Mark(Date.valueOf(c.getString(1)), c.getInt(2), c.getInt(3),
				c.getInt(0));
	}

	/**
	 * Ruft alle Zensuren eines Fachs ab
	 * 
	 * @param subjectId
	 *            Die rowid des Fachs, dessen Zensuren abgerufen werden
	 * @return Eine Auflistung aller Zensuren des Fachs
	 */
	public ArrayList<Mark> getMarks(int subjectId) {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		String[] selectionArgs = new String[] { String.valueOf(subjectId) };
		Cursor c = db.query(MARK_TABLE, new String[] { "rowid", "*" },
				"subject = ?", selectionArgs, null, null, null);
		db.close();
		ArrayList<Mark> result = new ArrayList<Mark>();
		if (c.getCount() == 0)
			return result;
		c.moveToFirst();
		do {
			result.add(new Mark(Date.valueOf(c.getString(1)), c.getInt(2), c
					.getInt(3), c.getInt(0)));
		} while (c.moveToNext());
		return result;
	}

	/**
	 * Fügt ein Semester hinzu
	 * 
	 * @param beginning
	 *            Das Anfangsdatum des Semesters
	 */
	public void addSemester(Date beginning) {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		ContentValues values = new ContentValues();
		values.put("beginning", beginning.toString());
		db.insert(SEMESTER_TABLE, null, values);
		db.close();
	}

	/**
	 * Ändert ein Semester
	 * 
	 * @param id
	 *            Die rowid des Semesters
	 * @param beginning
	 *            Das neue Anfangsdatum
	 */
	public void editSemester(int id, Date beginning) {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		ContentValues values = new ContentValues();
		values.put("beginning", beginning.toString());
		String[] whereArgs = new String[] { String.valueOf(id) };
		db.update(SEMESTER_TABLE, values, "rowid = ?", whereArgs);
		db.close();
	}

	/**
	 * Ruft eine sortierte Liste aller Semester ab
	 * 
	 * @return Die Liste aller Semester
	 */
	public ArrayList<Semester> getSemesters() {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		Cursor c = db.query(SEMESTER_TABLE, new String[] { "rowid", "*" },
				null, null, null, null, "beginning");
		db.close();
		ArrayList<Semester> result = new ArrayList<Semester>();
		if (c.getCount() == 0)
			return result;
		c.moveToFirst();
		do {
			result.add(new Semester(c.getInt(0), Date.valueOf(c.getString(1))));
		} while (c.moveToNext());
		return result;
	}

	/**
	 * Fügt einen neuen Zensurentyp hinzu
	 * 
	 * @param name
	 *            Der Name des Zensurentyps
	 * @param highlightColor
	 *            Die Zahl, die die Farbe der Umrandung des Eintrags darstellt
	 */
	public void addMarkType(String name, int highlightColor) {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("highlightColor", highlightColor);
		db.insert(MARK_TYPE_TABLE, null, values);
		db.close();

	}

	/**
	 * Bearbeitet einen Zensurentyp
	 * 
	 * @param id
	 *            Die rowid des Zensurentyps
	 * @param name
	 *            Der neue Name des Zensurentyps
	 * @param highlightColor
	 *            Die Umrandungsfarbe des Zensurentyps
	 */
	public void editMarkType(int id, String name, int highlightColor) {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("highlightColor", highlightColor);
		String[] whereArgs = new String[] { String.valueOf(id) };
		db.update(MARK_TYPE_TABLE, values, "rowid = ?", whereArgs);
		db.close();
	}

	/**
	 * Löscht einen Zensurentyp
	 * 
	 * @param id
	 *            Die rowid des Zensurentyps
	 */
	public void deleteMarkType(int id) {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		String[] whereArgs = new String[] { String.valueOf(id) };
		db.delete(MARK_TYPE_TABLE, "rowid = ?", whereArgs);
		db.close();
	}

	public MarkType getMarkType(int id) {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		String[] whereArgs = new String[] { String.valueOf(id) };
		Cursor c = db.query(MARK_TYPE_TABLE, null, "rowid = ?", whereArgs,
				null, null, null);
		db.close();
		if (c.getCount() == 0)
			return null;
		c.moveToFirst();
		return new MarkType(c.getInt(1), c.getString(0), id);
	}

	public ArrayList<MarkType> getMarkTypes() {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		Cursor c = db.query(MARK_TYPE_TABLE, new String[] { "rowid", "*" },
				null, null, null, null, null);
		db.close();
		ArrayList<MarkType> result = new ArrayList<MarkType>();
		if (c.getCount() == 0)
			return result;
		c.moveToFirst();
		do {
			result.add(new MarkType(c.getInt(2), c.getString(1), c.getInt(0)));
		} while (c.moveToNext());
		return result;
	}

	public void addSubjectType(SubjectType type) {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		ContentValues values = new ContentValues();
		values.put("name", type.getName());
		db.insert(SUBJECT_TYPE_TABLE, null, values);
		Cursor c = db.query(SUBJECT_TYPE_TABLE, new String[] { "rowid" },
				"name = ?", new String[] { type.getName() }, null, null, null);
		c.moveToFirst();
		int id = c.getInt(0);
		for (int i = 0; i < type.getConfigurations().size(); i++) {
			Configuration configuration = type.getConfigurations().get(i);
			for (int j = 0; j < configuration.getMarkTypes().size(); j++) {
				MarkType mark = configuration.getMarkTypes().get(j);
				values = new ContentValues();
				values.put("subjectTypeId", id);
				values.put("markTypeId", mark.getId());
				values.put("weight", configuration.getWeight(mark));
				values.put("beginningSemester", type.getSemesters().get(i));
				db.insert(TYPE_RELATION_TABLE, null, values);
			}
		}
		db.close();
	}

	public void editSubjectType(SubjectType type) {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		ContentValues values = new ContentValues();
		values.put("name", type.getName());
		db.update(SUBJECT_TYPE_TABLE, values, "rowid = ?",
				new String[] { String.valueOf(type.getId()) });
		for (int i = 0; i < type.getConfigurations().size(); i++) {
			Configuration configuration = type.getConfigurations().get(i);
			for (int j = 0; j < configuration.getMarkTypes().size(); j++) {
				MarkType mark = configuration.getMarkTypes().get(j);
				values = new ContentValues();
				values.put("subjectTypeId", type.getId());
				values.put("markTypeId", mark.getId());
				values.put("weight", configuration.getWeight(mark));
				values.put("beginningSemester", type.getSemesters().get(i));
				db.update(TYPE_RELATION_TABLE, values, "subjectTypeId = ?",
						new String[] { String.valueOf(type.getId()) });
			}
		}
		db.close();
	}

	/**
	 * Ruft den Kurstyp mit der gegeben rowid ab
	 * @param id Die rowid des Kurstyps
	 * @return Das entprechende SubjectType-Objekt
	 */
	public SubjectType getSubjectType(int id) {
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE,
				MODE_PRIVATE, null);
		SubjectType result = new SubjectType();
		Cursor c = db.query(SUBJECT_TYPE_TABLE, new String[] { "name" },
				"rowid = ?", new String[] { String.valueOf(id) }, null, null,
				null);
		if (c.getCount() == 0){
			db.close();
			return null;
		}
		c.moveToFirst();
		result.setName(c.getString(0));
		result.setId(id);
		c = db.query(TYPE_RELATION_TABLE, new String[] { "markTypeId",
				"weight", "beginningSemester" }, "subjectTypeId = ?",
				new String[] { String.valueOf(id) }, null, null,
				"beginningSemester");
		db.close();
		if (c.getCount() == 0)
			return result;
		c.moveToFirst();
		int currentSemester = 1;
		Configuration newConfiguration = new Configuration();
		do {
			if (c.getInt(2) > currentSemester) {
				if (newConfiguration.getMarkTypes().size() != 0) {
					result.addConfig(currentSemester, newConfiguration);
					newConfiguration = new Configuration();
				}
				currentSemester++;
			}
			newConfiguration.addMarkType(getMarkType(c.getInt(0)), c.getInt(1));
		} while (c.moveToNext());
		return result;
	}
	
	public ArrayList<SubjectType> getSubjectTypes(){
		boolean gotAll = false;
		ArrayList<SubjectType> result = new ArrayList<SubjectType>();
		for (int i = 1; !gotAll; i++){
			SubjectType nextType = getSubjectType(i);
			if (nextType == null)
				gotAll = true;
			else
				result.add(nextType);
		}
		return result;
	}
	
	public void deleteSubjectType(int id){
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE, MODE_PRIVATE, null);
		String[] whereArgs = new String[] {String.valueOf(id)};
		db.delete(SUBJECT_TYPE_TABLE, "rowid = ?", whereArgs);
		db.delete(TYPE_RELATION_TABLE, "subjectTypeId = ?", whereArgs);
		db.close();
	}
	
	/**
	 * Richtet alle Tabellen der Datenbank ein.
	 */
	public void createDatabase(){
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE, MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE " + SUBJECT_TABLE+" (name VARCHAR(30), short VARCHAR(10), type INTEGER, mean DECIMAL(4,2) )");
		db.execSQL("CREATE TABLE " + MARK_TABLE + " (date DATE, mark INTEGER, type INTEGER, subjectId INTEGER )");
		db.execSQL("CREATE TABLE " + SEMESTER_TABLE + " (beginning DATE)");
		db.execSQL("CREATE TABLE " + MARK_TYPE_TABLE + " (name VARCHAR(30), highlightColor INTEGER )");
		db.execSQL("CREATE TABLE " + SUBJECT_TYPE_TABLE + "(name VARCHAR(30) )");
		db.execSQL("CREATE TABLE " + TYPE_RELATION_TABLE + " (subjectTypeId INTEGER, markTypeId INTEGER, weight INTEGER, beginningSemester INTEGER )");
		db.close();
	}
}
