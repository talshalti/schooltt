package com.aplus;

import java.util.ArrayList;

public class Subject {
	int myId;
	public String myName;
	public String myTeacher;
	public int myColor;
	public ArrayList<String> m_rooms;

	public Subject(int id, String subject, String teacher, int color,
			String[] rooms) {
		myId = id;
		myName = subject;
		myTeacher = teacher;
		myColor = color;
		m_rooms = new ArrayList<String>();
		for (String i : rooms) {
			m_rooms.add(i);
		}
	}

	public Subject(int id, String subject, String teacher, int color,
			ArrayList<String> rooms) {
		myId = id;
		myName = subject;
		myTeacher = teacher;
		myColor = color;
		m_rooms = new ArrayList<String>();
		for (String i : rooms) {
			m_rooms.add(i);
		}
	}

	public void addRoom(String roomnum) {
		m_rooms.add(roomnum);
	}

}
