import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Driver1 {

	public static NaiveBayesClassifier instantiateNBClassifierWithTrainingData(String fileName) throws Exception{
		ArrayList<Record> recordsToReturn = new ArrayList<>();
		
		// for continuous variables (key is column, value is range (array of len 2)
		HashMap<Integer, double[]> rangeAtColumn = new HashMap<>();
		// for ordinal variables
		HashMap<Integer, HashMap<String, Double>> valsForOrdinalVarAtColumn = new HashMap<>();
		//for binary variables
		HashMap<String, Integer> binaryNameToIntSymbol = new HashMap<>();
		//for categorical variables
		HashMap<String, Integer> categoricalNameToIntSymbol = new HashMap<>();
		
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
			} else {
				throw new Exception(
						"attribute in file not one of the correct attributes");
			}
		}
		
		//for binary variables
		int binaryVariableCounter = 0;
		//for categorical variables
		int categoricalVariableCounter = 0;
		
		String[] listOfRanges = lines.get(1).split(whitespace);
		for (int colIndex = 0; colIndex < listOfRanges.length - 1; colIndex++) {
			// range symbols are low to high
			String[] strRange = listOfRanges[colIndex].split(",");
			double[] range = new double[strRange.length];
			String typeOfAttrAtIndex = attributeList.get(colIndex);
			switch (typeOfAttrAtIndex) {
			case NaiveBayesClassifier.ORDINAL:
				// range symbols are low to high
				int index = 0;
				for (String symbol : strRange) {
					range[index] = (double)index / (strRange.length - 1);
					if (valsForOrdinalVarAtColumn.containsKey(colIndex)) {
						HashMap<String, Double> map = valsForOrdinalVarAtColumn
								.get(colIndex);
						map.put(symbol, range[index]);
					} else {// create the hash map
						HashMap<String, Double> map = new HashMap<>();
						map.put(symbol, range[index]);
						valsForOrdinalVarAtColumn.put(colIndex, map);
					}
					index++;
				}
				rangeAtColumn.put(colIndex, range);
				break;
			case NaiveBayesClassifier.CONTINUOUS:
				range[0] = Double.parseDouble(strRange[0]);
				range[1] = Double.parseDouble(strRange[1]);
				rangeAtColumn.put(colIndex, range);
				break;
			case NaiveBayesClassifier.BINARY:
				for(String binaryName: strRange){
					binaryNameToIntSymbol.put(binaryName, binaryVariableCounter);
					binaryVariableCounter++;
				}
				break;// do later because first file doesn't have binary
			case NaiveBayesClassifier.CATEGORICAL:
				for(String categoricalName: strRange){
					categoricalNameToIntSymbol.put(categoricalName, categoricalVariableCounter);
					categoricalVariableCounter++;
				}
				break;
			}
		}
		
		// now I have to get all of the records
		for (int i = 2; i < lines.size(); i++) {
			String line = lines.get(i);
			String[] comps = line.split(whitespace);
			double[] attrs = new double[comps.length - 1];
			String label = comps[comps.length - 1];
			for(int colIndex = 0; colIndex < comps.length - 1; colIndex++){
				String stringValAtColIndex = comps[colIndex];
				String typeOfAttr = attributeList.get(colIndex);
				switch (typeOfAttr) {
				case NaiveBayesClassifier.ORDINAL:
					HashMap<String, Double> levelOfOrdinalToDoubleAmount = valsForOrdinalVarAtColumn.get(colIndex);
					double dub = levelOfOrdinalToDoubleAmount.get(stringValAtColIndex);
					attrs[colIndex] = dub;
					break;
				case NaiveBayesClassifier.CONTINUOUS://will have to normalize after
					double amountAtColIndex = Double.parseDouble(stringValAtColIndex);
					attrs[colIndex] = amountAtColIndex;
					break;
				case NaiveBayesClassifier.BINARY:
					attrs[colIndex] = (double)binaryNameToIntSymbol.get(stringValAtColIndex);
					break;
				case NaiveBayesClassifier.CATEGORICAL:
					attrs[colIndex] = (double)categoricalNameToIntSymbol.get(stringValAtColIndex);
					break;
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

	}

}
























