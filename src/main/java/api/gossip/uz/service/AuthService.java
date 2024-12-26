package api.gossip.uz.service;

import api.gossip.uz.dto.RegistrationDTO;
import api.gossip.uz.entity.ProfileEntity;
import api.gossip.uz.enums.GeneralStatus;
import api.gossip.uz.enums.ProfileRole;
import api.gossip.uz.exception.ExceptionUtil;
import api.gossip.uz.repository.ProfileRepository;
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

    public String registration(RegistrationDTO registrationDTO) {
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
                throw ExceptionUtil.throwConflictException("username already exists!");
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
        emailSendingService.sendRegistrationEmail(registrationDTO.getUsername(), profileEntity.getId());

        return "successfully registration";
    }

    public String regVerification(String token) {
        try {
            Integer profileId = JwtUtil.decodeRegVerToken(token);

            ProfileEntity profile = profileService.getVerification(profileId);
            if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                // ACTIVE
                profileRepository.changeStatus(profileId, GeneralStatus.ACTIVE);
                return "Verification successful";
            }
        } catch (JwtException e) {
            System.out.println("error");
        }
        throw ExceptionUtil.throwConflictException("Registration failed: User is blocked.");
    }
}
