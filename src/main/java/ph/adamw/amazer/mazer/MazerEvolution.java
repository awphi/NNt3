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

package ph.adamw.amazer.mazer;

import lombok.Getter;
import ph.adamw.amazer.nnt3.Evolution;
import ph.adamw.amazer.nnt3.Generation;
import ph.adamw.amazer.nnt3.neural.NeuralNetSettings;
import ph.adamw.amazer.gui.grid.data.DataGrid;
import ph.adamw.amazer.mazer.entity.MazerEntity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MazerEvolution extends Evolution<MazerAgent> {
	private final NeuralNetSettings currentSettings;

	@Getter
	private final DataGrid dataGrid;
	private final int generationSize;

	private int offlineNameCount = 0;

	public MazerEvolution(DataGrid dataGrid, NeuralNetSettings currentSettings, int generationSize) {
		this.dataGrid = dataGrid;
		this.currentSettings = currentSettings;
		this.generationSize = generationSize;
	}

	@Override
	protected Generation<MazerAgent> populate(Generation<MazerAgent> generation) {
		generation.setSize(generationSize);
		final String[] names = getRandomNames(generationSize);

		for(int i = 0; i < generation.getSize(); i ++) {
			final MazerAgent brain = new MazerAgent(currentSettings, parent, names == null ? "MazerAgent" + offlineNameCount : names[i]);
			brain.setEntity(new MazerEntity(dataGrid));

			generation.add(brain);

			offlineNameCount ++;
		}

		return generation;
	}

	private String[] getRandomNames(int size) {
		final String[] ret = new String[size];

		try {
			URL url = new URL("http://names.drycodes.com/" + ret.length + "?format=text");
			URLConnection connection = url.openConnection();

			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String l;
			int c = 0;

			while ((l = br.readLine()) != null) {
				ret[c] = l;
				c ++;
			}

			br.close();
		} catch (Exception e) {
			return null;
		}

		return ret;
	}
}
