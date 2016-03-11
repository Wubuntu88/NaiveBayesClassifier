
public class StatsBundle {
	
	private double mean;
	public double getMean() {
		return mean;
	}
	
	private double stdev;
	public double getStdev() {
		return stdev;
	}

	
	
	public StatsBundle(double mean, double stdev){
		this.mean = mean;
		this.stdev = stdev;
	}

}
