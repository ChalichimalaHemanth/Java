import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class VehicleRentalServer {
    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(8081); // changed to 8081
        System.out.println("Car/Bike Rental Server running at http://localhost:8081");

        while (true) {
            Socket client = server.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            OutputStream out = client.getOutputStream();

            String line;
            boolean isCssRequest = false;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                if (line.startsWith("GET")) {
                    if (line.contains("style.css")) isCssRequest = true;
                    break;
                }
            }

            try {
                if (isCssRequest) {
                    byte[] css = Files.readAllBytes(Paths.get("style.css"));
                    out.write("HTTP/1.1 200 OK\r\n".getBytes());
                    out.write("Content-Type: text/css\r\n".getBytes());
                    out.write(("Content-Length: " + css.length + "\r\n").getBytes());
                    out.write("\r\n".getBytes());
                    out.write(css);
                } else {
                    byte[] html = Files.readAllBytes(Paths.get("rental.html"));
                    out.write("HTTP/1.1 200 OK\r\n".getBytes());
                    out.write("Content-Type: text/html\r\n".getBytes());
                    out.write(("Content-Length: " + html.length + "\r\n").getBytes());
                    out.write("\r\n".getBytes());
                    out.write(html);
                }
                out.flush(); // force send
            } catch (IOException e) {
                System.out.println("Client closed connection early: " + e.getMessage());
            }

            out.close();
            in.close();
            client.close();
        }
    }
}
