package com.ericsson.gic.tms.tvs.domain.repositories;

import com.ericsson.gic.tms.tvs.domain.model.MongoEntity;
import com.ericsson.gic.tms.tvs.domain.model.listeners.BeforeSaveListener;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import com.mongodb.WriteResult;
import org.jongo.Find;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.jongo.Oid;
import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.google.common.collect.Lists.*;
import static java.util.stream.Collectors.*;

@NoRepositoryBean
public abstract class BaseJongoRepository<T extends MongoEntity<ID>, ID extends Serializable> {
    private static final String DEFAULT_ID_FIELD_NAME = "_id";

    @Autowired
    private Jongo jongo;

    @Autowired(required = false)
    private List<BeforeSaveListener> listeners;

    private String idFieldName;
    private Class<T> type;

    protected BaseJongoRepository(Class<T> type) {
        this(type, DEFAULT_ID_FIELD_NAME);
    }

    protected BaseJongoRepository(Class<T> type, String idFieldName) {
        this.idFieldName = idFieldName;
        this.type = type;
    }

    protected abstract String getCollectionName();

    protected Query cloneQuery(Query query) {
        return Optional.ofNullable(query)
            .map(Query::new)
            .orElse(new Query());
    }

    protected MongoCollection getCollection() {
        return jongo.getCollection(getCollectionName());
    }

    protected T findOneBy(String query, Object... args) {
        return getCollection()
            .findOne(query, args)
            .as(type);
    }

    protected Iterable<T> findBy(String query, Object... args) {
        return getCollection()
            .find(query, args)
            .as(type);
    }

    protected Page<T> findBy(String query, Pageable pageable, Object... args) {
        Find find = getCollection()
            .find(query, args);

        return paginate(find, pageable, () -> count(query, args));
    }

    protected T findOneBy(Query query) {
        return getCollection()
            .findOne(query.toString(), query.getQueryParameters().toArray())
            .as(type);
    }

    protected Iterable<T> findBy(Query query) {
        return getCollection()
            .find(query.toString(), query.getQueryParameters().toArray())
            .as(type);
    }

    protected Page<T> findBy(Query q, Pageable pageable) {
        String filter = q.toString();
        Object[] queryParameters = q.getQueryParameters().toArray();
        Find find = getCollection()
            .find(filter, queryParameters);

        return paginate(find, pageable, () -> count(filter, queryParameters));
    }

    public Iterable<T> findAll(Sort sort) {
        return getCollection()
            .find()
            .sort(getMongoSortString(sort))
            .as(type);
    }

    public Page<T> findAll(Pageable pageable) {
        Find find = getCollection().find();
        return paginate(find, pageable, this::count);
    }

    protected Page<T> paginate(Find findPipe, Pageable pageable, Supplier<Long> consumer) {
        MongoCursor<T> result = findPipe
            .sort(getMongoSortString(pageable))
            .skip(pageable.getPageSize() * pageable.getPageNumber())
            .limit(pageable.getPageSize())
            .as(type);

        return new PageImpl<>(newArrayList(result.iterator()), pageable, consumer.get());
    }

    @SuppressWarnings("unchecked")
    public T save(T entity) {
        getCollectionListeners().stream()
            .forEach(listener -> listener.onBeforeSave(entity));

        WriteResult save = getCollection()
            .save(entity);

        if (save.getUpsertedId() != null) {
            entity.setId((ID) save.getUpsertedId().toString());
        }
        return entity;
    }

    public Iterable<T> save(Iterable<T> entities) {
        entities.forEach(this::save);

        return entities;
    }

    public T findOne(ID id) {
        return getCollection()
            .findOne(getIdFieldQuery(id))
            .as(type);
    }

    public boolean exists(ID id) {
        return findOne(id) != null;
    }

    public Iterable<T> findAll() {
        return getCollection()
            .find()
            .as(type);
    }

    public Iterable<T> findAll(Iterable<ID> ids) {
        return getCollection()
            .find("{" + idFieldName + ": {$in: #}}", ids)
            .as(type);
    }

    public long count() {
        return getCollection()
            .count();
    }

    public long count(String query, Object... args) {
        return getCollection()
            .count(query, args);
    }

    public void delete(ID id) {
        getCollection()
            .remove(getIdFieldQuery(id));
    }

    public void delete(T entity) {
        getCollection()
            .remove(getIdFieldQuery(entity.getId()));
    }

    public void delete(Iterable<T> entities) {
        Set<ID> ids = StreamSupport.stream(entities.spliterator(), false)
            .map(MongoEntity::getId)
            .collect(toSet());

        getCollection()
            .remove("{" + idFieldName + ": {$in: #}}", ids);
    }

    public void deleteAll() {
        getCollection()
            .remove();
    }

    public static String getMongoSortString(Pageable pageable) {
        return getMongoSortString(pageable.getSort());
    }

    public static String getMongoSortString(Sort sort) {
        if (sort == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder("{");
        Iterator<Sort.Order> iterator = sort.iterator();
        while (iterator.hasNext()) {
            Sort.Order order = iterator.next();
            sb.append(order.getProperty()).append(":");
            if (Sort.Direction.ASC.equals(order.getDirection())) {
                sb.append(1);
            } else {
                sb.append(-1);
            }

            if (iterator.hasNext()) {
                sb.append(",");
            }
        }

        return sb.append("}").toString();
    }

    private Set<BeforeSaveListener> getCollectionListeners() {
        return listeners.stream()
            .filter(listener ->
                Stream.of(listener.getClass().getGenericInterfaces())
                    .map(ParameterizedType.class::cast)
                    .map(ParameterizedType::getActualTypeArguments)
                    .flatMap(Stream::of)
                    .map(typeItem -> (Class<?>) typeItem)
                    .anyMatch(aClass -> aClass.isAssignableFrom(type)))
            .collect(toSet());
    }

    private String getIdFieldQuery(ID id) {
        if (hasMongoObjectId()) {
            return Oid.withOid(String.valueOf(id));
        } else {
            return "{" + idFieldName + ": \"" + id + "\" }";
        }
    }

    private boolean hasMongoObjectId() {
        long count = Stream.of(type.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(MongoId.class))
            .filter(field -> field.isAnnotationPresent(MongoObjectId.class))
            .filter(field -> field.getName().equals(idFieldName) ||
                (idFieldName.equals(DEFAULT_ID_FIELD_NAME) && "id".equals(field.getName())))
            .count();
        return count > 0;
    }
}
