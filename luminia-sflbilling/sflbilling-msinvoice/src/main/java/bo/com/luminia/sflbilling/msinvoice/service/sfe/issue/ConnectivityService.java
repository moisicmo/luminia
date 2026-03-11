package bo.com.luminia.sflbilling.msinvoice.service.sfe.issue;

import bo.com.luminia.sflbilling.msinvoice.repository.OfflineRepository;
import bo.com.luminia.sflbilling.domain.*;
import bo.com.luminia.sflbilling.msinvoice.repository.*;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.spec.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class ConnectivityService {

    private final OfflineRepository offlineRepository;

    /**
     * Método que verifica si el sistema se encuentra EN LINEA.
     *
     * @return
     */
    public boolean isSiatOffline() {
        //Optional<Offline> searchOffline = offlineRepository.findAll().stream().findFirst();
        Optional<Offline> searchOffline = offlineRepository.findAllNative().stream().findFirst();

        if (searchOffline.isPresent()) {
            Offline updateOffline = searchOffline.get();
            return updateOffline.getActive();
        }
        return false;
    }

}
