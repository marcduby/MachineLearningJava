package com.doobs.mlbeta.util;

import java.util.HashMap;
import java.util.Map;

public class MlUtils {

    /**
     * create an error map of the exception
     *
     * @param exception
     * @return
     */
    public static Map<String, Object> getErrorMap(MlException exception) {
        // local variables
        Map<String, Object> errorMap= new HashMap<String, Object>();

        // set the data
        errorMap.put("is_error", new Boolean(true));
        errorMap.put("message", exception.getMessage());

        // return
        return errorMap;
    }

}
