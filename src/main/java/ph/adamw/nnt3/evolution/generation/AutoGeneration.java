package ph.adamw.nnt3.evolution.generation;

import ph.adamw.nnt3.neural.NeuralNet;

import java.util.Arrays;

public class AutoGeneration<T extends NeuralNet> extends Generation<T> {
	public AutoGeneration(Class<T> type, T parent) {
		super(type);

		for (int i = 0; i < getSize(); i++) {
			map.put(NeuralNetFactory.build(networkType, parent, "Network" + i), null);
		}
	}

	private static class NeuralNetFactory {
		static <T extends NeuralNet> T build(Class<T> type, T parent, String name) {
			T network = getInstanceOfT(type);

			network.setParent(parent);
			network.setThreadName(name);
			network.init();

			return network;
		}

		private static <T extends NeuralNet> T getInstanceOfT(Class<T> type) {
			try {
				return type.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(Arrays.toString(e.getStackTrace()));
			}
		}
	}
}
