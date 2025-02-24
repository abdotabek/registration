package api.gossip.uz.repository.mapper;

import api.gossip.uz.dto.ProfileDTO;
import api.gossip.uz.entity.ProfileEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper extends BaseMapper<ProfileDTO, ProfileEntity> {

//    @Mapping(target = "visible", defaultValue = "true")
    ProfileDTO toDTO(ProfileEntity entity);
}
