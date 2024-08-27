package alba.hibernate_starter;


import alba.hibernate_starter.converter.BirthdayConverter;
import alba.hibernate_starter.entity.Birthday;
import alba.hibernate_starter.entity.Company;
import alba.hibernate_starter.entity.Role;
import alba.hibernate_starter.entity.User;
import alba.hibernate_starter.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class HibernateRunner {

    private static final Logger logger = LoggerFactory.getLogger(HibernateRunner.class);

    public static void main(String[] args) {

        Company company = Company.builder()
                .name("Sovcombank")
                .build();

        //Transient
        User user = User.builder()
                    .username("alba1737@mail.ru")
                    .birthdate(new Birthday(LocalDate.of(1998,12,13)))
                    .lastname("khakimov")
                    .firstname("alba")
                    .role(Role.ADMIN)
                    .company(company)
                    .build();
        logger.info("User object on tranisent state: {}", user);
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory()){
            try (Session session1 = sessionFactory.openSession()) {

                session1.beginTransaction();
                //Persistence session 1 tranisent session2
                session1.saveOrUpdate(company);
                session1.saveOrUpdate(user);
//                session1.evict(user);
//                session1.flush();
                logger.warn("User object on tranisent state: {}", user);
                session1.getTransaction().commit();
            }

        }

        }

}

