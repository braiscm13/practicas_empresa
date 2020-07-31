package com.opentach.common.tacho.data;

import java.util.Date;

public class TransTren extends AbstractData implements Comparable<TransTren> {
	public Date	time;

	public TransTren(Date time) {
		this.time = time;
	}

	@Override
	public int compareTo(TransTren o) {
		return this.time.compareTo(o.time);
	}


}
