package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Impl1 implements Service1 {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public int method(int param) {
        logger.info("invoked method(" + param + ")");
        return param + 1;
    }
}
