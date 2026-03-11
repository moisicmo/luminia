package bo.com.luminia.sflbilling.domain.enumeration;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public enum ModalitySiatEnum {
    ELECTRONIC_BILLING(1, "Facturacion Electronica en Linea"),
    COMPUTERIZED_BILLING(2, "Facturacion Computarizada en Linea");

    private final Integer key;
    private final String description;

    private static Map<Integer, ModalitySiatEnum> envMap = new HashMap<>();

    static {
        for (ModalitySiatEnum e : ModalitySiatEnum.values()) {
            envMap.put(e.getKey(), e);
        }
    }

    private ModalitySiatEnum(Integer key, String description) {
        this.key = key;
        this.description = description;
    }

    public Integer getKey() {
        return key;
    }

    public static ModalitySiatEnum get(Integer key) {
        return envMap.get(key);
    }
}
