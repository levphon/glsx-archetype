package cn.com.glsx.storage.modules.mapper;

import cn.com.glsx.storage.modules.entity.Storage;
import com.glsx.plat.mybatis.mapper.CommonBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StorageMapper extends CommonBaseMapper<Storage> {

    Storage selectByCommodityCode(String commodityCode);

}