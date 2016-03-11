import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class NaiveBayesClassifier {

	public static final String BINARY = "binary";
	public static final String CATEGORICAL = "categorical";
	public static final String ORDINAL = "ordinal";
	public static final String CONTINUOUS = "continuous";
	public static final String LABEL = "label";
	public static final TreeSet<String> attributeTypes = new TreeSet<String>(
			Arrays.asList(BINARY, CATEGORICAL, ORDINAL, CONTINUOUS, LABEL));

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	// list of the type of the variables in records (ordinal, continuous, etc)
	private ArrayList<String> headerList;
	private ArrayList<Record> records;
	/*
	// for continuous variables (key is column, value is range (array of len 2)
	private HashMap<Integer, double[]> rangeAtColumn = new HashMap<>();
	// for ordinal variables
	private HashMap<Integer, HashMap<String, Double>> valsForOrdinalVarAtColumn = new HashMap<>();
	//for binary variables
	private HashMap<String, Integer> binaryNameToIntSymbol = new HashMap<>();
	//for categorical variables
	private HashMap<String, Integer> categoricalNameToIntSymbol = new HashMap<>();
	*/
	private HashMap<Integer, Double> labelProbabilities = new HashMap<>(10);
	// [column][class][valueAtColumn]
	private double[][][] probMatrix;
	private int[] numberOfDistinctValuesAtCol;
	private int numberOfAttributes;
	private int numberOfLabels;
	/*need to add data structure that keeps stdev and mean for continuous variables*/
	private HashMap<Integer, StatsBundle> statsInfoAtColumn;
	
	public NaiveBayesClassifier(ArrayList<Record> records,
			ArrayList<String> attributesTypeList) {
		this.records = records;
		this.headerList = attributesTypeList;
		this.numberOfAttributes = headerList.size() - 1;
		this.labelProbabilities = this.calculateLabelProbabilities(this.records);
		this.numberOfDistinctValuesAtCol = new int[this.headerList.size()];
		this.numberOfDistinctValuesAtCol = this.numberOfDistinctValuesAtCol(this.records);
		this.numberOfLabels = this.numberOfDistinctValuesAtCol[this.numberOfDistinctValuesAtCol.length - 1];
		// builds probability atrix for binary, categorical, and ordinal data
		// and the hashmap that stores the mean and stdev of a column (for continuous var columns)
		buildProbabilityDataStructures();
	}
	
	public void buildProbabilityDataStructures(){
		this.probMatrix = this.buildProbabilityMatrix(this.records);
		this.statsInfoAtColumn = this.calculateParametersForContinuousData(this.records);
	}

	public double[][][] buildProbabilityMatrix(ArrayList<Record> theRecords) {
		double[][][] probMtrx = new double[this.headerList.size() - 1][][];
		for(int colIndex = 0; colIndex < probMtrx.length; colIndex++){
			probMtrx[colIndex] = probTableAtColumn(colIndex);
		}
		return probMtrx;
	}
	
	public double[][] probTableAtColumn(int colIndex){
		int numberOfLabels = this.numberOfDistinctValuesAtCol[this.numberOfDistinctValuesAtCol.length - 1];
		int numberOfValuesForAttributeAtColIndex = this.numberOfDistinctValuesAtCol[colIndex];
		double[][] probTable = new double[numberOfLabels][numberOfValuesForAttributeAtColIndex];
		//computing the frequencies of attributes associated with a specific label
		for(Record record: this.records){
			int labelOfRecord = record.getLabel();
			int valueAtColIndexOfRecord = (int) record.getAttrList()[colIndex];
			probTable[labelOfRecord][valueAtColIndexOfRecord] += 1;
		}
		//computing the laplace correction
		int numberOfRecords = this.records.size();
		int numberOfAttributes = this.headerList.size() - 1;
		for(int labelCounter = 0; labelCounter < numberOfLabels; labelCounter++){
			double labelProbability = this.labelProbabilities.get(labelCounter);
			for(int attrCounter = 0; attrCounter < numberOfValuesForAttributeAtColIndex; attrCounter++){
				//numerator and denominator used in laplace correction
				double numerator = probTable[labelCounter][attrCounter] + 1;
				double denominator = (int)(labelProbability*numberOfRecords) + numberOfAttributes;
				double result = numerator / denominator;
				probTable[labelCounter][attrCounter] = result;
			}
		}
		return probTable;
	}
	/**
	 * Creates a hashmap where the key refers to a column of the records and the value
	 * is the StatsBundle (the mean and standard deviation of the continuous values in that column)
	 * @param theRecords
	 * @return HashMap of the mean and stdev of the values in a column key
	 */
	public HashMap<Integer, StatsBundle> calculateParametersForContinuousData(ArrayList<Record> theRecords){
		HashMap<Integer, StatsBundle> infoAtColumns = new HashMap<>();
		
		HashMap<Integer, ArrayList<Double>> valuesAtColumns = new HashMap<>();
		for(int i = 0; i <= headerList.size() - 2; i++){//size()-2 since attrList has labels at len - 1
			if(headerList.get(i).equals(NaiveBayesClassifier.CONTINUOUS)){
				valuesAtColumns.put(1, new ArrayList<Double>(theRecords.size()));
			}
		}
		
		// now I  to collect all the values at a given column and 
		// get the mean and standard deviation, put them in the hashmap (statsInfoAtColumn)
		Set<Integer> colKeySet = valuesAtColumns.keySet();
		for(Record record: theRecords){
			for(Integer colIndex: colKeySet){
				ArrayList<Double> valsAtCol = valuesAtColumns.get(colIndex);
				valsAtCol.add(record.getAttrList()[colIndex]);
			}
		}
		//now I have the arrays of the columns; I must calculate the mean and stdev of the cols
		for(Integer colIndex: colKeySet){
			ArrayList<Double> valsAtCol = valuesAtColumns.get(colIndex);
			double mean = this.mean(valsAtCol);
			double stdev = this.stdev(valsAtCol);
			StatsBundle statsBundle = new StatsBundle(mean, stdev);
			infoAtColumns.put(colIndex, statsBundle);
		}
		return infoAtColumns;
	}
	
	private double mean(ArrayList<Double> numbers){
		double sum = 0;
		for(Double value:numbers){
			sum += value;
		}
		return sum / numbers.size();
	}
	
	private double stdev(ArrayList<Double> numbers){
		double sumOfSquaredDeviations = 0;
		double mean = this.mean(numbers);
		for(Double value:numbers){
			double squaredDeviation = value - mean;//deviation
			squaredDeviation *= squaredDeviation;//square the deviation
			sumOfSquaredDeviations += squaredDeviation;
		}
		return sumOfSquaredDeviations / (numbers.size() - 1); // sample stdev
	}

	public HashMap<Integer, Double> calculateLabelProbabilities(
			ArrayList<Record> theRecords) {
		HashMap<Integer, Double> labelProbs = new HashMap<>(10);
		for (Record rec : theRecords) {//first get the frequencies
			int label = rec.getLabel();
			if (labelProbs.containsKey(label)) {
				labelProbs.put(label, labelProbs.get(label) + 1);
			} else {
				labelProbs.put(label, 1.0);
			}
		}
		for(Integer labelKey: labelProbs.keySet()){
			labelProbs.put(labelKey, labelProbs.get(labelKey) / theRecords.size());
		}
		return labelProbs;
	}

	public ArrayList<String> getAttributeList() {
		return this.headerList;
	}

	private int[] numberOfDistinctValuesAtCol(ArrayList<Record> records) {
		// -1 because labels is in the list
		int numberOfCols = this.headerList.size();
		int[] numberOfVarsAtCol = new int[numberOfCols];

		HashMap<Integer, TreeSet<Integer>> map = new HashMap<>();
		for (int i = 0; i < numberOfCols; i++) {
			if (this.headerList.get(i)
					.equals(NaiveBayesClassifier.CONTINUOUS) == false) {
				map.put(i, new TreeSet<Integer>());
			}
		}
		for (Record record : records) {
			double[] attrs = record.getAttrList();
			for (int index = 0; index < numberOfCols; index++) {
				if (map.containsKey(index)) {
					TreeSet<Integer> localMap = map.get(index);
					int valueAtIndex = -1;
					if(index == numberOfCols - 1){
						valueAtIndex = record.getLabel();
					}else{
						valueAtIndex = (int) attrs[index];
					}
					
					if (localMap.contains(valueAtIndex) == false) {
						localMap.add(valueAtIndex);
					}
				}
			}
		}
		for (Integer indexKey : map.keySet()) {
			TreeSet<Integer> valuesAtColumn = map.get(indexKey);
			numberOfVarsAtCol[indexKey] = valuesAtColumn.size();
		}
		return numberOfVarsAtCol;
	}

	public void numberOfVarsAtColumnTest() {
		int[] arr = this.numberOfDistinctValuesAtCol(this.records);
		ArrayList<Integer> arrayList = new ArrayList<>();
		for (int my_int : arr) {
			arrayList.add(my_int);
		}
		System.out.println(arrayList);
	}
	
	public double normal(double input, double mean, double variance){
		double coeff = 1 / (Math.sqrt(variance*2*Math.PI));
		double rest = Math.exp(-(Math.pow(input-mean, 2) / (2*variance)));
		return coeff * rest;
	}
	
	public ArrayList<Integer> classifyRecords(ArrayList<Record> recordsToClassify){
		ArrayList<Integer> labels = new ArrayList<>(recordsToClassify.size());
		for(Record record: recordsToClassify){
			Integer label = classify(record);
			labels.add(label);
		}
		return labels;
	}
	
	public Integer classify(Record theRecord){
		double maxProbability = 0;
		int maxLabel = -1;
		for(int labelCounter = 0; labelCounter < this.numberOfLabels; labelCounter++){
			//probability of a record with attributes having a given label
			double probability = findProbability(theRecord, labelCounter);
			if(probability > maxProbability){
				maxProbability = probability;
				maxLabel = labelCounter;
			}
		}
		return maxLabel;
	}
	
	private double findProbability(Record theRecord, int label){
		double rollingProbability = 1.0;
		for(int attrCounter = 0; attrCounter < this.numberOfAttributes; attrCounter++){
			if(headerList.get(attrCounter).equals(NaiveBayesClassifier.CONTINUOUS) == false){
				int valueOfRecordAtColumn = (int)theRecord.getAttrList()[attrCounter];
				rollingProbability *= probMatrix[attrCounter][label][valueOfRecordAtColumn];
			}else{// if it is continuous
				int valueOfRecordAtColumn = (int)theRecord.getAttrList()[attrCounter];
				StatsBundle statsBundle = statsInfoAtColumn.get(attrCounter);
				double mean = statsBundle.getMean();
				double variance = Math.pow(statsBundle.getStdev(), 2);
				double value = normal(valueOfRecordAtColumn, mean, variance);
				rollingProbability *= value;
			}
		}
		double labelProbability = labelProbabilities.get(theRecord.getLabel());
		rollingProbability *= labelProbability;
		return rollingProbability;
	}
	
	public double calculateTrainingError(){
		int numberOfMisclassifiedRecords = 0;
		ArrayList<Integer> labelsOfClassifiedRecords = classifyRecords(this.records);
		assert labelsOfClassifiedRecords.size() == this.records.size();
		for(int i = 0; i < labelsOfClassifiedRecords.size(); i++){
			int labelOfClassifiedRecord = labelsOfClassifiedRecords.get(i);
			int labelOfRecord = this.records.get(i).getLabel();
			if(labelOfClassifiedRecord != labelOfRecord){
				numberOfMisclassifiedRecords++;
			}
		}
		return (double)numberOfMisclassifiedRecords / labelsOfClassifiedRecords.size();
	}

	@Override
	public String toString() {
		StringBuffer sBuffer = new StringBuffer("");
		for (Record record : this.records) {
			sBuffer.append(record.toString() + "\n");
		}
		sBuffer.deleteCharAt(sBuffer.length() - 1);
		return sBuffer.toString();
	}
}
