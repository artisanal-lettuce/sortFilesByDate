package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class main {

	static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

	private static String folderPath = "";
	private static String outputPath = "";
	private static String mode = "";
	private static boolean recursive = false;

	public static void main(String[] args) throws InterruptedException, FileNotFoundException, IOException {
		long time = System.currentTimeMillis()/1000;

		// handle arguments
		for(int i = 0; i < args.length; i ++) {
			if(args[i].equalsIgnoreCase("-h") || args[i].equalsIgnoreCase("--help")) {
				printHelpMessage();
				System.exit(0);;
			}
			else if((args[i].equalsIgnoreCase("-i") || args[i].equalsIgnoreCase("--input")) && i+1 < args.length) {
				folderPath = args[i+1];
			}
			else if((args[i].equalsIgnoreCase("-o") || args[i].equalsIgnoreCase("--output")) && i+1 < args.length) {
				outputPath = args[i+1];
			}
			else if((args[i].equalsIgnoreCase("-r") || args[i].equalsIgnoreCase("--recursive"))) {
				recursive = true;
			}
			else if((args[i].equalsIgnoreCase("-y") || args[i].equalsIgnoreCase("--year"))) {
				if(mode.isEmpty())
					mode = "y";
				else {
					System.out.println("ERROR: You must specify a single mode");
					System.out.println("Use -h or --help for instructions");
					System.exit(1);						
				}
			}
			else if((args[i].equalsIgnoreCase("-ym") || args[i].equalsIgnoreCase("--yearmonth"))) {
				if(mode.isEmpty())
					mode = "ym";
				else {
					System.out.println("ERROR: You must specify a single mode");
					System.out.println("Use -h or --help for instructions");
					System.exit(1);						
				}
			}
			else if((args[i].equalsIgnoreCase("-ymd") || args[i].equalsIgnoreCase("--yearmonthday"))) {
				if(mode.isEmpty())
					mode = "ymd";
				else {
					System.out.println("ERROR: You must specify a single mode");
					System.out.println("Use -h or --help for instructions");
					System.exit(1);						
				}
			}
		}

		if(mode.isEmpty()) {
			System.out.println("No mode specified, using default \'year-month\'");
			System.out.println("Use -h or --help for instructions");
			mode = "ym"; 
		}
		if(folderPath.isEmpty()) {
			System.out.println("ERROR: You must specify a folder to organize with option -f or --folder");
			System.out.println("Use -h or --help for instructions");
			System.exit(1);
		}
		
		if(outputPath.contains(folderPath)) {
			System.out.println("ERROR: output folder cannot be inside the input folder");
			System.out.println("output folder: " + outputPath);
			System.out.println("input folder: " + folderPath);
			System.out.println("Use -h or --help for instructions");
			System.exit(1);
		}

		printStartMessage();

		// create output folder
		new File(outputPath).mkdirs();

		// open folder
		File folder = new File(folderPath);
		if(folder.isDirectory()) { // check it is a folder

			// check folder size and available free space
			long folderSize = FileUtils.sizeOfDirectory(folder);
			File diskPartition = new File("/");
			long freeSpace = diskPartition.getFreeSpace();
			if(freeSpace < folderSize) {
				System.out.println("ERROR: not enough free space to create a copy of folder");
				System.out.println("folder size is " + humanReadableByteCountBin(folderSize) + " and free space is " + humanReadableByteCountBin(freeSpace));
				System.exit(1);
			}

			sortFiles(folder);

		}
		else {
			System.out.println("ERROR: You must specify a folder. " + folderPath + " is not a folder");
			System.exit(1);
		}

		System.out.println("Done!");
		time = System.currentTimeMillis()/1000 - time;
		System.out.println(dtf.format(LocalDateTime.now()) + " | Finished in " + time + " seconds");
	}

	private static void sortFiles(File folder) throws FileNotFoundException, IOException {
		// iterate through files in folder
		for(File file : folder.listFiles()) {
			if(file.isDirectory() && recursive) // sort files inside folder if recursive option is active
					sortFiles(file);
			else { // otherwise treat it as a file
				
				String created = Files.getAttribute(Paths.get(file.getAbsolutePath()), "creationTime").toString();

				// if created date exists
				if(created != null) {
					// save parts of date
					String date = StringUtils.substringBefore(created, "T");
					String year = date.split("-")[0];
					String month = date.split("-")[1];
					String day = date.split("-")[2];

					// create prefix from mode
					String prefix = "";
					if(mode.equals("y"))
						prefix = year;
					else if(mode.equals("ym"))
						prefix = year + "-" +  month;
					else if(mode.equals("ymd"))
						prefix = year + "-" + month + "-" + day;

					File newdir;
					if(outputPath.endsWith("/"))
						newdir = new File(outputPath + prefix);
					else
						newdir = new File(outputPath + "/" + prefix);
					
					newdir.mkdirs();
					if(file.isDirectory())
						FileUtils.copyDirectoryToDirectory(file, newdir);
					else
						FileUtils.copyFileToDirectory(file, newdir);
					System.out.println(" \u2713  " + file.getAbsolutePath());
				}
				else {
					// if no creation date is found
					File newdir;
					String prefix = "nodate";
					if(outputPath.endsWith("/"))
						newdir = new File(outputPath + prefix);
					else
						newdir = new File(outputPath + "/" + prefix);
					
					newdir.mkdirs();
					FileUtils.copyFileToDirectory(file, newdir);
					
					System.out.print(" X  " + file.getAbsolutePath());
					System.out.print(" (failed - couldn't access the file's date, copying to " + newdir.getAbsolutePath() + ")\n");				
				}
			}
		}
	}

	private static void printStartMessage() throws InterruptedException {
		System.out.println("Hi!");
		TimeUnit.MILLISECONDS.sleep(1000);
		System.out.println("I will be sorting the folder " + folderPath + " according to the mode " + mode + "");
		TimeUnit.MILLISECONDS.sleep(1000);
		System.out.println("A new folder named " + outputPath + " will be created and a copy of the files will be sorted within it");
		TimeUnit.MILLISECONDS.sleep(1000);
		if(recursive) {
			System.out.println("Recursive mode selected, will parse sub-folders");
			TimeUnit.MILLISECONDS.sleep(1000);
		}
		System.out.println(".");
		TimeUnit.MILLISECONDS.sleep(1000);
		System.out.println(".");
		TimeUnit.MILLISECONDS.sleep(1000);
		System.out.println(".");
		TimeUnit.MILLISECONDS.sleep(1000);
	}

	private static void printHelpMessage() {
		System.out.println(" ______________________________________________________________");
		System.out.println("/                                                              \\");
		System.out.println("|                                                              |");
		System.out.println("|                            USAGE:                            |");
		System.out.println("| java -jar sortFiles.jar OPTIONS                              |");
		System.out.println("|                                                              |");
		System.out.println("| The options are:                                             |");
		System.out.println("|  -i (--input) 'path to folder to be sorted'                  |");
		System.out.println("|  -o (--output) 'path to folder where files will be sorted'   |");
		System.out.println("|  -r (--recursive) 'open any sub-folders'                     |");
		System.out.println("|                                                              |");
		System.out.println("|                            MODES:                            |");
		System.out.println("|  -y (--year) -> sort by year                                 |");
		System.out.println("|   OR                                                         |");
		System.out.println("|  -ym (--yearmonth) -> sort by year and month [DEFAULT]       |");
		System.out.println("|   OR                                                         |");
		System.out.println("|  -ymd (--yearmonthday) -> sort by year, month and day        |");
		System.out.println("|                                                              |");
		System.out.println("\\______________________________________________________________/");

	}

	// from https://stackoverflow.com/questions/3758606/how-can-i-convert-byte-size-into-a-human-readable-format-in-java
	private static String humanReadableByteCountBin(long bytes) {
		long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
		if (absB < 1024) {
			return bytes + " B";
		}
		long value = absB;
		CharacterIterator ci = new StringCharacterIterator("KMGTPE");
		for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
			value >>= 10;
			ci.next();
		}
		value *= Long.signum(bytes);
		return String.format("%.1f %ciB", value / 1024.0, ci.current());
	}

}
