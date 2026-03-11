package bo.com.luminia.sflbilling.msbatch.repository;

import bo.com.luminia.sflbilling.domain.Offline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OfflineRepository extends JpaRepository<Offline, Long> {

    @Query(value = "SELECT \n" +
        "  * \n" +
        "FROM \n" +
        "  public.offline " , nativeQuery = true)
    List<Offline> findAllNative();

}
