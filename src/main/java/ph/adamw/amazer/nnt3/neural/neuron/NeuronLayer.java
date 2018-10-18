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

package ph.adamw.amazer.nnt3.neural.neuron;

import java.util.ArrayList;
import java.util.List;

public class NeuronLayer extends ArrayList<Neuron> {
	public NeuronLayer(int size, ActivationFunction activationFunction) {
		for (int i = 0; i < size; i++) {
			add(new Neuron(activationFunction));
		}
	}

	public List<Double> getValues() {
		List<Double> values = new ArrayList<>();
		for (Neuron i : this) {
			values.add(i.getValue());
		}
		return values;
	}

	public void setValues(List<Double> inputs) {
		for (int i = 0; i < size(); i++) {
			get(i).setValue(inputs.get(i));
		}
	}

	public void connectToLayer(NeuronLayer other) {
		for (Neuron t : this) {
			for (Neuron anOther : other) {
				// Creates a backwards connection i.e. h0 <- h1
				// this is then used in h1 to iterate it's connections and pull all the data required
				// everything works out a lot nicer when a pull architecture is used over a push
				t.addConnection(new NeuronConnection(anOther));
			}
		}
	}

	public void feedForward() {
		for (Neuron i : this) {
			i.feedForward();
		}
	}
}
