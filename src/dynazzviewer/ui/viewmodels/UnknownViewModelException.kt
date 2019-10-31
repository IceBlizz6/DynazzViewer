package dynazzviewer.ui.viewmodels

import java.lang.RuntimeException

class UnknownViewModelException(
    message: String
) : RuntimeException(message)
