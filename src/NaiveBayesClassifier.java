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
	private ArrayList<String> headerList = new ArrayList<>();
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
	private int[] numberOfValuesAtCol;
	/*need to add data structure that keeps stdev and mean for continuous variables*/
	private HashMap<Integer, StatsBundle> statsInfoAtColumn;
	
	public NaiveBayesClassifier(ArrayList<Record> records,
			ArrayList<String> attributesTypeList) {
		this.records = records;
		this.headerList = attributesTypeList;
		this.labelProbabilities = this.calculateLabelProbabilities(this.records);
		this.numberOfValuesAtCol = new int[this.headerList.size()];
		System.out.println(this.labelProbabilities);
		this.numberOfValuesAtCol = this.numberOfValuesAtCol(this.records);
	}
	
	public void buildProbabilityDataStructures(){
		this.probMatrix = this.buildProbabilityMatrix(this.records);
		this.statsInfoAtColumn = this.calculateParametersForContinuousData(this.records);
	}

	public double[][][] buildProbabilityMatrix(ArrayList<Record> theRecords) {
		double[][][] probMtrx = new double[this.headerList.size()][][];

		return null;
	}
	
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

	private int[] numberOfValuesAtCol(ArrayList<Record> records) {
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
		int[] arr = this.numberOfValuesAtCol(this.records);
		ArrayList<Integer> arrayList = new ArrayList<>();
		for (int my_int : arr) {
			arrayList.add(my_int);
		}
		System.out.println(arrayList);
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
