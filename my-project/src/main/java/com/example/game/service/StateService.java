package com.example.game.service;

import com.example.game.dragons.core.Field;

public interface StateService {
    void setField(String userName, Field field);
    Field getField(String userName);
}
