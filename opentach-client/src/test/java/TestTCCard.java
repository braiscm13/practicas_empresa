import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.model.TachoFile;
import com.imatia.tacho.model.tc.TCFile;
import com.imatia.tacho.model.vu.VUFile;
import com.imatia.tacho.tool.ILogger;
import com.ontimize.jee.common.tools.FileTools;

public class TestTCCard {
	private final static Logger	logger	= LoggerFactory.getLogger(TestTCCard.class);

	public TestTCCard() {}

	public static void main(String[] args) {
		com.imatia.tacho.tool.LoggerFactory.setLogger(new ILogger() {
			@Override
			public void log(String level,String s) {
				TestTCCard.logger.info("[" + level + "] " + s);
			}

			@Override
			public void error(Throwable o) {
				TestTCCard.logger.error(null, o);
			}

			@Override
			public void print(Throwable arg0) {
				this.error(arg0);
			}

			@Override
			public void println(String arg0) {
				this.log(ILogger.INFO, arg0);
			}
		});

		File folder = new File("E:\\tmp\\infrac\\ficheros");
		for (File file : folder.listFiles()) {
			try {
				TachoFile tachoFile = TachoFile.readTachoFile(FileTools.getBytesFromFile(file));
				if (tachoFile instanceof TCFile) {
					TestTCCard.logger.warn("TCFile: {}", file);
				} else if (tachoFile instanceof VUFile) {
					TestTCCard.logger.warn("VUFile: {}", file);
				}
				// TCFile card = new TCFile();
				// card.setValue(FileUtils.getBytesFromFile(file), 0);
				// System.out.println(file.getName());
				// System.out.println(card.getEfCACertificate().hasSignature());
				// System.out.println(card.getEfCardCertificate().hasSignature());
			} catch (Throwable e) {
				TestTCCard.logger.error("Error file {}", file, e);
			}
		}
	}
}
