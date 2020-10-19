package cn.com.glsx.storage.modules.service;

import cn.com.glsx.storage.model.StorageDTO;
import cn.com.glsx.storage.modules.converter.StorageConverter;
import cn.com.glsx.storage.modules.entity.Storage;
import cn.com.glsx.storage.modules.mapper.StorageMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品业务逻辑实现
 *
 * @author payu
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StorageService {

    @Autowired
    private StorageMapper mapper;

    /**
     * 查询商品详情
     *
     * @param goodsId {@link Storage#getId()}
     * @return {@link Storage}
     */
    public StorageDTO findById(Long goodsId) {
        Storage storage = mapper.selectByPrimaryKey(goodsId);
        return StorageConverter.INSTANCE.do2dto(storage);
    }

    public StorageDTO findByCommodityCode(String commodityCode) {
        Storage storage = mapper.selectByCommodityCode(commodityCode);
        return StorageConverter.INSTANCE.do2dto(storage);
    }

    /**
     * 扣除商品库存
     *
     * @param commodityCode {@link Storage#getId()}
     * @param count         扣除的库存数量
     */
    public void deduct(String commodityCode, int count) {
        Storage goods = mapper.selectByCommodityCode(commodityCode);
        if (ObjectUtils.isEmpty(goods)) {
            throw new RuntimeException("商品：" + commodityCode + ",不存在.");
        }
        if (goods.getCount() - count < 0) {
            throw new RuntimeException("商品：" + commodityCode + "库存不足.");
        }
        goods.setCount(goods.getCount() - count);
        mapper.updateByPrimaryKeySelective(goods);
    }

}
