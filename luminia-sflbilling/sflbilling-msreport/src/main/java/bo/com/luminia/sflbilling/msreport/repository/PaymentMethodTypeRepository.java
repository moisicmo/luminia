package bo.com.luminia.sflbilling.msreport.repository;

import bo.com.luminia.sflbilling.domain.PaymentMethodType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodTypeRepository extends JpaRepository<PaymentMethodType, Long> {
    List<PaymentMethodType> findAllByCompanyIdAndActiveIsTrue(Integer companyId);
    Optional<PaymentMethodType> findAllByCompanyIdAndSiatIdAndActiveIsTrue(Integer companyId, Integer siatId);
}
