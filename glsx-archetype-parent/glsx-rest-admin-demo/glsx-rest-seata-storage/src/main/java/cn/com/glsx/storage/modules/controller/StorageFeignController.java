package cn.com.glsx.storage.modules.controller;

import cn.com.glsx.storage.api.StorageFeignClient;
import cn.com.glsx.storage.model.StorageDTO;
import cn.com.glsx.storage.modules.entity.Storage;
import cn.com.glsx.storage.modules.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 库存接口定义实现
 *
 * @author payu
 */
@RestController
@RequestMapping("/storage")
public class StorageFeignController implements StorageFeignClient {
    /**
     * 商品业务逻辑
     */
    @Autowired
    private StorageService storageService;

    @Override
    @GetMapping("/findByCommodityCode")
    public StorageDTO findByCommodityCode(@RequestParam("commodityCode") String commodityCode) {
        return storageService.findByCommodityCode(commodityCode);
    }

    /**
     * 扣减商品库存
     *
     * @param commodityCode {@link Storage#getCommodityCode()} ()}
     * @param stock         减少库存的数量
     */
    @Override
    @PostMapping("/reduceStock")
    public void reduceStock(@RequestParam("commodityCode") String commodityCode, @RequestParam("stock") int stock) {
        storageService.deduct(commodityCode, stock);
    }

}
