import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Driver1 {

	public static NaiveBayesClassifier instantiateNBClassifierWithTrainingData(
			String fileName) throws Exception {
		ArrayList<Record> recordsToReturn = new ArrayList<>();

		// for continuous variables (key is column, value is range (array of len
		// 2)
		// HashMap<Integer, double[]> rangeAtColumn = new HashMap<>();
		// for ordinal variables
		// HashMap<Integer, HashMap<String, Double>> valsForOrdinalVarAtColumn =
		// new HashMap<>();
		// for binary variables
		// HashMap<String, Integer> binaryNameToIntSymbol = new HashMap<>();
		// for categorical variables
		// HashMap<String, Integer> categoricalNameToIntSymbol = new
		// HashMap<>();

		String whitespace = "[ ]+";
		List<String> lines = Files.readAllLines(Paths.get(fileName),
				Charset.defaultCharset());
		// first line ( these is the attribute types )
		String[] componentsOfFirstLine = lines.get(0).split(whitespace);
		ArrayList<String> attributeList = new ArrayList<>();
		for (String attrType : componentsOfFirstLine) {
			if (NaiveBayesClassifier.attributeTypes
					.contains(attrType) == true) {
				attributeList.add(attrType);
				System.out.println(attrType);
			} else {
				throw new Exception(
						"attribute in file not one of the correct attributes");
			}
		}

		/*this data structure holds they symbol to int mapping at a column
		 * i.e. at column 1, "java" maps to a 1 and "no" maps to a zero */
		ArrayList<HashMap<String, Integer>> symbolToIntAtColumn = new ArrayList<>();
		
		for (int i = 0; i < attributeList.size(); i++) {
			if (attributeList.get(i).equals(NaiveBayesClassifier.CONTINUOUS)) {
				symbolToIntAtColumn.add(null);
			} else {
				symbolToIntAtColumn.add(new HashMap<String,Integer>(10));
			}
		}

		String[] listOfRanges = lines.get(1).split(whitespace);
		int[] valueOfVarAtCol = new int[listOfRanges.length];
		for (int colIndex = 0; colIndex < listOfRanges.length; colIndex++) {
			// range symbols are low to high
			String[] strRange = listOfRanges[colIndex].split(",");
			String typeOfAttrAtIndex = attributeList.get(colIndex);
			if(typeOfAttrAtIndex.equals(NaiveBayesClassifier.CONTINUOUS) == false){
				for (String symbol : strRange) {
					HashMap<String, Integer> hMap = symbolToIntAtColumn
							.get(colIndex);
					hMap.put(symbol, valueOfVarAtCol[colIndex]++);
				}
			}
		}

		// now I have to get all of the records
		for (int i = 2; i < lines.size(); i++) {
			String line = lines.get(i);
			String[] comps = line.split(whitespace);
			double[] attrs = new double[comps.length - 1];
			int label = -1;
			for (int colIndex = 0; colIndex < comps.length; colIndex++) {
				String stringValAtColIndex = comps[colIndex];
				String typeOfAttr = attributeList.get(colIndex);
				HashMap<String, Integer> hMap = symbolToIntAtColumn
						.get(colIndex);
				
				if(typeOfAttr.equals(NaiveBayesClassifier.LABEL)){//if it is label
					label = hMap.get(stringValAtColIndex);
				}else if(typeOfAttr.equals(NaiveBayesClassifier.CONTINUOUS) == false){//not label or continuous
					attrs[colIndex] = hMap.get(stringValAtColIndex);
				}else{//if it is continuous
					double amountAtColIndex = Double
							.parseDouble(stringValAtColIndex);
					attrs[colIndex] = amountAtColIndex;
				}
			}
			Record recordToAdd = new Record(attrs, label);
			recordsToReturn.add(recordToAdd);
		}
		return new NaiveBayesClassifier(recordsToReturn, attributeList);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String fileName = "myTrain2";
		NaiveBayesClassifier nb = null;
		try {
			nb = Driver1.instantiateNBClassifierWithTrainingData(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(nb);
		double trainingError = nb.calculateTrainingError();
		System.out.println("training error: " + trainingError);
	}

}
