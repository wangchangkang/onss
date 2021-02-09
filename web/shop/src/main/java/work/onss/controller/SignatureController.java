package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import work.onss.domain.Product;
import work.onss.vo.Work;

@Log4j2
@RestController
public class SignatureController {

    @GetMapping(value = {"signature"})
    public Work<Product> product() {
      return null;
    }

}
