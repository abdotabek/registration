package api.gossip.uz.service;

import api.gossip.uz.dto.AppResponse;
import api.gossip.uz.dto.ProfileDTO;
import api.gossip.uz.dto.dto.ProfileDetailUpdateDTO;
import api.gossip.uz.dto.dto.ProfilePasswordUpdate;
import api.gossip.uz.entity.ProfileEntity;
import api.gossip.uz.enums.AppLanguage;
import api.gossip.uz.exception.ExceptionUtil;
import api.gossip.uz.repository.ProfileRepository;
import api.gossip.uz.repository.mapper.ProfileMapper;
import api.gossip.uz.util.SpringSecurityUtil;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProfileService {

    ProfileRepository profileRepository;
    ProfileMapper mapper;
    ResourceBundleService bundleService;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public ProfileDTO get(Integer id) {
        return profileRepository.findById(id).map(mapper::toDTO).orElseThrow(
                () -> ExceptionUtil.throwNotFoundException("profile with id does not exist!"));
    }

    public ProfileEntity getVerification(Integer id) {
        return profileRepository.findByIdAndVisibleTrue(id).orElseThrow(() -> ExceptionUtil.throwNotFoundException("profile with does not exist!"));
    }

    public void delete(Integer id) {
        profileRepository.deleteById(id);
    }

    public AppResponse<String> updateDetail(ProfileDetailUpdateDTO profileDetailUpdateDTO, AppLanguage language) {
        Integer profileId = SpringSecurityUtil.getCurrentProfileId();

        //update profile detail я оюновляю пока только имя профиля
        // в этом методе обновится весь поля
  /*      ProfileEntity profile = mapper.toEntity(get(id));
        profile.setName(profileDetailUpdateDTO.getProfileName());
        profileRepository.save(profile);*/
        // здесь обновится только поля который я указываю в запросе
        profileRepository.updateDetail(profileId, profileDetailUpdateDTO.getName());

        return new AppResponse<>(bundleService.getMessage("profile.detail.update.success", language));
    }

    public AppResponse<String> updatePassword(ProfilePasswordUpdate profilePasswordUpdate, AppLanguage language) {

        Integer profileId = SpringSecurityUtil.getCurrentProfileId();
        Optional<ProfileEntity> optionalProfile = profileRepository.findById(profileId);
        if (optionalProfile.isEmpty()) {
            return new AppResponse<>(bundleService.getMessage("profile.not.found", language));
        }
        ProfileEntity profile = optionalProfile.get();

        if (!bCryptPasswordEncoder.matches(profilePasswordUpdate.getOldPassword(), profile.getPassword())) {
            return new AppResponse<>(bundleService.getMessage("update.password.invalid.old", language));
        }

        profileRepository.updatePassword(profileId, bCryptPasswordEncoder.encode(profilePasswordUpdate.getNewPassword()));

        return new AppResponse<>(bundleService.getMessage("update.password.success", language));
    }
}
