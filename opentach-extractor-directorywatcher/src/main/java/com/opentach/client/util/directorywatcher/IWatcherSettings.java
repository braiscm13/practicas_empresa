package com.opentach.client.util.directorywatcher;

public interface IWatcherSettings {
	public enum WatchFolderMode {
		LOCAL, FTP;

		@Override
		public String toString() {
			switch (this) {
				case FTP:
					return "ftp";
				default:
					return "local";
			}

		}

		public static WatchFolderMode fromString(String val, WatchFolderMode defaultValue) {
			switch (val) {
				case "local":

					return LOCAL;
				case "ftp":
					return FTP;

				default:
					return defaultValue;
			}
		}

	}

	String getCompanyId();

	WatchFolderMode getMode();
}
