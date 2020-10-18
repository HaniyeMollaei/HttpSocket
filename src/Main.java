import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class Main {

    final static int port = 80 ;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        var studentId = "";
        var input = "";
        var methods = Set.of("GET" , "POST" , "PATCH" , "PUT" , "DELETE");
        System.out.println("Welcome To HTTP Project");

       // System.out.println("Pleas enter IP : ");
        var pattern = Pattern.compile("(?i)(http://)?([-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b)([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");

        while (true){
            System.out.println("Pleas enter URL or command : ");
            input = sc.nextLine();
            input = input.trim().toLowerCase();

            if (input.equals("exit")){
                break;
            }
            if(input.equals("set-student-id-header")){
                System.out.println("enter student id: ");
                input=sc.nextLine();
                input=input.trim().toLowerCase();
                studentId = input;
                continue;
            }
            if (input.equals("remove-student-id-header")){
                studentId = "";
                continue;
            }
            var tmp_url = pattern.matcher(input);
            if (tmp_url.matches()){
                System.out.println("Enter http method : ");
                System.out.println(
                        "GET for get method,\n" +
                                "POST for post method, \n" +
                                "PUT for put method, \n" +
                                "PATCH for patch method, \n" +
                                "DELETE for delete method, \n");

                var method= sc.nextLine().trim().toUpperCase();

                if (!methods.contains(method)){
                    System.out.println("bad method");
                    continue;
                }
                System.out.println("matches: ");
                var hostname =tmp_url.group(2);
                var resorse =tmp_url.group(3);
                if (resorse.isEmpty()){
                    resorse="/";
                }

                try {
                    var socket = new Socket(hostname,port);
                    var output = new OutputStreamWriter(socket.getOutputStream());
                    var inpStream = socket.getInputStream();

                    output.write(makeRequestBody(method,resorse,studentId));
                    output.flush();
                    var inSc= new Scanner(socket.getInputStream());
                    var inputSb=new StringBuilder();
                    while (inSc.hasNextLine()){
                        inputSb.append(inSc.nextLine()).append("\n");
                    }

                    socket.close();

                    System.out.println(inputSb.toString());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String makeRequestBody(String method, String resource, String studentId) {
        String crlf="\r\n";
        var sb = new StringBuilder();
        sb.append(method).append(" ").append(resource).append(" ").append("HTTP/1.0").append(crlf);
        sb.append("Accept: */*").append(crlf);
        sb.append("Accept-Encoding: gzip, deflate, br").append(crlf);
        if (!studentId.isEmpty()){
            sb.append("x-student-id: ").append(studentId).append(crlf);
        }
        sb.append(crlf);

        return sb.toString();
    }

    private static void parsInput(InputStream inputStream){

        var headerScanner = new Scanner(inputStream);

        var header = new StringBuilder();

        var inp = "#";
        var charset= "utf-8";
        var reachedBody=false;
        var isHtml= false;

        var body = new StringBuilder();

        while (headerScanner.hasNextLine()){
            System.out.println("here");
            inp=headerScanner.nextLine().toLowerCase();
            if (inp.isEmpty()){
                reachedBody=true;
            }

            if (inp.contains("content-type")){
                if (inp.contains("text/html")){
                    isHtml=true;
                }
            }

            if (!reachedBody) {
                header.append(inp).append("\n");
            }else {
                body.append(inp).append("\n");
            }

        }

        if (isHtml){
            // Files.writeString(Path.of("./taha.html") , body.toString());
        }
        System.out.println("****** Server response ******");
        System.out.println(header.toString());
        System.out.println(body.toString());
        System.out.println("****** End of server response ******");


    }
}

