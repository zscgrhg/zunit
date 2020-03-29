package moc.etz.zunit;

import com.fasterxml.jackson.databind.JsonNode;
import moc.etz.zunit.builder.SpecFactory;
import moc.etz.zunit.instrument.BMUtil;
import moc.etz.zunit.parse.SubjectManager;
import moc.etz.zunit.targets.ServiceA;
import moc.etz.zunit.targets.ServiceAImpl;
import moc.etz.zunit.trace.Invocation;
import moc.etz.zunit.trace.TraceReaderImpl;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        BMUtil.loadAgent();
        //BMUtil.submitFile("C:\\data\\moc.etz\\src\\main\\resources\\btm\\hello.btm");
        SubjectManager.getInstance().loadFromPkg("moc.etz.zunit.targets");


        ServiceA serviceA = new ServiceAImpl();
        serviceA.hello(5, Arrays.asList("a", "b", "c"));

        TraceReaderImpl traceReader = new TraceReaderImpl();
        Invocation invocation = traceReader.readInvocation(1L);
        JsonNode jsonNode = traceReader.readInParam(1L);
        JsonNode jsonNode1 = traceReader.readOutParam(1L);
        System.out.println(invocation);
        SpecFactory.writeSpec(1L);

    }
}
