import java.util.ArrayList;


public class NB_Driver1 {

	public static void main(String[] args) {
		
		System.out.println("Naive Bayes part 1: William Gillespie");
		String trainingFileName = "part1/myTrain1";
		String testFileName = "part1/test1";
		String outputFileName = "part1/output1";
		
		NaiveBayesClassifier nb = null;
		NaiveBayesIO nbIO = new NaiveBayesIO();
		
		try {
			nb = nbIO.instantiateNBClassifierWithTrainingData(trainingFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/* a) Classification of records in test file (stored in part1/output1)*/
		ArrayList<Record> recordsToClassify = null;
		try {
			recordsToClassify = nbIO.readRecordsFromFile(testFileName);
		} catch (Exception e) {
			System.out.println("error in loading test data");
			e.printStackTrace();
		}
		ArrayList<ClassificationInfo<Integer>> classificationInfos = nb.classifyRecords(recordsToClassify);
		nbIO.writeRecordsToFile(outputFileName, recordsToClassify, classificationInfos);
		
		/* b) training error on the data */
		double trainingError = nb.calculateTrainingError();
		System.out.println("training error: " + trainingError);
		/* c) one out error on the data */
		double oneOutError = nb.calculateOneOutError();
		System.out.println("one out error: " + oneOutError);
		
		/* d) probability tables */
		System.out.println(nbIO.probabilityMatrixStringInHumanReadableForm(nb.probMatrixCopy()));
	}
}
















