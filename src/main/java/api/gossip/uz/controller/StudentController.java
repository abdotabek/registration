package api.gossip.uz.controller;

import api.gossip.uz.dto.StudentDTO;
import api.gossip.uz.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static api.gossip.uz.dto.jwt.AuthoritiesConstants.OWNER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/student")
public class StudentController {

    private final StudentService studentService;

    @Secured(OWNER)
    @PostMapping
    public ResponseEntity<StudentDTO> create(@Valid @RequestBody StudentDTO studentDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(studentDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> get(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(studentService.get(id));
    }

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getList() {
        return ResponseEntity.ok(studentService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") Integer id, @RequestBody StudentDTO studentDTO) {
        studentService.update(id, studentDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        studentService.delete(id);
        return ResponseEntity.ok().build();
    }
}
