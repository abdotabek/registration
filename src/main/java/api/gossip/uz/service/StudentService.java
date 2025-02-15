package api.gossip.uz.service;

import api.gossip.uz.dto.StudentDTO;
import api.gossip.uz.entity.StudentEntity;
import api.gossip.uz.exception.EntityNotFound;
import api.gossip.uz.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public StudentDTO get(Integer id) throws EntityNotFound {
        return toDTO(studentRepository.findById(id).orElseThrow(notFound(bundleService.getMessage("student", id))));
    }

    public StudentDTO update(Integer id, StudentDTO studentDTO) throws EntityNotFound {
        StudentEntity studentEntity = studentRepository.findById(id)
                .orElseThrow(notFound(bundleService.getMessage("student", id)));
        studentEntity.setName(studentDTO.getName());
        studentEntity.setSurname(studentDTO.getSurname());
        studentEntity.setAge(studentDTO.getAge());
        return this.toDTO(studentRepository.save(studentEntity));
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
