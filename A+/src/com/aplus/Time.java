package com.aplus;

public class Time {

	int t;

	public Time(int t) {
		this.t = t;
	}

	public Time(int hour, int min) {
		this.t = min + hour * 60;
	}

	public int geMinute() {
		return t % 60;
	}

	public int getHour() {
		return t / 60;
	}

	public void setTime(int hour, int min) {
		this.t = min + hour * 60;
	}

	@Override
	public String toString() {
		return t / 60 + ":" + t % 60;
	}

	public int getCodedInt() {
		return t;
	}

}
