package GRP;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Operation {
	private static double[][] trainingSet;
	private static int erosionSize = 5;
	private static int threshSize = 500;

	public static void setSet(double[][] set) {
		trainingSet = set;
	}

	public static void deal(ImageObject imageObject) {
		findSkin(imageObject);
		erosion(imageObject);
		regionSelect(imageObject);

	}

	private static void findSkin(ImageObject imageObject) {
		/*
		 * find the pixel that seems to be the skin
		 */
		for (int i = 0; i < imageObject.getWidth(); i++) {
			for (int j = 0; j < imageObject.getHeight(); j++) {
				if (trainingSet[imageObject.rgb2hsb(i, j)[0] / 8][imageObject
						.rgb2hsb(i, j)[1] / 8] > 1e-6) {
					imageObject.getImage().setRGB(i, j, 0);
				}
			}
		}
	}

	private static void erosion(ImageObject imageObject) {
		/*
		 * the erroison function
		 */
		int[][] mask = new int[imageObject.getWidth()][imageObject.getHeight()];
		BufferedImage image = imageObject.getImage();
		for (int i = erosionSize; i < imageObject.getWidth() - erosionSize; i++) {
			for (int j = erosionSize; j < imageObject.getHeight() - erosionSize; j++) {
				if (checkMask(imageObject, i, j)) {
					mask[i][j] = 1;
				}
			}
		}
		for (int i = erosionSize; i < imageObject.getWidth() - erosionSize; i++) {
			for (int j = erosionSize; j < imageObject.getHeight() - erosionSize; j++) {
				if (mask[i][j] == 1) {
					image.setRGB(i, j, 0);
				}
			}
		}

		/*
		 * cut the edge
		 */
		for (int i = 0; i < imageObject.getWidth(); i++) {
			for (int j = 0; j < erosionSize + 1; j++) {
				image.setRGB(i, j, 0);
			}
			for (int j = imageObject.getHeight() - erosionSize; j < imageObject
					.getHeight(); j++) {
				image.setRGB(i, j, 0);
			}
		}
		for (int j = 0; j < imageObject.getHeight(); j++) {
			for (int i = 0; i < erosionSize + 1; i++) {
				image.setRGB(i, j, 0);
			}
			for (int i = imageObject.getWidth() - erosionSize; i < imageObject
					.getWidth(); i++) {
				image.setRGB(i, j, 0);
			}
		}

	}

	private static boolean checkMask(ImageObject imageObject, int i, int j) {
		/*
		 * applying the mask
		 */
		for (int i2 = i - erosionSize; i2 < i + erosionSize + 1; i2++) {
			for (int j2 = j - erosionSize; j2 < j + erosionSize + 1; j2++) {
				if (imageObject.getRGB(i2, j2) == -16777216) {
					return true;
				}
			}
		}
		return false;
	}

	private static int findDad(ArrayList<Integer> list, int number) {
		/*
		 * find the daddy of the number in the mask
		 * XD
		 */
		if (list.get(number) == number || list.get(number) == 0) {
			return number;
		}else {
			return list.get(number);
		}
	}

	private static void regionSelect(ImageObject imageObject) {
		/*
		 * the regionSelection method there exists some bugs i will fix it asap
		 * XD
		 */
		int[][] mask = new int[imageObject.getWidth()][imageObject.getHeight()];
		int thresholder = imageObject.getHeight() * imageObject.getWidth()
				/ threshSize;
		BufferedImage image = imageObject.getImage();
		int counter = 1;
		ArrayList<Integer> regionSize = new ArrayList<>();
		ArrayList<Integer> sameValues = new ArrayList<>();
		regionSize.add(0);
		sameValues.add(0);

		/*
		 * checking the upper and left pixel is background or not
		 */
		for (int i = 1; i < imageObject.getWidth(); i++) {
			for (int j = 1; j < imageObject.getHeight(); j++) {

				if (imageObject.getRGB(i, j) == -16777216) {
					continue;
				} else {
					if (mask[i - 1][j] == 0 && mask[i][j - 1] == 0) {
						mask[i][j] = counter;
						counter++;
						regionSize.add(1);
						sameValues.add(0);
					} else if (mask[i - 1][j] != 0 && mask[i][j - 1] == 0) {
						mask[i][j] = mask[i - 1][j];

						int temp = findDad(sameValues, mask[i][j]);
						regionSize.set(temp, regionSize.get(temp) + 1);


					} else if (mask[i - 1][j] == 0 && mask[i][j - 1] != 0) {
						mask[i][j] = mask[i][j - 1];

						int temp = findDad(sameValues, mask[i][j]);
						regionSize.set(temp, regionSize.get(temp) + 1);

					} else if (mask[i - 1][j] != 0 && mask[i][j - 1] != 0
							&& mask[i - 1][j] == mask[i][j - 1]) {
						mask[i][j] = mask[i][j - 1];
						int temp = findDad(sameValues, mask[i][j]);
						regionSize.set(temp, regionSize.get(temp) + 1);

					} else {
						mask[i][j] = mask[i][j - 1];
						int temp1 = findDad(sameValues, mask[i][j]);
						int temp2 = findDad(sameValues, mask[i - 1][j]);
						regionSize.set(temp2, regionSize.get(temp1)
								+ regionSize.get(temp2) + 1);
						regionSize.set(temp1, 0);
						sameValues.set(mask[i][j], temp2);
						sameValues.set(temp1, temp2);

					}
				}
			}
		}

		/*
		 * debug
		 */
		// for (int i = 120; i < imageObject.getWidth() - 50; i++) {
		// for (int j = 100; j < imageObject.getHeight() - 100; j++) {
		// System.out.printf("%3d", mask[i][j]);
		// }
		// System.out.println();
		// }

		for (int i = 0; i < imageObject.getWidth(); i++) {
			for (int j = 0; j < imageObject.getHeight(); j++) {
				if (mask[i][j] == 0) {
					continue;
				} else if (sameValues.get(mask[i][j]) == 0) {
					if (regionSize.get(mask[i][j]) < thresholder) {
						image.setRGB(i, j, 0);
					}
				} else {
					if (regionSize.get(sameValues.get(mask[i][j])) < thresholder) {
						image.setRGB(i, j, 0);
					}
				}
			}
		}
	}
}
