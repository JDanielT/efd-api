package br.com.jbssistemas.efdclient.mapper;

import br.com.jbssistemas.efdclient.model.User;
import br.com.jbssistemas.efdclient.response.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper extends AbstractMapper<User, UserDto> {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto mapForRead(User category);

    User createToEntity(UserDto dto);

    @Mapping(target = "id", ignore = true)
    void createToEntity(UserDto dto, @MappingTarget User category);

}
