package GRP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TrainingSet {
	private static long total;
	private static int binSize = 32;
	private static long[][] set = new long[binSize][binSize];
	private static File file = new File("trainingSet.csv");

	private static void loading() {
		/*
		 * load the csv file
		 */
		if (!file.exists()) {
			/*
			 * create the file if it does not exist.
			 */
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			total = 0;
		} else {
			/*
			 * load the data from the file
			 */
			try (BufferedReader CSVFile = new BufferedReader(new FileReader(
					file))) {
				total = Integer.parseInt(CSVFile.readLine());
				String dataRow;
				String[] dataArray;
				for (int i = 0; i < binSize; i++) {
					dataRow = CSVFile.readLine();
					dataArray = dataRow.split(",");
					for (int j = 0; j < binSize; j++) {
						set[i][j] = (long) (Double.parseDouble(dataArray[j]) * total);
					}
				}

			} catch (IOException e) {
			}

		}
	}

	private static void save() {
		/*
		 * save the result to the csv file
		 */
		try (BufferedWriter CSVFile = new BufferedWriter(new FileWriter(file))) {
			CSVFile.write(total + "\n");
			for (int i = 0; i < binSize; i++) {
				CSVFile.write((double) set[i][0] / total + "");
				for (int j = 1; j < binSize; j++) {
					CSVFile.write("," + (double) set[i][j] / total);
				}
				CSVFile.write("\n");
			}

		} catch (IOException e) {
		}
	}

	public static void addSet(ImageObject imageObject) {
		/*
		 * adding a training set
		 */
		loading();
		total += imageObject.getHeight() * imageObject.getWidth();
		for (int i = 0; i < imageObject.getWidth(); i++) {
			for (int j = 0; j < imageObject.getHeight(); j++) {
				set[imageObject.rgb2hsb(i, j)[0] / 8][imageObject.rgb2hsb(i, j)[1] / 8]++;
			}
		}
		save();
	}

	public static double[][] getSet() {
		/*
		 * get the training set
		 * all the value in the set is the possibility of the pixel that could be the skin
		 */
		double[][] trainingSet = new double[binSize][binSize];
		try (BufferedReader CSVFile = new BufferedReader(new FileReader(file))) {
			// the first line is the total number which is not useful in this situation
			CSVFile.readLine();
			String dataRow;
			String[] dataArray;
			for (int i = 0; i < binSize; i++) {
				dataRow = CSVFile.readLine();
				dataArray = dataRow.split(",");
				for (int j = 0; j < binSize; j++) {
					trainingSet[i][j] = Double.parseDouble(dataArray[j]);
				}
			}
		} catch (IOException e) {
		}
		return trainingSet;
	}
}
