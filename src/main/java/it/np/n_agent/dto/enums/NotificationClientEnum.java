package it.np.n_agent.dto.enums;

import java.util.List;

import static it.np.n_agent.dto.UserSettingDto.RepositoryConfigDto.NotificationSettingsDto;

public enum NotificationClientEnum {
    GITHUB,
    SLACK,
    EMAIL;

    public static List<NotificationClientEnum> getUserClients(NotificationSettingsDto setting){
        if(setting == null){
            return List.of(GITHUB); // Default to GITHUB if no settings are provided
        }
        List<NotificationClientEnum> clients = new java.util.ArrayList<>();
        if(setting.getGithubComments())
            clients.add(GITHUB);
        if(setting.getEmailDigestEnabled()) //TODO manage email frequency ect..
            clients.add(EMAIL);
        //TODO add SLACK support

        return clients;
    }
}
