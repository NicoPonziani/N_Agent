package it.np.n_agent.mapper;

import it.np.n_agent.dto.UserSettingDto;
import it.np.n_agent.entity.UserSetting;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserSettingMapper {

    UserSetting settingToEntity(UserSettingDto dto);

    UserSettingDto settingToDto(UserSetting entity);
}
