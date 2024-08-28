package com.ericsson.gic.tms.tvs.domain.repositories;

import com.ericsson.gic.tms.tvs.domain.model.verdict.UserMetricsObject;
import com.ericsson.gic.tms.tvs.domain.model.verdict.UserSessionEntity;
import org.jongo.Aggregate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.USER_SESSION;

@Repository
public class UserSessionRepository extends BaseJongoRepository<UserSessionEntity, String> {

    @Override
    protected String getCollectionName() {
        return USER_SESSION.getName();
    }

    public UserSessionRepository() {
        super(UserSessionEntity.class);
    }

    public List<UserMetricsObject> findByDate(Date date) {
        Aggregate aggregate = getCollection().aggregate("{$match : { createdAt: {$gte:#}}}", date)
                .and("{ $group: { _id: {month:{$month: '$createdAt'}, year: {$year: '$createdAt'}}, total: {$sum: 1}}}")
                .and("{ $sort: { _id.year: 1, _id.month: 1 } }");

        Iterator<UserMetricsObject> iterator = aggregate.as(UserMetricsObject.class).iterator();
        ArrayList list = new ArrayList();
        iterator.forEachRemaining(list::add);

        return list;
    }
}
