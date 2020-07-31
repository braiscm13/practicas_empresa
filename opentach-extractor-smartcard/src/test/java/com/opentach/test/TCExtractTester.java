package com.opentach.test;

import java.io.File;

import com.opentach.model.scard.SmartCardMonitor;

public class TCExtractTester {

	public static void main(String[] args) {
		SmartCardMonitor mon = new SmartCardMonitor();
		mon.setRepositoryDir(new File("e:/tmp/"));
	}
}
