package bo.com.luminia.sflbilling.domain.enumeration;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public enum EnvironmentSiatEnum {
    PRODUCTION(1, "Ambiente de Produccion"),
    TEST(2, "Ambiente de Pruebas");

    private final Integer key;
    private final String description;

    private static Map<Integer, EnvironmentSiatEnum> envMap = new HashMap<>();

    static {
        for (EnvironmentSiatEnum e : EnvironmentSiatEnum.values()) {
            envMap.put(e.getKey(), e);
        }
    }

    private EnvironmentSiatEnum(Integer key, String description) {
        this.key = key;
        this.description = description;
    }

    public Integer getKey() {
        return key;
    }

    public static EnvironmentSiatEnum get(Integer key) {
        return envMap.get(key);
    }

}
