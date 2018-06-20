package ph.adamw.nnt3.evolution.generation;

import lombok.Getter;
import lombok.Setter;
import ph.adamw.nnt3.neural.NeuralNet;

import java.util.HashMap;
import java.util.Map;

public abstract class Generation<T extends NeuralNet> {
	protected final Class<T> networkType;

	@Getter
	@Setter
	public int size = 10;

	protected final Map<T, Double> map = new HashMap<>();

	protected Generation(Class<T> networkType) {
		this.networkType = networkType;
	}

	public void run(boolean threaded) {
		for (NeuralNet network : map.keySet()) {
			network.start(threaded);
		}
	}

	public T getBestPerformer() {
		for (NeuralNet network : map.keySet()) {
			if(!network.isDone()) {
				return null;
			}
		}

		T best = null;
		for (T key : map.keySet()) {
			if (best == null || key.getScore() >= best.getScore()) {
				best = key;
			}
		}
		return best;
	}
}
