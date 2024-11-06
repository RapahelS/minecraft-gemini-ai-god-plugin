package net.bigyous.gptgodmc.utils;

import net.bigyous.gptgodmc.interfaces.SimpFunction;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Task<T> implements Runnable {
    private SimpFunction<T> task;
    private T object;

    public Task(SimpFunction<T> task, T object) {
        this.task = task;
        this.object = object;
    }

    public void run() {
        this.task.run(object);
    }
}

public class TaskQueue<T> {
    private SimpFunction<T> task;
    private ExecutorService pool;

    public TaskQueue(SimpFunction<T> task) {
        this.task = task;
        this.pool = Executors.newCachedThreadPool();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void insert(T object) {
        pool.execute(new Task(task, object));
    }

}
