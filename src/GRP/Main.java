package GRP;

import java.io.File;

public class Main {

	public static void main(String[] args) {
		


		for (int i = 1; i < 19; i++) {
			TrainingSet.addSet(new ImageObject(new File(i+".jpg")));
		}
		Operation.setSet(TrainingSet.getSet());
		
//		File file = new File("test6.jpg");
//		ImageObject imageObject = new ImageObject();
//		imageObject.loadImage(file);
//		Operation.deal(imageObject);
//		imageObject.drawImage();
		
		
		for (int i = 1; i < 6; i++) {
			File file = new File("test"+i+".jpg");
			ImageObject imageObject = new ImageObject();
			imageObject.loadImage(file);
			Operation.deal(imageObject);
			imageObject.drawImage();
		}
		
		
	}
}
