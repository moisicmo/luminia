package bo.com.luminia.sflbilling.msinvoice.repository;

import bo.com.luminia.sflbilling.domain.PointSale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointSaleRepository extends JpaRepository<PointSale, Long> {

    List<PointSale> findAllByBranchOfficeIdAndActiveIsTrue(Long branchOfficeId);
}
