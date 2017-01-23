package au.edu.qut.processmining.miners.heuristic.ui;

/**
 * Created by Adriano on 29/02/2016.
 */
public class HMPlusUIResult {

    public enum StructuringTime {NONE, POST, PRE}

    public static final double DEPENDENCY_THRESHOLD = 0.95;
    public static final double POSITIVE_OBSERVATIONS = 0.01;
    public static final double RELATIVE2BEST_THRESHOLD = 0.05;

    private double dependencyThreshold;
    private double positiveObservations;
    private double relative2BestThreshold;

    private boolean replaceIORs;

    private StructuringTime structuringTime;

    public HMPlusUIResult() {
        dependencyThreshold = DEPENDENCY_THRESHOLD;
        positiveObservations = POSITIVE_OBSERVATIONS;
        relative2BestThreshold = RELATIVE2BEST_THRESHOLD;
        structuringTime = StructuringTime.NONE;
    }

    public StructuringTime getStructuringTime() { return structuringTime; }
    public void setStructuringTime(StructuringTime structuringTime) { this.structuringTime = structuringTime; }

    public boolean isReplaceIORs() { return replaceIORs; }
    public void setReplaceIORs(boolean replaceIORs) { this.replaceIORs = replaceIORs; }

    public void disablePositiveObservations() { positiveObservations = 0.0; }
    public void disableRelative2BestThreshold() { relative2BestThreshold = 1.0; }

    public double getDependencyThreshold() {
        return dependencyThreshold;
    }

    public void setDependencyThreshold(double dependencyThreshold) {
        this.dependencyThreshold = dependencyThreshold;
    }

    public double getPositiveObservations() {
        return positiveObservations;
    }

    public void setPositiveObservations(double positiveObservations) {
        this.positiveObservations = positiveObservations;
    }

    public double getRelative2BestThreshold() {
        return relative2BestThreshold;
    }

    public void setRelative2BestThreshold(double relative2BestThreshold) {
        this.relative2BestThreshold = relative2BestThreshold;
    }
}