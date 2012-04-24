package apps.schmitzi.Zensurenverwaltung;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

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
		Cursor c = db.query(SUBJECT_TABLE, null, "rowid = ?",
				new String[] { String.valueOf(id) }, null, null, null);
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
	 * @return Die Liste aller Semester
	 */
	public ArrayList<Date> getSemesters(){
		SQLiteDatabase db = activity.openOrCreateDatabase(DATABASE, MODE_PRIVATE, null);
		Cursor c = db.query(SEMESTER_TABLE, null, null, null, null, null, null);
		db.close();
		ArrayList<Date> result = new ArrayList<Date>();
		if (c.getCount() == 0) return result;
		c.moveToFirst();
		do{
			result.add(Date.valueOf(c.getString(0)));
		} while (c.moveToNext());
		Collections.sort(result);
		return result;
	}
}
