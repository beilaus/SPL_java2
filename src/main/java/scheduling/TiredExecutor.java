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
        if(numThreads <= 0){
            throw new IllegalArgumentException("[TiredExecutor]: Number of threads must be positive!");
        }
        workers = new TiredThread[numThreads];
        for(int i = 0; i < workers.length; i++){
            TiredThread thread = new TiredThread(i, Math.random()+0.5); // range [0.5,1.5)
            workers[i] = thread;
            idleMinHeap.put(thread);
            thread.start(); //Kickstarting each thread
        }
    }

    public void submit(Runnable task) {
        // TODO
        // synchronized(TiredExecutor.class){ //Locks onto the class to connect with TiredThread
        //     updateWorkers();
        //     while(idleMinHeap.isEmpty()){
        //         try{
        //             TiredExecutor.class.wait();
        //             updateWorkers();
        //         }
        //         catch(InterruptedException e){
        //             Thread.currentThread().interrupt(); //TiredExecutor.interrupt() shouldn't happen.
        //             return;
        //         }
        //     }
        // }
        // TiredThread work = idleMinHeap.poll(); //Taking the least tired worker
        // inFlight.incrementAndGet();
        // work.newTask(task);
        checkCrash();
        try{
            TiredThread worker = idleMinHeap.take();
            Runnable wrappedTask = () -> { //wraps the task
                try{
                    task.run();
                    inFlight.decrementAndGet();
                    idleMinHeap.put(worker);
                }
                finally{
                    synchronized(this){
                        this.notifyAll();
                    }
                }
            };
            inFlight.incrementAndGet();
            worker.newTask(wrappedTask);
        }
        catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    public void submitAll(Iterable<Runnable> tasks){
        // TODO: submit tasks one by one and wait until all finish
        Iterator<Runnable> iter = tasks.iterator();
        while(iter.hasNext()){
            this.submit(iter.next());
        }
        synchronized(this){
            while(inFlight.get() > 0){
                try{
                    wait();
                    checkCrash();   
                }
                catch (Exception e) {
                    Thread.currentThread().interrupt();
                }    
            }
        }



        // while(inFlight.get() > 0){
        //     synchronized(TiredExecutor.class){
        //         updateWorkers();
        //         if(inFlight.get() == 0){
        //             break;
        //         }
        //         try{
        //             TiredExecutor.class.wait();
        //             updateWorkers();
        //         }
        //         catch(InterruptedException e){
        //             Thread.currentThread().interrupt(); //TiredExecutor.interrupt() shouldn't happen.
        //             return;
        //         }
        //     }
        // }
    }

    public void shutdown() throws InterruptedException {
        // TODO
        for(int i = 0; i <workers.length;i++){ //Shuts down all workers one after the other
            TiredThread cur=workers[i];
            cur.shutdown();                  
        }
        for(int i = 0; i < workers.length;i++){
            workers[i].join(); //Waits for all workers to terminate
        }
    }

    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
        String output = "";
        for(int i = 0; i < workers.length; i++){ //All readable statistics for each worker
            TiredThread cur = workers[i];
            output += "Worker " + i + ":\n";
            output += "\tFatigue: " + cur.getFatigue() + "\n";
            output += "\tTime Used (ns): " + cur.getTimeUsed() + "\n";
            output += "\tTime Idle (ns): " + cur.getTimeIdle() + "\n";
            output += "\tIs Busy: " + cur.isBusy() + "\n";
        }
        return output;
    }

    private void checkCrash(){
        for(int i = 0; i < workers.length; i++){   //Handles when a thread crashes.
            if(workers[i].getState().equals(Thread.State.TERMINATED)){
                throw new IllegalThreadStateException("[checkCrash]: A thread has crashed and been terminated."); 
            }
        }
    }
}
