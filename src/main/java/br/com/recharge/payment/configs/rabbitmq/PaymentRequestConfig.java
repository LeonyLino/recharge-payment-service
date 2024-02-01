package br.com.recharge.payment.configs.rabbitmq;

import java.util.concurrent.TimeUnit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class PaymentRequestConfig {

	@Value("${rabbitmq.payment.request.routing-key.main}")
	private String paymentRequestRoutingKey;

	@Value("${rabbitmq.payment.request.queues.main}")
	private String paymentRequestQueue;

	@Value("${rabbitmq.payment.request.queues.dead}")
	private String deadPaymentRequestQueue;

	@Bean
	Queue paymentRequestQueue() {
		return QueueBuilder.durable(paymentRequestQueue).withArgument("x-dead-letter-exchange", "")
				.withArgument("x-dead-letter-routing-key", deadPaymentRequestQueue)
				.withArgument("x-message-ttl", TimeUnit.MINUTES.toMillis(20)).build();
	}

	@Bean
	Queue deadPaymentRequestQueue() {
		return QueueBuilder.durable(deadPaymentRequestQueue).build();
	}

	@Lazy
	@Bean
	Binding bindingPaymentRequest(TopicExchange processosExchange) {
		return BindingBuilder.bind(paymentRequestQueue()).to(processosExchange).with(paymentRequestRoutingKey);
	}

}
