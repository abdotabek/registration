package api.gossip.uz.service;

import api.gossip.uz.dto.AppResponse;
import api.gossip.uz.dto.AuthDTO;
import api.gossip.uz.dto.ProfileDTO;
import api.gossip.uz.dto.RegistrationDTO;
import api.gossip.uz.dto.sms.SmsVerificationDTO;
import api.gossip.uz.entity.ProfileEntity;
import api.gossip.uz.enums.AppLanguage;
import api.gossip.uz.enums.GeneralStatus;
import api.gossip.uz.enums.ProfileRole;
import api.gossip.uz.exception.ExceptionUtil;
import api.gossip.uz.repository.ProfileRepository;
import api.gossip.uz.repository.ProfileRoleRepository;
import api.gossip.uz.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthService {

    ProfileRepository profileRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    ProfileRoleService profileRoleService;
    EmailSendingService emailSendingService;
    ProfileService profileService;
    ProfileRoleRepository profileRoleRepository;
    ResourceBundleService bundleService;
    SmsSendService smsSendService;
    private final SmsHistoryService smsHistoryService;

    public AppResponse<String> registration(RegistrationDTO registrationDTO, AppLanguage language) {
        //1. validation
        //2. check for email
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(registrationDTO.getUsername());
        if (optional.isPresent()) {
            ProfileEntity profile = optional.get();
            if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                profileRoleService.deleteRoles(profile.getId());
                profileRepository.delete(profile);
                // send sms/email
            } else {
                throw ExceptionUtil.throwConflictException(bundleService.getMessage("email.phone.exist", language));
            }
        }
        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setName(registrationDTO.getName());
        profileEntity.setUsername(registrationDTO.getUsername());
        profileEntity.setPassword(bCryptPasswordEncoder.encode(registrationDTO.getPassword()));
        profileEntity.setStatus(GeneralStatus.IN_REGISTRATION);
        profileEntity.setVisible(true);
        profileEntity.setCreatedDate(LocalDateTime.now());
        profileRepository.save(profileEntity);     //save
        //insert Role
        profileRoleService.create(profileEntity.getId(), ProfileRole.ROLE_USER);
//        emailSendingService.sendRegistrationEmail(registrationDTO.getUsername(), profileEntity.getId(), language);
        smsSendService.sendRegistration(registrationDTO.getUsername());
        return new AppResponse<>(bundleService.getMessage("email.confirm.send", language));
    }

    public String registrationEmailVerification(String token, AppLanguage language) {
        try {
            Integer profileId = JwtUtil.decodeRegVerToken(token);

            ProfileEntity profile = profileService.getVerification(profileId);
            if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                // ACTIVE
                profileRepository.changeStatus(profileId, GeneralStatus.ACTIVE);
                return bundleService.getMessage("email.ver.success", language);
            }
        } catch (JwtException e) {
            System.out.println("error");
        }
        throw ExceptionUtil.throwConflictException(bundleService.getMessage("reg.failed.profile.block", language));
    }

    public ProfileDTO login(AuthDTO authDTO, AppLanguage language) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(authDTO.getUsername());
        if (optional.isEmpty()) {
            throw ExceptionUtil.throwCustomIllegalArgumentException(bundleService.getMessage("profile.password.wrong", language));
        }
        ProfileEntity profile = optional.get();
        if (!bCryptPasswordEncoder.matches(authDTO.getPassword(), profile.getPassword())) {
            throw ExceptionUtil.throwCustomIllegalArgumentException(bundleService.getMessage("profile.password.wrong", language));
        }
        if (GeneralStatus.ACTIVE != profile.getStatus()) {
            throw ExceptionUtil.throwConflictException(bundleService.getMessage("profile.status", language));
        }

        return getLoginInResponse(profile);
    }

    public ProfileDTO registrationSmsVerification(SmsVerificationDTO smsVerificationDTO, AppLanguage language) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(smsVerificationDTO.getPhone());
        if (optional.isEmpty()) {
            throw ExceptionUtil.throwNotFoundException(bundleService.getMessage("profile.not.found", language));
        }
        ProfileEntity profile = optional.get();
        if (GeneralStatus.IN_REGISTRATION != profile.getStatus()) {
            throw ExceptionUtil.throwConflictException(bundleService.getMessage("email.phone.exist", language));
        }

        smsHistoryService.check(smsVerificationDTO.getPhone(), smsVerificationDTO.getCode(), language);

        profileRepository.changeStatus(profile.getId(), GeneralStatus.ACTIVE);
        return getLoginInResponse(profile);
    }

    public ProfileDTO getLoginInResponse(ProfileEntity profile) {
        ProfileDTO response = new ProfileDTO();
        response.setName(profile.getName());
        response.setUsername(profile.getUsername());
        response.setRoleList(profileRoleRepository.getAllRolesListByProfileId(profile.getId()));
        response.setJwt(JwtUtil.encode(profile.getUsername(), profile.getId(), response.getRoleList()));
        return response;
    }
}
