package api.gossip.uz.service;

import api.gossip.uz.dto.ProfileDTO;
import api.gossip.uz.entity.ProfileEntity;
import api.gossip.uz.exception.ExceptionUtil;
import api.gossip.uz.repository.ProfileRepository;
import api.gossip.uz.repository.mapper.ProfileMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProfileService {
    ProfileRepository profileRepository;
    ProfileMapper mapper;

    public ProfileDTO create(ProfileDTO profileDTO) {
        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setName(profileDTO.getName());
        profileEntity.setUsername(profileDTO.getUsername());
        profileEntity.setPassword(profileDTO.getPassword());
        profileEntity.setStatus(profileDTO.getStatus());
        profileEntity.setVisible(profileDTO.getVisible());
        profileEntity.setCreatedDate(LocalDateTime.now());
        return mapper.toDTO(profileRepository.save(profileEntity));
    }

    public ProfileDTO get(Integer id) {
        return profileRepository.findById(id).map(mapper::toDTO).orElseThrow(
                () -> ExceptionUtil.throwNotFoundException("profile with id does not exist!"));
    }

    public ProfileEntity getVerification(Integer id) {
        return profileRepository.findByIdAndVisibleTrue(id).orElseThrow(() -> ExceptionUtil.throwNotFoundException("profile with does not exist!"));
    }


    public List<ProfileDTO> getList() {
        return profileRepository.findAll().stream().map(this::toDTO).toList();
    }

    public ProfileDTO update(Integer id, ProfileDTO profileDTO) {
        profileRepository.findById(id)
                .map(profileEntity -> {
                    profileEntity.setName(profileDTO.getName());
                    profileEntity.setUsername(profileDTO.getUsername());
                    profileEntity.setPassword(profileDTO.getPassword());
                    profileEntity.setStatus(profileDTO.getStatus());
                    profileEntity.setVisible(profileDTO.getVisible());
                    profileEntity.setCreatedDate(LocalDateTime.now());
                    profileRepository.save(profileEntity);
                    return profileEntity.getId();
                }).orElseThrow(() -> ExceptionUtil.throwNotFoundException("profile with id does not exist!"));
        return profileDTO;
    }

    public void delete(Integer id) {
        profileRepository.deleteById(id);
    }

    private ProfileDTO toDTO(ProfileEntity profileEntity) {
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(profileEntity.getId());
        profileDTO.setName(profileEntity.getName());
        profileDTO.setUsername(profileEntity.getUsername());
        profileDTO.setPassword(profileEntity.getPassword());
        profileDTO.setStatus(profileEntity.getStatus());
        profileDTO.setVisible(profileEntity.getVisible());
        profileDTO.setCreatedDate(profileEntity.getCreatedDate());
        return profileDTO;
    }
}
