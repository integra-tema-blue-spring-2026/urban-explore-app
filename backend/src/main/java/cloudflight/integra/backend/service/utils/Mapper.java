package cloudflight.integra.backend.service.utils;

import java.util.function.BiConsumer;
import java.util.function.Function;

public final class Mapper {

    private Mapper() {
    }

    public static <E, DTO > DTO toDto(E E, Function<E, DTO> mapper) {
        return mapper.apply(E);
    }


    public static <E, DTO> E toEntity(DTO dto, Function<DTO, E> mapper){
        return mapper.apply(dto);
    }

    public static <E, DTO> void mapUpdateToEntity(DTO dto, E E, BiConsumer<DTO, E> mappingRule) {
        mappingRule.accept(dto, E);
    }
}
