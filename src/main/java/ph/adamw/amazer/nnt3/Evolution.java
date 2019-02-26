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
import ph.adamw.amazer.nnt3.neural.Agent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Evolution<T extends Agent> implements Serializable {
	private List<T> parents = new ArrayList<>();

	private int currentParentIndex = 0;

	@Getter
	protected int generationCount = 0;

	@Getter
	private Generation<T> generation;

	protected abstract Generation<T> populate(Generation<T> generation);

	public void run(int generations, boolean threadNetworks) {
		for (int i = 0; i < generations; i++) {
			generation = populate(new Generation<>());
			generation.run(threadNetworks);
			parents.clear();
			// Longest aspect
			parents.addAll(generation.waitForSortedAgents());
			generationCount ++;
		}
	}

	protected T getNextParent() {
		if(parents.size() == 0) {
			return null;
		}

		// TODO play around with these numbers - currently the top perfomer would have a 3/4 chance of producing a child each time this method is invoked
		if(ThreadLocalRandom.current().nextDouble() > (double) ((parents.size() - currentParentIndex) * 3) / (double) (4 * parents.size())) {
			currentParentIndex = (currentParentIndex + 1) % parents.size();
		}

		return parents.get(currentParentIndex);
	}
}
