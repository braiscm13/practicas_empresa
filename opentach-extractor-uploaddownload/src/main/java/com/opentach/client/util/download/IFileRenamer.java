package com.opentach.client.util.download;

import java.io.File;

public interface IFileRenamer {

	String renameFile(File currentLocation, String originalName, String destinationName);

}