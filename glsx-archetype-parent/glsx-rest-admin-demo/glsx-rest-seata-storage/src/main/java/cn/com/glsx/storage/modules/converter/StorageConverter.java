package cn.com.glsx.storage.modules.converter;

import cn.com.glsx.storage.model.StorageBO;
import cn.com.glsx.storage.model.StorageDTO;
import cn.com.glsx.storage.modules.entity.Storage;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 解耦dao与api的实体映射
 * 字段名不一样，用@Mapping来处理
 * https://github.com/mapstruct/mapstruct-examples
 *
 * @author payu
 */
@Mapper
public interface StorageConverter {

    StorageConverter INSTANCE = Mappers.getMapper(StorageConverter.class);

    StorageDTO do2dto(Storage storage);

    Storage dto2do(StorageDTO storageDTO);

    StorageBO do2bo(Storage storage);

    Storage bo2do(StorageBO storageBO);

}
