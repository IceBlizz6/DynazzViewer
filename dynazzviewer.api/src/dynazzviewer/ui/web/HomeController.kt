package dynazzviewer.ui.web

import graphql.schema.GraphQLSchema
import graphql.schema.idl.SchemaPrinter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class HomeController {
    @Autowired
    private lateinit var schema: GraphQLSchema

    @ResponseBody
    @GetMapping(value = ["/schema"], produces = ["text/plain"])
    fun schemaDisplay(): String? {
        val printer = SchemaPrinter()
        return printer.print(schema)
            .replace("@_mappedOperation(operation : \"__internal__\")", "")
            .replace("@_mappedType(type : \"__internal__\")", "")
    }
}
