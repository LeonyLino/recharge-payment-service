package br.com.recharge.payment.listeners;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import br.com.recharge.payment.models.dto.PaymentDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentRequestQueueListener {

	@RabbitListener(queues = "#{@paymentRequestQueue}", containerFactory = "rabbitListenerRequestFactory", id = "PaymentRequestListener")
	public void handlePayment(PaymentDTO dto) {
		log.info("Starting payment {}", dto);
		/**/
		log.info("Payment completed");
	}

	@RabbitListener(queues = "#{@deadPaymentRequestQueue}", id = "PaymentRequestErrorListener")
	public void handlePaymentError(PaymentDTO dto) {
		log.info("Something went wrong with payment {}", dto);

	}
}
