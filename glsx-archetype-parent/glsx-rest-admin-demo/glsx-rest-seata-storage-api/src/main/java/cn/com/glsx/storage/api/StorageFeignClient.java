package cn.com.glsx.storage.api;

import cn.com.glsx.storage.model.StorageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 商品服务接口定义
 *
 * @author payu
 */
@FeignClient(name = "glsx-rest-seata-storage", path = "/storage")
public interface StorageFeignClient {

    @GetMapping
    StorageDTO findByCommodityCode(@RequestParam("commodityCode") String commodityCode);

    /**
     * 减少商品的库存
     *
     * @param commodityCode {@link StorageDTO#getCommodityCode()} ()}
     * @param stock         减少库存的数量
     */
    @PostMapping
    void reduceStock(@RequestParam("commodityCode") String commodityCode, @RequestParam("stock") int stock);

}
