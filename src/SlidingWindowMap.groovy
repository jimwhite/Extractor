// http://beust.com/weblog/2012/09/02/coding-challenge-a-sliding-window-map/

import java.util.concurrent.PriorityBlockingQueue

public class SlidingWindowMap
{
    def queue
    def windowWidthMillis

    public SlidingWindowMap(Set<String> keys, int maxCount, long periodMs)
    {
        queue = new PriorityBlockingQueue(keys.size() * maxCount, { a, b -> a[0].peek() <=> b[0].peek() } as Comparator)

        def random = new Random()

        // Seed the timestamps with random values that are earlier than the beginning of the window could be.
        // Since System.currentTimeMillis() is a positive long we can use negative millions safely.
        keys.each { key -> maxCount.times { queue.add([-random.nextInt(2**24), key]) } }

        windowWidthMillis = periodMs
    }

    /**
     * @return a key that has been used less than `maxCount` times during the
     * past `periodMs` milliseconds or null if no such key exists.
     */
    public String getNextKey()
    {
        // Get the key that was used first.
        def keyrec = queue.poll()

        if (keyrec) {
            // Can we use it again?
            if (keyrec[0] < System.currentTimeMillis() - windowWidthMillis) {
                def key = keyrec[1]

                queue.add ([System.currentTimeMillis(), key])

                return key
            }

            // No.  Just put it back.
            queue.add(keyrec)
        }

        return null
    }
}
