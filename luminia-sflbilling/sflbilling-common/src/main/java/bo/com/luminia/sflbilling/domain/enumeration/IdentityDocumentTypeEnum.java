package bo.com.luminia.sflbilling.domain.enumeration;

import lombok.Getter;
import lombok.ToString;
import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public enum IdentityDocumentTypeEnum {

    CI(1, "CI - CÉDULA DE IDENTIDAD"),
    CI_EXT(2, "CEX - CÉDULA DE IDENTIDAD DE EXTRANJERO"),
    PASSPORT(3, "PAS - PASAPORTE"),
    OTHER(4, "OD - OTRO DOCUMENTO DE IDENTIDAD"),
    NIT(5, "NIT - NÚMERO DE IDENTIFICACIÓN TRIBUTARIA");

    private final Integer siatId;
    private final String description;

    private static Map<Integer, IdentityDocumentTypeEnum> envMap = new HashMap<>();

    static {
        for (IdentityDocumentTypeEnum e : IdentityDocumentTypeEnum.values()) {
            envMap.put(e.getKey(), e);
        }
    }

    private IdentityDocumentTypeEnum(Integer siatId, String description) {
        this.siatId = siatId;
        this.description = description;
    }

    public Integer getKey() {
        return siatId;
    }

    public static IdentityDocumentTypeEnum get(Integer key) {
        return envMap.get(key);
    }

}
