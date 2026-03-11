package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.PaymentMethodType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentMethodTypeRepository extends JpaRepository<PaymentMethodType, Long> {
    List<PaymentMethodType> findAllByCompanyIdAndActiveIsTrue(Integer companyId);
}
