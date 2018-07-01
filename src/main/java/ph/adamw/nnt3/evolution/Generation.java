package ph.adamw.nnt3.evolution;

import lombok.Getter;
import lombok.Setter;
import ph.adamw.nnt3.neural.NeuralNet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Generation<T extends NeuralNet> {
	protected final Map<T, Double> map = new HashMap<>();

	@Getter
	@Setter
	public int size = 10;

	public void run(boolean threaded) {
		if (map.keySet().size() <= 0) {
			throw new RuntimeException("No neural networks provided for generation to populate!");
		}

		for (NeuralNet network : map.keySet()) {
			network.start(threaded);
		}
	}

	public T getBestPerformer() {
		boolean processing = true;
		while (processing) {
			processing = false;
			for (NeuralNet network : map.keySet()) {
				if (!network.isDone()) {
					processing = true;
				}
			}
		}

		T best = null;
		for (T key : map.keySet()) {
			if (best == null || key.getFitness() >= best.getFitness()) {
				best = key;
			}
		}
		return best;
	}

	public void populate(List<T> networks) {
		if (networks.size() != getSize()) {
			throw new RuntimeException("Number of networks given is not equal to size of the generation!");
		}

		for (T net : networks) {
			map.put(net, null);
		}
	}

	public void populateFromParent(Class<T> type, T parent, String baseName) {
		for (int i = 0; i < size; i++) {
			T net;
			try {
				net = type.newInstance();
				net.init(parent, baseName + i);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}

			map.put(net, null);
		}
	}
}
