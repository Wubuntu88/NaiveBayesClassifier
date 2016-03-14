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

	public NaiveBayesClassifier instantiateNBClassifierWithTrainingData(
			String fileName) throws Exception {
		ArrayList<Record> recordsToReturn = new ArrayList<>();

		String whitespace = "[ ]+";
		List<String> lines = Files.readAllLines(Paths.get(fileName),
				Charset.defaultCharset());
		// first line ( these is the attribute types )
		String[] componentsOfFirstLine = lines.get(0).split(whitespace);
		ArrayList<String> attributeList = new ArrayList<>();
		for (String attrType : componentsOfFirstLine) {
			if (NaiveBayesClassifier.attributeTypes.contains(attrType) == true) {
				attributeList.add(attrType);
				System.out.println(attrType);
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
	}
	
	public void writeRecordsToFile(String fileName, ArrayList<Record> recordsToPrint){
		/*
		PrintWriter pw = null;
		try {
			pw = new PrintWriter("testOutput.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		*/
		StringBuffer sBuffer = new StringBuffer("");
		for(Record record: recordsToPrint){
			String recordDescriptionForHuman = convertRecordToHumanReadableString(record);
			sBuffer.append(recordDescriptionForHuman + "\n");
		}
		sBuffer.replace(sBuffer.length() - 1, sBuffer.length(), "");
		System.out.println(sBuffer);
		//pw.close();
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
	
}




























