package com.glsx.plat.loggin;

public enum LogginStrategy {

    MONGODB("mongo"), KAFKA("kafka"), MYSQL("mysql");

    private String strategy;

    LogginStrategy(String strategy) {
        this.strategy = strategy;
    }

}
