package no.ntnu.coderunnerbackend;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("http://localhost:8080")
public class Controller {

    @PostMapping("/run")
    public String run(@RequestBody CodeDTO code) {
        Command command = new Command();
        try {
            return command.run(code.getCode());
        } catch (Exception e) {
            e.printStackTrace();
            return "There was an error";
        }
    }
}
