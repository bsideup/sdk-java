package io.cloudevents.kafka;

import io.cloudevents.CloudEvent;
import io.cloudevents.format.EventFormat;
import io.cloudevents.format.EventFormatProvider;
import io.cloudevents.kafka.impl.KafkaSerializerMessageVisitorImpl;
import io.cloudevents.message.Encoding;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Serializer for {@link CloudEvent}.
 * <p>
 * To configure the encoding to serialize the event, you can use the {@link CloudEventSerializer#ENCODING_CONFIG} configuration key,
 * which accepts both a {@link String} or a variant of the enum {@link Encoding}. If you configure the Encoding as {@link Encoding#STRUCTURED},
 * you MUST configure the {@link EventFormat} to use with {@link CloudEventSerializer#EVENT_FORMAT_CONFIG}, specifying a {@link String}
 * corresponding to the content type of the event format or specifying an instance of {@link EventFormat}.
 */
public class CloudEventSerializer implements Serializer<CloudEvent> {

    public final static String ENCODING_CONFIG = "cloudevents.serializer.encoding";
    public final static String EVENT_FORMAT_CONFIG = "cloudevents.serializer.event_format";

    private Encoding encoding = Encoding.BINARY;
    private EventFormat format = null;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        if (isKey) {
            throw new IllegalArgumentException("Cannot use CloudEventSerializer as key serializer");
        }
        Object encodingConfig = configs.get(ENCODING_CONFIG);
        if (encodingConfig instanceof String) {
            this.encoding = Encoding.valueOf((String) encodingConfig);
        } else if (encodingConfig instanceof Encoding) {
            this.encoding = (Encoding) encodingConfig;
        } else if (encodingConfig != null) {
            throw new IllegalArgumentException(ENCODING_CONFIG + " can be of type String or " + Encoding.class.getCanonicalName());
        }
        if (this.encoding == Encoding.UNKNOWN) {
            throw new IllegalArgumentException(ENCODING_CONFIG + " cannot be " + Encoding.UNKNOWN);
        }

        if (this.encoding == Encoding.STRUCTURED) {
            Object eventFormatConfig = configs.get(EVENT_FORMAT_CONFIG);
            if (eventFormatConfig instanceof String) {
                this.format = EventFormatProvider.getInstance().resolveFormat((String) eventFormatConfig);
                if (this.format == null) {
                    throw new IllegalArgumentException(EVENT_FORMAT_CONFIG + " cannot be resolved with " + eventFormatConfig);
                }
            } else if (eventFormatConfig instanceof EventFormat) {
                this.format = (EventFormat) eventFormatConfig;
            } else {
                throw new IllegalArgumentException(EVENT_FORMAT_CONFIG + " cannot be null and can be of type String or " + EventFormat.class.getCanonicalName());
            }
        }
    }

    @Override
    public byte[] serialize(String topic, CloudEvent data) {
        throw new UnsupportedOperationException("CloudEventSerializer supports only the signature serialize(String, Headers, CloudEvent)");
    }

    @Override
    public byte[] serialize(String topic, Headers headers, CloudEvent data) {
        if (encoding == Encoding.STRUCTURED) {
            return data.asStructuredMessage(this.format).visit(new KafkaSerializerMessageVisitorImpl(headers));
        } else {
            return data.asBinaryMessage().visit(new KafkaSerializerMessageVisitorImpl(headers));
        }
    }
}
