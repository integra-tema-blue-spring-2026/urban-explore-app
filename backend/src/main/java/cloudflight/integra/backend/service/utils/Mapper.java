package cloudflight.integra.backend.service.utils;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class Mapper {
    public static <Entity, DTO > DTO toDto(Entity entity, Function<Entity, DTO> mapper) {
        return mapper.apply(entity);
    }


    public static <Entity, DTO> Entity toEntity(DTO dto, Function<DTO, Entity> mapper){
        return mapper.apply(dto);
    }

    public static <Entity, DTO> void mapUpdateToEntity(DTO dto, Entity entity, BiConsumer<DTO, Entity> mappingRule) {
        mappingRule.accept(dto, entity);
    }
}
