package br.com.jbssistemas.efdclient.service.commom;

import br.com.jbssistemas.efdclient.exception.RecordNotFoundException;
import br.com.jbssistemas.efdclient.mapper.AbstractMapper;
import br.com.jbssistemas.efdclient.model.BaseEntity;
import br.com.jbssistemas.efdclient.repository.AbstractRepository;
import br.com.jbssistemas.efdclient.response.dto.BaseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Objects;

public interface AbstractService<T extends BaseEntity, E extends BaseDto> {

    AbstractRepository<T> getRepository();

    AbstractMapper<T, E> getMapper();

    default T persist(E dto) {
        if (Objects.isNull(dto.getId())) {
            return create(dto);
        }
        return update(dto);
    }

    default T create(E dto) {
        var entity = getMapper().createToEntity(dto);
        return getRepository().save(entity);
    }

    default T update(E dto) {
        return getRepository().findById(dto.getId()).map(entity -> {
                    getMapper().createToEntity(dto, entity);
                    return getRepository().save(entity);
                }
        ).orElseThrow(RecordNotFoundException::new);
    }

    default T create(T entity) {
        return getRepository().save(entity);
    }

    default T update(T entity) {
        return getRepository().findById(entity.getId())
                .map(e -> getRepository().save(entity))
                .orElseThrow(RecordNotFoundException::new);
    }

    default Page<E> read(Pageable pageable) {
        return getRepository().findAll(pageable).map(getMapper()::mapForRead);
    }

    default E read(Long id) {
        return getRepository().findById(id)
                .map(getMapper()::mapForRead)
                .orElseThrow(RecordNotFoundException::new);
    }

    default void delete(Long id) {
        var entity = read(id);
        getRepository().deleteById(entity.getId());
    }

}
