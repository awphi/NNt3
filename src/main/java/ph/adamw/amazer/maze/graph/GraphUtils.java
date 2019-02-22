/*
 * MIT License
 *
 * Copyright (c) 2019 awphi
 *
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

package ph.adamw.amazer.maze.graph;

import ph.adamw.amazer.agent.entity.EntityDirection;
import ph.adamw.amazer.maze.Cell;
import ph.adamw.amazer.maze.CellState;
import ph.adamw.amazer.maze.Maze;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GraphUtils {
    public static Graph buildGraph(Maze maze) {
        Graph graph = new Graph();

        // Set up nodes
        for(Cell[] i : maze.getCells()) {
            for(Cell j : i) {
                if(!j.getState().equals(CellState.WALL)) {
                    graph.add(new GraphNode(j.toString()));
                }
            }
        }

        // Set up edges
        for(Cell[] i : maze.getCells()) {
            for(Cell j : i) {
                if (!j.getState().equals(CellState.WALL)) {
                    for (EntityDirection dir : EntityDirection.VALUES) {
                        final Cell neighbour = maze.getCellInDirection(j, dir);
                        if(neighbour != null && neighbour.getState() != CellState.WALL) {
                            final GraphNode nodeA = graph.getNode(neighbour.toString());
                            final GraphNode nodeB = graph.getNode(j.toString());

                            nodeA.addAdjacent(nodeB, 1);
                            nodeB.addAdjacent(nodeA, 1);
                        }
                    }
                }
            }
        }

        return djikstra(graph, graph.getNode(maze.getGoal().toString()));
    }

    private static Graph djikstra(Graph graph, GraphNode src) {
        final Set<GraphNode> settled = new HashSet<>();
        final Set<GraphNode> unsettled = new HashSet<>();
        unsettled.add(src);

        src.setShortestDistanceToSource(0);

        while (unsettled.size() != 0) {
            final GraphNode current = closestNode(unsettled);
            unsettled.remove(current);
            for (Map.Entry<GraphNode, Integer> entry : current.getAdjacentNodes().entrySet()) {
                final GraphNode adj = entry.getKey();

                if (!settled.contains(adj)) {
                    minimumDistance(adj, entry.getValue(), current);
                    unsettled.add(adj);
                }
            }

            settled.add(current);
        }

        return graph;
    }

    private static void minimumDistance(GraphNode node, Integer weight, GraphNode src) {
        final Integer dis = src.getShortestDistanceToSource();

        if (dis + weight < node.getShortestDistanceToSource()) {
            node.setShortestDistanceToSource(dis + weight);

            final ArrayList<GraphNode> shortestPath = new ArrayList<>(src.getShortestPathToSource());
            shortestPath.add(src);
            node.setShortestPathToSource(shortestPath);
        }
    }

    private static GraphNode closestNode(Set<GraphNode> unsettled) {
        GraphNode closest = null;
        int dis = Integer.MAX_VALUE;

        for(GraphNode node : unsettled) {
            final int nodeDis = node.getShortestDistanceToSource();
            if (nodeDis < dis) {
                dis = nodeDis;
                closest = node;
            }
        }

        return closest;
    }
}
