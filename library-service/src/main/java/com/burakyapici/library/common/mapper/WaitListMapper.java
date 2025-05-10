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

    WaitListDto waitListToWaitListDto(WaitList waitList);

    List<WaitListDto> waitListToWaitListDto(List<WaitList> waitLists);

    WaitListResponse waitListDtoToWaitListResponse(WaitListDto waitListDto);

    PageableResponse<WaitListResponse> pageableDtoToPageableResponse(PageableDto<WaitListDto> pageableDto);

    List<WaitListResponse> waitListDtoToWaitListResponse(List<WaitListDto> waitListDtoList);
}
