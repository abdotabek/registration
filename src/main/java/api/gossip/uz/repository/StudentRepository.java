package api.gossip.uz.repository;

import api.gossip.uz.entity.StudentEntity;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<StudentEntity, Integer> {
    boolean existsById(@NonNull Integer studentId);

    @Modifying
    @Transactional
    @Query("update StudentEntity set name =:name, surname =:surname, age =:age where id =:id")
    void updateStudent(@Param("id") Integer id,
                    @Param("name") String name,
                    @Param("surname") String surname,
                    @Param("age") Long age);
}
