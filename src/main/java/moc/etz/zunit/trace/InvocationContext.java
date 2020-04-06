package moc.etz.zunit.trace;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import moc.etz.zunit.builder.SpecWriter;
import moc.etz.zunit.config.TraceConfig;
import moc.etz.zunit.instrument.MethodNames;
import moc.etz.zunit.parse.RefsInfo;
import moc.etz.zunit.util.LoggerUtil;
import shade.zunit.ch.qos.logback.classic.Logger;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Data
public class InvocationContext {
    private static final Logger LOGGER = LoggerUtil.of(InvocationContext.class);
    private static final SpecWriter SPEC_WRITER = TraceConfig.INSTANCE.getSpecWriter();

    public final static TransmittableThreadLocal<Invocation> PREVIOUS = new TransmittableThreadLocal<>();
    public static final AtomicLong CXT_INCR = new AtomicLong(1);
    public final static ThreadLocal<Invocation> STAGED = new ThreadLocal<>();
    public final static ThreadLocal<InvocationContext> CONTEXT = new ThreadLocal<>();
    public final static ThreadLocal<Stack<Invocation>> STACK_THREAD_LOCAL = new ThreadLocal<>();

    public final AtomicInteger ENTRY_COUNTER = new AtomicInteger(Integer.MIN_VALUE);
    public final AtomicInteger EXIT_COUNTER = new AtomicInteger(Integer.MAX_VALUE);

    public final Long id = CXT_INCR.getAndIncrement();
    @JsonIgnore
    public final Map<Long, Invocation> map = new ConcurrentHashMap<>();
    final TraceWriter traceWriter = new TraceWriterImpl();

    public static InvocationContext getCurrent(boolean create) {
        InvocationContext current = CONTEXT.get();
        if (create && current == null) {
            CONTEXT.set(new InvocationContext());
        }
        return CONTEXT.get();
    }

    private static boolean checkTheadId(Invocation invocation) {
        return invocation.threadId == Thread.currentThread().getId();
    }

    public boolean canPush() {
        long prev = ENTRY_COUNTER.get();
        int length = Thread.currentThread().getStackTrace().length;
        boolean success = false;
        if (length > prev) {
            ENTRY_COUNTER.set(length);
            EXIT_COUNTER.set(Integer.MAX_VALUE);
            success = true;
        }
        LOGGER.debug("canPush :" + success);
        return success;
    }

    public boolean canPop() {
        long prev = EXIT_COUNTER.get();
        int length = Thread.currentThread().getStackTrace().length;
        boolean success = false;
        if (length < prev) {
            EXIT_COUNTER.set(length);
            ENTRY_COUNTER.set(Integer.MIN_VALUE);
            success = true;
        }
        LOGGER.debug("canPop :" + success);
        return success;
    }

    public void push(Invocation invocation, Object[] args) {
        Stack<Invocation> stack = STACK_THREAD_LOCAL.get();
        if (stack == null) {
            stack = new Stack<>();
            STACK_THREAD_LOCAL.set(stack);
        }
        Invocation prevTTL = PREVIOUS.get();
        if (prevTTL != null && !checkTheadId(prevTTL) && stack.isEmpty()) {
            //in spawned thread
            LOGGER.debug("stage:" + prevTTL);
            STAGED.set(prevTTL);
        }


        Invocation prev = PREVIOUS.get();

        if (prev != null) {
            invocation.parent = prev;
            if (prev.thisObjectSource == invocation.thisObjectSource) {
                assert prev.threadId == Thread.currentThread().getId();
                int andIncrement = prev.stackCounter.getAndIncrement();
                LOGGER.debug("stackCounter ++ :" + andIncrement);
                return;
            }
            RefsInfo refsInfo = prev.refs.get(invocation.thisObjectSource);
            invocation.refsInfo = refsInfo;
            invocation.declaredClass = refsInfo.declaredType;
            prev.getChildren().add(invocation);
        }
        stack.push(invocation);
        PREVIOUS.set(invocation);
        map.put(invocation.id, invocation);
        MethodNames names = MethodNames.METHOD_NAMES_MAP.get(invocation.mid);
        ParamModel p = new ParamModel();
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg != null) {
                Object argSource = Invocation.resolveSource(arg);
                RefsInfo argRefInfo = invocation.refs.get(argSource);
                if (argRefInfo != null) {
                    args[i] = argRefInfo;
                    invocation.argsNames.put(i, argRefInfo);
                }
            }
        }
        p.args = args;
        p.argsGenericType = invocation.genericArgs;
        p.argsType = ParamModel.valuesTypeOf(args);
        invocation.argsType = p.argsType;
        p.invocationId = invocation.id;
        p.name = ParamModel.INPUTS;
        traceWriter.write(p);
    }

    public void pop(Object[] args, Object returnValue, Throwable exception) {
        Stack<Invocation> stack = STACK_THREAD_LOCAL.get();
        Invocation last = stack.lastElement();
        int stackCounter = last.stackCounter.decrementAndGet();
        if (stackCounter > 0) {
            assert last.threadId == Thread.currentThread().getId();
            LOGGER.debug("stackCounter -- :" + stackCounter);
            return;
        }
        Invocation pop = stack.pop();
        MethodNames names = MethodNames.METHOD_NAMES_MAP.get(pop.mid);
        ParamModel p = new ParamModel();
        p.invocationId = pop.id;
        p.args = args;
        p.argsType = ParamModel.valuesTypeOf(args);
        p.argsGenericType = pop.genericArgs;
        p.returned = returnValue;
        p.returnedType = ParamModel.typeOf(p.returned);

        p.returnedGenericType = pop.genericReturned;
        p.thrown = exception;
        if (exception != null) {
            p.exception = exception.getClass().getName();
        }
        p.name = ParamModel.OUTPUTS;
        traceWriter.write(p);
        pop.finished = true;

        if (stack.isEmpty()) {
            traceWriter.write(this);
            SPEC_WRITER.write(pop.id);

            CONTEXT.remove();
            PREVIOUS.remove();
            Invocation prevTTL = STAGED.get();
            if (prevTTL != null) {
                STAGED.remove();
                LOGGER.debug("unstaged:" + prevTTL);
                PREVIOUS.set(prevTTL);
            }
        } else {
            PREVIOUS.set(stack.lastElement());
        }
    }

    public List<Invocation> getNodes() {
        List<Invocation> root = map.values().stream().filter(Invocation::isSubject).collect(Collectors.toList());
        return root;
    }


}
