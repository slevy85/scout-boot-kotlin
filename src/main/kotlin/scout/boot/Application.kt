package scout.boot

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}

@SpringBootApplication
@Import(ServletConfiguration::class)
@ComponentScan(basePackages = arrayOf("org.eclipse.scout.boot"))
class Application
