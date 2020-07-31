package com.opentach.common.filereception;

public enum UploadSourceType {
	OPENTACH_MOVIL("MOV"), //
	ESCRITORIO("DKT"), //
	CENTRO_DESCARGAS("DCN"), //
	REMOTA_JALTEST("RJL"), //
	REMOTA_OPENTACH("ROP"), //
	FTP_SERVER("FSR"), //
	TACHOCABLE("TCC"), //
	REMOTA_TELEPASS("RTP"), //
	REMOTA_VOLVO("RVL");

	private String id;

	UploadSourceType(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return this.id;
	}

	public static UploadSourceType fromString(String str) {
		if (str == null) {
			return null;
		}
		switch (str) {
			case "MOV":
				return UploadSourceType.OPENTACH_MOVIL;
			case "DKT":
				return ESCRITORIO;
			case "DCN":
				return CENTRO_DESCARGAS;
			case "RJL":
				return UploadSourceType.REMOTA_JALTEST;
			case "ROP":
				return UploadSourceType.REMOTA_OPENTACH;
			case "FSR":
				return UploadSourceType.FTP_SERVER;
			case "TCC":
				return UploadSourceType.TACHOCABLE;
			default:
				break;
		}
		return null;
	}
}
