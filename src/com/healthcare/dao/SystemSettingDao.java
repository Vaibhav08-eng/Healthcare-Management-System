package com.healthcare.dao;

import com.healthcare.model.SystemSetting;

import java.util.List;
import java.util.Optional;

public interface SystemSettingDao {
    List<SystemSetting> findAll();

    Optional<SystemSetting> findByKey(String key);

    void saveOrUpdate(SystemSetting setting);
}

