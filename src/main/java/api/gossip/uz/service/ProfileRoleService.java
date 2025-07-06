package api.gossip.uz.service;

import api.gossip.uz.dto.ProfileRoleDTO;
import api.gossip.uz.entity.ProfileRoleEntity;
import api.gossip.uz.enums.ProfileRole;
import api.gossip.uz.exception.ExceptionUtil;
import api.gossip.uz.repository.ProfileRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileRoleService {

    private final ProfileRoleRepository profileRoleRepository;
    private final ResourceBundleService bundleService;

    public ProfileRoleDTO create(final Integer profileId, final ProfileRole profileRole) {
        if (profileId == null) {
            throw ExceptionUtil.throwNotFoundException(bundleService.getMessage("not.found"));
        }
        final ProfileRoleEntity profileRoleEntity = new ProfileRoleEntity();
        profileRoleEntity.setProfileId(profileId);
        profileRoleEntity.setRoles(profileRole);
        profileRoleEntity.setCreatedDate(LocalDateTime.now());
        return toDTO(profileRoleRepository.save(profileRoleEntity));
    }

    public ProfileRoleDTO get(final Integer id) {
        return profileRoleRepository.findById(id).map(this::toDTO).orElseThrow(()
            -> ExceptionUtil.throwNotFoundException(bundleService.getMessage("profile.role.with.id.does.not.exist")));
    }

    public List<ProfileRoleDTO> getList() {
        return profileRoleRepository.findAll().stream().map(this::toDTO).toList();
    }

    public ProfileRoleDTO update(final Integer id, final ProfileRoleDTO profileRoleDTO) {
        profileRoleRepository.findById(id)
            .map(profileRole -> {
                profileRole.setProfileId(profileRoleDTO.getProfileId());
                profileRole.setRoles(profileRoleDTO.getRoles());
                profileRole.setCreatedDate(LocalDateTime.now());
                profileRoleRepository.save(profileRole);
                return profileRole.getId();
            }).orElseThrow(() -> ExceptionUtil.throwNotFoundException(bundleService.getMessage("profile.role.with.id.does.not.exist")));
        return profileRoleDTO;
    }

    public void delete(final Integer id) {
        profileRoleRepository.deleteById(id);
    }

    public void deleteRoles(final Integer profileId) {
        profileRoleRepository.deleteByProfileId(profileId);
    }

    private ProfileRoleDTO toDTO(final ProfileRoleEntity profileRoleEntity) {
        final ProfileRoleDTO profileRoleDTO = new ProfileRoleDTO();
        profileRoleDTO.setId(profileRoleEntity.getId());
        profileRoleDTO.setProfileId(profileRoleEntity.getProfileId());
        profileRoleDTO.setRoles(profileRoleEntity.getRoles());
        profileRoleDTO.setCreatedDate(profileRoleEntity.getCreatedDate());
        return profileRoleDTO;
    }

}
