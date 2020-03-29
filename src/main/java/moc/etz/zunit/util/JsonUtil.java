package moc.etz.zunit.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;

public class JsonUtil {

    static ObjectMapper MAPPER = new ObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    @SneakyThrows
    public static String write(Object obj) {
        return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

}
