package api.gossip.uz.repository.mapper;

import api.gossip.uz.dto.ProfileDTO;
import api.gossip.uz.entity.ProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper extends BaseMapper<ProfileDTO, ProfileEntity> {

    @Mapping(target = "visible", defaultValue = "true")
    ProfileDTO toDTO(ProfileEntity entity);
}
