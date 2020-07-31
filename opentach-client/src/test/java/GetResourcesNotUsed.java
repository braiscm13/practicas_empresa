import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GetResourcesNotUsed {

	public static void main(String[] args) {
		File folder = new File("C:\\PROJECTS_MARS\\opentach_trunk\\opentach-client\\src\\main\\resources\\com\\opentach\\client\\rsc");
		File[] listImagesFiles = folder.listFiles();
		try {
			GetResourcesNotUsed.checkFolder(listImagesFiles);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void checkFolder(File[] listOfFiles) throws Exception {
		System.out.println("All Images");
		for (File file : listOfFiles) {
			System.out.println(file.getName());
		}
		for (File file : listOfFiles) {
			if (file.isFile()) {
				// System.out.println("Search " + file.getName());
				Boolean delete = GetResourcesNotUsed.checkWordInAllFiles(file.getName(), "C:\\PROJECTS_MARS\\opentach_trunk\\opentach-client", true);
				if (delete) {
					System.out.println("Delete: " + file.getName());
					file.delete();
				}
			}
			// else if (file.isDirectory()) {
			// File[] files = { file };
			// GetResourcesNotUsed.checkFolder(files);
			// }
		}
	}

	private static Boolean checkWordInAllFiles(String name, String folderName, Boolean backValue) throws Exception {
		File folder = new File(folderName);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				String value = GetResourcesNotUsed.checkFile(file, name);
				if (value != null) {
					return false;
				}
			} else {
				Boolean value = GetResourcesNotUsed.checkWordInAllFiles(name, file.getAbsolutePath(), backValue);
				if (!value) {
					return false;
				}
			}
		}
		return backValue;

	}

	public static String checkFile(File file, String wordToFind) throws Exception {

		try {
			Scanner scanner = new Scanner(file);

			// now read the file line by line...
			int lineNum = 0;
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				lineNum++;
				if (line.contains(wordToFind)) {
					// System.out.println("Imagen: " + wordToFind + " Fichero: " + file.getName() + " Línea: " + lineNum);
					return String.valueOf(lineNum);
				}
			}
			return null;
		} catch (FileNotFoundException e) {
			System.err.println("Error " + e.toString());
			throw new RuntimeException(e);
		}
	}
}
