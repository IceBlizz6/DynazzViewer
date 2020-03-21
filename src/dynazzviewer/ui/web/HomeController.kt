package dynazzviewer.ui.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class HomeController {
    @ResponseBody
    @GetMapping("/")
    fun home(): String {
        return "Hello World"
    }
}
