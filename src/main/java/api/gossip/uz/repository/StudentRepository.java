package api.gossip.uz.repository;

import api.gossip.uz.entity.StudentEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<StudentEntity, Integer> {
    boolean existsById(@NonNull Integer studentId);
}
