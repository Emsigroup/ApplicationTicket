package DbTest;

import java.sql.Connection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class ConnectHibernate {
    public static void main(String[] args) {
        SessionFactory factory = new Configuration().configure().buildSessionFactory();
        Session session = factory.openSession();
        System.out.println("Hibernate fonctionne !");
        ConnectionDb.ConnectDb()   ;
        session.close();
        factory.close();
    }
}

