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
import ph.adamw.nnt3.evolution.neural.NeuralNet;

public abstract class Evolution<T extends NeuralNet> implements Runnable {
	protected T parent;

	@Getter
	protected int generationCount = 0;

	@Getter
	private Generation<T> generation;

	private Thread thread;
	private int nextGen;
	private boolean nextThreadNetworks;

	@Getter
	protected boolean isDone = false;

	protected abstract Generation<T> populate(Generation<T> generation);

	private void runGenerations(int number, boolean threaded) {
		for (int i = 0; i < number; i++) {
			generation = populate(new Generation<>());

			generation.run(threaded);
			generationCount ++;

			parent = generation.getBestPerformer();
		}
	}

	public void start(int generations, boolean threadNetworks) {
		nextGen = generations;
		nextThreadNetworks = threadNetworks;

		thread = new Thread(this, getClass().getSimpleName() + getClass().hashCode() + "thread");
		thread.start();
	}

	@Override
	public void run() {
		runGenerations(nextGen, nextThreadNetworks);

		synchronized (this) {
			isDone = true;
			notifyAll();
		}
	}
}
