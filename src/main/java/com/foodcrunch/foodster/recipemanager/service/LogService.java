package com.foodcrunch.foodster.recipemanager.service;

import com.foodcrunch.foodster.recipemanager.constant.LogLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogService {

    public String buildLogEvent(String message, LogLevel logLevel, Exception e, Object... args) {
        String notification = MessageFormat.format(message, args);
        if (logLevel.equals(LogLevel.INFO)) {
            log.info("{}", notification);
        }
        if (logLevel.equals(LogLevel.ERROR)) {
            if (e != null) {
                log.error("{} \n {}", notification, e.getLocalizedMessage());
            } else {
                log.error("{}", notification);
            }
        }
        return notification;
    }

}
