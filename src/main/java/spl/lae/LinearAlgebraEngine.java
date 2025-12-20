package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        // TODO: create executor with given thread count
        executor = new TiredExecutor(numThreads);
    }

    public ComputationNode run(ComputationNode computationRoot) {
        // TODO: resolve computation tree step by step until final matrix is produced
        ComputationNode compNode = computationRoot.findResolvable();
        compNode.associativeNesting();
        compNode = computationRoot.findResolvable();
        loadAndCompute(compNode);
        
        //TO BE CONTINUED.........
        return compNode;
    }

    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
        ComputationNodeType nodeType = node.getNodeType();
        List<ComputationNode> listNode = node.getChildren();
        if(nodeType.equals(ComputationNodeType.ADD)){
            leftMatrix.loadRowMajor(listNode.getFirst().getMatrix());
            rightMatrix.loadRowMajor(listNode.getLast().getMatrix());
            createAddTasks();
        }
        if(nodeType.equals(ComputationNodeType.MULTIPLY)){
            leftMatrix.loadRowMajor(listNode.getFirst().getMatrix());
            rightMatrix.loadColumnMajor(listNode.getLast().getMatrix());
            createMultiplyTasks();
        }
        if(nodeType.equals(ComputationNodeType.TRANSPOSE)){
            createTransposeTasks();
        }
        if(nodeType.equals(ComputationNodeType.NEGATE)){
            createNegateTasks();
        }
    }

    public List<Runnable> createAddTasks() {
        // TODO: return tasks that perform row-wise addition
        return null;
    }

    public List<Runnable> createMultiplyTasks() {
        // TODO: return tasks that perform row × matrix multiplication
        return null;
    }

    public List<Runnable> createNegateTasks() {
        // TODO: return tasks that negate rows
        return null;
    }

    public List<Runnable> createTransposeTasks() {
        // TODO: return tasks that transpose rows
        return null;
    }

    public String getWorkerReport() {
        // TODO: return summary of worker activity
        return executor.getWorkerReport();
    }
}
