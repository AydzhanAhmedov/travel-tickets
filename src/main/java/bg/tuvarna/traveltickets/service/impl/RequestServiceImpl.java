package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.entity.Distributor;
import bg.tuvarna.traveltickets.entity.RequestStatus;
import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.entity.TravelDistributorRequest;
import bg.tuvarna.traveltickets.repository.RequestRepository;
import bg.tuvarna.traveltickets.repository.TravelRepository;
import bg.tuvarna.traveltickets.repository.impl.RequestRepositoryImpl;
import bg.tuvarna.traveltickets.repository.impl.TravelRepositoryImpl;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.RequestService;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static bg.tuvarna.traveltickets.entity.ClientType.Enum.DISTRIBUTOR;
import static bg.tuvarna.traveltickets.entity.RequestStatus.Enum.APPROVED;
import static bg.tuvarna.traveltickets.entity.RequestStatus.Enum.PENDING;
import static bg.tuvarna.traveltickets.entity.RequestStatus.Enum.REJECTED;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;

public class RequestServiceImpl implements RequestService {

    private static final Logger LOG = LogManager.getLogger(RequestServiceImpl.class);

    private final Map<Long, RequestStatus> requestStatusByIdCache;
    private final Map<RequestStatus.Enum, RequestStatus> requestStatusByNameCache;

    private final TravelRepository travelRepository = TravelRepositoryImpl.getInstance();
    private final RequestRepository requestRepository = RequestRepositoryImpl.getInstance();

    private final AuthService authService = AuthServiceImpl.getInstance();

    @Override
    public List<TravelDistributorRequest> findAll() {
        final Long clientId = authService.getLoggedUser().getId();
        final ClientType.Enum clientTypeName = authService.getLoggedClientTypeName();

        return switch (clientTypeName) {
            case DISTRIBUTOR -> travelRepository.findAllRequestsByDistributorId(clientId);
            case COMPANY -> travelRepository.findAllRequestsByCompanyIdAndRequestStatusId(clientId, findStatusByName(PENDING).getId());
            default -> throw new RuntimeException("Only distributors and companies are able to view requests!");
        };
    }

    @Override
    public TravelDistributorRequest createRequest(final Travel travel) {
        if (!DISTRIBUTOR.equals(authService.getLoggedClientTypeName())) {
            throw new RuntimeException("Only distributors can create requests for travels!");
        }
        return travelRepository.save(new TravelDistributorRequest(travel, (Distributor) (authService.getLoggedClient())));
    }

    @Override
    public void acceptRequest(final TravelDistributorRequest travelDistributorRequest) {
        travelDistributorRequest.setRequestStatus(findStatusByName(APPROVED));
        requestRepository.save(travelDistributorRequest);
    }

    @Override
    public void declineRequest(final TravelDistributorRequest travelDistributorRequest) {
        travelDistributorRequest.setRequestStatus(findStatusByName(REJECTED));
        requestRepository.save(travelDistributorRequest);
    }

    @Override
    public RequestStatus findStatusById(final Long id) {
        return requestStatusByIdCache.get(Objects.requireNonNull(id));
    }

    @Override
    public RequestStatus findStatusByName(final RequestStatus.Enum requestStatusName) {
        return requestStatusByNameCache.get(Objects.requireNonNull(requestStatusName));
    }

    private static RequestServiceImpl instance;

    public static RequestServiceImpl getInstance() {
        if (instance == null) {
            synchronized (RequestServiceImpl.class) {
                if (instance == null)
                    instance = new RequestServiceImpl();
            }
        }
        return instance;
    }

    private RequestServiceImpl() {
        final List<RequestStatus> statuses = JpaOperationsUtil.execute(em ->
                em.createQuery("FROM RequestStatus", RequestStatus.class)
                        .getResultStream()
                        .collect(toUnmodifiableList())
        );

        requestStatusByIdCache = statuses.stream()
                .collect(toUnmodifiableMap(RequestStatus::getId, Function.identity()));

        requestStatusByNameCache = statuses.stream()
                .collect(toUnmodifiableMap(RequestStatus::getName, Function.identity()));

        LOG.info("{} instantiated, request statuses fetched and cached.", getClass());
    }

}
