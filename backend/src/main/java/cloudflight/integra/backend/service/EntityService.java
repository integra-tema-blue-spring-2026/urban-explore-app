package cloudflight.integra.backend.service;

import cloudflight.integra.backend.domain.Entity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.Optional;

public abstract class EntityService<T extends Entity<ID>, ID extends Serializable>
    implements Service<T, ID>{

    protected JpaRepository<T, ID> repository;

    public EntityService(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }



    @Override
    public T save(T t) {
        return repository.save(t);
    }

    @Override
    public void delete(ID id) {
        repository.deleteById(id);
    }
}
