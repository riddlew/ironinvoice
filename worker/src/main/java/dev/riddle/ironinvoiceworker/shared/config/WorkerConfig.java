package dev.riddle.ironinvoiceworker.shared.config;

import dev.riddle.ironinvoiceworker.upload.application.UploadJobReceiver;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.DefaultJacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkerConfig {

	static final String topicExchangeName = "invoice-upload-exchange";
	static final String queueName = "invoice-upload-queue";

	@Bean
	Queue queue() {
		return new Queue(queueName, false);
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange(topicExchangeName);
	}

	@Bean
	Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder
			.bind(queue)
			.to(exchange)
			.with("invoice.upload.#");
	}

	@Bean
	JacksonJsonMessageConverter jacksonJsonMessageConverter() {
		JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();

		DefaultJacksonJavaTypeMapper typeMapper = new DefaultJacksonJavaTypeMapper();
		typeMapper.addTrustedPackages("dev.riddle.ironinvoiceshared.uploads.contracts");
		converter.setJavaTypeMapper(typeMapper);

		return converter;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(
		UploadJobReceiver uploadJobReceiver,
		JacksonJsonMessageConverter jacksonJsonMessageConverter
	) {
		MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(uploadJobReceiver, "receiveJob");

		listenerAdapter.setMessageConverter(jacksonJsonMessageConverter);

		return listenerAdapter;
	}

	@Bean
	SimpleMessageListenerContainer container(
		ConnectionFactory connectionFactory,
		MessageListenerAdapter listenerAdapter
	) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

}
