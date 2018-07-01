package ph.adamw.nnt3.twoway;

import ph.adamw.nnt3.neural.NeuralNet;
import ph.adamw.nnt3.neural.NeuralNetSettings;
import ph.adamw.nnt3.neural.neuron.ActivationFunction;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwoWayNeuralNet extends NeuralNet {
	private static final NeuralNetSettings SETTINGS = new NeuralNetSettings(1, 2, 3, 1, 0.025, ActivationFunction.getSigmoid());

	private static Map<List<Double>, List<Double>> DATA = new HashMap<>();

	static {
		try {
			FileReader fr = new FileReader("C:\\Users\\Adam\\Desktop\\data.txt");
			int i;
			StringBuilder text = new StringBuilder();

			while ((i = fr.read()) != -1) {
				text.append((char) i);
			}

			String t = text.toString();

			String[] lines = t.split("(\\r\\n|\\r|\\n)");

			for (String j : lines) {
				String[] split = j.split("\\s+");
				List<Double> inp = new ArrayList<>();
				inp.add(Double.parseDouble(split[0]));

				List<Double> out = new ArrayList<>();
				out.add(Double.parseDouble(split[1]));
				DATA.put(inp, out);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public TwoWayNeuralNet() {
		super(SETTINGS);
	}

	@Override
	protected void calculateFitness() {
		final List<List<Double>> guesses = new ArrayList<>();

		// Evaluation of the input
		for (List<Double> input : DATA.keySet()) {
			guesses.add(evaluate(input));
		}

		final List<List<Double>> answers = new ArrayList<>(DATA.values());

		for (int i = 0; i < guesses.size(); i++) {
			List<Double> guess = guesses.get(i);
			List<Double> answer = answers.get(i);

			for (int j = 0; j < guess.size(); j++) {
				final double sign = answer.get(j) == 0 ? -1 : 1;
				final double certainty = (guess.get(j) - 0.5) * 2 * sign;

				fitness += certainty;
			}
		}
	}
}
