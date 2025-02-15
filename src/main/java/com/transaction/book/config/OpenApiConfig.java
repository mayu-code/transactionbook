package com.transaction.book.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Transaction Book System REST API",
        version = "1.0.0",
        description = "API documentation for Transaction Book.\n"+
                       "\nInstructions:\n" +
                      "1. Authenticate via the `/auth/login` endpoint to obtain a JWT token.\n" +
                      "2. Use the token in the `Authorization` header as `Bearer <token>` for subsequent API calls.\n" +
                      "3. For more detailed information, refer to the respective endpoint documentation.\n" +
                      "4. Please reach out to our support team for any issues or inquiries.",
        // termsOfService = "https://www.college.stpaul/terms",
        contact = @Contact(
            name = "Support Team",
            email = "#",
            url = "#"
        )
        // license = @License(
        //     name = "Apache 2.0",
        //     url = "https://www.apache.org/licenses/LICENSE-2.0"
        // )
    ),
    servers = {
        // @Server(url = "#", description = "Production Server"),
        @Server(url = "http://localhost:8080/", description = "Development Server")
    }
)
public class OpenApiConfig {
}
