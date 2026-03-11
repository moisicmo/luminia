package bo.com.luminia.sflbilling.msaccount.repository;

import bo.com.luminia.sflbilling.domain.BranchOffice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BranchOfficeRepository extends JpaRepository<BranchOffice, Long> {

    Optional<BranchOffice> findByCompanyIdAndBranchOfficeSiatIdAndActiveIsTrue(Long companyId, Integer Id);

    List<BranchOffice> findByCompanyIdAndBranchOfficeSiatId(Long companyId, Integer branchId);

    List<BranchOffice> findByCompanyId(Long companyId);

}
