package evaluation.optimisers;

import evaluation.GameEvaluator;
import evodef.*;
import org.jetbrains.annotations.NotNull;
import utilities.Pair;

import java.util.Arrays;
import java.util.Random;
import java.util.TreeMap;

public class MapElites implements EvoAlg {
    EliteMap model;
    int nRandomSolutions = 5;
    double mutRate = 0.3;
    Random random;

    public MapElites(SearchSpace searchSpace, TreeMap<String, Object[]> behaviourDescriptors) {
        model = new EliteMap(searchSpace, behaviourDescriptors);
        random = new Random(); // TODO seed?
    }

    @NotNull
    @Override
    public LandscapeModel getModel() {
        return model;
    }

    @NotNull
    @Override
    public double[] runTrial(@NotNull MultiSolutionEvaluator multiSolutionEvaluator, int nEvals) {
        // Ignored for this implementation
        return new double[0];
    }

    @NotNull
    @Override
    public double[] runTrial(@NotNull SolutionEvaluator solutionEvaluator, int nEvals) {
        for (int i = 0; i < nEvals; i++) {
            int[] point;
            if (i < nRandomSolutions) {
                point = SearchSpaceUtil.randomPoint(model.searchSpace);  // Random point
            } else {
                // Random selection from map
                double[] p = model.getRandomElite();
                point = new int[p.length];

                for (int j = 0; j < p.length; j++) {
                    if (random.nextDouble() < mutRate) {
                        // Random variation
                        point[j] = random.nextInt(model.searchSpace.nValues(j));
                    } else {
                        // Convert to integer for compatibility with other functions in interfaces
                        point[j] = (int) p[j];
                    }
                }
            }
            try {
                Pair<Double, TreeMap<String, Object>> stats = ((GameEvaluator) solutionEvaluator).evaluateWithStats(point);
                double performance = stats.a;
                TreeMap<String, Object> behaviours = stats.b;
                model.addPoint(point, performance, behaviours);
            } catch (Exception ignored) {
//                System.out.println("Invalid configuration: " + Arrays.toString(point));
            }
        }
        return model.getBestOfSampled();
    }

    public void logResults() {
        // Print elite map
        System.out.println("\nElite Map\n\n");
        for (int i = 0; i < model.solutionMap.length; i++) {
            if (model.solutionMap[i] != null) {
                TreeMap<String, Object> behaviourDescriptors = model.findBehavioursByIndex(i);
                double performance = model.valueMap[i];
                System.out.println(Arrays.toString(model.solutionMap[i]) + " ; " + behaviourDescriptors + " ; " + performance);
            }
        }
    }

    /**
     * Elite map using the LandscapeModel interface for compatibility
     */
    static class EliteMap implements LandscapeModel {
        SearchSpace searchSpace;
        double[][] solutionMap;
        double[] valueMap;
        TreeMap<String, Object[]> behaviourDescriptors;

        public EliteMap(SearchSpace searchSpace, TreeMap<String, Object[]> behaviourDescriptors) {
            this.searchSpace = searchSpace;

            int mapSize = 1;
            for (Object[] b: behaviourDescriptors.values()) {
                mapSize *= b.length;
            }
            this.solutionMap = new double[mapSize][];
            this.valueMap = new double[mapSize];
            this.behaviourDescriptors = behaviourDescriptors;
        }

        @NotNull
        @Override
        public double[] getBestOfSampled() {
            int bestIdx = -1;
            double bestValue = -Double.MIN_VALUE;
            for (int i = 0; i < valueMap.length; i++) {
                if (valueMap[i] > bestValue) {
                    bestValue = valueMap[i];
                    bestIdx = i;
                }
            }
            return solutionMap[bestIdx];
        }

        @NotNull
        @Override
        public double[] getBestSolution() {
            return getBestOfSampled();
        }

        @NotNull
        @Override
        public SearchSpace getSearchSpace() {
            return searchSpace;
        }

        @Override
        public void addPoint(@NotNull double[] doubles, double v) {
            // We don't use this one
        }

        public void addPoint(int[] ints, double performance, TreeMap<String, Object> behaviours) {
            int idx = findIndex(behaviours);
            if (solutionMap[idx] == null || valueMap[idx] < performance) {
                solutionMap[idx] = Arrays.stream(ints).asDoubleStream().toArray();
                valueMap[idx] = performance;
            }
        }

        public double[] getRandomElite() {
            // TODO: return a random solution from the solution map (which is not null!)
            return new double[0];
        }

        private int findIndex(TreeMap<String, Object> behaviours) {
            int[] indexes = new int[behaviours.size()];
            int[] lengths = new int[behaviours.size()];
            int count = 0;
            for (String s: behaviourDescriptors.descendingKeySet()) {
                indexes[count] = findIndex(s, behaviours.get(s));
                lengths[count] = behaviourDescriptors.get(s).length;
                count++;
            }
            int idx = indexes[0];
            for (int i = 1; i < indexes.length; i++) {
                int mult = indexes[i];
                for (int j = 0; j < i; j++) {
                    mult *= lengths[j];
                }
                idx += mult;
            }
            return idx;
        }

        private int findIndex(String behaviour, Object value) {
            Object[] allValues = behaviourDescriptors.get(behaviour);
            if (value instanceof Number) {
                for (int i = 0; i < allValues.length; i++) {
                    int val1 = ((Number)value).intValue();
                    int val2 = ((Number)allValues[i]).intValue();
                    if (val1 <= val2) return i;
                }
            } else {
                for (int i = 0; i < allValues.length; i++) {
                    if (allValues[i].equals(value)) return i;
                }
            }
            return allValues.length-1;
        }

        public TreeMap<String, Object> findBehavioursByIndex(int idx) {
            int nBehaviours = behaviourDescriptors.size();

            int[] indexes = new int[nBehaviours];
            int[] lengths = new int[nBehaviours];
            int count = 0;
            for (String s: behaviourDescriptors.descendingKeySet()) {
                lengths[count] = behaviourDescriptors.get(s).length;
                count++;
            }

            for (int i = 0; i < nBehaviours; i++) {
                indexes[i] = idx % lengths[i];
                idx = idx / lengths[i];
            }

            TreeMap<String, Object> retValue = new TreeMap<>();
            count = 0;
            for (String s: behaviourDescriptors.descendingKeySet()) {
                retValue.put(s, behaviourDescriptors.get(s)[indexes[count]]);
                count++;
            }
            return retValue;
        }

        @Override
        public double getExplorationEstimate(@NotNull double[] doubles) {
            // Ignored, maybe add later?
            return 0;
        }

        @Override
        public double getMeanEstimate(@NotNull double[] doubles) {
            // Ignored, maybe add later?
            return 0;
        }

        @NotNull
        @Override
        public LandscapeModel reset() {
            return new EliteMap(searchSpace, behaviourDescriptors);
        }

        @NotNull
        @Override
        public LandscapeModel setEpsilon(double v) {
            return this;
        }
    }
}