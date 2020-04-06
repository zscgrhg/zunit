package moc.etz.zunit.instrument;

import com.alibaba.ttl.threadpool.TtlForkJoinPoolHelper;
import moc.etz.zunit.util.LoggerUtil;
import org.jboss.byteman.rule.Rule;
import shade.zunit.ch.qos.logback.classic.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

public class TtlHelper {
    private static final Logger LOGGER = LoggerUtil.of(TtlHelper.class);
    public static final ThreadLocal<Map<String, Integer>> BARRIER = new ThreadLocal<>();


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

    public boolean cleanBarrier(Long mid) {
        MethodNames names = MethodNames.METHOD_NAMES_MAP.get(mid);
        String barrier = names.genericSymbol;
        Map<String, Integer> barrierMap = BARRIER.get();
        if (barrierMap == null) {
            return false;
        }
        Integer remove = barrierMap.remove(barrier);

        if (barrierMap.isEmpty()) {
            BARRIER.remove();

        }
        boolean success = (remove != null);
        LOGGER.debug("cleanBarrier " + mid + ": " + success);
        return success;
    }



}
