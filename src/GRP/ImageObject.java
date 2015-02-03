package GRP;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImageObject {

	private BufferedImage image;
	private static int binSize = 32;
	private static long[][] set;
	private static double[][] histogram;

	public ImageObject() {
		image = null;
	}

	public ImageObject(BufferedImage image) {
		this.image = image;
	}

	public ImageObject(File imageFile) {
		loadImage(imageFile);
	}

	public int[] rgb2hsb(int x, int y) {
		/*
		 * the method that get the hsb value of a particular pixel
		 */
		float[] hsb = new float[3];
		int[] result = new int[3];
		Color color = new Color(image.getRGB(x, y));
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
		for (int i = 0; i < result.length; i++) {
			result[i] = (int) (hsb[i] * 255);
		}
		return result;
	}

	public void drawImage() {
		/*
		 * draw the image clearly show the result
		 */
		JFrame frame = new JFrame();
		JPanel jPanel = new JPanel() {
			private Image image2 = image;

			public void paint(Graphics g) {
				g.drawImage(this.image2, 0, 0, this);
			}
		};

		frame.add(jPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(image.getWidth(), image.getHeight());
		frame.setVisible(true);
	}

	public int[][][] hs() {
		/*
		 * return a matrix of hs value of the image
		 */
		int[][][] hsValue = new int[2][this.getWidth()][this.getHeight()];
		for (int i = 0; i < this.getWidth(); i++) {
			for (int j = 0; j < this.getHeight(); j++) {
				hsValue[0][i][j] = rgb2hsb(i, j)[0];
				hsValue[1][i][j] = rgb2hsb(i, j)[1];
			}
		}
		return hsValue;
	}

	public void loadImage(File imageFile) {
		/*
		 * load image from a file
		 */
		try {
			this.image = ImageIO.read(imageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getWidth() {
		return image.getWidth();
	}

	public int getHeight() {
		return image.getHeight();
	}

	public BufferedImage getImage() {
		return image;
	}

	public int getRGB(int i, int j) {
		return image.getRGB(i, j);
	}

	public double[][] getHistogram() {
		set = new long[binSize][binSize];
		histogram = new double[binSize][binSize];
		long total = 0;
		for (int i = 0; i < this.getWidth(); i++) {
			for (int j = 0; j < this.getHeight(); j++) {
				if (image.getRGB(i, j) != -16777216) {
					set[this.rgb2hsb(i, j)[0] / 8][this.rgb2hsb(i, j)[1] / 8]++;
					total++;
				}
			}
		}
		for (int i = 0; i < binSize; i++) {
			for (int j = 0; j < binSize; j++) {
				histogram[i][j] = (double) (set[i][j] / total);
			}
		}
		return histogram;
	}
}
