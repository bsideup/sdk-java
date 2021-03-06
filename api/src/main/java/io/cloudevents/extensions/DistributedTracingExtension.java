package io.cloudevents.extensions;

import io.cloudevents.CloudEvent;
import io.cloudevents.Extension;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class DistributedTracingExtension implements Extension {

    public static final String TRACEPARENT = "traceparent";
    public static final String TRACESTATE = "tracestate";

    private String traceparent;
    private String tracestate;

    public String getTraceparent() {
        return traceparent;
    }

    public void setTraceparent(String traceparent) {
        this.traceparent = traceparent;
    }

    public String getTracestate() {
        return tracestate;
    }

    public void setTracestate(String tracestate) {
        this.tracestate = tracestate;
    }

    @Override
    public void readFromEvent(CloudEvent event) {
        Object tp = event.getExtensions().get(TRACEPARENT);
        if (tp != null) {
            this.traceparent = tp.toString();
        }
        Object ts = event.getExtensions().get(TRACESTATE);
        if (ts != null) {
            this.tracestate = ts.toString();
        }
    }

    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(TRACEPARENT, this.traceparent);
        map.put(TRACESTATE, this.tracestate);
        return Collections.unmodifiableMap(map);
    }

    @Override
    public String toString() {
        return "DistributedTracingExtension{" +
                "traceparent='" + traceparent + '\'' +
                ", tracestate='" + tracestate + '\'' +
                '}';
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((traceparent == null) ? 0
				: traceparent.hashCode());
		result = prime * result + ((tracestate == null) ? 0
				: tracestate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DistributedTracingExtension other = (DistributedTracingExtension) obj;
		if (traceparent == null) {
			if (other.traceparent != null)
				return false;
		} else if (!traceparent.equals(other.traceparent))
			return false;
		if (tracestate == null) {
			if (other.tracestate != null)
				return false;
		} else if (!tracestate.equals(other.tracestate))
			return false;
		return true;
	}
}
