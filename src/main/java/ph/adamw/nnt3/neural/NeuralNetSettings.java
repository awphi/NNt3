package ph.adamw.nnt3.neural;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ph.adamw.nnt3.neural.neuron.ActivationFunction;

@AllArgsConstructor
@Getter
public class NeuralNetSettings {
	private final int inputs;
	private final int hiddenLayersAmount;
	private final int hiddenLayersSize;
	private final int outputs;

	private final double mutationRate;

	private final ActivationFunction activationFunction;
}
