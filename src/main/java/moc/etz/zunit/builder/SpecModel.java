package moc.etz.zunit.builder;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Data
public class SpecModel {
    String pkg = "pkg" + new SimpleDateFormat("yyyyMMddHmmss").format(new Date());
    Long id;
    String fileName;
    String subject;
    String method;
    String subjectDecl;
    List<String> mockBlock;
    String actionDecl = "1==1";
    String assertDecl = "1==1";
    Set<HashMap.Entry<String, List<GroovyLine>>> Inputs;
    Set<HashMap.Entry<String, List<GroovyLine>>> Outputs;
    Set<HashMap.Entry<String, List<GroovyLine>>> Returned;
}
