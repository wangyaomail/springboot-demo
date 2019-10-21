package demo.user.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import demo.common.dao.BaseDao;
import demo.common.service.BaseCRUDService;
import demo.common.util.set.HBCollectionUtil;
import demo.user.bean.enums.HBRoleEnum;
import demo.user.bean.http.HBUserBasic;
import demo.user.bean.mongo.HBUserGroup;
import demo.user.dao.UserGroupDao;

@Service
public class UserGroupService extends BaseCRUDService<HBUserGroup> {
    @Resource
    private UserGroupDao userGroupDao;

    @Override
    public BaseDao<HBUserGroup> dao() {
        return userGroupDao;
    }

    /**
     * 根据用户所在组的信息和用户本身的信息判断用户所在的用户组
     */
    public List<String> findLeaderByUser(String userid,
                                         List<String> groups) {
        List<String> leadGroup = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(groups) && StringUtils.isNotEmpty(userid)) {
            for (String group : groups) {
                HBUserGroup theGroup = userGroupDao.findOne(group);
                if (theGroup.getLeader() != null && userid.equals(theGroup.getLeader().getId())) {
                    leadGroup.add(theGroup.getId());
                } else if (CollectionUtils.isNotEmpty(theGroup.getViceLeaders())) {
                    for (HBUserBasic user : theGroup.getViceLeaders()) {
                        if (userid.equals(user.getId())) {
                            leadGroup.add(theGroup.getId());
                        }
                    }
                }
            }
        }
        return leadGroup;
    }

    /**
     * 获取所有某用户属于leader的组
     */
    public List<String> findLeaderByUser(String userid) {
        List<String> leadGroup = new ArrayList<>();
        if (StringUtils.isNotBlank(userid)) {
            leadGroup = dao().findAll()
                             .stream()
                             .filter(group -> ((group.getLeader() != null
                                     && group.getLeader().getId() != null
                                     && group.getLeader().getId().equals(userid))
                                     || (group.getViceLeaders() != null
                                             && group.getViceLeaders()
                                                     .stream()
                                                     .filter(u -> u.getId().equals(userid))
                                                     .count() > 0)))
                             .map(group -> group.getId())
                             .collect(Collectors.toList());
        }
        return leadGroup;
    }

    /**
     * 根据用戶組的id列表得到所有的role
     */
    public Set<String> getAllUserRolesByGroup(List<String> groupids) {
        Set<String> userRoles = new HashSet<>();
        if (CollectionUtils.isNotEmpty(groupids)) {
            Collection<HBUserGroup> groups = dao().findAll(groupids);
            if (CollectionUtils.isNotEmpty(groups)) {
                for (HBUserGroup group : groups) {
                    if (CollectionUtils.isNotEmpty(group.getRoles())) {
                        userRoles.addAll(group.getRoles());
                    }
                }
            }
        }
        return userRoles;
    }

    /**
     * 获取后台初始化用户组需要的数据
     */
    public Collection<HBUserGroup> findBackendResources() {
        Collection<HBUserGroup> bGroups = userGroupDao.findAll(HBCollectionUtil.getMapSplit("roles",
                                                                                            HBRoleEnum.ADMIN_ROLE.getName()));
        return bGroups;
    }
}
