package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        workers = new TiredThread[numThreads];
        for(int i = 0; i < workers.length; i++){
            TiredThread thread = new TiredThread(i, Math.random()+0.5);
            workers[i] = thread;
            idleMinHeap.put(thread);
            thread.start();
        }
    }

    public void submit(Runnable task) {
        // TODO
        synchronized(TiredExecutor.class){
            updateWorkers();
            while(idleMinHeap.isEmpty()){
                try{
                    TiredExecutor.class.wait();
                    updateWorkers();
                }
                catch(InterruptedException e){
                    Thread.currentThread().interrupt(); //TiredExecutor.interrupt() shouldn't happen.
                    return;
                }
            }
        }
        TiredThread work = idleMinHeap.poll();
        inFlight.incrementAndGet();
        work.newTask(task);
    }

    public void submitAll(Iterable<Runnable> tasks) {
        // TODO: submit tasks one by one and wait until all finish
        Iterator<Runnable> iter = tasks.iterator();
        while(iter.hasNext()){
            this.submit(iter.next());
        }
        while(inFlight.get() > 0){
            synchronized(TiredExecutor.class){
                updateWorkers();
                if(inFlight.get() == 0){
                    break;
                }
                try{
                    TiredExecutor.class.wait();
                    updateWorkers();
                }
                catch(InterruptedException e){
                    Thread.currentThread().interrupt(); //TiredExecutor.interrupt() shouldn't happen.
                    return;
                }
            }
        }
    }

    public void shutdown() throws InterruptedException {
        // TODO
        for(int i=0;i<workers.length;i++){
            TiredThread cur=workers[i];
            cur.shutdown();                  
        }
        for(int i=0;i<workers.length;i++){
            workers[i].join();
        }
    }

    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
        String output = "";
        for(int i = 0; i < workers.length; i++){
            TiredThread cur = workers[i];
            output += "Worker " + i + ":\n";
            output += "\tFatigue: " + cur.getFatigue() + "\n";
            output += "\tTime Used (ns): " + cur.getTimeUsed() + "\n";
            output += "\tTime Idle (ns): " + cur.getTimeIdle() + "\n";
            output += "\tIs Busy: " + cur.isBusy() + "\n";
        }
        return output;
    }

    private void updateWorkers(){
        for(int i = 0; i < workers.length; i++){ //Handles fetching non-busy workers
            if(!workers[i].isBusy() && !idleMinHeap.contains(workers[i])){
                idleMinHeap.put(workers[i]);
                inFlight.decrementAndGet();
            }
        }
    }
}
