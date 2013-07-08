package com.aplus;

public class HourData {

	private int mHour;
	private Time mStart;
	private Time mEnd;

	public HourData(int Hour, Time Start, Time End) {
		this.mHour = Hour;
		this.mStart = Start;
		this.mEnd = End;
	}

	public int getHour() {
		return mHour;
	}

	public void setHour(int Hour) {
		this.mHour = Hour;
	}

	public Time getStart() {
		return mStart;
	}

	public void setStart(Time Start) {
		this.mStart = Start;
	}

	public Time getEnd() {
		return mEnd;
	}

	public void setEnd(Time End) {
		this.mEnd = End;
	}
}