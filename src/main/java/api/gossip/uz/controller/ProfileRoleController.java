package api.gossip.uz.controller;

import api.gossip.uz.dto.ProfileRoleDTO;
import api.gossip.uz.enums.ProfileRole;
import api.gossip.uz.service.ProfileRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile-roles")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name = "ProfileController", description = "Api for with working Profile_Role")
public class ProfileRoleController {

    ProfileRoleService profileRoleService;


    @PostMapping("/{id}")
    @Operation(summary = "Create profile role", description = "Api used cred profile role")
    public ResponseEntity<Void> create(@PathVariable("id") Integer id, @RequestBody ProfileRole profileRole) {
        profileRoleService.create(id, profileRole);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get profile_role by id", description = "Api used get profile_role by id")
    public ResponseEntity<ProfileRoleDTO> get(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(profileRoleService.get(id));
    }

    @GetMapping
    @Operation(summary = "Get list profile_role", description = "Api used get list profile_role")
    public ResponseEntity<List<ProfileRoleDTO>> getList() {
        return ResponseEntity.ok(profileRoleService.getList());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update profile_role", description = "Api used update profile_role bu id")
    public ResponseEntity<ProfileRoleDTO> update(@PathVariable("id") Integer id, @RequestBody ProfileRoleDTO profileRoleDTO) {
        return ResponseEntity.ok(profileRoleService.update(id, profileRoleDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete profile_role", description = "Api used delete profile_role by id")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        profileRoleService.delete(id);
        return ResponseEntity.ok().build();
    }

}
