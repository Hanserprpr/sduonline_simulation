package com.orbithy.sduonline_simulation.service;

import com.orbithy.sduonline_simulation.cache.IGlobalCache;
import com.orbithy.sduonline_simulation.data.po.User;
import com.orbithy.sduonline_simulation.data.vo.Result;
import com.orbithy.sduonline_simulation.mapper.UserMapper;
import com.orbithy.sduonline_simulation.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RankService {

    private final UserMapper userMapper;
    private final IGlobalCache globalCache;
    
    // Redis中存储排行榜的key
    private static final String RANK_KEY = "user_coins_rank";
    
    @Autowired
    public RankService(UserMapper userMapper, IGlobalCache globalCache) {
        this.userMapper = userMapper;
        this.globalCache = globalCache;
    }
    
    /**
     * 获取学线币排行榜数据
     * 从Redis中获取基于coins总量的排行榜数据，如果不存在则从数据库获取并存入Redis
     * @return 排行榜数据
     */
    public ResponseEntity<Result> getGrossAmountRank() {
        Map<String, Object> rankData = new HashMap<>();
        
        // 从Redis中获取排行榜数据
        Set<Object> rankSet = globalCache.zReverseRange(RANK_KEY, 0, -1);
        
        // 如果Redis中没有数据，则从数据库获取并存入Redis
        if (rankSet == null || rankSet.isEmpty()) {
            updateRankDataToRedis();
            rankSet = globalCache.zReverseRange(RANK_KEY, 0, -1);
        }
        
        // 转换排行榜数据格式
        List<Map<String, Object>> userRankList = new ArrayList<>();
        int rank = 1;
        for (Object obj : rankSet) {
            if (obj instanceof User) {
                User user = (User) obj;
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("username", user.getUsername());
                userMap.put("coins", globalCache.zScore(RANK_KEY, user));
                userMap.put("rank", rank++);
                userRankList.add(userMap);
            }
        }
        
        rankData.put("userRankList", userRankList);
        return ResponseUtil.build(Result.success(rankData, "获取排行信息成功"));
    }

    private void updateRankDataToRedis() {
    }


//    /**
//     * 更新单个用户的排行榜数据
//     * @param userId 用户ID
//     * @param coins 新的coins总量
//     */
//    public ResponseEntity<Result> updateUserRank(Long userId, double coins) {
//        User user = userMapper.selectById(userId);
//        if (user != null) {
//            // 更新用户coins总量
//            // 注意：这里只更新Redis中的排行榜数据，实际的coins更新应该在coins表中进行
//            // 这里假设coins表的更新已经在其他地方完成
//
//            // 更新Redis中的排行榜数据
//            if (globalCache.hasKey(RANK_KEY)) {
//                // 如果用户已在排行榜中，先移除
//                globalCache.zRemove(RANK_KEY, user);
//                // 添加用户到排行榜，使用新的coins总量
//                globalCache.zAdd(RANK_KEY, user, coins);
//            }
//        }
//        return ResponseUtil.build(Result.ok());
//    }
    
    /**
     * 定时更新排行榜数据
     * 每小时执行一次
     */
    @Scheduled(fixedRate = 3600000)
    public void scheduledUpdateRank() {
        updateRankDataToRedis();
    }


    public ResponseEntity<Result> getMaximumRank() {
        return null;
    }
}
