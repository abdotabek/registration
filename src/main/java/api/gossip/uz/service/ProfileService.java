package api.gossip.uz.service;

import api.gossip.uz.dto.AppResponse;
import api.gossip.uz.dto.CodeConfirmDTO;
import api.gossip.uz.dto.ProfileDTO;
import api.gossip.uz.dto.profile.ProfileDetailUpdateDTO;
import api.gossip.uz.dto.profile.ProfileFilterDTO;
import api.gossip.uz.dto.profile.ProfilePasswordUpdateDTO;
import api.gossip.uz.dto.profile.ProfileUsernameUpdateDTO;
import api.gossip.uz.entity.ProfileEntity;
import api.gossip.uz.entity.ProfileRoleEntity;
import api.gossip.uz.enums.AppLanguage;
import api.gossip.uz.enums.ProfileRole;
import api.gossip.uz.exception.ExceptionUtil;
import api.gossip.uz.repository.ProfileRepository;
import api.gossip.uz.repository.ProfileRoleRepository;
import api.gossip.uz.repository.mapper.ProfileMapper;
import api.gossip.uz.util.EmailUtil;
import api.gossip.uz.util.JwtUtil;
import api.gossip.uz.util.PhoneUtil;
import api.gossip.uz.util.SpringSecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProfileService {

    ProfileRepository profileRepository;
    ProfileMapper mapper;
    ResourceBundleService bundleService;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    SmsSendService smsSendService;
    EmailSendingService emailSendingService;
    SmsHistoryService smsHistoryService;
    EmailHistoryService emailHistoryService;
    ProfileRoleRepository profileRoleRepository;
    private final AttachService attachService;

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

        //update profile detail я обновляю пока только имя профиля
        // в этом методе обновится весь поля
  /*      ProfileEntity profile = mapper.toEntity(get(id));
        profile.setName(profileDetailUpdateDTO.getProfileName());
        profileRepository.save(profile);*/
        // здесь обновится только поля который я указываю в запросе
        profileRepository.updateDetail(profileId, profileDetailUpdateDTO.getName());

        return new AppResponse<>(bundleService.getMessage("profile.detail.update.success", language));
    }

    public AppResponse<String> updatePassword(ProfilePasswordUpdateDTO profilePasswordUpdateDTO, AppLanguage language) {

        Integer profileId = SpringSecurityUtil.getCurrentProfileId();
        Optional<ProfileEntity> optionalProfile = profileRepository.findById(profileId);
        if (optionalProfile.isEmpty()) {
            return new AppResponse<>(bundleService.getMessage("profile.not.found", language));
        }
        ProfileEntity profile = optionalProfile.get();

        if (!bCryptPasswordEncoder.matches(profilePasswordUpdateDTO.getOldPassword(), profile.getPassword())) {
            return new AppResponse<>(bundleService.getMessage("update.password.invalid.old", language));
        }

        profileRepository.updatePassword(profileId, bCryptPasswordEncoder.encode(profilePasswordUpdateDTO.getNewPassword()));

        return new AppResponse<>(bundleService.getMessage("update.password.success", language));
    }

    public AppResponse<String> updateUsername(ProfileUsernameUpdateDTO profileUsernameUpdateDTO, AppLanguage language) {
        Optional<ProfileEntity> optionalProfile = profileRepository.findByUsernameAndVisibleTrue(profileUsernameUpdateDTO.getUsername());
        if (optionalProfile.isPresent()) {
            return new AppResponse<>(bundleService.getMessage("email.phone.exist", language));
        }

        if (PhoneUtil.isPhone(profileUsernameUpdateDTO.getUsername())) {
            smsSendService.sendUsernameChangeConfirmSms(profileUsernameUpdateDTO.getUsername(), language);
        }
        if (EmailUtil.isEmail(profileUsernameUpdateDTO.getUsername())) {
            emailSendingService.sendUsernameChangeEmail(profileUsernameUpdateDTO.getUsername(), language);
        }

        Integer profileId = SpringSecurityUtil.getCurrentProfileId();
        profileRepository.updateTempUsername(profileId, profileUsernameUpdateDTO.getUsername());

        return new AppResponse<>(bundleService.getMessage("reset.password.response", language));
    }

    public AppResponse<String> updatePhoto(String photoId, AppLanguage language) {
        Integer profileId = SpringSecurityUtil.getCurrentProfileId();
        /*ProfileEntity profile = new ProfileEntity();
        profile.setPhotoId(attachId);
        profileRepository.save(profile);*/

//        ProfileEntity profile = profileRepository.findById(profileId).orElseThrow();
        ProfileEntity profile = mapper.toEntity(get(profileId));
        profileRepository.updatePhoto(profileId, photoId);


        if (profile.getPhotoId() != null && !profile.getPhotoId().equals(photoId)) {
            attachService.delete(profile.getPhotoId());
        }
        return new AppResponse<>(bundleService.getMessage("change.photo.success", language));
    }

    public AppResponse<String> updateUsernameConfirm(CodeConfirmDTO codeConfirmDTO, AppLanguage language) {
        Integer profileId = SpringSecurityUtil.getCurrentProfileId();
        Optional<ProfileEntity> optionalProfile = profileRepository.findById(profileId);

        if (optionalProfile.isEmpty()) {
            return new AppResponse<>(bundleService.getMessage("profile.not.found", language));
        }
        ProfileEntity profile = optionalProfile.get();
        String tempUsername = profile.getTempUsername();

        if (PhoneUtil.isPhone(tempUsername)) {
            smsHistoryService.check(tempUsername, codeConfirmDTO.getCode(), language);
        }
        if (EmailUtil.isEmail(tempUsername)) {
            emailHistoryService.check(tempUsername, codeConfirmDTO.getCode(), language);
        }
        profileRepository.updateUsername(profileId, codeConfirmDTO.getCode());

        List<ProfileRole> roles = profileRoleRepository.getAllRolesListByProfileId(profile.getId());
        String jwt = JwtUtil.encode(tempUsername, profile.getId(), roles);
        return new AppResponse<>(jwt, bundleService.getMessage("change.username.success", language));
    }

    public PageImpl<ProfileDTO> filter(ProfileFilterDTO filterDTO, int page, int size, AppLanguage language) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ProfileEntity> filterResult = null;
        if (filterDTO.getQuery() == null) {
            filterResult = profileRepository.customFilter(pageRequest);
        } else {
            filterResult = profileRepository.filter("%" + filterDTO.getQuery() + "%", pageRequest);
        }
        List<ProfileDTO> resultList = filterResult.stream().map(this::toDTO).toList();

        return new PageImpl<>(resultList, pageRequest, filterResult.getTotalElements());
    }

    private ProfileDTO toDTO(ProfileEntity entity) {
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(entity.getId());
        profileDTO.setName(entity.getName());
        profileDTO.setUsername(entity.getUsername());
        if (entity.getRoleeList() != null) {
            profileDTO.setRoleList(entity.getRoleeList()
                    .stream()
                    .map(ProfileRoleEntity::getRoles)
                    .toList());
        }
        profileDTO.setPhoto(attachService.attachDTO(entity.getPhotoId()));
        profileDTO.setStatus(entity.getStatus());
        return profileDTO;
    }
}
