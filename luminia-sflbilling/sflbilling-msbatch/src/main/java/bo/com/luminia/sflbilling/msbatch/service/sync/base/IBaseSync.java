package bo.com.luminia.sflbilling.msbatch.service.sync.base;

import bo.com.luminia.sflbilling.msbatch.service.dto.RequestSync;
import bo.com.luminia.sflbilling.msbatch.service.dto.ResponseSync;

public interface IBaseSync {
    ResponseSync synchronize(RequestSync requestSync);
}
