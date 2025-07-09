package com.orbithy.sduonline_simulation.service;

import com.orbithy.sduonline_simulation.annotation.sub;
import com.orbithy.sduonline_simulation.cache.IGlobalCache;
import com.orbithy.sduonline_simulation.data.po.Temp;
import com.orbithy.sduonline_simulation.data.vo.Result;
import com.orbithy.sduonline_simulation.mapper.TempMapper;
import com.orbithy.sduonline_simulation.utils.LogUtil;
import com.orbithy.sduonline_simulation.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.jdbc.Null;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class ProgressService {

    private final TempMapper tempMapper;
    private final LogUtil logUtil;
    private final IGlobalCache redis;

    public ProgressService(TempMapper tempMapper, LogUtil logUtil, IGlobalCache redis) {
        this.tempMapper = tempMapper;
        this.logUtil = logUtil;
        this.redis = redis;
    }

    public ResponseEntity<Result> begin(@sub String sub, String level, HttpServletRequest request) {
        Temp temp = tempMapper.findBySub(sub);
        List<String> levels = temp.getCompleted_levels();
        // 如果level在已完成关卡列表中，返回错误
        if (levels != null && levels.contains(level)) {
            logUtil.error(sub, null, request, level, "重复开始关卡");
            return ResponseUtil.build(Result.error(403,"Level already completed"));
        }
        redis.set(sub, System.currentTimeMillis());
        return ResponseUtil.build(Result.ok());
    }

    public ResponseEntity<Result> end(@sub String sub, String level, HttpServletRequest request) {
        Temp temp = tempMapper.findBySub(sub);
        List<String> levels = temp.getCompleted_levels();
        if (levels != null && levels.contains(level)) {
            logUtil.error(sub, null, request, level, "关卡重复结束");
            return ResponseUtil.build(Result.error(403,"Level not started"));
        }
        // redis 中没有记录，关卡未开始
        if (!redis.hasKey(sub)) {
            logUtil.error(sub, null, request, level, "关卡未开始");
            return ResponseUtil.build(Result.error(403,"Level not started"));
        }
        long startTime = redis.get(sub) != null ? (long) redis.get(sub) : 0;
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;

        if (levels != null) {
            levels.add(level);
        }
        temp.setCompleted_levels(levels);
        temp.setTotal_duration(temp.getTotal_duration() + duration);

        tempMapper.updateById(temp);
        redis.del(sub);

        logUtil.info(sub, duration, request, level, "关卡完成");
        return ResponseUtil.build(Result.ok());
    }
}
