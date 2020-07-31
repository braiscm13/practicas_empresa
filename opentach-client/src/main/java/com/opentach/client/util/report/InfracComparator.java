package com.opentach.client.util.report;

import java.util.Comparator;

public class InfracComparator implements Comparator, Comparable {

	@Override
	public int compare(Object o1, Object o2) {
		if ((o1 instanceof String) && (o2 instanceof String)) {
			String s1 = (String) o1;
			String s2 = (String) o2;
			if ((s1.compareTo("L") == 0) && (s2.compareTo("G") == 0)) {
				return -1;
			}
			if ((s1.compareTo("G") == 0) && (s2.compareTo("L") == 0)) {
				return 1;
			}
			if ((s1.compareTo("G") == 0) && (s2.compareTo("MG") == 0)) {
				return -1;
			}
			if ((s1.compareTo("MG") == 0) && (s2.compareTo("G") == 0)) {
				return 1;
			}
			if ((s1.compareTo("L") == 0) && (s2.compareTo("MG") == 0)) {
				return -1;
			}
			if ((s1.compareTo("MG") == 0) && (s2.compareTo("L") == 0)) {
				return 1;
			}
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		return this.compare(this, obj) == 0;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

}
