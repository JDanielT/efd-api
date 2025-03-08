package br.com.jbssistemas.efdclient.mapper;

import br.com.jbssistemas.efdclient.model.BaseEntity;
import br.com.jbssistemas.efdclient.response.dto.BaseDto;

public interface AbstractMapper<T extends BaseEntity, E extends BaseDto> {

    E mapForRead(T entity);

    T createToEntity(E dto);

    void createToEntity(E dto, T entity);

}
