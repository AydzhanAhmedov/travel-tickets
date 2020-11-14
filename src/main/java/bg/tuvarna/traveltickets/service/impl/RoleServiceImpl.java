package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.Role;
import bg.tuvarna.traveltickets.service.RoleService;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;

public final class RoleServiceImpl implements RoleService {

    private static final Logger LOG = LogManager.getLogger(RoleServiceImpl.class);

    private final List<Role> rolesCache;
    private final Map<Role.Enum, Role> rolesByNameCache;

    @Override
    public List<Role> findAll() {
        return rolesCache;
    }

    @Override
    public Role findByName(final Role.Enum roleName) {
        return rolesByNameCache.get(Objects.requireNonNull(roleName));
    }

    private static RoleServiceImpl instance;

    public static RoleServiceImpl getInstance() {
        if (instance == null) {
            synchronized (RoleServiceImpl.class) {
                if (instance == null)
                    instance = new RoleServiceImpl();
            }
        }
        return instance;
    }

    private RoleServiceImpl() {
        rolesCache = JpaOperationsUtil.execute(em ->
                em.createQuery("FROM Role", Role.class)
                        .getResultStream()
                        .collect(toUnmodifiableList())
        );

        rolesByNameCache = rolesCache.stream()
                .collect(toUnmodifiableMap(Role::getName, Function.identity()));

        LOG.info("{} instantiated, roles fetched and cached.", getClass());
    }

}
