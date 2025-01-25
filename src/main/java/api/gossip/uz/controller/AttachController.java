package api.gossip.uz.controller;

import api.gossip.uz.dto.AttachDTO;
import api.gossip.uz.service.AttachService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/attaches")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AttachController {

    AttachService attachService;

    @PostMapping("/upload")
    public ResponseEntity<AttachDTO> create(@RequestParam("multipartFile") MultipartFile multipartFile) {
        return ResponseEntity.ok(attachService.upload(multipartFile));
    }

    @GetMapping("/open/{fileName}")
    public ResponseEntity<Resource> open(@PathVariable String fileName) {
        return attachService.open(fileName);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> download(@PathVariable("fileName") String fileName) {
        return attachService.download(fileName);
    }

    @GetMapping
    public ResponseEntity<PageImpl<AttachDTO>> getAll(@RequestParam(value = "page", defaultValue = "1") int page,
                                                      @RequestParam(value = "size", defaultValue = "15") int size) {
        return ResponseEntity.ok(attachService.getAll(page - 1, size));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("id") String id) {
        return ResponseEntity.ok(attachService.delete(id));
    }
}
