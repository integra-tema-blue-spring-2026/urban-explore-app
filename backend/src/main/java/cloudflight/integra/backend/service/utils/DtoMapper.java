package cloudflight.integra.backend.service.utils;


import java.util.function.Function;

public class DtoMapper {

    public static <Entity, DTO > DTO toDto(Entity entity, Function<Entity, DTO> mapper) {
        return mapper.apply(entity);
    }
}
