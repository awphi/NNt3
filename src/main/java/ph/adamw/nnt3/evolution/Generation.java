/*
 * MIT License
 *
 * Copyright (c) 2018 awphi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ph.adamw.nnt3.evolution;

import lombok.Getter;
import lombok.Setter;
import ph.adamw.nnt3.evolution.neural.NeuralNet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Generation<T extends NeuralNet> {
	protected final Map<T, Double> map = new HashMap<>();

	@Getter
	@Setter
	public int size = 10;

	public void run(boolean threaded) {
		if (map.keySet().size() <= 0) {
			throw new RuntimeException("No neural networks provided for generation to populate!");
		}

		if(map.keySet().size() != size) {
			throw new RuntimeException("Generation is under-populated! Expected " + size + " networks but only got " + map.keySet().size() + "!");
		}

		for (NeuralNet network : map.keySet()) {
			network.start(threaded);
		}
	}

	public Set<T> getMembers() {
		// Feed them a non-modifiable version as it's just used for visual displays etc.
		return Collections.unmodifiableSet(map.keySet());
	}

	public T getBestPerformer() {
		T best = null;

		for (T key : map.keySet()) {
			synchronized (key) {
				if(!key.isDone()) {
					try {
						key.wait();
					} catch (InterruptedException ignored) {}
				}
			}

			if (best == null || key.getFitness() >= best.getFitness()) {
				best = key;
			}
		}

		return best;
	}

	public void add (T network) {
		map.put(network, null);
	}
}
