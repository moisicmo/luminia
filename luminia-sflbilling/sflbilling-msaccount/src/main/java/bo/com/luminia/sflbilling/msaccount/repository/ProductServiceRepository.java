package bo.com.luminia.sflbilling.msaccount.repository;

import bo.com.luminia.sflbilling.domain.ProductService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductServiceRepository extends JpaRepository<ProductService, Long> {
    List<ProductService> findAllByCompanyIdAndActiveIsTrue(Integer companyId);
    Optional<ProductService> findFirstBySiatIdAndCompanyIdAndActiveIsTrue(Integer siatId, Integer companyId);
}
