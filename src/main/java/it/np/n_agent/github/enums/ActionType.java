package it.np.n_agent.github.enums;

import it.np.n_agent.dto.UserSettingDto;
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
            case REOPENED -> setting.getOnPRReopen();
            case EDITED -> setting.getOnPRUpdate();
            default -> false;
        };
    }
}
