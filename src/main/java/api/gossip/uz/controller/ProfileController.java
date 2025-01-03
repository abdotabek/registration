package api.gossip.uz.controller;

import api.gossip.uz.dto.ProfileDTO;
import api.gossip.uz.service.ProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProfileController {
    ProfileService profileService;

   /* @PostMapping
    public ResponseEntity<ProfileDTO> create(@RequestBody ProfileDTO profileDTO) {
        return ResponseEntity.ok(profileService.create(profileDTO));
    }
*/
    @GetMapping("/{id}")
    public ResponseEntity<ProfileDTO> get(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(profileService.get(id));
    }
/*

    @GetMapping
    public ResponseEntity<List<ProfileDTO>> getList() {
        return ResponseEntity.ok(profileService.getList());
    }
*/

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        profileService.delete(id);
        return ResponseEntity.ok().build();
    }
}
