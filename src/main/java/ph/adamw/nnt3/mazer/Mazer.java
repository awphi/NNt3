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

package ph.adamw.nnt3.mazer;

import lombok.Getter;
import lombok.Setter;
import ph.adamw.nnt3.evolution.neural.NeuralNet;
import ph.adamw.nnt3.evolution.neural.NeuralNetSettings;
import ph.adamw.nnt3.evolution.neural.neuron.ActivationFunction;
import ph.adamw.nnt3.gui.grid.LiveGrid;

import java.util.ArrayList;
import java.util.List;

public class Mazer extends NeuralNet {
	@Setter
	@Getter
	private MazerEntity entity;

	private boolean killed = false;

	private static final int CYCLE_MULTIPLIER = 4;
	private static final int FITNESS_MULTIPLIER = 100;

	/**
	 * 6 INPUTS = * 4-directional distance to closest obstacle (starts from up then goes clockwise)
	 * 			  * Distance from current position to goal
	 * 			  * Turns in a row that the character has not changed location (to prevent wall-walking
	 *
	 * 4 OUTPUTS = * 4-directional output (starts from up then goes clockwise) (evaluated and acted upon by character)
	 *
	 */
	public static final NeuralNetSettings STATIC_SETTINGS =
			new NeuralNetSettings(6, 0, 0, 4, 0.05, ActivationFunction.getSigmoid());


	public Mazer(NeuralNetSettings settings) {
		super(settings);
	}

	@Override
	protected void calculateFitness() {
		final int cycles = ((int) Math.sqrt(entity.dataGrid.getHeight() * entity.dataGrid.getWidth())) * CYCLE_MULTIPLIER;
		killed = false;

		for(int i = 0; i < cycles; i ++) {
			// If the thread is terminated (mainly used by the GUI) then we return without modifying the fitness
			if(killed) {
				return;
			}

			List<Double> inputs = new ArrayList<>();

			// 4-directional inputs
			for(EntityDirection dir : EntityDirection.VALUES) {
				inputs.add((double) entity.dataGrid.getDistanceToNextObstacle(entity.currentCol, entity.currentRow, dir));
			}

			inputs.add(MazerUtils.distanceBetween(entity.currentCol, entity.currentRow, entity.dataGrid.getGoal()));

			// Number of cycles in a row that the entity hasn't moved
			inputs.add((double) entity.getStationaryCount());

			entity.move(evaluate(inputs).toArray(new Double[STATIC_SETTINGS.getOutputs()]));

			if(entity instanceof DrawingMazerEntity) {
				try {
					Thread.sleep(((DrawingMazerEntity) entity).getInterval());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		fitness = FITNESS_MULTIPLIER / MazerUtils.distanceBetween(entity.currentCol, entity.currentRow, entity.dataGrid.getGoal());
	}

	/**
	 * Runs a thread that draws a mazer entity onto a LiveGrid step by step.
	 * @param liveGrid the grid you wish to play this mazer's actions back onto
	 */
	public void playOnGrid(LiveGrid liveGrid, int interval) {
		entity = new DrawingMazerEntity(entity.dataGrid, liveGrid, interval);
		start(true);
	}

	public void stop() {
		killed = true;
		entity.reset();
	}
}
