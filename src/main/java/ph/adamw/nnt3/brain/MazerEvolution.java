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

package ph.adamw.nnt3.brain;

import lombok.Setter;
import ph.adamw.nnt3.evolution.Evolution;
import ph.adamw.nnt3.evolution.Generation;
import ph.adamw.nnt3.evolution.neural.NeuralNetSettings;
import ph.adamw.nnt3.gui.grid.DataGrid;

public class MazerEvolution extends Evolution<Mazer> {
	private final NeuralNetSettings currentSettings;

	@Setter
	private int genSize = 10;

	private final DataGrid dataGrid;

	public MazerEvolution(DataGrid dataGrid, NeuralNetSettings currentSettings) {
		this.dataGrid = dataGrid;
		this.currentSettings = currentSettings;
	}

	@Override
	protected Generation<Mazer> populate(Generation<Mazer> generation) {
		generation.setSize(genSize);

		for(int i = 0; i < generation.getSize(); i ++) {
			Mazer brain = new Mazer(currentSettings);
			brain.setEntity(new MazerEntity(dataGrid));
			brain.init(parent, "mazer" + i);

			generation.add(brain);
		}

		return generation;
	}

	public void start(int generations) {
		start(generations, true);
	}
}
