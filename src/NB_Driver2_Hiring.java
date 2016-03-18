import java.util.ArrayList;


public class NB_Driver2_Hiring {

	public static void main(String[] args) {
		String trainingFileName = "part1/myTrain2";
		String testFileName = "part1/test2";
		String outputFileName = "part1/output2";
		
		NaiveBayesClassifier nb = null;
		NaiveBayesIO nbIO = new NaiveBayesIO();
		
		try {
			nb = nbIO.instantiateNBClassifierWithTrainingData(trainingFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* a) Classification of records in test file (stored in part1/output2)*/
		ArrayList<Record> recordsToClassify = null;
		try {
			recordsToClassify = nbIO.readRecordsFromFile(testFileName);
		} catch (Exception e) {
			System.out.println("error in loading test data");
			e.printStackTrace();
		}
		ArrayList<ClassificationInfo<Integer>> classificationInfos = nb.classifyRecords(recordsToClassify);
		nbIO.writeRecordsToFile(outputFileName, recordsToClassify, classificationInfos);
		
		/* b) Training error on the data */
		double trainingError = nb.calculateTrainingError();
		System.out.println("training error: " + trainingError);
		
		/* c) validation error one out method */
		double oneOutError = nb.calculateOneOutError();
		System.out.println("one out error: " + oneOutError);
	}
}






























