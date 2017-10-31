package com.github.nduyhai.multiasync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableAsync
public class MultiAsyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiAsyncApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(final PersonRepository repository) {
        final Person john = new Person();
        john.setName("John Snow");
        repository.save(john);

        final Person arya = new Person();
        arya.setName("Arya Stark");
        repository.save(arya);

        return args -> System.err.println(repository.findAll());
    }


    @Bean
    public Executor statisticExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Statistic-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Executor mailExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Mail-");
        executor.initialize();
        return executor;
    }

}


@RestController
class StatisticController {

    @Autowired
    private StatisticService service;


    
    @GetMapping("/statistic/{personId}")
    public ResponseEntity<String> statistic(@PathVariable Long personId) {
        try {
            service.submit(personId);
            return new ResponseEntity<>("Please wait a minute. We will send result", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error!", HttpStatus.BAD_REQUEST);
        }
    }
}

@Service
class StatisticService {
    private static final Logger LOG = LoggerFactory.getLogger(StatisticService.class);

    @Autowired
    private PersonRepository repository;

    @Autowired
    private MailService mailService;

    @Async("statisticExecutor")
    public void submit(Long personId) {
        LOG.info("Begin process person {}", personId);
        try {
            //This is example for other delay business
            TimeUnit.MILLISECONDS.sleep(3000);
            this.now(personId);
            this.mailService.submit(personId);

        } catch (InterruptedException e) {
            LOG.debug("Oops! just error when sleep.");
        }
        LOG.info("Process person {} completed", personId);
    }

    private void now(final Long personId) {
        Person person = this.repository.findOne(personId);
        if (person != null) {
            Long statistic = person.getStatistic();
            if (statistic == null) {
                statistic = 1L;
            } else {
                statistic++;
            }
            person.setStatistic(statistic);
            this.repository.save(person);
        }
    }
}

@Service
class MailService {
    private static final Logger LOG = LoggerFactory.getLogger(StatisticService.class);

    @Async("mailExecutor")
    public void submit(final Long personId) {
        LOG.info("Begin send statistic of person {}", personId);
        try {
            //This is example for delay of sending email

            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
            LOG.debug("Oops! just error when sleep.");
        }
        LOG.info("Process send statistic of person {} completed", personId);
    }
}
@Repository
interface PersonRepository extends CrudRepository<Person, Long> {
}

@Entity
class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long statistic;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStatistic() {
        return statistic;
    }

    public void setStatistic(Long statistic) {
        this.statistic = statistic;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", statistic=" + statistic +
                '}';
    }
}