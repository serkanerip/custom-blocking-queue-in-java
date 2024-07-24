import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class QueueBenchmark {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: QueueBenchmark <threadCount> <itemCount> <queueType> (java|custom)");
            System.exit(1);
        }
        var producerCount = Integer.parseInt(args[0]);
        var itemCount = Integer.parseInt(args[1]);
        BlockingQueue<Integer> queue = args[2].equals("java") ? new ArrayBlockingQueue<>(itemCount)
                : new CustomBlockingQueue<>(itemCount);
        System.out.println("Using queue: " + queue.getClass().getName());
        var warmupStartMs = System.currentTimeMillis();
        for (int i = 0; i < itemCount; i++) {
            queue.offer(i);
        }
        System.out.println("Warmup done... 1 thread produced " + itemCount + " items in "
                + (System.currentTimeMillis() - warmupStartMs) + "ms");
        queue.clear();
        var producerThreads = new ArrayList<Thread>();
        for (int i = 0; i < producerCount; i++) {
            var producerThread = Thread.ofPlatform()
                    .unstarted(() -> {
                        for (int j = 0; j < itemCount / producerCount; j++) {
                            queue.offer(j);
                        }
                    });
            producerThreads.add(producerThread);
        }
        for (var producerThread : producerThreads) {
            producerThread.start();
        }
        for (var producerThread : producerThreads) {
            try {
                producerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
