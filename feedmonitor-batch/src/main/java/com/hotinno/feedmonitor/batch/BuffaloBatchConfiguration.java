package com.hotinno.feedmonitor.batch;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.cloudfoundry.runtime.env.RdbmsServiceInfo;
import org.cloudfoundry.runtime.service.relational.RdbmsServiceCreator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.Assert;

import com.hotinno.feedmonitor.batch.feed.FeedsMonitorBatch;

@ImportResource("/launch-context.xml")
// to enable the use of the @Scheduled annotation
@EnableScheduling
public class BuffaloBatchConfiguration {
	private static final Log log = LogFactory.getLog(BuffaloBatchConfiguration.class);

	@Autowired
	@Qualifier("jobRepository")
	private JobRepository jobRepository;

	@Autowired
	private EntityManagerFactory emf;

    @Bean
    public CloudEnvironment cloudEnvironment() {
        return new CloudEnvironment();
    }

    @Bean
    public DataSource dataSource() {
        Collection<RdbmsServiceInfo> servicesInfosForTheDbms = this.cloudEnvironment().getServiceInfos(RdbmsServiceInfo.class);
        Assert.isTrue(servicesInfosForTheDbms.size() > 0, "please ensure that you have created a PostgreSQL RDBMS and " +
                                                    "bound it appropriately to your Cloud Foundry application instance.");
        RdbmsServiceInfo rdbmsServiceInfo = servicesInfosForTheDbms.iterator().next();
        RdbmsServiceCreator rdbmsServiceCreator = new RdbmsServiceCreator();
        return rdbmsServiceCreator.createService(rdbmsServiceInfo);
    }

	@Bean
	public SimpleJobLauncher jobLauncher() {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		return jobLauncher;
	}

	@Bean
	@Autowired
	@Qualifier("feedsMonitor")
	public FeedsMonitorBatch feedsMonitorBatch(Job job) throws Throwable {
		return new FeedsMonitorBatch(job);
	}

	@Bean
	public ItemReader<String> feedReader() {
		log.debug("Initializing feedReader...");

		JpaPagingItemReader<String> readerOfSymbols = new JpaPagingItemReader<String>();
		readerOfSymbols.setEntityManagerFactory(emf);
		readerOfSymbols.setQueryString("SELECT distinct f.url FROM Feed f");
		readerOfSymbols.setPageSize(100);
        return readerOfSymbols;
	}

}