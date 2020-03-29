package moc.etz.zunit;

import moc.etz.zunit.instrument.BMUtil;
import moc.etz.zunit.parse.SubjectManager;
import moc.etz.zunit.targets.ServiceA;
import moc.etz.zunit.targets.ServiceAImpl;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        BMUtil.loadAgent();
        //BMUtil.submitFile("C:\\data\\moc.etz\\src\\main\\resources\\btm\\hello.btm");
        SubjectManager.getInstance().loadFromPkg("moc.etz.zunit.targets");


        ServiceA serviceA = new ServiceAImpl();
        serviceA.hello(5, Arrays.asList("a", "b", "c"));
    }
}
