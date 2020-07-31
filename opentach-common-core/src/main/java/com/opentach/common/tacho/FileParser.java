package com.opentach.common.tacho;

import com.imatia.tacho.model.TachoFile;
import com.imatia.tacho.model.tc.TCFile;
import com.imatia.tacho.model.vu.VUFile;

public abstract class FileParser implements IFileParser {

	public static IFileParser createParserFor(TachoFile tachoFile) {
		if (tachoFile instanceof TCFile) {
			return new TcFileParser((TCFile) tachoFile);
		} else if (tachoFile instanceof VUFile) {
			return new VuFileParser((VUFile) tachoFile);
		} else {
			throw new RuntimeException("Invalid tachofile type for parser");
		}
	}

}