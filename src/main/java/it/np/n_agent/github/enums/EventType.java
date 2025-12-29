package it.np.n_agent.github.enums;

import it.np.n_agent.github.dto.GHWebhookInstallationPaylaod;
import it.np.n_agent.github.dto.GHWebhookPrPayload;
import it.np.n_agent.utilities.RequestUtility;
import lombok.Getter;

@Getter
public enum EventType {
    PUSH("push"),
    PULL_REQUEST("pull_request", GHWebhookPrPayload.class),
    INSTALLATION("installation", GHWebhookInstallationPaylaod.class),
    OTHER("other");

    private final String eventValue;
    private final Class<?> payloadClass;

    EventType(String s) {
        this.eventValue = s;
        this.payloadClass = null;
    }
    EventType(String s, Class<?> payloadClass) {
        this.eventValue = s;
        this.payloadClass = payloadClass;
    }

    public static EventType fromValue(String value){
        for(EventType type : EventType.values()){
            if(type.getEventValue().equalsIgnoreCase(value)){
                return type;
            }
        }
        return OTHER;
    }

    public static Object retrievePayload(String payload,String eventType,RequestUtility requestUtility){
        EventType type = fromValue(eventType);
        if(type.getPayloadClass() != null){
            return requestUtility.parsePayload(payload, type.getPayloadClass());
        }
        return null;
    }
}

