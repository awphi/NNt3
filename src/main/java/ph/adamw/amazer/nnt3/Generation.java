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

package ph.adamw.amazer.nnt3;

import lombok.Getter;
import lombok.Setter;
import ph.adamw.amazer.nnt3.neural.NeuralNet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class Generation<T extends NeuralNet> {
	private final HashSet<T> members = new HashSet<>();

	@Getter
	@Setter
	public int size = 10;

	public void run(boolean threaded) {
		if(members.size() != size) {
			throw new RuntimeException("Generation has been wrongly populated! Expected " + size + " networks but got " + members.size() + "!");
		}

		for (T network : members) {
			network.start(threaded);
		}
	}

	public List<T> getSortedCopyOfMembers() {
		List<T> sorted = new ArrayList<>(members);
		Collections.sort(sorted);
		return sorted;
	}

	T waitForBestPerformer() {
		T best = null;

		for (T key : members) {
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
		members.add(network);
	}
}
