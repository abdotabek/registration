package api.gossip.uz.repository.mapper;

import api.gossip.uz.dto.ProfileRoleDTO;
import api.gossip.uz.entity.ProfileRoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileRoleMapper extends BaseMapper<ProfileRoleDTO, ProfileRoleEntity> {

    @Mapping(source = "profileId", target = "profileId")
    ProfileRoleDTO toDTO(ProfileRoleEntity entity);
}
