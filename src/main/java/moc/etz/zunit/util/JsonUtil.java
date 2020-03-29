package moc.etz.zunit.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import moc.etz.zunit.trace.Invocation;

public class JsonUtil {

    static ObjectMapper MAPPER = new ObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    @SneakyThrows
    public static String write(Object obj) {
        return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

    @SneakyThrows
    public static JsonNode readTree(byte[] bytes) {
        return MAPPER.readTree(bytes);
    }

    @SneakyThrows
    public static <T> T readerFor(Class<T> clazz, byte[] bytes) {
        return MAPPER.readerFor(Invocation.class).readValue(bytes);
    }

    @SneakyThrows
    public static <T> T readerFor(TypeReference<T> tTypeReference, String data) {
        return MAPPER.readerFor(tTypeReference).readValue(data);
    }
}
