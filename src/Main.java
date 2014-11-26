import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Main {
	public static final String TRAIN_DATASET = "train.csv";
	public static final String VALIDATION_DATASET = "validation.csv";
	public static final String TEST_DATASET = "test-ids.csv";
	public static final String OUTPUT_FILENAME = "output.csv";

	public static final int FACTORS_COUNT = 10;

	public static void main(String[] args) throws IOException {
		ArrayList<RateInfo> trainData = readFile(TRAIN_DATASET);
		ArrayList<RateInfo> validateData = readFile(VALIDATION_DATASET);
		trainData.addAll(validateData);
		SVDPredictor predictor = new SVDPredictor(FACTORS_COUNT);
		predictor.train(trainData);

		try (BufferedReader br = new BufferedReader(new FileReader(TEST_DATASET));
				PrintWriter out = new PrintWriter(OUTPUT_FILENAME)) {
			br.readLine(); // skip fields description
			out.println("id,rating");
			String line;
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(",");
				int testId = Integer.parseInt(fields[0]);
				long user = Long.parseLong(fields[1]);
				long item = Long.parseLong(fields[2]);
				double rating = predictor.predict(new RateInfo(user, item));
				out.println(testId + "," + rating);
			}
		}
	}

	public static ArrayList<RateInfo> readFile(String filename)
			throws IOException {
		ArrayList<RateInfo> data = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			br.readLine(); // skip fields description
			String line;
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(",");
				long user = Long.parseLong(fields[0]);
				long item = Long.parseLong(fields[1]);
				int rating = Integer.parseInt(fields[2]);
				data.add(new RateInfo(user, item, rating));
			}
		}
		return data;
	}
}
