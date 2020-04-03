package com.adidas.cm.omp.ompawsservice;

import com.integralblue.log4jdbc.spring.Log4jdbcBeanPostProcessor;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;
import net.sf.log4jdbc.sql.Spy;
import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

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