package api.gossip.uz.service;

import api.gossip.uz.dto.AppResponse;
import api.gossip.uz.dto.auth.AuthDTO;
import api.gossip.uz.dto.ProfileDTO;
import api.gossip.uz.dto.auth.RegistrationDTO;
import api.gossip.uz.dto.auth.ResetPasswordConfirmDTO;
import api.gossip.uz.dto.auth.ResetPasswordDTO;
import api.gossip.uz.dto.sms.SmsResendDTO;
import api.gossip.uz.dto.sms.SmsVerificationDTO;
import api.gossip.uz.entity.ProfileEntity;
import api.gossip.uz.enums.AppLanguage;
import api.gossip.uz.enums.GeneralStatus;
import api.gossip.uz.enums.ProfileRole;
import api.gossip.uz.exception.ExceptionUtil;
import api.gossip.uz.repository.ProfileRepository;
import api.gossip.uz.repository.ProfileRoleRepository;
import api.gossip.uz.util.EmailUtil;
import api.gossip.uz.util.JwtUtil;
import api.gossip.uz.util.PhoneUtil;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static api.gossip.uz.enums.ProfileRole.USER;

@Slf4j
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
    SmsHistoryService smsHistoryService;
    EmailHistoryService emailHistoryService;
    private final AttachService attachService;

    public AppResponse<String> registration(RegistrationDTO registrationDTO, AppLanguage language) {
        //1. validation
        //2. check for email
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(registrationDTO.getUsername());
        if (optional.isPresent()) {
            ProfileEntity profile = optional.get();
            if (GeneralStatus.IN_REGISTRATION == profile.getStatus()) {
                profileRoleService.deleteRoles(profile.getId());
                profileRepository.delete(profile);
                // send sms/email
            } else {
                log.warn("Profile already exist with name {}", registrationDTO.getName());
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
        profileRoleService.create(profileEntity.getId(), USER /*ProfileRole.ROLE_USER*/);
        if (PhoneUtil.isPhone(registrationDTO.getUsername())) {
            smsSendService.sendRegistration(registrationDTO.getUsername(), language);
        } else if (EmailUtil.isEmail(registrationDTO.getUsername())) {
            emailSendingService.sendRegistrationEmail(registrationDTO.getUsername(), profileEntity.getId(), language);
        }
        return new AppResponse<>(bundleService.getMessage("email.confirm.send", language));
    }

    public String registrationEmailVerification(String token, AppLanguage language) {
        try {
            Integer profileId = JwtUtil.decodeRegVerToken(token);

            ProfileEntity profile = profileService.getVerification(profileId);
            if (GeneralStatus.IN_REGISTRATION == profile.getStatus()) {
                // ACTIVE
                profileRepository.changeStatus(profileId, GeneralStatus.ACTIVE);
                return bundleService.getMessage("email.ver.success", language);
            }
        } catch (JwtException e) {

        }
        log.warn("Registration email verification failed {}", token);
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
            log.info("Wrong status {}", authDTO.getUsername());
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
            log.info("Verification failed {}", smsVerificationDTO.getPhone());
            throw ExceptionUtil.throwConflictException(bundleService.getMessage("email.phone.exist", language));
        }

        smsHistoryService.check(smsVerificationDTO.getPhone(), smsVerificationDTO.getCode(), language);

        profileRepository.changeStatus(profile.getId(), GeneralStatus.ACTIVE);
        return getLoginInResponse(profile);
    }

    public AppResponse<String> registrationSmsVerificationResend(@Valid SmsResendDTO smsResendDTO, AppLanguage language) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(smsResendDTO.getPhone());
        if (optional.isEmpty()) {
            throw ExceptionUtil.throwNotFoundException(bundleService.getMessage("profile.not.found", language));
        }
        ProfileEntity profile = optional.get();
        if (GeneralStatus.IN_REGISTRATION != profile.getStatus()) {
            log.info("Registration failed {}", smsResendDTO.getPhone());
            throw ExceptionUtil.throwConflictException(bundleService.getMessage("email.phone.exist", language));
        }
        //resend sms
        smsSendService.sendRegistration(smsResendDTO.getPhone(), language);
        return new AppResponse<>(bundleService.getMessage("sms.resend", language));
    }

    public AppResponse<String> resetPassword(ResetPasswordDTO resetPasswordDTO, AppLanguage language) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(resetPasswordDTO.getUsername());
        if (optional.isEmpty()) {
            throw ExceptionUtil.throwNotFoundException(bundleService.getMessage("profile.not.found", language));
        }
        ProfileEntity profile = optional.get();
        if (GeneralStatus.ACTIVE != profile.getStatus()) {
            log.info("Profile status is wrong {}", resetPasswordDTO.getUsername());
            throw ExceptionUtil.throwCustomIllegalArgumentException(bundleService.getMessage("profile.status", language));
        }
        //send
        if (PhoneUtil.isPhone(resetPasswordDTO.getUsername())) {
            smsSendService.sendResetPasswordSms(resetPasswordDTO.getUsername(), language);
        } else if (EmailUtil.isEmail(resetPasswordDTO.getUsername())) {
            emailSendingService.sendResetPasswordEmail(resetPasswordDTO.getUsername(), language);
        }
        return new AppResponse<>(bundleService.getMessage("reset.password.response", language));
    }

    public AppResponse<String> resetPasswordConfirm(ResetPasswordConfirmDTO resetPasswordConfirmDTO, AppLanguage language) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(resetPasswordConfirmDTO.getUsername());
        if (optional.isEmpty()) {
            throw ExceptionUtil.throwNotFoundException(bundleService.getMessage("profile.not.found", language));
        }
        ProfileEntity profile = optional.get();
        if (GeneralStatus.ACTIVE != profile.getStatus()) {
            log.info("Profile status wrong {}", resetPasswordConfirmDTO.getUsername());
            throw ExceptionUtil.throwConflictException(bundleService.getMessage("profile.status", language));
        }
        //check
        if (PhoneUtil.isPhone(resetPasswordConfirmDTO.getUsername())) {
            smsHistoryService.check(resetPasswordConfirmDTO.getUsername(), resetPasswordConfirmDTO.getConfigCode(), language);
        } else if (EmailUtil.isEmail(resetPasswordConfirmDTO.getUsername())) {
            emailHistoryService.check(resetPasswordConfirmDTO.getUsername(), resetPasswordConfirmDTO.getConfigCode(), language);
        }
        profileRepository.updatePassword(profile.getId(), bCryptPasswordEncoder.encode(resetPasswordConfirmDTO.getPassword()));

        return new AppResponse<>(bundleService.getMessage("reset.password.success", language));
    }

    public ProfileDTO getLoginInResponse(ProfileEntity profile) {
        ProfileDTO response = new ProfileDTO();
        response.setName(profile.getName());
        response.setUsername(profile.getUsername());
        response.setRoleList(profileRoleRepository.getAllRolesListByProfileId(profile.getId()));
        response.setJwt(JwtUtil.encode(profile.getUsername(), profile.getId(), response.getRoleList()));
        response.setPhoto(attachService.attachDTO(profile.getPhotoId()));
        return response;
    }

    public String get() {
        String password = "12345";
        return bCryptPasswordEncoder.encode(password);
    }
}
