package moc.etz.zunit.trace;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;
import moc.etz.zunit.parse.RefsInfo;
import moc.etz.zunit.parse.SubjectManager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;


@Data
@ToString(exclude = {
        "refs",
        "thisObject",
        "parent",
        "children",
})
public class Invocation {
    public static final AtomicLong INVOCATION_INCR = new AtomicLong(1);

    public final Long id = INVOCATION_INCR.getAndIncrement();
    public final long threadId = Thread.currentThread().getId();
    public Long mid;
    @JsonIgnore
    public final Map<Object, RefsInfo> refs = new HashMap<>();
    public final List<Invocation> children = new CopyOnWriteArrayList<>();
    @JsonIgnore
    public Object thisObject;
    public String method;
    public String signature;
    public String refPath;
    public Class clazz;
    public boolean staticInvoke = false;
    public boolean subject = false;
    public volatile boolean finished = false;
    @JsonIgnore
    Invocation parent;
    @SneakyThrows
    public void saveObjectsRef(String methodSignure, Object[] args) {
        try {
            boolean isSubject = SubjectManager.isSubject(clazz);
            if (isSubject) {
                Map<String, RefsInfo> refMap = SubjectManager.getInstance().SUBJECT_CLASS_REFS.get(clazz);
                Map<String, Object> argMap = new HashMap<>();
                if (args != null && args.length > 0) {
                    for (int i = 0; i < args.length; i++) {
                        argMap.put(SubjectManager.keyOfArgs(methodSignure, i), args[i]);
                    }
                }
                RefsInfo refThis = new RefsInfo();
                refThis.name = "this";
                refThis.type = RefsInfo.RefType.FIELD;
                refs.put(thisObject, refThis);
                Set<Map.Entry<String, RefsInfo>> entries = refMap.entrySet();
                for (Map.Entry<String, RefsInfo> entry : entries) {
                    String key = entry.getKey();
                    RefsInfo value = entry.getValue();
                    if (value.type.equals(RefsInfo.RefType.ARG)) {
                        refs.put(argMap.get(key), value);
                    } else if (value.type.equals(RefsInfo.RefType.FIELD)) {
                        Field field = clazz.getDeclaredField(value.name);
                        field.setAccessible(true);
                        Object feildValue = field.get(thisObject);
                        refs.put(feildValue, value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
