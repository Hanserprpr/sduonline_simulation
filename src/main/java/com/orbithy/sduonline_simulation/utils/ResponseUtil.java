package com.orbithy.sduonline_simulation.utils;

import com.orbithy.sduonline_simulation.data.vo.Result;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static ResponseEntity<Result> build(Result result) {
        return ResponseEntity
                .status(result.httpStatus())
                .body(result);
    }
}
