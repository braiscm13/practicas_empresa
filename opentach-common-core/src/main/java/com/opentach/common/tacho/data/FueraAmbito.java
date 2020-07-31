package com.opentach.common.tacho.data;

import java.util.Date;

public class FueraAmbito extends AbstractData implements Comparable<FueraAmbito> {
	public Date begin;
	public Date end;

	@Override
	public int compareTo(FueraAmbito o) {
		return this.begin.compareTo(o.begin);
	}
}
