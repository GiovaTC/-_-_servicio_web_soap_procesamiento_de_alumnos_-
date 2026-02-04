package publisher;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import service.AlumnoService; // Importa tu clase lógica

public class Publicador {

    // 🔹 Ruta absoluta del WSDL
    private static final String WSDL_PATH =
            "C:/Users/USUARIO/IdeaProjects/soap_alumnos/src/resources/AlumnoService.wsdl";

    public static void main(String[] args) throws Exception {
        File wsdlFile = new File(WSDL_PATH);
        System.out.println("Ruta WSDL usada: " + wsdlFile.getAbsolutePath());

        if (!wsdlFile.exists()) {
            System.err.println("❌ ERROR: WSDL no encontrada");
            return;
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Contexto principal del servicio
        server.createContext("/AlumnoService", new SoapHandler());

        server.start();

        System.out.println("SOAP publicado en:");
        System.out.println("http://localhost:8080/AlumnoService");
        System.out.println("WSDL disponible en:");
        System.out.println("http://localhost:8080/AlumnoService?wsdl");
    }

    static class SoapHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();

            // 🔹 Si piden el WSDL
            if (query != null && query.toLowerCase().contains("wsdl")) {
                File wsdlFile = new File(WSDL_PATH);
                byte[] bytes = java.nio.file.Files.readAllBytes(wsdlFile.toPath());

                exchange.getResponseHeaders().set("Content-Type", "text/xml; charset=utf-8");
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
                exchange.close();
                return;
            }

            // 🔹 Procesar request SOAP normal
            try {
                MessageFactory factory = MessageFactory.newInstance();
                SOAPMessage request = factory.createMessage(null, exchange.getRequestBody());

                // Delegamos al AlumnoService real
                AlumnoService alumnoService = new AlumnoService();
                SOAPMessage response = alumnoService.invoke(request);

                exchange.getResponseHeaders().set("Content-Type", "text/xml; charset=utf-8");
                exchange.sendResponseHeaders(200, 0);
                response.writeTo(exchange.getResponseBody());
                exchange.close();

            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
                exchange.close();
            }
        }
    }
}