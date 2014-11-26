import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SVDPredictor {
	private final static int ITERATIONS_COUNT = 10;
	private final static double EPS = 1e-5;

	private double mu = 0;
	private double gamma = 0.01;
	private double lambda = 0.01;

	private final int factorsCount;

	private Map<Long, Double> bu = new HashMap<Long, Double>();
	private Map<Long, double[]> fu = new HashMap<Long, double[]>();
	private Map<Long, Double> bv = new HashMap<Long, Double>();
	private Map<Long, double[]> fv = new HashMap<Long, double[]>();

	public SVDPredictor(int factorsCount) {
		this.factorsCount = factorsCount;
	}

	public void train(List<RateInfo> trainingData) {
		double threshold = 0.01;
		double oldRmse = 10;
		double rmse = 0;
		int iteration = 0;
		for (RateInfo item : trainingData) {
			addItem(item);
		}

		while (Math.abs(oldRmse - rmse) > EPS) {
			oldRmse = rmse;
			rmse = 0;

			for (RateInfo curr : trainingData) {
				long u = curr.getUser(), v = curr.getItem(), r = curr.getRating();
				double err = r - mu - bu.get(u) - bv.get(v) - crossProduct(fu.get(u), fv.get(v));
				rmse += err * err;
				mu += gamma * err;
				bu.put(u, bu.get(u) + gamma * (err - lambda * bu.get(u)));
				bv.put(v, bv.get(v) + gamma * (err - lambda * bv.get(v)));
				for (int i = 0; i < factorsCount; i++) {
					fu.get(u)[i] += gamma * (err * fv.get(v)[i] - lambda * fu.get(u)[i]);
					fv.get(v)[i] += gamma * (err * fu.get(u)[i] - lambda * fv.get(v)[i]);
				}
			}
			iteration++;
			rmse = Math.sqrt(rmse / trainingData.size());
			gamma *= 0.95;
			/*
			 * if (iteration >= ITERATIONS_COUNT) { break; }
			 */
			if (rmse > oldRmse - threshold) {
				gamma *= 0.66;
				threshold *= 0.5;
			}
			System.out.println(iteration + ": current rmse " + rmse);
		}
	}

	public double predict(RateInfo item) {
		addItem(item);
		return mu + bu.get(item.getUser()) + bv.get(item.getItem())
				+ crossProduct(fu.get(item.getUser()), fv.get(item.getItem()));
	}

	private double crossProduct(double[] x, double[] y) {
		double result = 0;
		for (int i = 0; i < Math.min(x.length, y.length); i++) {
			result += x[i] * y[i];
		}
		return result;
	}

	private void addItem(RateInfo item) {
		if (!bu.containsKey(item.getUser())) {
			bu.put(item.getUser(), 0.0);
			fu.put(item.getUser(), new double[factorsCount]);
		}
		if (!bv.containsKey(item.getItem())) {
			bv.put(item.getItem(), 0.0);
			fv.put(item.getItem(), new double[factorsCount]);
		}
	}
}
