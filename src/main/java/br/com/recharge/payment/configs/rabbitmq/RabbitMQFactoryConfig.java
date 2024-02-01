package br.com.recharge.payment.configs.rabbitmq;

import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RabbitMQFactoryConfig {

	@Value("${rabbitmq.max-attempts}")
	private Integer queueMaxAttempts;

	private final ConnectionFactory connectionFactory;

	@Bean
	SimpleRabbitListenerContainerFactory rabbitListenerRequestFactory() {
		return createFactory(8, 18, queueMaxAttempts);
	}

	private SimpleRabbitListenerContainerFactory createFactory(int concurrentConsumers, int maxConcurrentConsumers,
			Integer maxAttempts) {

		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setConcurrentConsumers(concurrentConsumers);
		factory.setMaxConcurrentConsumers(maxConcurrentConsumers);
		factory.setPrefetchCount(1);
		factory.setAdviceChain(retries(maxAttempts));
		factory.setContainerCustomizer(container -> container.setShutdownTimeout(TimeUnit.SECONDS.toMillis(30)));
		return factory;
	}

	private RetryOperationsInterceptor retries(Integer maxAttempts) {
		return RetryInterceptorBuilder.stateless().maxAttempts(maxAttempts)
				.recoverer(new RejectAndDontRequeueRecoverer()).build();
	}
}
