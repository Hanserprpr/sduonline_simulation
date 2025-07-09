package com.orbithy.sduonline_simulation.utils;

import com.orbithy.sduonline_simulation.data.po.ScoreRecords;
import com.orbithy.sduonline_simulation.mapper.LogMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Component;

@Component
public class LogUtil {

    private final LogMapper logMapper;

    public LogUtil(LogMapper logMapper) {
        this.logMapper = logMapper;
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
        ScoreRecords records = initLog(sub, duration_seconds, request, level, message);
        records.setLogLevel("ERROR");
        logMapper.insert(records);
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
        ScoreRecords records = initLog(sub, duration_seconds, request, level, message);
        records.setLogLevel("INFO");
        logMapper.insert(records);
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
