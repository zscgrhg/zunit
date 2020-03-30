package moc.etz.zunit.instrument;

import com.alibaba.ttl.threadpool.TtlForkJoinPoolHelper;
import org.jboss.byteman.rule.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

import static com.alibaba.ttl.TransmittableThreadLocal.Transmitter.*;

public class TtlHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(TtlHelper.class);
    public static final ThreadLocal<Map<String, Integer>> BARRIER = new ThreadLocal<>();
    public static final ThreadLocal<Object> TTL_BACKUP = new ThreadLocal<>();
    public static final Map<Object, Object> CAPTURED = new ConcurrentHashMap<>();

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

    public void backupTtl() {
        final Object backup = clear();
        TTL_BACKUP.set(backup);
    }

    public void restoreTtl() {
        Object backup = TTL_BACKUP.get();
        restore(backup);
    }

    public void saveCaptured(Object task) {
        CAPTURED.put(task, capture());
    }

    public void setCaptured(Object task) {
        Object captured = CAPTURED.get(task);
        Object backup = replay(captured);
        TTL_BACKUP.set(backup);
    }

    public void cleanCaptured(Object task) {
        Object backup = TTL_BACKUP.get();
        restore(backup);
        CAPTURED.remove(task);
    }

}
