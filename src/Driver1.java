import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Driver1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String fileName = "part1/myTrain2";
		NaiveBayesClassifier nb = null;
		NaiveBayesIO nbIO = new NaiveBayesIO();
		
		try {
			nb = nbIO.instantiateNBClassifierWithTrainingData(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(nb);
		double trainingError = nb.calculateTrainingError();
		System.out.println("training error: " + trainingError);
		double oneOutError = nb.calculateOneOutError();
		System.out.println("one out error: " + oneOutError);
		//note: I have a problem where the naive bayes IO knows how to do the printing
		//of the records in human readable form and the NBClassifier has the classified records
		//and I'm not sure how they will interact with the driver to get the records to print
	}

}
