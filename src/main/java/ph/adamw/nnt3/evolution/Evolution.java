package ph.adamw.nnt3.evolution;

import ph.adamw.nnt3.neural.NeuralNet;

public abstract class Evolution<T extends NeuralNet> {
	protected T parent;
	protected Generation<T> generation;

	protected abstract void populate();

	public void run(boolean threaded) {
		generation = new Generation<>();
		populate();
		generation.run(threaded);
		parent = generation.getBestPerformer();
	}

	public T getHighestPerformer() {
		if (generation == null) {
			return null;
		} else {
			return generation.getBestPerformer();
		}
	}

	public void run(int number, boolean threaded) {
		for (int i = 0; i < number; i++) {
			run(threaded);
		}
	}
}
