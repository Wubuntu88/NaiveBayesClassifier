import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
	private ArrayList<String> attributeList = new ArrayList<>();
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
	private HashMap<String, Integer> labelFrequencies = new HashMap<>(10);
	// [column][class][valueAtColumn]
	private double[][][] probMatrix;
	private int[] numberOfValuesAtCol;
	/*need to add data structure that keeps stdev and mean for continuous variables*/
	private HashMap<Integer, StatsBundle> statsInfoAtColumn;
	
	public NaiveBayesClassifier(ArrayList<Record> records,
			ArrayList<String> attributesTypeList) {
		this.records = records;
		this.attributeList = attributesTypeList;
		this.labelFrequencies = this.calculateLabelFrequencies(this.records);
		this.numberOfValuesAtCol = new int[this.attributeList.size()];
		System.out.println(this.labelFrequencies);
	}

	public double[][][] buildProbabilityMatrix(ArrayList<Record> theRecords) {
		double[][][] probMtrx = new double[this.attributeList.size()][][];

		return null;
	}
	
	public HashMap<Integer, StatsBundle> findParametersForContinuousData(ArrayList<Record> theRecords){
		HashMap<Integer, StatsBundle> infoAtColumn = new HashMap<>();
		
		HashMap<Integer, ArrayList<Double>> valuesAtColumn = new HashMap<>();
		for(int i = 0; i <= attributeList.size() - 2; i++){//size()-2 since attrList has labels at len - 1
			if(attributeList.get(i).equals(NaiveBayesClassifier.CONTINUOUS)){
				valuesAtColumn.put(1, new ArrayList(theRecords.size()));
			}
		}
		
		// now I have to collect all the values at a given column and 
		// get the mean and standard deviation, put them in the hashmap
		// and return that hashmap.
		return null;
	}

	public HashMap<String, Integer> calculateLabelFrequencies(
			ArrayList<Record> theRecords) {
		HashMap<String, Integer> labelFreqs = new HashMap<>(10);
		for (Record rec : theRecords) {
			String label = rec.getLabel();
			if (labelFreqs.containsKey(label)) {
				labelFreqs.put(label, labelFreqs.get(label) + 1);
			} else {
				labelFreqs.put(label, 1);
			}
		}
		return labelFreqs;
	}

	public ArrayList<String> getAttributeList() {
		return this.attributeList;
	}

	private int[] numberOfVarsAtCol(ArrayList<Record> records) {
		// -1 because labels is in the list
		int numAttrs = this.attributeList.size() - 1;
		int[] numberOfVarsAtCol = new int[numAttrs];

		HashMap<Integer, TreeSet<Integer>> map = new HashMap<>();
		for (int i = 0; i < numAttrs; i++) {
			if (this.attributeList.get(i)
					.equals(NaiveBayesClassifier.CONTINUOUS) == false) {
				map.put(i, new TreeSet<Integer>());
			}
		}
		for (Record record : records) {
			double[] attrs = record.getAttrList();
			for (int index = 0; index < numAttrs; index++) {
				if (map.containsKey(index)) {
					TreeSet<Integer> localMap = map.get(index);
					int valAtIndex = (int) attrs[index];
					if (localMap.contains(valAtIndex) == false) {
						localMap.add(valAtIndex);
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
		int[] arr = this.numberOfVarsAtCol(this.records);
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
