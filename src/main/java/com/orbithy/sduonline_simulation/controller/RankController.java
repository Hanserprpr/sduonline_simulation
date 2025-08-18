package com.orbithy.sduonline_simulation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.orbithy.sduonline_simulation.annotation.sub;
import com.orbithy.sduonline_simulation.data.vo.Result;
import com.orbithy.sduonline_simulation.service.RankService;
import com.orbithy.sduonline_simulation.utils.ResponseUtil;

@RestController
@RequestMapping("/rank")
public class RankController {
    private final RankService rankService;
    
    public RankController(RankService rankService) {
        this.rankService = rankService;
    }

    @GetMapping("/getGrossAmountRank")
    public ResponseEntity<Result> getGrossAmountRank(@sub String sub) {
        return rankService.getGrossAmountRank();
    }

    @GetMapping("/getMaximumRank")
    public ResponseEntity<Result> getMaximumRank(@sub String sub) {
        return rankService.getMaximumRank();
    }
    

    
//    /**
//     * 更新用户coins总量
//     * @param userId 用户ID
//     * @param coins 新的coins总量
//     * @return 更新结果
//     */
//    @PostMapping("/updateUserCoins")
//    public ResponseEntity<Result> updateUserCoins(@RequestParam Long userId, @RequestParam Double coins) {
//
//        return rankService.updateUserRank(userId, coins);;
//    }
    
//    /**
//     * 手动更新coins排行榜数据
//     * @return 更新结果
//     */
//    @PostMapping("/updateRank")
//    public ResponseEntity<Result> updateRank() {
//        rankService.updateRankData();
//        return ResponseUtil.success("coins排行榜数据已更新");
//    }
}
