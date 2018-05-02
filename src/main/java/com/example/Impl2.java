package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Impl2 implements Service2 {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public int method2(int param) {
        logger.info("invoked method2(" + param + ")");
        return param + 2;
    }
}
