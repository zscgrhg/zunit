package moc.etz.zunit.trace;

import com.fasterxml.jackson.databind.JsonNode;

public interface TraceReader {
    JsonNode readInParam(Long invocationId);

    JsonNode readOutParam(Long invocationId);

    Invocation readInvocation(Long invocationId);
}
