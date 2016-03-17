
public class ClassificationInfo<T> {
	T label;
	double confidenceLevel;
	public ClassificationInfo(T data, double confidenceLevel) {
		super();
		this.label = data;
		this.confidenceLevel = confidenceLevel;
	}
	public T getLabel() {
		return label;
	}
	public double getConfidenceLevel() {
		return confidenceLevel;
	}
	
}
