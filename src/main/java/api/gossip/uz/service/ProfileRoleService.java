package api.gossip.uz.service;

import api.gossip.uz.dto.ProfileRoleDTO;
import api.gossip.uz.entity.ProfileRoleEntity;
import api.gossip.uz.enums.ProfileRole;
import api.gossip.uz.exception.ExceptionUtil;
import api.gossip.uz.repository.ProfileRoleRepository;
import api.gossip.uz.repository.mapper.ProfileRoleMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProfileRoleService {

    ProfileRoleRepository profileRoleRepository;
    ProfileRoleMapper mapper;

    public void create(Integer profileId, ProfileRole profileRole) {
        ProfileRoleEntity profileRoleEntity = new ProfileRoleEntity();
        profileRoleEntity.setProfileId(profileId);
        profileRoleEntity.setRoles(profileRole);
        profileRoleEntity.setCreatedDate(LocalDateTime.now());
        profileRoleRepository.save(profileRoleEntity);
    }

    public ProfileRoleDTO get(Integer id) {
        return profileRoleRepository.findById(id).map(mapper::toDTO).orElseThrow(()
                -> ExceptionUtil.throwNotFoundException("profileRole with id does not exist!"));
    }

    public List<ProfileRoleDTO> getList() {
        return profileRoleRepository.findAll().stream().map(this::toDTO).toList();
    }

    public ProfileRoleDTO update(Integer id, ProfileRoleDTO profileRoleDTO) {
        profileRoleRepository.findById(id)
                .map(profileRole -> {
                    profileRole.setProfileId(profileRoleDTO.getProfileId());
                    profileRole.setRoles(profileRoleDTO.getRoles());
                    profileRole.setCreatedDate(LocalDateTime.now());
                    profileRoleRepository.save(profileRole);
                    return profileRole.getId();
                }).orElseThrow(() -> ExceptionUtil.throwNotFoundException("profileRole with id does not exist!"));
        return profileRoleDTO;
    }

    public void delete(Integer id) {
        profileRoleRepository.deleteById(id);
    }

    public void deleteRoles(Integer profileId) {
        profileRoleRepository.deleteByProfileId(profileId);
    }

    private ProfileRoleDTO toDTO(ProfileRoleEntity profileRoleEntity) {
        ProfileRoleDTO profileRoleDTO = new ProfileRoleDTO();
        profileRoleDTO.setId(profileRoleEntity.getId());
        profileRoleDTO.setProfileId(profileRoleEntity.getProfileId());
        profileRoleDTO.setRoles(profileRoleEntity.getRoles());
        profileRoleDTO.setCreatedDate(profileRoleEntity.getCreatedDate());
        return profileRoleDTO;
    }

}
