package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.ProductService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductServiceRepository extends JpaRepository<ProductService, Long> {
    List<ProductService> findAllByCompanyIdAndActiveIsTrue(Integer companyId);
}
