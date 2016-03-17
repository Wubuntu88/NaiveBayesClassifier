import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NaiveBayesIO {
	/*
	 * this data structure holds they symbol to int mapping at a column i.e. at
	 * column 1, "java" maps to a 1 and "no" maps to a zero
	 */
	private ArrayList<HashMap<String, Integer>> symbolToIntAtColumn = new ArrayList<>();
	private ArrayList<String> attributeList = new ArrayList<>();
	public NaiveBayesClassifier instantiateNBClassifierWithTrainingData(
			String fileName) throws Exception {
		ArrayList<Record> recordsToReturn = new ArrayList<>();

		String whitespace = "[ ]+";
		List<String> lines = Files.readAllLines(Paths.get(fileName),
				Charset.defaultCharset());
		// first line ( these is the attribute types )
		String[] componentsOfFirstLine = lines.get(0).split(whitespace);
		for (String attrType : componentsOfFirstLine) {
			if (NaiveBayesClassifier.attributeTypes.contains(attrType) == true) {
				attributeList.add(attrType);
			} else {
				throw new Exception(
						"attribute in file not one of the correct attributes");
			}
		}

		for (int i = 0; i < attributeList.size(); i++) {
			if (attributeList.get(i).equals(NaiveBayesClassifier.CONTINUOUS)) {
				symbolToIntAtColumn.add(null);
			} else {
				symbolToIntAtColumn.add(new HashMap<String, Integer>(10));
			}
		}

		String[] listOfRanges = lines.get(1).split(whitespace);
		int[] valueOfVarAtCol = new int[listOfRanges.length];
		for (int colIndex = 0; colIndex < listOfRanges.length; colIndex++) {
			// range symbols are low to high
			String[] strRange = listOfRanges[colIndex].split(",");
			String typeOfAttrAtIndex = attributeList.get(colIndex);
			if (typeOfAttrAtIndex.equals(NaiveBayesClassifier.CONTINUOUS) == false) {
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

				if (typeOfAttr.equals(NaiveBayesClassifier.LABEL)) {// if it is
																	// label
					label = hMap.get(stringValAtColIndex);
				} else if (typeOfAttr.equals(NaiveBayesClassifier.CONTINUOUS) == false) {
					attrs[colIndex] = hMap.get(stringValAtColIndex);
				} else {// if it is continuous
					double amountAtColIndex = Double
							.parseDouble(stringValAtColIndex);
					attrs[colIndex] = amountAtColIndex;
				}
			}
			Record recordToAdd = new Record(attrs, label);
			recordsToReturn.add(recordToAdd);
		}
		return new NaiveBayesClassifier(recordsToReturn, attributeList);
	}//end of instantiate naive bayes classifier
	
	public ArrayList<Record> readRecordsFromFile(String fileName) throws Exception{
		ArrayList<Record> recordsToReturn = new ArrayList<>();
		String whitespace = "[ ]+";
		List<String> lines = Files.readAllLines(Paths.get(fileName), Charset.defaultCharset());;
		for(String line: lines){
			String[] comps = line.split(whitespace);
			double[] attrs = new double[comps.length];
			int label = -1;
			for (int colIndex = 0; colIndex < comps.length; colIndex++) {
				String stringValAtColIndex = comps[colIndex];
				String typeOfAttr = attributeList.get(colIndex);
				HashMap<String, Integer> hMap = symbolToIntAtColumn
						.get(colIndex);
				if (typeOfAttr.equals(NaiveBayesClassifier.LABEL)) {// if it is label
					label = hMap.get(stringValAtColIndex);
				} else if (typeOfAttr.equals(NaiveBayesClassifier.CONTINUOUS) == false) {
					attrs[colIndex] = hMap.get(stringValAtColIndex);
				} else {// if it is continuous
					double amountAtColIndex = Double
							.parseDouble(stringValAtColIndex);
					attrs[colIndex] = amountAtColIndex;
				}
			}
			Record recordToAdd = new Record(attrs, label);
			recordsToReturn.add(recordToAdd);
		}
		return recordsToReturn;
	}
	
	public void writeRecordsToFile(String fileName, 
			ArrayList<Record> recordsToPrint, ArrayList<ClassificationInfo<Integer>> classificationInfos){
		assert recordsToPrint.size() == classificationInfos.size();
		/*
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		*/
		StringBuffer sBuffer = new StringBuffer("");
		int index = 0;
		for(Record record: recordsToPrint){
			ClassificationInfo<Integer> ci = classificationInfos.get(index);
			Record rec = new Record(record.getAttrList(), ci.getLabel());
			String recordDescriptionForHuman = convertRecordToHumanReadableString(rec);
			sBuffer.append(recordDescriptionForHuman + "confidence: " + ci.getConfidenceLevel() + "\n");
			index++;
		}
		sBuffer.replace(sBuffer.length() - 1, sBuffer.length(), "");
		System.out.println(sBuffer);
		
	}
	
	private String convertRecordToHumanReadableString(Record record){
		StringBuffer sBuffer = new StringBuffer("");
		int lenOfRecordAttrList = record.getAttrList().length;
		double[] attrList = record.getAttrList();
		for(int index = 0; index < lenOfRecordAttrList; index++){
			HashMap<String, Integer> hMap = symbolToIntAtColumn.get(index);
			for(String key: hMap.keySet()){
				int valForKey = hMap.get(key);
				if(valForKey == attrList[index]){
					sBuffer.append(key + ", ");
					break;
				}
			}
		}
		sBuffer.replace(sBuffer.length() - 2, sBuffer.length(), "");
		
		//now for the label
		HashMap<String, Integer> hMap = symbolToIntAtColumn.get(symbolToIntAtColumn.size() - 1);
		for(String labelKey: hMap.keySet()){
			int valForKey = hMap.get(labelKey);
			if(valForKey == record.getLabel()){
				sBuffer.append(" || " + labelKey);
			}
		}
		return sBuffer.toString();
	}
	
	public String probabilityMatrixStringInHumanReadableForm(double[][][] probabilityMatrix){	
		StringBuffer sBuffer = new StringBuffer("");
		for(int columnIndex = 0; columnIndex < probabilityMatrix.length; columnIndex++){
			if(probabilityMatrix[columnIndex] != null){
				//now I print the table
				double[][] probTable = probabilityMatrix[columnIndex];
				sBuffer.append("column index: " + columnIndex + "\n");
				sBuffer.append("Class X valAtColumn\n");
				
				int len = probTable[0].length;
				for(int i = 0; i < len; i++){
					String strToAppend = findStringLabelForIntValue(i, columnIndex);
					if(i == 0){
						sBuffer.append(String.format("%10s , ", strToAppend));
					}else if(i == len - 1){
						sBuffer.append(String.format("%3s \n", strToAppend));
					}else{
						sBuffer.append(String.format("%3s , ", strToAppend));
					}
				}
				
				for(int theClass = 0; theClass < probTable.length; theClass++){
					String className = findStringLabelForIntValue(theClass, probabilityMatrix.length);
					sBuffer.append(String.format("%-4s | ", className));
					for(int valueAtColum = 0; valueAtColum < probTable[theClass].length; valueAtColum++){
						sBuffer.append(String.format("%.2f, ", probTable[theClass][valueAtColum]));
					}
					sBuffer.replace(sBuffer.length() - 2, sBuffer.length(), "\n");
				}
				sBuffer.append("\n");
			}
		}
		return sBuffer.toString();
	}
	
	private String findStringLabelForIntValue(int intValue, int column){
		HashMap<String, Integer> hMap = symbolToIntAtColumn.get(column);
		for(String key: hMap.keySet()){
			if((int)hMap.get(key) == intValue){
				return key;
			}
		}
		return null; // if it was not found
	}
	
}




























