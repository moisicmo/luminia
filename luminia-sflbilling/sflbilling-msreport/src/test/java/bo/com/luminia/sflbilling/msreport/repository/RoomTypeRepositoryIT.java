package bo.com.luminia.sflbilling.msreport.repository;

import bo.com.luminia.sflbilling.domain.RoomType;
import bo.com.luminia.sflbilling.msreport.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureMockMvc
@IntegrationTest
public class RoomTypeRepositoryIT {

    public static final Logger log = LoggerFactory.getLogger(RoomTypeRepositoryIT.class);

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Test
    void getRoomTypeId1() {
        Optional<RoomType> roomType = roomTypeRepository.findOneById(1L);
        assertThat(roomType).isNotEmpty();
        log.debug("Room type: {}", roomType.get());
    }
}
