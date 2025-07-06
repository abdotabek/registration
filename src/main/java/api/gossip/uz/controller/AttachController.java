package api.gossip.uz.controller;

import api.gossip.uz.dto.AttachDTO;
import api.gossip.uz.service.AttachService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/attaches")
@RequiredArgsConstructor
@Tag(name = "AttachController", description = "Api set for working with Attach")
public class AttachController {

    private final AttachService attachService;

    @PostMapping("/upload")
    @Operation(summary = "Upload", description = "Api used upload")
    public ResponseEntity<AttachDTO> create(@RequestParam("multipartFile") MultipartFile multipartFile) {
        return ResponseEntity.ok(attachService.upload(multipartFile));
    }

    @GetMapping("/open/{fileName}")
    @Operation(summary = "Open by filename", description = "Api used open")
    public ResponseEntity<Resource> open(@PathVariable String fileName) {
        return attachService.open(fileName);
    }

    @GetMapping("/download/{fileName}")
    @Operation(summary = "Download by file name", description = "Api used download")
    public ResponseEntity<Resource> download(@PathVariable("fileName") String fileName) {
        return attachService.download(fileName);
    }

    @GetMapping
    @Operation(summary = "Get list from attach", description = "Api used get list")
    public ResponseEntity<PageImpl<AttachDTO>> getAll(@RequestParam(value = "page", defaultValue = "1") int page,
                                                      @RequestParam(value = "size", defaultValue = "15") int size) {
        return ResponseEntity.ok(attachService.getAll(page - 1, size));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete from attach", description = "Api used from delete attach")
    public ResponseEntity<Boolean> delete(@PathVariable("id") String id) {
        return ResponseEntity.ok(attachService.delete(id));
    }
}
