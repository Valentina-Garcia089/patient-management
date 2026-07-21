package com.pm.apigateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class JwtValidationGatewayFilterFactory extends
        AbstractGatewayFilterFactory<Object> {


    private final WebClient webClient;

    public JwtValidationGatewayFilterFactory(
            WebClient.Builder webClientBuilder,
            @Value("${auth-service.url}") String authServiceUrl) {

        this.webClient = webClientBuilder.baseUrl(authServiceUrl).build();
    }


    @Override
    // este metodo se ejecuta cada vez que se aplica el filtro, es decir, cada vez que se hace una petición a un endpoint protegido
    public GatewayFilter apply(Object config){
        return (exchange, chain) ->{
            // Obtener el token JWT del encabezado de autorización
            String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if(token == null || !token.startsWith("Bearer ")){
                // Si no hay token o no es un token Bearer, devolver un error 401
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete(); // Completa la respuesta sin cuerpo
            }


            // Llamar al endpoint de validación del Auth Service para validar el token
            return webClient.get()
                    .uri("/validate")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .toBodilessEntity()// Obtener la respuesta sin cuerpo
                    .then(chain.filter(exchange)); // Si el token es válido, continuar con el request
        };
    }
}
