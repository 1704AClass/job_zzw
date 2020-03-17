package com.ningmeng.ucenter.dao;

import com.ningmeng.framework.domain.ucenter.NmMenu;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface NmMenuMapper {
    public List<NmMenu> selectPermissionByUserId(String userid);
}
