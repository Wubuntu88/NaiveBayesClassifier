import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public class NaiveBayesClassifier {

	public static final String BINARY = "binary";
	public static final String CATEGORICAL = "categorical";
	public static final String ORDINAL = "ordinal";
	public static final String CONTINUOUS = "continuous";
	public static final String LABEL = "label";
	public static final TreeSet<String> attributeTypes = new TreeSet<String>(
			Arrays.asList(BINARY, CATEGORICAL, ORDINAL, CONTINUOUS, LABEL));
	// list of the type of the variables in records (ordinal, continuous, etc)
	private ArrayList<String> attributeList = new ArrayList<>();
	public ArrayList<String> getAttributeList() {
		return attributeList;
	}
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
	//[column][class][valueAtColumn]
	private double[][][] probMatrix;
	
	public NaiveBayesClassifier(ArrayList<Record> records, ArrayList<String> attributesTypeList){
		this.records = records;
		this.attributeList = attributesTypeList;
		this.labelFrequencies = calculateLabelFrequencies(this.records);
		System.out.println(this.labelFrequencies);
	}
	
	public double[][][] buildProbabilityMatrix(ArrayList<Record> theRecords){
		
		return null;
	}
	
	public HashMap<String, Integer> calculateLabelFrequencies(ArrayList<Record> theRecords){
		HashMap<String, Integer> labelFreqs = new HashMap<>(10);
		for(Record rec: theRecords){
			String label = rec.getLabel();
			if(labelFreqs.containsKey(label)){
				labelFreqs.put(label, labelFreqs.get(label) + 1);
			}else{
				labelFreqs.put(label, 1);
			}
		}
		return labelFreqs;
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public String toString(){
		StringBuffer sBuffer = new StringBuffer("");
		for (Record record : this.records) {
			sBuffer.append(record.toString() + "\n");
		}
		sBuffer.deleteCharAt(sBuffer.length() - 1);
		return sBuffer.toString();
	}
}










































