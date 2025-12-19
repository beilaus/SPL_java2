package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        workers = new TiredThread[numThreads];
    }

    public void submit(Runnable task) {
        // TODO
        TiredThread curWorker= idleMinHeap.poll();
        if(curWorker!=null){
            curWorker.newTask(task);
            inFlight.incrementAndGet();
        }
        while(curWorker.isBusy()){
              curWorker.wait();  
        }

    }

    public void submitAll(Iterable<Runnable> tasks) {
        // TODO: submit tasks one by one and wait until all finish
    }

    public void shutdown() throws InterruptedException {
        // TODO
        for(int i=0;i<workers.length();i++){
            try{
                TiredThread cur=workers[i];
                cur.shutdown();   
            }
            catch(InterruptedException e){
                  cur.shutdown();  
            }
        }
    }

    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
        return null;
    }

    
}
