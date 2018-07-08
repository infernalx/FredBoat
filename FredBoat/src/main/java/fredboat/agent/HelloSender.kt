package fredboat.agent

import com.fredboat.sentinel.SentinelExchanges
import com.fredboat.sentinel.entities.FredBoatHello
import fredboat.config.property.AppConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class HelloSender(
        private val rabbitTemplate: RabbitTemplate,
        private val appConfig: AppConfig
) : FredBoatAgent("HelloSender", 5, TimeUnit.MINUTES) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(HelloSender::class.java)
    }
    private var firstRun = true

    override fun doRun() {
        // Send a hello when we start so we get SentinelHellos in return
        rabbitTemplate.convertAndSend(SentinelExchanges.FANOUT, "", FredBoatHello(
                firstRun,
                appConfig.status
        ))
        if (firstRun) log.info("Sent first hello to Sentinels")
        firstRun = false
    }
}