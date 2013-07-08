package com.aplus;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;

public class GlobalData {
	public static ArrayList<Subject> MySubjects = new ArrayList<Subject>();
	public static ArrayList<TimeLesson> MyTimeLessons = new ArrayList<TimeLesson>();
	// TODO Set sharedPrefrences to this ID
	public static int openId = 1;

	public static void initialize(Context c) {
		DatabaseEntry de = new DatabaseEntry(c);
		de.open();
		Bundle[] b = de.getAllData(DatabaseEntry.TABLE_SUB);
		for (Bundle bundle : b) {
			if (openId <= bundle.getInt(DatabaseEntry.KEY_ROWID))
				openId = bundle.getInt(DatabaseEntry.KEY_ROWID) + 1;
			MySubjects.add(new Subject(bundle.getInt(DatabaseEntry.KEY_ROWID), bundle.getString(DatabaseEntry.KEYSUB_NAME), bundle.getString(DatabaseEntry.KEYSUB_TEACHER), bundle
					.getInt(DatabaseEntry.KEYSUB_COLOUR), DatabaseEntry.decodeRoomArray(bundle.getString(DatabaseEntry.KEYSUB_ROOMS))));
		}
		b = de.getAllData(DatabaseEntry.TABLE_TL);
		for (Bundle bundle : b) {
			Subject s = getSubjectById(bundle.getInt(DatabaseEntry.KEYTL_SUBID));
			if (s != null)
				MyTimeLessons.add(new TimeLesson(s, bundle.getInt(DatabaseEntry.KEYTL_DAY), bundle.getInt(DatabaseEntry.KEYTL_HOUR), bundle.getInt(DatabaseEntry.KEYTL_SPAN), bundle
						.getString(DatabaseEntry.KEYTL_ROOM)));
		}
		de.close();
	}

	public static Subject getSubjectById(int id) {
		for (int i = 0; i < MySubjects.size(); i++) {
			if (MySubjects.get(i).myId == id)
				return MySubjects.get(i);
		}
		return null;
	}

	public static void saveTimeLessons(Context c) {
		DatabaseEntry de = new DatabaseEntry(c);
		de.open();
		de.deleteTimeLessons();
		for (TimeLesson tl : MyTimeLessons) {
			de.createHourEntry(tl);
		}
		de.close();
	}

	public static int addNewSubject(String subject, String teacher, int color, ArrayList<String> rooms, Context c) {
		Subject s = new Subject(openId, subject, teacher, color, rooms);
		MySubjects.add(s);
		DatabaseEntry de = new DatabaseEntry(c);
		de.open();
		de.createSubjectEntry(s);
		de.close();
		return openId++;
	}

	public static void updateSubject(int id, String subject, String teacher, int color, ArrayList<String> rooms, Context c) {
		Subject j = getSubjectById(id);
		j.myColor = color;
		j.m_rooms.clear();
		for (String room : rooms)
			j.m_rooms.add(room);
		j.myName = subject;
		j.myTeacher = teacher;
		DatabaseEntry de = new DatabaseEntry(c);
		de.open();
		de.updateSubjectEntryById(j);
		de.close();
	}
}
