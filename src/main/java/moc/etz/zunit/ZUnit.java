package moc.etz.zunit;

import com.fasterxml.jackson.databind.JsonNode;
import moc.etz.zunit.builder.GroovyLine;
import moc.etz.zunit.builder.SpecFactory;
import moc.etz.zunit.instrument.BMUtil;
import moc.etz.zunit.instrument.wrapper.*;
import moc.etz.zunit.parse.SubjectManager;
import moc.etz.zunit.targets.ServiceA;
import moc.etz.zunit.targets.ServiceAImpl;
import moc.etz.zunit.trace.Invocation;
import moc.etz.zunit.trace.TraceReaderImpl;
import moc.etz.zunit.util.MustacheUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

public class ZUnit {


    public static void loadWrapper() throws Exception {
        new ExecutorBMWrapper().wrap();
        new ExecutorServiceBMWrapper().wrap();
        new ScheduledExecutorServiceBMWrapper().wrap();
        new ThreadFactoryBMWrapper().wrap();
        new ForkJoinWorkerThreadFactoryBMWrapper().wrap();
    }

    public static void loadAgent() throws Exception {
        BMUtil.loadAgent();
        loadWrapper();
        Properties config = new Properties();
        config.load(ZUnit.class.getClassLoader().getResourceAsStream("zunit.properties"));
        String subjectPkgs = config.getProperty("zunit.subject.pkg");
        Stream.of(subjectPkgs.split(",")).map(String::trim).filter(s -> !s.isEmpty())
                .forEach(pkg -> SubjectManager.getInstance().loadFromPkg(subjectPkgs));

    }


    public static void test() throws Exception {


        ServiceA serviceA = new ServiceAImpl();
        serviceA.hello(5, Arrays.asList("a", "b", "c"));

        TraceReaderImpl traceReader = new TraceReaderImpl();
        Invocation invocation = traceReader.readInvocation(1L);
        JsonNode jsonNode = traceReader.readInParam(1L);
        JsonNode jsonNode1 = traceReader.readOutParam(1L);
        System.out.println(invocation);
        SpecFactory.writeSpec(1L);
        List<GroovyLine> groovyLines = SpecFactory.buildArgsLine(jsonNode1);
        String render = MustacheUtil.render("btm/groovy.mustache", groovyLines);
        System.out.println(render);
    }

    public static void main(String[] args) throws Exception {
        loadAgent();
        new ForkJoinWorkerThreadFactoryBMWrapper().wrap();
        ForkJoinPool forkJoinPool = new ForkJoinPool(10);
        for (int i = 0; i < 10; i++) {
            forkJoinPool.submit(() -> System.out.println("haha"));
        }

        test();
    }

}
