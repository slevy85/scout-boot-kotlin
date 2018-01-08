package scout.boot.kotlin.standard

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(SecurityConfiguration::class)
@ComponentScan(basePackages = arrayOf("scout.boot.kotlin.standard", "org.eclipse.scout.boot"))
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}