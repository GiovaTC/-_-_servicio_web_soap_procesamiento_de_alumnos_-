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

            try {
                // 🔹 Leer SOAP Request
                MessageFactory factory = MessageFactory.newInstance();
                SOAPMessage request =
                        factory.createMessage(null, exchange.getRequestBody());

                // (aquí podrías procesar el request si lo deseas)

                // 🔹 Crear SOAP Response correctamente (SAAJ)
                SOAPMessage response = factory.createMessage();
                SOAPEnvelope envelope = response.getSOAPPart().getEnvelope();
                SOAPBody body = envelope.getBody();

                SOAPBodyElement resultado =
                        body.addBodyElement(
                                envelope.createName(
                                        "resultado",
                                        "ns1",
                                        "http://service.alumno/"
                                )
                        );

                resultado.addTextNode("PROCESADO OK");

                response.saveChanges();

                // 🔹 Enviar respuesta HTTP
                exchange.getResponseHeaders()
                        .set("Content-Type", "text/xml; charset=utf-8");

                exchange.sendResponseHeaders(200, 0);
                OutputStream os = exchange.getResponseBody();
                response.writeTo(os);
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        }
    }
}
