package com.example.attendance_tracking_app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.attendance_tracking_app.models.AttendanceRecord;
import com.example.attendance_tracking_app.models.ClassModel;
import com.example.attendance_tracking_app.models.SessionModel;
import com.example.attendance_tracking_app.models.StudentModel;
import java.util.ArrayList;
import java.util.List;
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME    = "attendance.db";
    private static final int    DB_VERSION = 1;

    // Table names
    private static final String TABLE_CLASSES    = "classes";
    private static final String TABLE_STUDENTS   = "students";
    private static final String TABLE_SESSIONS   = "sessions";
    private static final String TABLE_ATTENDANCE = "attendance";

    // Shared
    private static final String COL_ID = "id";

    // Classes
    private static final String COL_CLASS_NAME     = "name";
    private static final String COL_TOTAL_STUDENTS = "total_students";
    private static final String COL_TOTAL_SESSIONS = "total_sessions";

    // Students
    private static final String COL_STUDENT_CLASS_ID = "class_id";
    private static final String COL_STUDENT_NAME     = "name";
    private static final String COL_STUDENT_NUMBER   = "student_number";

    // Sessions
    private static final String COL_SESSION_CLASS_ID = "class_id";
    private static final String COL_SESSION_DATE     = "date";
    private static final String COL_SESSION_TOPIC    = "topic";

    // Attendance
    private static final String COL_ATT_SESSION_ID = "session_id";
    private static final String COL_ATT_STUDENT_ID = "student_id";
    private static final String COL_ATT_IS_PRESENT = "is_present";

    // Singleton
    private static DatabaseHelper instance;

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null)
            instance = new DatabaseHelper(context.getApplicationContext());
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CLASSES + " (" +
                COL_ID             + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CLASS_NAME     + " TEXT NOT NULL, " +
                COL_TOTAL_STUDENTS + " INTEGER DEFAULT 0, " +
                COL_TOTAL_SESSIONS + " INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE " + TABLE_STUDENTS + " (" +
                COL_ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_STUDENT_CLASS_ID + " INTEGER NOT NULL, " +
                COL_STUDENT_NAME     + " TEXT NOT NULL, " +
                COL_STUDENT_NUMBER   + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COL_STUDENT_CLASS_ID + ") REFERENCES " + TABLE_CLASSES + "(" + COL_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_SESSIONS + " (" +
                COL_ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SESSION_CLASS_ID + " INTEGER NOT NULL, " +
                COL_SESSION_DATE     + " TEXT NOT NULL, " +
                COL_SESSION_TOPIC    + " TEXT, " +
                "FOREIGN KEY(" + COL_SESSION_CLASS_ID + ") REFERENCES " + TABLE_CLASSES + "(" + COL_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_ATTENDANCE + " (" +
                COL_ID              + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ATT_SESSION_ID  + " INTEGER NOT NULL, " +
                COL_ATT_STUDENT_ID  + " INTEGER NOT NULL, " +
                COL_ATT_IS_PRESENT  + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY(" + COL_ATT_SESSION_ID + ") REFERENCES " + TABLE_SESSIONS + "(" + COL_ID + "), " +
                "FOREIGN KEY(" + COL_ATT_STUDENT_ID + ") REFERENCES " + TABLE_STUDENTS + "(" + COL_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
        onCreate(db);
    }

    // ---------------------------------------------------------------
    // CLASSES
    // ---------------------------------------------------------------
    private boolean classExists(String name) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_CLASSES +
                        " WHERE " + COL_CLASS_NAME + "=?",
                new String[]{name});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public long addClass(ClassModel c) {
        if (classExists(c.getClassName()))
            return -1;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CLASS_NAME,     c.getClassName());
        values.put(COL_TOTAL_STUDENTS, c.getStudentCount());
        values.put(COL_TOTAL_SESSIONS, c.getSessionCount());
        return db.insert(TABLE_CLASSES, null, values);
    }

    public List<ClassModel> getAllClasses() {
        List<ClassModel> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLASSES, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new ClassModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CLASS_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOTAL_STUDENTS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOTAL_SESSIONS))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void deleteClass(int classId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CLASSES, COL_ID + "=?", new String[]{String.valueOf(classId)});
    }

    // ---------------------------------------------------------------
    // STUDENTS
    // ---------------------------------------------------------------
    private boolean studentExists(String studentNumber, int classId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_STUDENTS +
                        " WHERE " + COL_STUDENT_NUMBER + "=? AND " + COL_STUDENT_CLASS_ID + "=?",
                new String[]{studentNumber, String.valueOf(classId)});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public long addStudent(StudentModel s) {
        if (studentExists(s.getStudentNumber(), s.getClassId()))
            return -1;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STUDENT_CLASS_ID, s.getClassId());
        values.put(COL_STUDENT_NAME,     s.getName());
        values.put(COL_STUDENT_NUMBER,   s.getStudentNumber());
        long newId = db.insert(TABLE_STUDENTS, null, values);
        db.execSQL("UPDATE " + TABLE_CLASSES + " SET " + COL_TOTAL_STUDENTS +
                " = " + COL_TOTAL_STUDENTS + " + 1 WHERE " + COL_ID + " = " + s.getClassId());
        return newId;
    }

    public List<StudentModel> getStudentsByClass(int classId) {
        List<StudentModel> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_STUDENTS + " WHERE " + COL_STUDENT_CLASS_ID + "=?",
                new String[]{String.valueOf(classId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(new StudentModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_STUDENT_CLASS_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_STUDENT_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_STUDENT_NUMBER))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void deleteStudent(int studentId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_STUDENTS, COL_ID + "=?", new String[]{String.valueOf(studentId)});

        // Keep total_students count in sync
        db.execSQL("UPDATE " + TABLE_CLASSES + " SET " + COL_TOTAL_STUDENTS +
                " = " + COL_TOTAL_STUDENTS + " - 1 WHERE " + COL_ID +
                " = (SELECT " + COL_STUDENT_CLASS_ID + " FROM " + TABLE_STUDENTS +
                " WHERE " + COL_ID + " = " + studentId + ")");
    }

    // ---------------------------------------------------------------
    // SESSIONS
    // ---------------------------------------------------------------
    public long addSession(SessionModel s) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SESSION_CLASS_ID, s.getClassId());
        values.put(COL_SESSION_DATE,     s.getDate());
        values.put(COL_SESSION_TOPIC,    s.getTopic());

        long newId = db.insert(TABLE_SESSIONS, null, values);

        // Keep total_sessions count in sync
        db.execSQL("UPDATE " + TABLE_CLASSES + " SET " + COL_TOTAL_SESSIONS +
                " = " + COL_TOTAL_SESSIONS + " + 1 WHERE " + COL_ID + " = " + s.getClassId());

        return newId;
    }

    public List<SessionModel> getSessionsByClass(int classId) {
        List<SessionModel> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_SESSIONS + " WHERE " + COL_SESSION_CLASS_ID +
                        "=? ORDER BY " + COL_SESSION_DATE + " DESC",
                new String[]{String.valueOf(classId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(new SessionModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SESSION_CLASS_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SESSION_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SESSION_TOPIC))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }


    // in DatabaseHelper.java
    public SessionModel getSessionById(int sessionId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_SESSIONS + " WHERE " + COL_ID + "=?",
                new String[]{String.valueOf(sessionId)});
        SessionModel session = null;
        if (cursor.moveToFirst()) {
            session = new SessionModel(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_SESSION_CLASS_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_SESSION_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_SESSION_TOPIC))
            );
        }
        cursor.close();
        return session;
    }

    public void deleteSession(int sessionId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_SESSIONS, COL_ID + "=?", new String[]{String.valueOf(sessionId)});
    }

    // ---------------------------------------------------------------
    // ATTENDANCE
    // ---------------------------------------------------------------
    public void markAttendance(AttendanceRecord record) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ATT_SESSION_ID, record.getSessionId());
        values.put(COL_ATT_STUDENT_ID, record.getStudentId());
        values.put(COL_ATT_IS_PRESENT, record.isPresent() ? 1 : 0);

        // Update if already exists, insert if not
        int rows = db.update(TABLE_ATTENDANCE, values,
                COL_ATT_SESSION_ID + "=? AND " + COL_ATT_STUDENT_ID + "=?",
                new String[]{String.valueOf(record.getSessionId()), String.valueOf(record.getStudentId())});
        if (rows == 0)
            db.insert(TABLE_ATTENDANCE, null, values);
    }

    public AttendanceRecord getAttendanceRecord(int sessionId, int studentId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_ATTENDANCE +
                        " WHERE " + COL_ATT_SESSION_ID + "=? AND " + COL_ATT_STUDENT_ID + "=?",
                new String[]{String.valueOf(sessionId), String.valueOf(studentId)});
        AttendanceRecord record = null;
        if (cursor.moveToFirst()) {
            record = new AttendanceRecord(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ATT_SESSION_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ATT_STUDENT_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ATT_IS_PRESENT)) == 1
            );
        }
        cursor.close();
        return record;
    }

    public List<AttendanceRecord> getAttendanceBySession(int sessionId) {
        List<AttendanceRecord> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_ATTENDANCE + " WHERE " + COL_ATT_SESSION_ID + "=?",
                new String[]{String.valueOf(sessionId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(new AttendanceRecord(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ATT_SESSION_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ATT_STUDENT_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ATT_IS_PRESENT)) == 1
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // ---------------------------------------------------------------
    // STATS — for reports screen
    // ---------------------------------------------------------------
    public int getStudentPresentCount(int studentId, int classId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_ATTENDANCE + " a " +
                        "JOIN " + TABLE_SESSIONS + " s ON a." + COL_ATT_SESSION_ID + " = s." + COL_ID +
                        " WHERE a." + COL_ATT_STUDENT_ID + "=? AND s." + COL_SESSION_CLASS_ID + "=? AND a." + COL_ATT_IS_PRESENT + "=1",
                new String[]{String.valueOf(studentId), String.valueOf(classId)});
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int getPresentCountForSession(int sessionId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_ATTENDANCE +
                        " WHERE " + COL_ATT_SESSION_ID + "=? AND " + COL_ATT_IS_PRESENT + "=1",
                new String[]{String.valueOf(sessionId)});
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    // get total present marks across all sessions in a class
    public int getTotalPresentForClass(int classId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_ATTENDANCE + " a " +
                        "JOIN " + TABLE_SESSIONS + " s ON a." + COL_ATT_SESSION_ID + " = s." + COL_ID +
                        " WHERE s." + COL_SESSION_CLASS_ID + "=? AND a." + COL_ATT_IS_PRESENT + "=1",
                new String[]{String.valueOf(classId)});
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    // get most absent students across all classes
// returns students sorted by absence count descending
    public List<String> getMostAbsentStudents(int limit) {
        List<String> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT s." + COL_STUDENT_NAME + ", " +
                        "COUNT(*) as absence_count " +
                        "FROM " + TABLE_ATTENDANCE + " a " +
                        "JOIN " + TABLE_STUDENTS + " s ON a." + COL_ATT_STUDENT_ID + " = s." + COL_ID +
                        " WHERE a." + COL_ATT_IS_PRESENT + "=0 " +
                        "GROUP BY a." + COL_ATT_STUDENT_ID + " " +
                        "ORDER BY absence_count DESC " +
                        "LIMIT ?",
                new String[]{String.valueOf(limit)});
        if (cursor.moveToFirst()) {
            do {
                String name         = cursor.getString(0);
                int    absenceCount = cursor.getInt(1);
                result.add(name + " — " + absenceCount + " absences");
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

}
