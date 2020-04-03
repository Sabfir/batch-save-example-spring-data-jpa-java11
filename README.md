JAVA 11

## Useful info:
### 1. Batching problem (see https://stackoverflow.com/questions/27697810/hibernate-disabled-insert-batching-when-using-an-identity-identifier-generator)
#### 1.1 To use batching you need only two things: (see https://stackoverflow.com/questions/27697810/hibernate-disabled-insert-batching-when-using-an-identity-identifier-generator)
- set batch_size property
```
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 100
```
- and then simple use saveAll
```
reposetoryArtile.saveAll(articles);
```
#### 1.2 To see it works add dependence:
```
<dependency>
  <groupId>com.integralblue</groupId>
  <artifactId>log4jdbc-spring-boot-starter</artifactId>
  <version>2.0.0</version>
</dependency>
```
and properties
```
logging:
  level:
    jdbc:
      sqlonly: info
      resultsettable: info
      sqltiming: fatal
      audit: fatal
      resultset: fatal
      connection: fatal
```
#### 1.3 In our project to make logging work we also need two steps: see https://github.com/candrews/log4jdbc-spring-boot-starter/issues/5 
- exclude problem class
```
@SpringBootApplication(exclude = {Log4jdbcAutoConfiguration.class})
```
- and override it
```
@Component
public class Spring2Log4jdbcBeanPostProcessor extends Log4jdbcBeanPostProcessor {
  @AllArgsConstructor
  private class WrappedSpyDataSource extends HikariDataSource {
    @Delegate(types = {DataSource.class, Spy.class})
    DataSourceSpy dataSourceSpy;
    @Delegate(types = HikariDataSource.class, excludes = DataSource.class)
    HikariDataSource original;
  }
  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
    throws BeansException {
    if (bean instanceof HikariDataSource) {
      return new WrappedSpyDataSource(
        (DataSourceSpy) super.postProcessBeforeInitialization(bean, beanName),
        (HikariDataSource) bean);
    } else {
      return bean;
    }
  }
}
```
#### 1.4 NOTE: Spring Data JPA batching doesnot work when used GeneratedType.IDENTITY:
```
@GeneratedValue(strategy = GenerationType.IDENTITY)
```
To overcome it we have two solutions:
- use pooled id generator (BEST SOLUTION, IMPLEMENTED IN THIS APPLICATION): https://dev.to/smartyansh/best-possible-hibernate-configuration-for-batch-inserts-2a7a
- use JOOQ: see https://stackoverflow.com/questions/27697810/hibernate-disabled-insert-batching-when-using-an-identity-identifier-generator

## 2. Run and check result
### 2.1 Download
### 2.2 Run main class Application.java
### 2.3 See logs. E.g. batch_size: 10
```
jdbc.sqlonly : batching 10 statements: 1: insert into application$article (art_number, art_id) values ('artNumber_1', 102) 2: insert into application$article (art_number, art_id) values ('artNumber_2', 103)
3: insert into application$article (art_number, art_id) values ('artNumber_3', 104) 
4: insert into application$article (art_number, art_id) values ('artNumber_4', 105) 
5: insert into application$article (art_number, art_id) values ('artNumber_5', 106) 
6: insert into application$article (art_number, art_id) values ('artNumber_6', 107) 
7: insert into application$article (art_number, art_id) values ('artNumber_7', 108) 
8: insert into application$article (art_number, art_id) values ('artNumber_8', 109) 
9: insert into application$article (art_number, art_id) values ('artNumber_9', 110) 
10: insert into application$article (art_number, art_id) values ('artNumber_10', 111) 
```
