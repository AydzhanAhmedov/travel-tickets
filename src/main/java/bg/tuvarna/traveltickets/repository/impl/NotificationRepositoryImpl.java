package bg.tuvarna.traveltickets.repository.impl;

import bg.tuvarna.traveltickets.entity.Notification;
import bg.tuvarna.traveltickets.entity.NotificationRecipient;
import bg.tuvarna.traveltickets.repository.NotificationRepository;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepositoryImpl;
import bg.tuvarna.traveltickets.util.EntityManagerUtil;

import javax.persistence.EntityManager;
import java.util.List;

import static bg.tuvarna.traveltickets.common.Constants.USER_ID_PARAM;

public class NotificationRepositoryImpl extends GenericCrudRepositoryImpl<Notification, Long> implements NotificationRepository {

    private static final String FIND_ALL_BY_RECIPIENT_ID_HQL = """
                SELECT nr FROM NotificationRecipient AS nr
                LEFT JOIN FETCH nr.notification AS n
                LEFT JOIN FETCH n.createdBy
                WHERE nr.recipient.id = :userId
                ORDER BY n.createdAt
            """;

    @Override
    public NotificationRecipient save(final NotificationRecipient notificationRecipient) {
        final EntityManager entityManager = EntityManagerUtil.getEntityManager();

        if (notificationRecipient.getNotificationRecipientID() != null) {
            return entityManager.merge(notificationRecipient);
        }

        entityManager.persist(notificationRecipient);
        return notificationRecipient;
    }

    @Override
    public List<NotificationRecipient> findAllByRecipientId(final Long recipientId) {
        return EntityManagerUtil.getEntityManager()
                .createQuery(FIND_ALL_BY_RECIPIENT_ID_HQL, NotificationRecipient.class)
                .setParameter(USER_ID_PARAM, recipientId)
                .getResultList();
    }

    private static NotificationRepositoryImpl instance;

    public static NotificationRepositoryImpl getInstance() {
        if (instance == null) {
            synchronized (NotificationRepositoryImpl.class) {
                if (instance == null)
                    instance = new NotificationRepositoryImpl();
            }
        }
        return instance;
    }

    private NotificationRepositoryImpl() {
        super();
    }

}
