package api.gossip.uz.controller;

import api.gossip.uz.dto.ProfileDTO;
import api.gossip.uz.dto.ProfileRoleDTO;
import api.gossip.uz.enums.ProfileRole;
import api.gossip.uz.service.ProfileRoleService;
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
public class ProfileRoleController {
    ProfileRoleService profileRoleService;

    /*@PostMapping
    public ResponseEntity<ProfileRoleDTO> create(@RequestBody ProfileRoleDTO profileDTO) {
        return ResponseEntity.ok(profileRoleService.create(profileDTO));
    }*/
    @PostMapping("/{id}")
    public ResponseEntity<Void> create(@PathVariable("id") Integer id, @RequestBody ProfileRole profileRole) {
        profileRoleService.create(id, profileRole);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileRoleDTO> get(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(profileRoleService.get(id));
    }

    @GetMapping
    public ResponseEntity<List<ProfileRoleDTO>> getList() {
        return ResponseEntity.ok(profileRoleService.getList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        profileRoleService.delete(id);
        return ResponseEntity.ok().build();
    }

}
