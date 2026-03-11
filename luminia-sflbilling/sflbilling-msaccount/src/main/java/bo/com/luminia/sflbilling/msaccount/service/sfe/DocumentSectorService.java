package bo.com.luminia.sflbilling.msaccount.service.sfe;

import bo.com.luminia.sflbilling.msaccount.repository.DocumentSectorRepository;
import bo.com.luminia.sflbilling.domain.DocumentSector;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.DocumentSectorCreateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.DocumentSectorUpdateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.DocumentSectorRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class DocumentSectorService {

    private final DocumentSectorRepository documentSectorRepository;

    @Transactional
    public DocumentSector create(DocumentSectorCreateReq request) {
        DocumentSector entity = new DocumentSector();
        entity.setSiatId(request.getSiatId());
        entity.setSiatId(request.getSiatId());
        entity.setActivityCode(request.getActivityCode());
        entity.setDocumentSectorType(request.getDocumentSectorType());
        entity.setActive(true); //first time
        entity.setCompanyId(request.getCompanyId());

        documentSectorRepository.save(entity);

        return entity;
    }

    @Transactional
    public Optional<DocumentSectorRes> update(DocumentSectorUpdateReq request) {
        return Optional
            .of(documentSectorRepository.findById(request.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(element -> {
                if (request.getActivityCode() != null) {
                    element.setActivityCode(request.getActivityCode());
                }

                if (request.getDocumentSectorType() != null) {
                    element.setDocumentSectorType(request.getDocumentSectorType());
                }

                element.setActive(request.getActive());

                log.info("Changed Information for Activity: {}", element);
                return element;
            })
            .map(DocumentSectorRes::new);
    }

    public void delete(Long id) {
        DocumentSectorUpdateReq deleteItem = new DocumentSectorUpdateReq();
        deleteItem.setId(id);

        update(deleteItem);
    }
}
