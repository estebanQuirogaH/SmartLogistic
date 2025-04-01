package com.projectStore.mapper;

import com.projectStore.dto.StoreCreationDTO;
import com.projectStore.dto.StoreDTO;
import com.projectStore.entity.Store;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StoreMapper {

    StoreMapper INSTANCE = Mappers.getMapper(StoreMapper.class);

    @Mapping(source = "location.address", target = "address")
    @Mapping(source = "location.latitude", target = "latitude")
    @Mapping(source = "location.longitude", target = "longitude")
    @Mapping(target = "creatorName", ignore = true)
    StoreDTO toDTO(Store store);

    List<StoreDTO> toDTOList(List<Store> stores);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(getCurrentDateTime())")
    @Mapping(target = "updatedAt", expression = "java(getCurrentDateTime())")
    @Mapping(target = "physicalStock", source = "initialStock")
    @Mapping(target = "virtualStock", ignore = true)
    Store toEntity(StoreCreationDTO storeDTO);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(getCurrentDateTime())")
    @Mapping(target = "physicalStock", source = "initialStock", conditionExpression = "java(storeDTO.getInitialStock() != null)")
    void updateEntityFromDTO(@MappingTarget Store store, StoreCreationDTO storeDTO);

    default LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
}