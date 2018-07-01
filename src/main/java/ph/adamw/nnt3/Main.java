package ph.adamw.nnt3;

import ph.adamw.nnt3.twoway.TwoWayEvolution;

public class Main {
	public static void main(String[] args) {
		TwoWayEvolution evo = new TwoWayEvolution();
		for (int i = 0; i < 1000; i++) {
			evo.run(false);
			System.out.println(evo.getHighestPerformer().getFitness());
			if (evo.getHighestPerformer().getFitness() == 1450) {
				System.out.println("Perfect score achieved at " + i + " generations!");
				return;
			}
		}
	}
}
