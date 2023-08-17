package com.game_trade.dto;

import com.game_trade.domain.Commodity;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 翁鹏
 */
@Data
public class CommodityDto extends Commodity {

    /**
     * 上传的文件文件
     */
    private MultipartFile[] files;
}
