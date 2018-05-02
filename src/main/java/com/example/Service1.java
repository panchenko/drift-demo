package com.example;

import io.airlift.drift.annotations.ThriftMethod;
import io.airlift.drift.annotations.ThriftService;

@ThriftService
public interface Service1 {
    @ThriftMethod
    int method1(int param);
}
