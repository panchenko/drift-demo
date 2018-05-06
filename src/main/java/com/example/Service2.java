package com.example;

import io.airlift.drift.annotations.ThriftMethod;
import io.airlift.drift.annotations.ThriftService;

@ThriftService
public interface Service2 {
    @ThriftMethod("method2")
    int method(int param);
}
