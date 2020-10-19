import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class Main {

    final static int port = 80;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        var studentId = "";
        var input = "";
        var methods = Set.of("GET", "POST", "PATCH", "PUT", "DELETE");
        var pattern = Pattern.compile("(?i)(http://)?([-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b)([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");

        while (true) {
            System.out.println("Pleas enter URL or command : ");
            input = sc.nextLine();
            input = input.trim().toLowerCase();

            if (input.equals("exit")) {
                break;
            }
            if (input.equals("set-student-id-header")) {
                System.out.println("enter student id: ");
                input = sc.nextLine();
                input = input.trim().toLowerCase();
                studentId = input;
                continue;
            }
            if (input.equals("remove-student-id-header")) {
                studentId = "";
                continue;
            }

            var tmp_url = pattern.matcher(input);
            if (tmp_url.matches()) {
                System.out.println("Enter http method : ");
                System.out.println(
                        "GET for get method,\n" +
                                "POST for post method, \n" +
                                "PUT for put method, \n" +
                                "PATCH for patch method, \n" +
                                "DELETE for delete method, \n");

                var method = sc.nextLine().trim().toUpperCase();

                if (!methods.contains(method)) {
                    System.out.println("bad method");
                    continue;
                }

                var hostname = tmp_url.group(2);
                var recource = tmp_url.group(3);
                if (recource.isEmpty()) {
                    recource = "/";
                }

                try {
                    var socket = new Socket(hostname, port);
                    var output = new OutputStreamWriter(socket.getOutputStream());

                    output.write(makeRequestBody(method, recource, studentId));
                    output.flush();

                    parsInput(socket.getInputStream());
                    socket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String makeRequestBody(String method, String resource, String studentId) {
        String crlf = "\r\n";
        var sb = new StringBuilder();
        sb.append(method).append(" ").append(resource).append(" ").append("HTTP/1.0").append(crlf);
        sb.append("Accept: */*").append(crlf);
        sb.append("Accept-Encoding: gzip, deflate, br").append(crlf);
        if (!studentId.isEmpty()) {
            sb.append("x-student-id: ").append(studentId).append(crlf);
        }
        sb.append(crlf);

        return sb.toString();
    }

    private static void parsInput(InputStream inputStream) {

        FileWriter writer ;
        var headerSc = new Scanner(inputStream);
        var response = new StringBuilder();

        var tmp_str = "";
        var type = "";
        var error_num = "";

        var first_line = headerSc.nextLine();
        //identify type of response and write it in CLI
        if (first_line.equals("HTTP/1.1 200 OK") || first_line.equals("HTTP/1.0 200 OK")) {
            while (headerSc.hasNextLine()) {
                tmp_str = headerSc.nextLine();
                if (tmp_str.toLowerCase().contains("content-type")) {
                    type = tmp_str.split("/")[1].split(";")[0];
                }
                response.append(tmp_str).append("\n");
            }
        } else {              //Error
            type = "error";
            while (headerSc.hasNextLine()) {
                tmp_str = headerSc.nextLine();
                response.append(tmp_str).append("\n");
            }
        }

        switch (type) {

            case "json":

                try {
                    writer = new FileWriter("test.json");
                    writer.write(response.toString().split("Connection: close\n\n")[1]);
                    writer.close();
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }

                System.out.println("*** Json file saved on your pc");
                break;


            case "html":

                try {
                    writer = new FileWriter("test.html");
                    writer.write(response.toString().split("Connection: close\n")[1]);
                    writer.close();
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
                System.out.println("*** Html file saved on your pc");
                break;
            case "plain":

                System.out.println(response.toString().split("Connection: close\n")[1]);
                break;
            default:    //error
                System.out.println("*** Error ***");
                error_num = first_line.split(" ")[1];
                System.out.println("*** Error number : " + error_num);
                System.out.print("*** Error description : ");
                for (int i = 2; i < first_line.split(" ").length; i++) {
                    System.out.print(first_line.split(" ")[i] + " ");

                }
                System.out.println();
                break;

        }

        System.out.println("****** Server response ******");
        System.out.println(response.toString());
        System.out.println("****** End of server response ******");
    }
}

