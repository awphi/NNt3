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
import lombok.Setter;
import ph.adamw.amazer.nnt3.neural.NeuralNet;
import ph.adamw.amazer.nnt3.neural.NeuralNetSettings;
import ph.adamw.amazer.nnt3.neural.neuron.ActivationFunction;
import ph.adamw.amazer.gui.grid.LiveGrid;
import ph.adamw.amazer.mazer.entity.DrawingMazerEntity;
import ph.adamw.amazer.mazer.entity.EntityDirection;
import ph.adamw.amazer.mazer.entity.MazerEntity;

import java.util.ArrayList;
import java.util.List;

public class Mazer extends NeuralNet {
	@Setter
	@Getter
	private MazerEntity entity;

	private boolean killed = false;

	private static final int CYCLE_MULTIPLIER = 6;
	private static final double FITNESS_MULTIPLIER = 100;

	/**
	 * 6 INPUTS = * 4-directional distance to closest obstacle (starts from up then goes clockwise)
	 * 			  * Distance from current position to goal
	 * 			  * Angle from current position to goal => w/ distance we effectively give it an *as the crow flies* vector to the goal
	 *
	 * 4 OUTPUTS = * 4-directional output (starts from up then goes clockwise) (evaluated and acted upon by character)
	 *
	 */
	public static final NeuralNetSettings STATIC_SETTINGS =
			new NeuralNetSettings(6, 0, 0, 4, 0.25, ActivationFunction.getSigmoid());


	public Mazer(NeuralNetSettings settings) {
		super(settings);
	}

	@Override
	protected void execute() {
		killed = false;
		entity.reset();

		final int cycles = ((int) Math.sqrt(entity.getDataGrid().getHeight() * entity.getDataGrid().getWidth())) * CYCLE_MULTIPLIER;

		boolean exitedEarly = false;

		int cyclesUsed;
		for(cyclesUsed = 0; cyclesUsed < cycles; cyclesUsed ++) {
			//TODO look into incremental learning - i.e. only give it X cycles increasing by Y every Z generations so it has to master the first X moves first

			// If the thread is halted from an outside thread then exit without modifying the fitness
			if(killed) {
				entity.reset();
				return;
			}

			final List<Double> inputs = new ArrayList<>();

			// 4-directional inputs
			for(EntityDirection dir : EntityDirection.VALUES) {
				inputs.add((double) entity.getDataGrid().getDistanceToNextObstacle(entity.getCurrentCol(), entity.getCurrentRow(), dir));
			}

			// Distance and angle (a vector) between entity and the goal
			inputs.add(MazerUtils.distanceBetween(entity.getCurrentCol(), entity.getCurrentRow(), entity.getDataGrid().getGoal()));
			inputs.add(MazerUtils.angle(entity.getCurrentCol(), entity.getCurrentRow(), entity.getDataGrid().getGoal()));

			entity.move(evaluate(inputs).toArray(new Double[STATIC_SETTINGS.getOutputs()]));

			final int intv = entity.getInterval();

			if(intv > 0) {
				try {
					Thread.sleep(intv);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}

			if(entity.getCurrentCol() == entity.getDataGrid().getGoal().getCol() && entity.getCurrentRow() == entity.getDataGrid().getGoal().getRow()) {
				break;
			}
		}

		//TODO use cyclesUsed here in some way to reward using less
		fitness = FITNESS_MULTIPLIER / MazerUtils.distanceBetween(entity.getCurrentCol(), entity.getCurrentRow(), entity.getDataGrid().getGoal());
	}

	public void playOnGrid(LiveGrid liveGrid, int interval) {
		entity = new DrawingMazerEntity(entity.getDataGrid(), liveGrid, interval);
		start(true);
	}

	public void kill() {
		killed = true;
		entity.reset();
	}
}
