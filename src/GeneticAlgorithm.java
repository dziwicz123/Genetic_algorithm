import org.knowm.xchart.*;
import java.util.*;
import java.util.stream.Collectors;

class Chromosome implements Comparable<Chromosome> {
    double x;
    double y;
    double fitness;
    private static final double MUTATION = 0.01;
    private static final int SIZE = 2;
    private static final int END = 1;
    public Chromosome(double x, double y) {
        this.x = x;
        this.y = y;
        this.fitness = calculateFitness();
    }

    private double calculateFitness() {
        return (1 - this.x) * (1 - this.x) + 100 * (this.y - this.x * this.x) * (this.y - this.x * this.x);
    }

    @Override
    public int compareTo(Chromosome o) {
        return Double.compare(this.fitness, o.fitness);
    }

    void mutate(double mutationRate) {
        if (Math.random() < mutationRate) {
            this.x += (Math.random() * SIZE - END) * MUTATION;
            this.y += (Math.random() * SIZE - END) * MUTATION;
            this.fitness = calculateFitness();
        }
    }
}

public class GeneticAlgorithm {
    private int populationSize = 100;
    private double crossoverRate = 0.8;
    private double mutationRate = 0.01;
    private int maxGenerations = 100;
    private static final int HEIGHT = 600;
    private static final int WIDTH = 800;
    List<Chromosome> population = new ArrayList<>();
    List<Double> averageFitnessHistory = new ArrayList<>();

    public static void main(String[] args) {
        GeneticAlgorithm ga = new GeneticAlgorithm();
        ga.initialize();
        ga.run();
        ga.plotResults();
    }

    void initialize() {
        // Inicjalizacja populacji
        for (int i = 0; i < populationSize; i++) {
            double x = Math.random();
            double y = Math.random();
            population.add(new Chromosome(x, y));
        }
    }

    void run() {
        Chromosome bestChromosome = null;

        for (int generation = 0; generation < maxGenerations; generation++) {
            List<Chromosome> newPopulation = new ArrayList<>();

            while (newPopulation.size() < populationSize) {
                Chromosome parent1 = selectParent();
                Chromosome parent2 = selectParent();

                if (Math.random() < crossoverRate) {
                    newPopulation.addAll(crossover(parent1, parent2));
                } else {
                    newPopulation.add(parent1);
                    newPopulation.add(parent2);
                }
            }
            for (Chromosome chromosome : newPopulation) {
                chromosome.mutate(mutationRate);
            }

            population = newPopulation.stream().limit(populationSize).collect(Collectors.toList());
            Chromosome best = Collections.min(population);
            double averageFitness = population.stream().mapToDouble(chromosome -> chromosome.fitness).average().orElse(0.0);
            averageFitnessHistory.add(averageFitness);

            if (bestChromosome == null || best.fitness < bestChromosome.fitness) {
                bestChromosome = best;
            }

            System.out.println("Generation " + generation + ": x = " + best.x + ", y = " + best.y);
        }

        System.out.println("Best Chromosome: x = " + bestChromosome.x + ", y = " + bestChromosome.y);
    }

    Chromosome selectParent() {
        Chromosome candidate1 = population.get((int) (Math.random() * populationSize));
        Chromosome candidate2 = population.get((int) (Math.random() * populationSize));
        return (candidate1.fitness < candidate2.fitness) ? candidate1 : candidate2;
    }

    List<Chromosome> crossover(Chromosome parent1, Chromosome parent2) {
        double alpha = Math.random();
        double newX = alpha * parent1.x + (1 - alpha) * parent2.x;
        double newY = alpha * parent1.y + (1 - alpha) * parent2.y;
        Chromosome child1 = new Chromosome(newX, newY);
        Chromosome child2 = new Chromosome((1 - alpha) * parent1.x + alpha * parent2.x, (1 - alpha) * parent1.y + alpha * parent2.y);
        return Arrays.asList(child1, child2);
    }

    void plotResults() {


        double[] generations = new double[maxGenerations];
        for (int i = 0; i < maxGenerations; i++) {
            generations[i] = i;
        }

        double[] averageFitnesses = averageFitnessHistory.stream().mapToDouble(Double::doubleValue).toArray();
        XYChart chart = new XYChartBuilder().width(WIDTH).height(HEIGHT).title("Genetic Algorithm Optimization").xAxisTitle("Generation").yAxisTitle("Best Fitness").build();
        chart.addSeries("Average Fitness", generations, averageFitnesses);
        new SwingWrapper(chart).displayChart();
    }
}
