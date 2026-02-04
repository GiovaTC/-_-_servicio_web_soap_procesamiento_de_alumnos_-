package publisher;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import javax.xml.soap.*;
import java.io.*;
import java.net.InetSocketAddress;

public class Publicador {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        server.createContext("/AlumnoService", new SoapHandler());
        server.setExecutor(null); // executor por defecto
        server.start();

        System.out.println("SOAP PURO publicado en:");
        System.out.println("http://localhost:8080/AlumnoService");
    }

    static class SoapHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            // 🔹 Si piden el WSDL
            if (exchange.getRequestURI().getQuery() != null &&
                    exchange.getRequestURI().getQuery().equalsIgnoreCase("wsdl")) {

                InputStream wsdl =
                        Publicador.class
                                .getResourceAsStream("/AlumnoService.wsdl");

                byte[] bytes = wsdl.readAllBytes();

                exchange.getResponseHeaders()
                        .set("Content-Type", "text/xml; charset=utf-8");

                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
                exchange.close();
                return;
            }

            // 🔹 SOAP Request normal
            try {
                MessageFactory factory = MessageFactory.newInstance();
                SOAPMessage request =
                        factory.createMessage(null, exchange.getRequestBody());

                SOAPMessage response = factory.createMessage();
                SOAPEnvelope envelope = response.getSOAPPart().getEnvelope();
                SOAPBody body = envelope.getBody();

                SOAPBodyElement result =
                        body.addBodyElement(
                                envelope.createName(
                                        "procesarAlumnoResponse",
                                        "ns1",
                                        "http://service.alumno/"
                                )
                        );

                SOAPElement value =
                        result.addChildElement("resultado");
                value.addTextNode("PROCESADO OK");

                response.saveChanges();

                exchange.getResponseHeaders()
                        .set("Content-Type", "text/xml; charset=utf-8");

                exchange.sendResponseHeaders(200, 0);
                response.writeTo(exchange.getResponseBody());
                exchange.close();

            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        }
    }
}