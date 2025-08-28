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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RankService {

    private final UserMapper userMapper;
    private final IGlobalCache cache;
    
    private static final String COINS_RANK_KEY = "rank:coins";
    private static final String MAX_COINS_RANK_KEY = "rank:maxcoins";
    private static final long CACHE_EXPIRE_TIME = 300; // 5分钟过期时间
    
    @Autowired
    public RankService(UserMapper userMapper, IGlobalCache cache) {
        this.userMapper = userMapper;
        this.cache = cache;
    }

    /**
     * 获取基于coins的排行榜
     * @param limit 返回排行榜的条数，默认10条
     * @return 排行榜数据
     */
    public ResponseEntity<Result> getCoinsRankList(Integer limit) {
        try {
            if (limit == null || limit <= 0) {
                limit = 10;
            }
            
            // 先尝试从缓存获取
            Set<Object> rankSet = cache.zReverseRange(COINS_RANK_KEY, 0, limit - 1);
            
            if (rankSet == null || rankSet.isEmpty()) {
                // 缓存未命中，重新构建排行榜
                log.info("Coins排行榜缓存未命中，重新构建");
                refreshCoinsRankCache();
                rankSet = cache.zReverseRange(COINS_RANK_KEY, 0, limit - 1);
            }
            
            List<Map<String, Object>> result = new ArrayList<>();
            int rank = 1;
            
            for (Object userIdObj : rankSet) {
                try {
                    Integer userId = Integer.valueOf(userIdObj.toString());
                    User user = userMapper.selectById(userId);
                    if (user != null) {
                        Map<String, Object> userRank = new HashMap<>();
                        userRank.put("rank", rank++);
                        userRank.put("userId", user.getId());
                        userRank.put("username", user.getUsername());
                        userRank.put("coins", user.getCoins());
                        userRank.put("avatar", user.getAvatar());
                        result.add(userRank);
                    }
                } catch (Exception e) {
                    log.error("处理用户排行榜数据时出错: {}", e.getMessage());
                }
            }
            
            return ResponseUtil.build(Result.success(result,"获取成功"));
        } catch (Exception e) {
            log.error("获取coins排行榜失败: {}", e.getMessage());
            return ResponseUtil.build(Result.error(403, e.getMessage()));
        }
    }

    /**
     * 获取基于maxCoins的排行榜
     * @param limit 返回排行榜的条数，默认10条
     * @return 排行榜数据
     */
    public ResponseEntity<Result> getMaxCoinsRankList(Integer limit) {
        try {
            if (limit == null || limit <= 0) {
                limit = 10;
            }
            
            // 先尝试从缓存获取
            Set<Object> rankSet = cache.zReverseRange(MAX_COINS_RANK_KEY, 0, limit - 1);
            
            if (rankSet == null || rankSet.isEmpty()) {
                // 缓存未命中，重新构建排行榜
                log.info("MaxCoins排行榜缓存未命中，重新构建");
                refreshMaxCoinsRankCache();
                rankSet = cache.zReverseRange(MAX_COINS_RANK_KEY, 0, limit - 1);
            }
            
            List<Map<String, Object>> result = new ArrayList<>();
            int rank = 1;
            
            for (Object userIdObj : rankSet) {
                try {
                    Integer userId = Integer.valueOf(userIdObj.toString());
                    User user = userMapper.selectById(userId);
                    if (user != null) {
                        Map<String, Object> userRank = new HashMap<>();
                        userRank.put("rank", rank++);
                        userRank.put("userId", user.getId());
                        userRank.put("username", user.getUsername());
                        userRank.put("maxCoins", user.getMaxCoins());
                        userRank.put("avatar", user.getAvatar());
                        result.add(userRank);
                    }
                } catch (Exception e) {
                    log.error("处理用户maxCoins排行榜数据时出错: {}", e.getMessage());
                }
            }

            return ResponseUtil.build(Result.success(result,"获取成功"));
        } catch (Exception e) {
            log.error("获取maxCoins排行榜失败: {}", e.getMessage());
            return ResponseUtil.build(Result.error(403, e.getMessage()));
        }
    }

    /**
     * 刷新coins排行榜缓存（使用快照+原子重命名避免读写冲突）
     */
    public void refreshCoinsRankCache() {
        try {
            log.info("开始刷新coins排行榜缓存");
            final String tmpKey = COINS_RANK_KEY + ":tmp";

            // 先清理临时键
            cache.del(tmpKey);

            // 查询所有用户的coins数据
            List<User> users = userMapper.selectList(null);

            // 将数据写入临时ZSet
            int added = 0;
            for (User user : users) {
                if (user.getCoins() != null && user.getCoins() > 0) {
                    if (cache.zAdd(tmpKey, user.getId(), user.getCoins().doubleValue())) {
                        added++;
                    }
                }
            }

            if (added == 0) {
                // 若没有数据，保持旧快照不动，避免出现空窗口
                log.info("coins排行榜暂无有效数据，保留旧快照");
                return;
            }

            // 设置临时键过期时间
            cache.expire(tmpKey, CACHE_EXPIRE_TIME);

            // 原子切换：将临时键重命名为正式键
            try {
                @SuppressWarnings("unchecked")
                RedisTemplate<String, Object> rt = (RedisTemplate<String, Object>) cache.getRedisTemplate();
                rt.rename(tmpKey, COINS_RANK_KEY);
            } catch (Exception e) {
                log.error("重命名coins快照键失败: {}", e.getMessage());
                // 失败时删除临时键，避免泄漏
                cache.del(tmpKey);
                throw e;
            }

            log.info("coins排行榜缓存刷新完成，写入 {} 条记录", added);
        } catch (Exception e) {
            log.error("刷新coins排行榜缓存失败: {}", e.getMessage());
        }
    }

    /**
     * 刷新maxCoins排行榜缓存（使用快照+原子重命名避免读写冲突）
     */
    public void refreshMaxCoinsRankCache() {
        try {
            log.info("开始刷新maxCoins排行榜缓存");
            final String tmpKey = MAX_COINS_RANK_KEY + ":tmp";

            // 先清理临时键
            cache.del(tmpKey);

            // 查询所有用户的maxCoins数据
            List<User> users = userMapper.selectList(null);

            // 将数据写入临时ZSet
            int added = 0;
            for (User user : users) {
                if (user.getMaxCoins() != null && user.getMaxCoins() > 0) {
                    if (cache.zAdd(tmpKey, user.getId(), user.getMaxCoins().doubleValue())) {
                        added++;
                    }
                }
            }

            if (added == 0) {
                // 若没有数据，保持旧快照不动，避免出现空窗口
                log.info("maxCoins排行榜暂无有效数据，保留旧快照");
                return;
            }

            // 设置临时键过期时间
            cache.expire(tmpKey, CACHE_EXPIRE_TIME);

            // 原子切换：将临时键重命名为正式键
            try {
                @SuppressWarnings("unchecked")
                RedisTemplate<String, Object> rt = (RedisTemplate<String, Object>) cache.getRedisTemplate();
                rt.rename(tmpKey, MAX_COINS_RANK_KEY);
            } catch (Exception e) {
                log.error("重命名maxCoins快照键失败: {}", e.getMessage());
                // 失败时删除临时键，避免泄漏
                cache.del(tmpKey);
                throw e;
            }

            log.info("maxCoins排行榜缓存刷新完成，写入 {} 条记录", added);
        } catch (Exception e) {
            log.error("刷新maxCoins排行榜缓存失败: {}", e.getMessage());
        }
    }

    /**
     * 定时任务：每5分钟自动刷新排行榜缓存
     */
    @Scheduled(fixedRate = 300000) // 5分钟 = 300000毫秒
    public void scheduledRefreshRankCache() {
        log.info("定时任务：开始刷新排行榜缓存");
        refreshCoinsRankCache();
        refreshMaxCoinsRankCache();
        log.info("定时任务：排行榜缓存刷新完成");
    }

}
