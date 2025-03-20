package codehows.com.daehoint.config.db;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * <b>ERPDataSourceConfig</b><br>
 * ERP 데이터베이스의 연결 및 MyBatis 설정을 관리하는 설정 클래스입니다.<br><br>
 *
 * <b>주요 기능:</b><br>
 * - ERP 데이터베이스에 대한 `DataSource` 및 MyBatis `SqlSessionFactory`, `SqlSessionTemplate`을 정의.<br>
 * - HikariCP를 사용하여 데이터베이스 연결 풀을 관리.<br>
 * - MyBatis 설정 파일(`mybatis-config.xml`)과 매퍼 패키지를 지정.<br><br>
 *
 * <b>구성 요소:</b><br>
 * 1. `erpDataSourceProperties()`:<br>
 *    - ERP 데이터베이스 연결 정보를 포함하는 `DataSourceProperties`를 반환.<br>
 * 2. `erpDataSource()`:<br>
 *    - `DataSourceProperties`를 기반으로 `HikariDataSource`를 초기화.<br>
 * 3. `sqlSessionFactory()`:<br>
 *    - ERP 데이터베이스를 위한 MyBatis `SqlSessionFactory`를 생성.<br>
 *    - MyBatis 설정 파일 경로를 지정.<br>
 * 4. `sqlSessionTemplate()`:<br>
 *    - `SqlSessionFactory`를 사용하여 MyBatis `SqlSessionTemplate` 생성.<br><br>
 *
 * <b>주요 어노테이션:</b><br>
 * - `@Configuration`: 스프링 설정 클래스임을 나타냄.<br>
 * - `@MapperScan`: ERP 매퍼 인터페이스의 기본 패키지와 `SqlSessionFactory` 참조를 지정.<br>
 * - `@ConfigurationProperties`: 설정 파일에서 데이터베이스 관련 프로퍼티를 읽어옴.<br><br>
 *
 * <b>설정 경로:</b><br>
 * - 데이터베이스 연결 설정: `spring.erp`<br>
 * - HikariCP 설정: `spring.erp.hikari`<br><br>
 *
 * <b>파일 경로:</b><br>
 * - MyBatis 설정 파일: `classpath:mybatis/mybatis-config.xml`<br>
 */
@Configuration
@MapperScan(basePackages = "codehows.com.daehoint.mapper.erp", sqlSessionFactoryRef = "erpSqlSessionFactory")
public class ERPDataSourceConfig {

	@Bean(name = "erpDataSourceProperties")
	@ConfigurationProperties("spring.erp")
	public DataSourceProperties erpDataSourceProperties(){
		return new DataSourceProperties();
	}

	@Bean
	@ConfigurationProperties("spring.erp.hikari")
	public DataSource erpDataSource(){
		return erpDataSourceProperties()
			.initializeDataSourceBuilder()
			.type(HikariDataSource.class)
			.build();
	}

	@Bean(name = "erpSqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory(
		@Qualifier("erpDataSource")DataSource dataSource, ApplicationContext applicationContext
	) throws Exception{
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(dataSource);
		factoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis/mybatis-config.xml"));

		return factoryBean.getObject();
	}

	@Bean(name = "erpSqlSessionTemplate")
	public SqlSessionTemplate sqlSessionTemplate(
		@Qualifier("erpSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

}
