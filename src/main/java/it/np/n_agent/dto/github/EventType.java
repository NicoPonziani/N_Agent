package it.np.n_agent.dto.github;

import lombok.Getter;

@Getter
public enum EventType {
    PUSH("push"),
    PULL_REQUEST("pull_request"),
    OTHER("other");

    private final String eventValue;

    EventType(String s) {
        this.eventValue = s;
    }

    public static EventType fromValue(String value){
        for(EventType type : EventType.values()){
            if(type.getEventValue().equalsIgnoreCase(value)){
                return type;
            }
        }
        return OTHER;
    }
}

