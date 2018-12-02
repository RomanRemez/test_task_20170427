package com.opinta.dao;

import com.opinta.entity.Client;
import com.opinta.entity.Shipment;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Repository
public class ShipmentDaoImpl implements ShipmentDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public ShipmentDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Shipment> getAll() {
        Session session = sessionFactory.getCurrentSession();
        List<Shipment> shipments = session.createCriteria(Shipment.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
        initializeCollections(shipments.toArray(new Shipment[0]));
        return shipments;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Shipment> getAllByClient(Client client) {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Shipment.class)
                .add(Restrictions.eq("sender", client))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public Shipment getById(long id) {
        Session session = sessionFactory.getCurrentSession();
        Shipment shipment = (Shipment)session.get(Shipment.class, id);
        initializeCollections(shipment);
        return shipment;
    }

    @Override
    public Shipment save(Shipment shipment) {
        Session session = sessionFactory.getCurrentSession();
        return (Shipment) session.merge(shipment);
    }

    @Override
    public void update(Shipment shipment) {
        Session session = sessionFactory.getCurrentSession();
        session.update(shipment);
    }

    @Override
    public void delete(Shipment shipment) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(shipment);
    }

    private void initializeCollections(Shipment... shipments) {
        Arrays.stream(shipments).filter(Objects::nonNull).forEach(shipment ->
                shipment.getParcels().forEach(parcel -> Hibernate.initialize(parcel.getParcelItems()))
        );
    }
}
