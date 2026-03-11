package bo.com.luminia.sflbilling.msreport.repository;

import bo.com.luminia.sflbilling.domain.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    Optional<RoomType> findOneById(Long id);
}
