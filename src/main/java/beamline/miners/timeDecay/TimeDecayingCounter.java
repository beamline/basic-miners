package beamline.miners.timeDecay;

/*
 * This class implements the solution presented at https://stackoverflow.com/a/42313759
 */
public class TimeDecayingCounter {

	private double alpha = 0.9;
	private double millisecondsDivisor = 1000;
	private long last_t = 0l;
	private double heat = 0d;
	
	public TimeDecayingCounter(double alpha, double millisecondsDivisor) {
		this.alpha = alpha;
		this.millisecondsDivisor = millisecondsDivisor;
		this.last_t = getNow();
	}
	
	public void increment() {
		increment(1d);
	}
	
	public void increment(double amount) {
		long now = getNow();
		long elapsed = now - last_t;
		if (elapsed >= 0) {
			heat = amount + heat * Math.pow(alpha , elapsed);
			last_t = now;
		}
	}
	
	public double get() {
		long now = getNow();
		long elapsed = now - last_t;
		if (elapsed >= 0) {
			System.out.println("GET - heat = " + heat + "; alpha = " + alpha + "; elapsed = " + elapsed);
			return heat * Math.pow(alpha, elapsed);
		}
		return -1d;
	}
	
	private long getNow() {
		return (long) (System.currentTimeMillis() / millisecondsDivisor);
	}
}
