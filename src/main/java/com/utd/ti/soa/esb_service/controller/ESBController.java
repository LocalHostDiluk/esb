package com.utd.ti.soa.esb_service.controller;

import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import com.utd.ti.soa.esb_service.model.Client;
import com.utd.ti.soa.esb_service.model.User;
import com.utd.ti.soa.esb_service.model.Product;
import com.utd.ti.soa.esb_service.utils.Auth;

@RestController
@RequestMapping("/app/esb")
public class ESBController {

    private final WebClient webClient = WebClient.create();
    private final Auth auth = new Auth();

    // Método auxiliar para validar token
    private boolean validateAuthToken(String token) {
        if (!auth.validateToken(token)) {
            return false;
        }
        return true;
    }

    // Método genérico para enviar solicitudes y obtener respuestas
    private ResponseEntity sendRequest(String url, String method, Object body, String token) {
        if (!validateAuthToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }

        WebClient.RequestBodySpec requestSpec = webClient.method(method)
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, token);

        if (body != null) {
            requestSpec.body(BodyInserters.fromValue(body));
        }

        String response = requestSpec
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> System.out.println("Error: " + error.getMessage()))
                .block();

        return ResponseEntity.ok(response);
    }

    // Obtener todos los usuarios
    @GetMapping("/user")
    public ResponseEntity getUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return sendRequest("https://users-production-b7a8.up.railway.app/app/users/all", "GET", null, token);
    }

    // Crear usuario
    @PostMapping("/user")
    public ResponseEntity createUser(@RequestBody User user, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return sendRequest("https://users-production-b7a8.up.railway.app/app/users/create", "POST", user, token);
    }

    // Actualizar usuario
    @PatchMapping("/user/update/{id}")
    public ResponseEntity updateUser(@PathVariable String id, @RequestBody User user, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return sendRequest("https://users-production-b7a8.up.railway.app/app/users/update/" + id, "PATCH", user, token);
    }

    // Eliminar usuario
    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity deleteUser(@PathVariable String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return sendRequest("https://users-production-b7a8.up.railway.app/app/users/delete/" + id, "DELETE", null, token);
    }

    // Login
    @PostMapping("/user/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> credentials) {
        return sendRequest("https://users-production-b7a8.up.railway.app/app/users/login", "POST", credentials, null);
    }

    // Recuperar contraseña
    @PostMapping("/user/recuperar")
    public ResponseEntity<String> recuperar(@RequestBody Map<String, String> credentials) {
        return sendRequest("https://users-production-b7a8.up.railway.app/app/users/recover", "POST", credentials, null);
    }

    // Crear cliente
    @PostMapping("/client")
    public ResponseEntity createClient(@RequestBody Client client, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        ResponseEntity clientResponse = sendRequest("https://cliente-production-841a.up.railway.app/app/client/create", "POST", client, token);
        if (clientResponse.getStatusCodeValue() == 200) {
            // Crear usuario
            User newUser = new User();
            newUser.setUsername(client.getMail());
            newUser.setPhone(client.getPhone());
            newUser.setPassword("123456789a");
            sendRequest("https://users-production-b7a8.up.railway.app/app/users/create", "POST", newUser, token);
            return ResponseEntity.ok("Cliente y usuario creados exitosamente.");
        }
        return clientResponse;
    }

    // Obtener todos los productos
    @GetMapping("/producto")
    public ResponseEntity getProduct(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return sendRequest("http://productos.railway.internal:5000/app/products/all", "GET", null, token);
    }

    // Crear producto
    @PostMapping("/producto")
    public ResponseEntity createProduct(@RequestBody Product product, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return sendRequest("http://productos.railway.internal:5000/app/products/create", "POST", product, token);
    }

    // Actualizar producto
    @PatchMapping("/producto/update/{id}")
    public ResponseEntity updateProduct(@PathVariable String id, @RequestBody Product product, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return sendRequest("http://productos.railway.internal:5000/app/products/update/" + id, "PATCH", product, token);
    }

    // Eliminar producto
    @DeleteMapping("/producto/delete/{id}")
    public ResponseEntity deleteProduct(@PathVariable String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return sendRequest("http://productos.railway.internal:5000/app/products/delete/" + id, "DELETE", null, token);
    }

    // Crear pedido
    @PostMapping("/pedido")
    public ResponseEntity createPedido(@RequestBody Map<String, Object> pedido, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return sendRequest("https://pedidos-production-2523.up.railway.app/app/pedidos/crear", "POST", pedido, token);
    }

    // Obtener todos los pedidos
    @GetMapping("/pedido")
    public ResponseEntity getAllPedidos(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return sendRequest("https://pedidos-production-2523.up.railway.app/app/pedidos/all", "GET", null, token);
    }

    // Actualizar estado pedido
    @PatchMapping("/pedido/actualizar/{id}")
    public ResponseEntity updatePedido(@PathVariable String id, @RequestBody Map<String, Object> pedido, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return sendRequest("https://pedidos-production-2523.up.railway.app/app/pedidos/actualizar/" + id, "PATCH", pedido, token);
    }

    // Cancelar pedido
    @DeleteMapping("/pedido/cancelar/{id}")
    public ResponseEntity cancelPedido(@PathVariable String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return sendRequest("https://pedidos-production-2523.up.railway.app/app/pedidos/cancelar/" + id, "DELETE", null, token);
    }
}
