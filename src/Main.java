import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        var studentId = "";
        var input = "";
        var methods = Set.of("GET" , "POST" , "PATCH" , "PUT" , "DELETE");
        System.out.println("Welcome To HTTP Project");

        System.out.println("Pleas enter IP : ");
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
            }

        }
    }
}

