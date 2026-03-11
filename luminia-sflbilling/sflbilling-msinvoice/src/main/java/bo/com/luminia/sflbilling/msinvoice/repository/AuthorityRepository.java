package bo.com.luminia.sflbilling.msinvoice.repository;

import bo.com.luminia.sflbilling.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
