package ph.adamw.nnt3;

import ph.adamw.nnt3.neural.NeuralNet;
import ph.adamw.nnt3.neural.NeuralNetSettings;
import ph.adamw.nnt3.neural.neuron.ActivationFunction;

import java.util.ArrayList;
import java.util.List;

public class TwoWayNeuralNet extends NeuralNet {
	private static NeuralNetSettings SETTINGS = new NeuralNetSettings(1, 2, 3, 1, 0.025, ActivationFunction.getSigmoid());

	@Override
	public NeuralNetSettings getSettings() {
		return SETTINGS;
	}

	@Override
	protected void score() {
		final List<List<Double>> guesses = new ArrayList<>();

		// Evaluation of the input
		for (List<Double> input : getDataSet().getInputs()) {
			guesses.add(evaluate(input));
		}

		final List<List<Double>> answers = getDataSet().getOutputs();

		for (int i = 0; i < guesses.size(); i++) {
			List<Double> guess = guesses.get(i);
			List<Double> answer = answers.get(i);

			for (int j = 0; j < guess.size(); j++) {
				final double sign = answer.get(j) == 0 ? -1 : 1;
				final double certainty = (guess.get(j) - 0.5) * 2 * sign;

				score += certainty;
			}
		}
	}
}
