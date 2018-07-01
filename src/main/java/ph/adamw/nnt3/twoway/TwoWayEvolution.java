package ph.adamw.nnt3.twoway;

import ph.adamw.nnt3.evolution.Evolution;

public class TwoWayEvolution extends Evolution<TwoWayNeuralNet> {
	@Override
	protected void populate() {
		generation.populateFromParent(TwoWayNeuralNet.class, parent, "TwoWay");
	}
}
