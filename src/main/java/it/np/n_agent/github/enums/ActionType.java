package it.np.n_agent.github.enums;

import static it.np.n_agent.dto.UserSettingDto.RepositoryConfigDto.TriggerSettingsDto;


public enum ActionType {
    ADDED,
    OPENED,
    REOPENED,
    SYNCHRONIZE,
    CLOSED,
    EDITED,
    DELETED,
    CREATED,
    REMOVED;

    public static ActionType fromValue(String value) {
        for (ActionType actionType : ActionType.values()) {
            if (actionType.name().equalsIgnoreCase(value)) {
                return actionType;
            }
        }
        throw new IllegalArgumentException("Unknown ActionType value: " + value);
    }

    public static boolean isValidActionPR(TriggerSettingsDto setting,String action) {
        return switch(fromValue(action)){
            case OPENED -> setting.getOnPROpen();
            case SYNCHRONIZE,EDITED -> setting.getOnPRUpdate();
            case REOPENED -> setting.getOnPRReopen();
            default -> false;
        };
    }
}
