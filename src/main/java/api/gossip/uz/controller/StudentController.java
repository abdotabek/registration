package api.gossip.uz.controller;

import api.gossip.uz.dto.StudentDTO;
import api.gossip.uz.exception.BadRequestException;
import api.gossip.uz.service.ResourceBundleService;
import api.gossip.uz.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import static api.gossip.uz.dto.jwt.AuthoritiesConstants.OWNER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService service;
    private final ResourceBundleService bundleService;

    @Secured(OWNER)
    @PostMapping
    public ResponseEntity<StudentDTO> create(@Valid @RequestBody StudentDTO studentDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(studentDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> get(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(service.get(id));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> update(@PathVariable("id") Integer id, @RequestBody StudentDTO studentDTO) {
        if (id == null) {
            throw new BadRequestException(bundleService.getMessage("invalid.id"));
        }
        return ResponseEntity.ok(service.update(id, studentDTO));
    }
}
