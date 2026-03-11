package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.BranchOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BranchOfficeRepository extends JpaRepository<BranchOffice, Long> {

    @Query("SELECT b " +
        "FROM BranchOffice b " +
        "INNER JOIN b.company c " +
        "WHERE b.active = TRUE " +
        "AND b.branchOfficeSiatId = :branchOfficeSiatId " +
        "AND c.id = :companyId ")
    BranchOffice findByBranchOfficeSiatIdActive(Integer branchOfficeSiatId, Long companyId);

    List<BranchOffice> findAllByCompanyIdAndActiveIsTrue(Long companyId);
}
