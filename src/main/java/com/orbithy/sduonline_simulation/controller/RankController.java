package com.orbithy.sduonline_simulation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.orbithy.sduonline_simulation.data.vo.Result;
import com.orbithy.sduonline_simulation.service.RankService;
import com.orbithy.sduonline_simulation.utils.ResponseUtil;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/rank")
public class RankController {
    private final RankService rankService;
    
    public RankController(RankService rankService) {
        this.rankService = rankService;
    }

    // 基于 coins 的排行榜
    @GetMapping("/coins")
    public ResponseEntity<Result> getCoinsRank(@RequestParam(value = "limit", required = false) Integer limit) {

        return rankService.getCoinsRankList(limit);
    }

    // 基于 maxCoins 的排行榜
    @GetMapping("/max-coins")
    public ResponseEntity<Result> getMaxCoinsRank(@RequestParam(value = "limit", required = false) Integer limit) {

        return rankService.getMaxCoinsRankList(limit);
    }
}
