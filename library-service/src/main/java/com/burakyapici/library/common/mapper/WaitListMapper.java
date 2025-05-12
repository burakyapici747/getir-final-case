package com.burakyapici.library.common.mapper;

import com.burakyapici.library.api.dto.response.PageableResponse;
import com.burakyapici.library.api.dto.response.WaitListResponse;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.dto.WaitListDto;
import com.burakyapici.library.domain.model.WaitList;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "default")
public interface WaitListMapper {
    WaitListMapper INSTANCE = Mappers.getMapper(WaitListMapper.class);

    WaitListDto toWaitListDtoList(WaitList waitList);

    List<WaitListDto> toWaitListDtoList(List<WaitList> waitLists);

    WaitListResponse toWaitListResponse(WaitListDto waitListDto);

    PageableResponse<WaitListResponse> toPageableResponse(PageableDto<WaitListDto> pageableDto);

    List<WaitListResponse> toWaitListResponse(List<WaitListDto> waitListDtoList);
}
