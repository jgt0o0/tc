package cn.tsinghua.tc.cache;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ji on 16-6-5.
 */
public class FutureCache {

    private static final Set<String> futureCache = new HashSet<String>();

    public static void addFuture(String future) {
        futureCache.add(future);
    }

    public static Set<String> getFutureCache() {
        return futureCache;
    }
}
