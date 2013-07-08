package com.aplus;

import org.apache.commons.lang3.StringUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

public class DatabaseEntry {

	public static final String KEYSUB_NAME = "sub_name";
	public static final String KEYSUB_TEACHER = "sub_teacher";
	public static final String KEYSUB_COLOUR = "sub_color";
	public static final String KEYSUB_ROOMS = "sub_rooms";

	public static final String KEY_ROWID = "_id";
	public static final String KEYTL_SUBID = "hour_sub_id";
	public static final String KEYTL_DAY = "hour_day";
	public static final String KEYTL_HOUR = "hour_hour";
	public static final String KEYTL_SPAN = "hour_span";
	public static final String KEYTL_ROOM = "hour_room";

	private static final String DATABASE_NAME = "shaltiDB";
	public static final String TABLE_SUB = "subjects";
	public static final String TABLE_TL = "hours";
	private static final int DATABASE_VERSION = 8;

	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;

	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String cmd = "CREATE TABLE " + TABLE_SUB + " (" + KEY_ROWID + " INTEGER, " + KEYSUB_NAME + " TEXT NOT NULL, " + KEYSUB_TEACHER + " TEXT NOT NULL, " + KEYSUB_COLOUR + " INTEGER, "
					+ KEYSUB_ROOMS + " TEXT);";
			db.execSQL(cmd);

			db.execSQL("CREATE TABLE " + TABLE_TL + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEYTL_SUBID + " INTEGER NOT NULL, " + KEYTL_DAY + " INTEGER NOT NULL, " + KEYTL_HOUR
					+ " INTEGER NOT NULL, " + KEYTL_SPAN + " INTEGER NOT NULL, " + KEYTL_ROOM + " TEXT);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUB);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_TL);
			onCreate(db);
		}
	}

	public DatabaseEntry(Context c) {
		ourContext = c;
	}

	public DatabaseEntry open() throws SQLException {
		ourHelper = new DbHelper(ourContext);

		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		ourHelper.close();
	}

	public void deleteTimeLessons() {
		ourDatabase.execSQL("DELETE FROM " + TABLE_TL);
	}

	public long createSubjectEntry(Subject s) {
		return createSubjectEntry(s.myId, s.myName, s.myTeacher, s.myColor, s.m_rooms.toArray(new String[0]));
	}

	public long createSubjectEntry(int id, String name, String teacher, int color, String[] rooms) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ROWID, id);
		cv.put(KEYSUB_NAME, name);
		cv.put(KEYSUB_TEACHER, teacher);
		cv.put(KEYSUB_COLOUR, color);
		cv.put(KEYSUB_ROOMS, encodeRoomArray(rooms));
		return ourDatabase.insert(TABLE_SUB, null, cv);
	}

	public int updateSubjectEntryById(Subject s) {
		String strFilter = KEY_ROWID + "=" + s.myId;
		ContentValues cv = new ContentValues();
		cv.put(KEYSUB_NAME, s.myName);
		cv.put(KEYSUB_TEACHER, s.myTeacher);
		cv.put(KEYSUB_COLOUR, s.myColor);
		cv.put(KEYSUB_ROOMS, encodeRoomArray(s.m_rooms.toArray(new String[0])));
		return ourDatabase.update(TABLE_SUB, cv, strFilter, null);
	}

	public long createHourEntry(TimeLesson tl) {
		return createHourEntry(tl.myDay, tl.myHour, tl.mySub.myId, tl.mySpan, tl.myRoom);
	}

	public long createHourEntry(int day, int hour, int subId, int span, String room) {
		ContentValues cv = new ContentValues();
		cv.put(KEYTL_DAY, day);
		cv.put(KEYTL_HOUR, hour);
		cv.put(KEYTL_SUBID, subId);
		cv.put(KEYTL_ROOM, room);
		cv.put(KEYTL_SPAN, span);
		return ourDatabase.insert(TABLE_TL, null, cv);
	}

	public String encodeRoomArray(String[] rooms) {
		if (rooms.length == 0)
			return "";
		if (rooms.length == 1)
			return rooms[0];
		return StringUtils.join(rooms, Character.toString((char) 255));
	}

	public static String[] decodeRoomArray(String rooms) {
		if (rooms == null || rooms.length() == 0)
			return new String[0];
		if (rooms.contains(String.valueOf((char) 255)))
			return StringUtils.split(rooms, Character.toString((char) 255));
		return new String[] { rooms };
	}

	public Bundle getHour(int row) {
		Bundle h = new Bundle();
		String[] columns = new String[] { KEY_ROWID, KEYTL_DAY, KEYTL_HOUR, KEYTL_ROOM, KEYTL_SPAN, KEYTL_SUBID };
		Cursor c = ourDatabase.query(TABLE_TL, columns, KEY_ROWID + "=" + row, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
			c.respond(h);
			return h;
		}
		return null;
	}

	public Bundle[] getAllData(String tableName) {

		Cursor mCount = ourDatabase.rawQuery("select count(*) from " + tableName, null);
		mCount.moveToFirst();
		int count = mCount.getInt(0);
		mCount.close();

		// int iName = c.getColumnIndex(KEY_NAME);
		// int iHotness = c.getColumnIndex(KEY_HOTNESS);
		//
		// for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
		// result = result + c.getString(iRow) + " " + c.getString(iName)
		// + " " + c.getString(iHotness) + "\n";
		// }
		if (tableName == TABLE_SUB) {
			Cursor c = ourDatabase.rawQuery("select * from " + tableName, null);

			int iRow = c.getColumnIndex(KEY_ROWID);
			int iCol = c.getColumnIndex(KEYSUB_COLOUR);
			int iRoom = c.getColumnIndex(KEYSUB_ROOMS);
			int iName = c.getColumnIndex(KEYSUB_NAME);
			int iT = c.getColumnIndex(KEYSUB_TEACHER);
			Bundle[] h = new Bundle[count];
			int index = 0;
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				h[index] = new Bundle();
				h[index].putInt(KEY_ROWID, c.getInt(iRow));
				h[index].putInt(KEYSUB_COLOUR, c.getInt(iCol));
				h[index].putString(KEYSUB_ROOMS, c.getString(iRoom));
				h[index].putString(KEYSUB_TEACHER, c.getString(iT));
				h[index++].putString(KEYSUB_NAME, c.getString(iName));
			}
			return h;
		} else {
			Cursor c = ourDatabase.rawQuery("select * from " + tableName, null);

			int iSubid = c.getColumnIndex(KEYTL_SUBID);
			int iRoom = c.getColumnIndex(KEYTL_ROOM);
			int iDay = c.getColumnIndex(KEYTL_DAY);
			int iHour = c.getColumnIndex(KEYTL_HOUR);
			int iSpan = c.getColumnIndex(KEYTL_SPAN);
			Bundle[] h = new Bundle[count];
			int index = 0;
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				h[index] = new Bundle();
				h[index].putInt(KEYTL_SUBID, c.getInt(iSubid));
				h[index].putString(KEYTL_ROOM, c.getString(iRoom));
				h[index].putInt(KEYTL_DAY, c.getInt(iDay));
				h[index].putInt(KEYTL_HOUR, c.getInt(iHour));
				h[index++].putInt(KEYTL_SPAN, c.getInt(iSpan));
			}
			return h;
		}
	}

	public void deleteEntry(long lRow1, String db) throws SQLException {
		ourDatabase.delete(db, KEY_ROWID + "=" + lRow1, null);
	}

}
