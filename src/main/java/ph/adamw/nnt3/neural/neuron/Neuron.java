/*
 * MIT License
 *
 * Copyright (c) 2018 AWPH-I
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

package ph.adamw.nnt3.neural.neuron;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Neuron {
	@Getter
	@Setter
	private double value = 0;

	@Getter
	protected List<NeuronConnection> connections = new ArrayList<>();

	private final ActivationFunction activationFunction;

	public Neuron(ActivationFunction activationFunction) {
		this.activationFunction = activationFunction;
	}

	public void feedForward() {
		for (NeuronConnection connection : connections) {
			setValue(getValue() + connection.getFrom().getValue() * connection.getWeight());
		}

		setValue(activationFunction.activate(getValue()));
	}

	void addConnection(NeuronConnection connection) {
		connections.add(connection);
	}

}
