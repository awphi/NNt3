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

public abstract class Evolution<T extends Agent> implements Serializable {
	private List<T> parents = new ArrayList<>();

	@Getter
	protected int generationCount = 0;

	@Getter
	private Generation<T> generation;

	protected abstract Generation<T> populate(Generation<T> generation);

	public void run(int generations, boolean threadNetworks) {
		for (int i = 0; i < generations; i++) {
			generation = populate(new Generation<>());

			generation.run(threadNetworks);

			// Longest aspect
			parents.addAll(generation.waitForSortedAgents());

			generationCount ++;
		}
	}

	protected T getParent(int i) {
		//TODO make it so the highest performers get more children on a linear scale so top gets a lot, bottom gets 0 or like 1
		return parents.size() > 0 ? parents.get(i % parents.size()) : null;
	}
}
