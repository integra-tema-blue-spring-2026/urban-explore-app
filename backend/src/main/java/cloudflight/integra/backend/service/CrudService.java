package cloudflight.integra.backend.service;


import java.util.Optional;

public interface CrudService<E, ID> {
    Optional<E> findById(ID id);
    E save(E e);
    void delete(ID id);
}
