package api.gossip.uz.service;

import api.gossip.uz.dto.StudentDTO;
import api.gossip.uz.entity.StudentEntity;
import api.gossip.uz.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService extends BaseService {

    private final StudentRepository studentRepository;
    private final ResourceBundleService bundleService;

    public StudentDTO create(StudentDTO studentDTO) {
        StudentEntity studentEntity = new StudentEntity();
        studentEntity.setName(studentDTO.getName());
        studentEntity.setSurname(studentDTO.getSurname());
        studentEntity.setAge(studentDTO.getAge());
        return this.toDTO(studentRepository.save(studentEntity));
    }

    public StudentDTO get(Integer id) {
        return toDTO(studentRepository.findById(id).orElseThrow(notFound(bundleService.getMessage("student", id))));
    }

    public List<StudentDTO> getAll() {
        return studentRepository.findAll().stream().map(this::toDTO).toList();
    }

    /*public StudentDTO update(Integer id, StudentDTO studentDTO) {
        StudentEntity studentEntity = studentRepository.findById(id)
                .orElseThrow(notFound(bundleService.getMessage("student", id)));

        studentEntity.setName(studentDTO.getName());
        studentEntity.setSurname(studentDTO.getSurname());
        studentEntity.setAge(studentDTO.getAge());
        return this.toDTO(studentRepository.save(studentEntity));
    }*/
    public void update(Integer id, StudentDTO studentDTO) {
        studentRepository.updateStudent(id,
                studentDTO.getName(),
                studentDTO.getSurname(),
                studentDTO.getAge());
    }

    public void delete(Integer id) {
        studentRepository.deleteById(id);
    }

    private StudentDTO toDTO(StudentEntity studentEntity) {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(studentEntity.getId());
        studentDTO.setName(studentEntity.getName());
        studentDTO.setSurname(studentEntity.getSurname());
        studentDTO.setAge(studentEntity.getAge());
        return studentDTO;
    }

}
