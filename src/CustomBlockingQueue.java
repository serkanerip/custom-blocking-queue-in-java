import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CustomBlockingQueue<T> implements BlockingQueue<T> {

    private final int capacity;
    private final Lock lock = new ReentrantLock(true);
    private final Condition condition = lock.newCondition();
    private final Condition capacityAvailableCond = lock.newCondition();
    private final Queue<T> queue;

    public CustomBlockingQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new ArrayDeque<>(capacity);
    }

    public void clear() {
        lock.lock();
        try {
            queue.clear();
            capacityAvailableCond.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public boolean offer(T item, long time, TimeUnit timeUnit) {
        return offer0(item, time, timeUnit);
    }

    public boolean offer(T item) {
        while (!offer0(item, Long.MAX_VALUE, TimeUnit.SECONDS)) {}
        return true;
    }

    /**
     * Polls the queue with a timeout of Long.MAX_VALUE seconds.
     * @return the item from the queue
     */
    public T poll() {
        T item;
        while ((item = poll(Long.MAX_VALUE, TimeUnit.SECONDS)) == null) {}
        return item;
    }

    /**
     * Polls the queue with a timeout.
     * @param time the time to wait
     * @param timeUnit the time unit
     * @return the head of this queue, or {@code null} if the specified waiting time elapses
     */
    public T poll(long time, TimeUnit timeUnit) {
        return poll0(time, timeUnit);
    }

    private boolean offer0(T item, long time, TimeUnit timeUnit) {
        // a primitive implementation of busy waiting
        // to avoid high number of context switches
        // var gotLock = false;
        // for (int i = 0; i < 10; i++) {
        //     if (lock.tryLock()) {
        //         gotLock = true;
        //         break;
        //     }
        // }
        // if (!gotLock) lock.lock();
        lock.lock(); // w/o busy wait
        try {
            while(!hasCapacity()) {
                if (!capacityAvailableCond.await(time, timeUnit)) {
                    return false;
                }
            }
            queue.add(item);
            condition.signal();
            return true;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    private boolean hasCapacity() {
        return capacity > queue.size();
    }

    private T poll0(long time, TimeUnit timeUnit) {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                if (!condition.await(time, timeUnit)) {
                    return null;
                }
            }
            capacityAvailableCond.signal();
            return this.queue.poll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return queue.isEmpty();
        } finally {
            lock.unlock();
        }
    }


    // The following methods are not implemented for the sake of simplicity

    @Override
    public T remove() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'remove'");
    }

    @Override
    public T element() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'element'");
    }

    @Override
    public T peek() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'peek'");
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'size'");
    }

    @Override
    public Iterator<T> iterator() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'iterator'");
    }

    @Override
    public Object[] toArray() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toArray'");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toArray'");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'containsAll'");
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addAll'");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeAll'");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'retainAll'");
    }

    @Override
    public boolean add(T e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'add'");
    }

    @Override
    public void put(T e) throws InterruptedException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'put'");
    }

    @Override
    public T take() throws InterruptedException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'take'");
    }

    @Override
    public int remainingCapacity() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'remainingCapacity'");
    }

    @Override
    public boolean remove(Object o) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'remove'");
    }

    @Override
    public boolean contains(Object o) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'contains'");
    }

    @Override
    public int drainTo(Collection<? super T> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drainTo'");
    }

    @Override
    public int drainTo(Collection<? super T> c, int maxElements) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drainTo'");
    }

}
