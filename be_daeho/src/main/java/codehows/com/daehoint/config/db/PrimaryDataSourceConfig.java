package codehows.com.daehoint.config.db;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * <b>PrimaryDataSourceConfig</b><br>
 * Primary 데이터베이스(MySQL) 연결 및 JPA 설정을 관리하는 설정 클래스입니다.<br><br>
 *
 * <b>주요 기능:</b><br>
 * - Primary 데이터베이스에 대한 `DataSource`, JPA `EntityManagerFactory`, `TransactionManager`를 설정.<br>
 * - Hibernate 설정 및 Naming Strategy를 지정.<br><br>
 *
 * <b>구성 요소:</b><br>
 * 1. `mysqlDataSourceProperties()`:<br>
 *    - Primary 데이터베이스 연결 정보를 포함하는 `DataSourceProperties`를 반환.<br>
 * 2. `primaryDataSource()`:<br>
 *    - `DataSourceProperties`를 기반으로 `HikariDataSource`를 초기화.<br>
 * 3. `primaryEntityManagerFactory()`:<br>
 *    - Primary 데이터베이스를 위한 JPA `EntityManagerFactory`를 생성.<br>
 *    - 엔티티 패키지 경로와 Hibernate 설정을 포함.<br>
 * 4. `platformTransactionManager()`:<br>
 *    - JPA 트랜잭션 관리자를 생성.<br>
 * 5. `hibernateProperties()`:<br>
 *    - Hibernate 관련 프로퍼티를 설정.<br>
 *      - `hibernate.hbm2ddl.auto`: DDL 자동 생성 옵션.<br>
 *      - `hibernate.format_sql`: SQL 포맷팅 옵션.<br>
 *      - `hibernate.show_sql`: SQL 출력 옵션.<br>
 *      - `hibernate.use_sql_comments`: SQL 주석 출력 옵션.<br>
 *      - `hibernate.generate_statistics`: Hibernate 통계 로그 옵션.<br>
 *      - `hibernate.physical_naming_strategy`: Naming Strategy 지정.<br><br>
 *
 * <b>주요 어노테이션:</b><br>
 * - `@Configuration`: 스프링 설정 클래스임을 나타냄.<br>
 * - `@EnableJpaRepositories`: JPA Repository를 활성화하고, 엔티티 관리자 및 트랜잭션 관리자 참조를 설정.<br>
 * - `@Primary`: 기본 `DataSource`, `EntityManagerFactory`, `TransactionManager`로 설정.<br>
 * - `@ConfigurationProperties`: 설정 파일에서 데이터베이스 관련 프로퍼티를 읽어옴.<br><br>
 *
 * <b>설정 경로:</b><br>
 * - 데이터베이스 연결 설정: `spring.datasource`<br>
 * - HikariCP 설정: `spring.datasource.hikari`<br><br>
 *
 * <b>엔티티 경로:</b><br>
 * - `codehows.com.daehoint.entity`<br><br>
 *
 * <b>Repository 경로:</b><br>
 * - `codehows.com.daehoint.repository`<br>
 */
@Configuration
@EnableJpaRepositories(
	basePackages = "codehows.com.daehoint.repository",
	entityManagerFactoryRef = "primaryEntityManagerFactory",
	transactionManagerRef = "primaryTransactionManager"
)
public class PrimaryDataSourceConfig {
	private static final String DEFAULT_NAMING_STRATEGY = "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy";

	@Bean(name = "primaryDataSourceProperties")
	@Primary
	@ConfigurationProperties("spring.datasource")
	public DataSourceProperties mysqlDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@Primary
	@ConfigurationProperties("spring.datasource.hikari")
	public DataSource primaryDataSource() {
		return mysqlDataSourceProperties()
			.initializeDataSourceBuilder()
			.type(HikariDataSource.class)
			.build();
	}

	@Bean(name = "primaryEntityManagerFactory")
	@Primary
	public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(EntityManagerFactoryBuilder builder
		, @Qualifier("primaryDataSource") DataSource dataSource) {
		return builder
			.dataSource(dataSource)
			.packages("codehows.com.daehoint.entity")
			.persistenceUnit("primaryEntityManager")
			.properties(hibernateProperties())
			.build();
	}

	@Bean(name = "primaryTransactionManager")
	@Primary
	public PlatformTransactionManager platformTransactionManager(
		final @Qualifier("primaryEntityManagerFactory") LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) {
		return new JpaTransactionManager(localContainerEntityManagerFactoryBean.getObject());
	}

	private Map<String, Object> hibernateProperties() {
		Map<String, Object> map = new HashMap<>();
		map.put("hibernate.hbm2ddl.auto", "update");  // ddl-auto 설정
		map.put("hibernate.format_sql", "true");     // SQL 포맷팅 비활성화
		map.put("hibernate.show_sql", "true");       // SQL 출력 비활성화
		map.put("hibernate.use_sql_comments", "false"); // SQL 주석 출력 비활성화
		map.put("hibernate.generate_statistics", "false"); // Hibernate 통계 로그 비활성화
		map.put("hibernate.physical_naming_strategy", DEFAULT_NAMING_STRATEGY);
		return map;
	}

}

