package api.gossip.uz.service;

import api.gossip.uz.dto.StudentDTO;
import api.gossip.uz.entity.StudentEntity;
import api.gossip.uz.exception.ExceptionUtil;
import api.gossip.uz.exception.NotFoundException;
import api.gossip.uz.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentDTO create(StudentDTO studentDTO) {
        StudentEntity studentEntity = new StudentEntity();
        studentEntity.setName(studentDTO.getName());
        studentEntity.setSurname(studentDTO.getSurname());
        studentEntity.setAge(studentDTO.getAge());
        return this.toDTO(studentRepository.save(studentEntity));
    }

    public StudentDTO get(Integer id) throws NotFoundException {
        if (!studentRepository.existsById(id)) {
            throw ExceptionUtil.throwNotFoundException("student with id does not exist!");
        }
        return this.toDTO(studentRepository.getReferenceById(id));
    }

    public StudentDTO update(Integer id, StudentDTO studentDTO) {
        StudentEntity studentEntity = studentRepository.getReferenceById(id);
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
