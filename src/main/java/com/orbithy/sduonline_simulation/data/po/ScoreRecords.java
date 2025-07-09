package com.orbithy.sduonline_simulation.data.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("score_records")
public class ScoreRecords {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String sub;
    private Long durationSeconds;
    private String ip;
    private String userAgent;
    private String level;
    private String logLevel;
    private String message;
    private String createdAt;
}
