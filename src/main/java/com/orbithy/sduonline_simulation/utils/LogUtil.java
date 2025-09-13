package com.orbithy.sduonline_simulation.utils;

import com.orbithy.sduonline_simulation.data.po.ScoreRecords;
import com.orbithy.sduonline_simulation.mapper.LogMapper;
import com.orbithy.sduonline_simulation.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Component;

@Component
public class LogUtil {

    private final LogMapper logMapper;
    private final UserMapper userMapper;

    public LogUtil(LogMapper logMapper, UserMapper userMapper) {
        this.logMapper = logMapper;
        this.userMapper = userMapper;
    }

    /**
     * 记录错误日志
     *
     * @param sub              用户的唯一标识
     * @param duration_seconds 关卡持续时间，单位为秒
     * @param request          HttpServletRequest 请求
     * @param level            关卡
     * @param message          错误信息
     */
    public void error(String sub, Long duration_seconds, HttpServletRequest request, String level, String message) {
        // 只有当 sub 不为空且不是 "null" 字符串，并且用户存在时才记录到数据库
        if (sub != null && !sub.trim().isEmpty() && !"null".equalsIgnoreCase(sub.trim())) {
            try {
                // 检查用户是否存在
                if (userMapper.findByCasdoorSub(sub) != null) {
                    ScoreRecords records = initLog(sub, duration_seconds, request, level, message);
                    records.setLogLevel("ERROR");
                    logMapper.insert(records);
                }
            } catch (Exception e) {
                // 如果数据库操作失败，只记录到应用日志，不影响主流程
                LogManager.getLogger(LogUtil.class).warn("Failed to insert log record to database: {}", e.getMessage());
            }
        }
        // 同时记录到应用日志
        LogManager.getLogger(LogUtil.class).error("Level: {}, Sub: {}, Message: {}", level, sub, message);
    }

    /**
     * 记录信息日志
     *
     * @param sub              用户的唯一标识
     * @param duration_seconds 关卡持续时间，单位为秒
     * @param request          HttpServletRequest 请求
     * @param level            关卡
     * @param message          信息内容
     */
    public void info(String sub, Long duration_seconds, HttpServletRequest request, String level, String message) {
        // 只有当 sub 不为空且不是 "null" 字符串，并且用户存在时才记录到数据库
        if (sub != null && !sub.trim().isEmpty() && !"null".equalsIgnoreCase(sub.trim())) {
            try {
                // 检查用户是否存在
                if (userMapper.findByCasdoorSub(sub) != null) {
                    ScoreRecords records = initLog(sub, duration_seconds, request, level, message);
                    records.setLogLevel("INFO");
                    logMapper.insert(records);
                }
            } catch (Exception e) {
                // 如果数据库操作失败，只记录到应用日志，不影响主流程
                LogManager.getLogger(LogUtil.class).warn("Failed to insert log record to database: {}", e.getMessage());
            }
        }
        // 同时记录到应用日志
        LogManager.getLogger(LogUtil.class).info("Level: {}, Sub: {}, Message: {}", level, sub, message);
    }

    private ScoreRecords initLog(String sub, Long duration_seconds, HttpServletRequest request, String level, String message) {
        ScoreRecords records = new ScoreRecords();
        records.setSub(sub);
        records.setDurationSeconds(duration_seconds);
        records.setIp(request.getRemoteAddr());
        records.setUserAgent(request.getHeader("User-Agent"));
        records.setLevel(level);
        records.setMessage(message);
        return records;
    }

}
