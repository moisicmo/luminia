package bo.com.luminia.sflbilling.msinvoice.repository;

import bo.com.luminia.sflbilling.domain.BranchOffice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BranchOfficeRepository extends JpaRepository<BranchOffice, Long> {

    List<BranchOffice> findAllByCompanyIdAndActiveIsTrue(Long companyId);

    BranchOffice findByCompanyIdAndBranchOfficeSiatIdAndActiveIsTrue(Long companyId, Integer branchOfficeSiatId);
}
