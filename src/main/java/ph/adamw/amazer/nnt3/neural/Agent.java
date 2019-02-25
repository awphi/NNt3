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

package ph.adamw.amazer.nnt3.neural;

import lombok.Getter;
import ph.adamw.amazer.nnt3.Generation;
import ph.adamw.amazer.nnt3.neural.neuron.Neuron;
import ph.adamw.amazer.nnt3.neural.neuron.NeuronConnection;
import ph.adamw.amazer.nnt3.neural.neuron.NeuronLayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Agent implements Runnable, Comparable<Agent>, Serializable {
	private final NeuralNetSettings settings;

	@Getter
	private double fitness = 0;

	@Getter
	private boolean isDone = false;

	@Getter
	private String threadName;

	@Getter
	private NeuronLayer inputLayer;

	@Getter
	private List<NeuronLayer> hiddenLayers = new ArrayList<>();

	@Getter
	private NeuronLayer outputLayer;

	private Thread thread;

	public Agent(NeuralNetSettings settings, Agent parent, String threadName) {
		this.settings = settings;

		this.threadName = threadName;

		inputLayer = new NeuronLayer(settings.getInputs(), settings.getActivationFunction());

		for (int i = 0; i < settings.getHiddenLayersAmount(); i++) {
			hiddenLayers.add(new NeuronLayer(settings.getHiddenLayersSize(), settings.getActivationFunction()));
		}

		outputLayer = new NeuronLayer(settings.getOutputs(), settings.getActivationFunction());

		// Inputs to first hidden layer
		hiddenLayers.get(0).connectToLayer(inputLayer);

		// Hidden layers to each other
		for (int i = 0; i <= hiddenLayers.size() - 2; i++) {
			hiddenLayers.get(i + 1).connectToLayer(hiddenLayers.get(i));
		}

		// Last hidden layer to output layer
		outputLayer.connectToLayer(hiddenLayers.get(hiddenLayers.size() - 1));

		randomizeWeights(parent);
	}

	private void randomizeWeights(Agent parent) {
		if (parent == null) {
			final Random random = new Random();
			for (NeuronLayer layer : getAllLayers()) {
				for (Neuron neuron : layer)
					for (NeuronConnection connection : neuron.getConnections()) {
						connection.setWeight(random.nextFloat() * 2 - 1);
					}
			}

			return;
		}

		// Mutate the weights from the parents if it is not null
		for (int i = 0; i < getAllLayers().size(); i++) {
			for (int j = 0; j < getAllLayers().get(i).size(); j++) {
				final Neuron thisNeuron = getAllLayers().get(i).get(j);
				final Neuron parentNeuron = parent.getAllLayers().get(i).get(j);

				for (int k = 0; k < thisNeuron.getConnections().size(); k++) {
					double w = parentNeuron.getConnections().get(k).getWeight();
					double mutatedWeight = Agent.Utils.mutateWeight(w, settings.getMutationRate());

					thisNeuron.getConnections().get(k).setWeight(mutatedWeight);
				}
			}
		}
	}

	private List<NeuronLayer> getAllLayers() {
		final List<NeuronLayer> layers = new ArrayList<>();

		layers.add(inputLayer);
		layers.addAll(hiddenLayers);
		layers.add(outputLayer);

		return layers;
	}

	protected double[] evaluate(double[] inputs) {
		if (inputs.length != inputLayer.size()) {
			throw new RuntimeException("Input sample size needs to match the size of the input layer!");
		}

		inputLayer.setValues(inputs);

		for (NeuronLayer hiddenLayer : hiddenLayers) {
			hiddenLayer.feedForward();
		}

		outputLayer.feedForward();

		return outputLayer.getValues();
	}

	public void start(boolean threaded) {
		if (threaded) {
			thread = new Thread(this, threadName);
			thread.start();
		} else {
			run();
		}
	}

	@Override
	public void run() {
		// If the agent is being reused, flush the values so behaviour is consistent
		if(isDone) {
			flushValues();
		}

		isDone = false;

		fitness = evaluateFitness();

		synchronized (this) {
			isDone = true;
			thread = null;
			notifyAll();
		}
	}

	private void flushValues() {
		for(NeuronLayer layer : getAllLayers()) {
			for(Neuron i : layer) {
				i.setValue(0);
			}
		}
	}

	protected abstract double evaluateFitness();

	public int compareTo(Agent other) {
		if(fitness == other.fitness) {
			return 0;
		}

		return fitness > other.fitness ? 1 : -1;
	}

	private static class Utils {
		private static final Random random = new Random();

		static double mutateWeight(double parentWeight, double mutateRate) {
			float percent = random.nextFloat() * 2 - 1;
			parentWeight += parentWeight * (mutateRate * percent);
			return parentWeight;
		}
	}
}
