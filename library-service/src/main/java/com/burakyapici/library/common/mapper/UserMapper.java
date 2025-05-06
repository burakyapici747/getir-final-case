package com.burakyapici.library.common.mapper;

import com.burakyapici.library.api.dto.request.UserUpdateRequest;
import com.burakyapici.library.api.dto.response.PageableResponse;
import com.burakyapici.library.api.dto.response.UserDetailResponse;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.dto.UserDetailDto;
import com.burakyapici.library.domain.dto.UserDto;
import com.burakyapici.library.domain.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toUserDto(User user);
    List<UserDto> toUserDtoList(List<User> users);

    UserDetailDto toUserDetailDto(User user);

    UserDetailResponse toUserDetailResponse(UserDetailDto userDetailDto);

    PageableResponse<UserDto> toPageableResponse(PageableDto<UserDto> pageableDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "waitLists", ignore = true)
    @Mapping(target = "borrowingList", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromUserUpdateRequest(UserUpdateRequest userUpdateRequest, User user);
}
