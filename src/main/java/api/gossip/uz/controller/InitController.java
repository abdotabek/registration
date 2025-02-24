package api.gossip.uz.controller;

import api.gossip.uz.entity.ProfileEntity;
import api.gossip.uz.enums.GeneralStatus;
import api.gossip.uz.repository.ProfileRepository;
import api.gossip.uz.service.ProfileRoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

import static api.gossip.uz.enums.ProfileRole.ADMIN;
import static api.gossip.uz.enums.ProfileRole.USER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InitController {

    ProfileRepository profileRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    ProfileRoleService profileRoleService;

    @GetMapping("/all")
    public String creteAdmin() {
        Optional<ProfileEntity> exist = profileRepository.findByUsernameAndVisibleTrue("admin@gmail.com");
        if (exist.isPresent()) {
            return "Present";
        }
        ProfileEntity profile = new ProfileEntity();
        profile.setName("ADMIN");
        profile.setUsername("admin@gmail.com");
        profile.setVisible(true);
        profile.setPassword(bCryptPasswordEncoder.encode("123456"));
        profile.setStatus(GeneralStatus.ACTIVE);
        profile.setCreatedDate(LocalDateTime.now());
        profileRepository.save(profile);
        profileRoleService.create(profile.getId(),USER /*ProfileRole.ROLE_USER*/);
        profileRoleService.create(profile.getId(), ADMIN/*ProfileRole.ROLE_ADMIN*/);
        return "DONE";
    }

}
