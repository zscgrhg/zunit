package moc.etz.zunit.instrument;

import com.alibaba.ttl.threadpool.TtlForkJoinPoolHelper;
import org.jboss.byteman.rule.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

public class TtlHelper {
    public static final ThreadLocal<Map<String, Integer>> BARRIER = new ThreadLocal<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(TtlHelper.class);
    Rule rule;

    public TtlHelper(Rule rule) {
        this.rule = rule;
    }

    public ForkJoinWorkerThread getFJPWrapper(ForkJoinPool.ForkJoinWorkerThreadFactory threadFactory, ForkJoinPool pool) {
        ForkJoinWorkerThread forkJoinWorkerThread = TtlForkJoinPoolHelper.getDisableInheritableForkJoinWorkerThreadFactory(threadFactory).newThread(pool);
        return forkJoinWorkerThread;
    }

    public boolean setBarrier(Long mid) {
        MethodNames names = MethodNames.METHOD_NAMES_MAP.get(mid);
        String barrier = names.genericSymbol;
        Map<String, Integer> barrierMap = BARRIER.get();
        if (barrierMap == null) {
            barrierMap = new HashMap<>();
            BARRIER.set(barrierMap);
        }
        boolean success = false;
        Integer exist = barrierMap.get(barrier);
        if (exist == null) {
            barrierMap.put(barrier, 1);
            success = true;
        }
        LOGGER.debug("setBarrier " + mid + ":" + success);
        return success;
    }

    public void cleanBarrier(Long mid) {
        MethodNames names = MethodNames.METHOD_NAMES_MAP.get(mid);
        String barrier = names.genericSymbol;
        Map<String, Integer> barrierMap = BARRIER.get();
        if (barrierMap == null) {
            return;
        }
        barrierMap.remove(barrier);
        if (barrierMap.isEmpty()) {
            BARRIER.remove();
            LOGGER.debug("cleanBarrier " + mid);
        }
    }


}
