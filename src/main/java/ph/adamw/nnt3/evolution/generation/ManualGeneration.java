package ph.adamw.nnt3.evolution.generation;

import ph.adamw.nnt3.neural.NeuralNet;

import java.util.List;

public class ManualGeneration<T extends NeuralNet> extends Generation<T> {
	public ManualGeneration(Class<T> networkType) {
		super(networkType);
	}

	public void populate(List<T> networks) {
		if(networks.size() != getSize()) {
			throw new RuntimeException("Manually generated networks are of a different size to the generation!");
		}

		for(T net : networks) {
			map.put(net, null);
		}
	}
}
