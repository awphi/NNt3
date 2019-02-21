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

package ph.adamw.amazer.agent;

import lombok.Getter;
import lombok.Setter;
import ph.adamw.amazer.nnt3.neural.Agent;
import ph.adamw.amazer.nnt3.neural.NeuralNetSettings;
import ph.adamw.amazer.nnt3.neural.neuron.ActivationFunction;
import ph.adamw.amazer.agent.entity.EntityDirection;
import ph.adamw.amazer.agent.entity.MazerEntity;

import java.util.ArrayList;
import java.util.List;

public class MazerAgent extends Agent {
	@Setter
	@Getter
	private transient MazerEntity entity;

	private static final int CYCLE_MULTIPLIER = 6;

	public static final int INPUTS = 5;
	public static final int OUTPUTS = 4;
	public static ActivationFunction ACTIVATION_FUNCTION = ActivationFunction.getSigmoid();


	public MazerAgent(NeuralNetSettings settings, MazerAgent parent, String name) {
		super(settings, parent, name);
	}

	@Override
	protected void execute() {
		final int maxCycles = ((int) Math.sqrt(entity.getMaze().getHeight() * entity.getMaze().getWidth())) * CYCLE_MULTIPLIER;

		int cyclesUsed = 0;
		while(cyclesUsed < maxCycles && !(entity.getCurrentCol() == entity.getMaze().getGoal().getCol() && entity.getCurrentRow() == entity.getMaze().getGoal().getRow())) {
			final List<Double> inputs = new ArrayList<>();

			// 4-directional inputs
			for(EntityDirection dir : EntityDirection.VALUES) {
				inputs.add((double) entity.getMaze().getDistanceToNextObstacle(entity.getCurrentCol(), entity.getCurrentRow(), dir));
			}

			// Distance and bearing (a vector) between entity and the goal
			//inputs.add(MazerUtils.distanceBetween(entity.getCurrentCol(), entity.getCurrentRow(), entity.getMaze().getGoal()));
			inputs.add(MazerUtils.bearing(entity.getCurrentCol(), entity.getCurrentRow(), entity.getMaze().getGoal()));

			entity.move(evaluate(inputs.stream().mapToDouble(i -> i).toArray()));

			cyclesUsed ++;

			final int interval = entity.getInterval();

			if(interval > 0) {
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		fitness = entity.getMaze().distanceFromGoal(entity.getCurrentCol(), entity.getCurrentRow());

		entity.reset();
	}
}
