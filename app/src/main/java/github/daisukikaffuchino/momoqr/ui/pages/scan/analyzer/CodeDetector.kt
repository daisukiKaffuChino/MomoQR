package github.daisukikaffuchino.momoqr.ui.pages.scan.analyzer

interface CodeDetector {
    fun onCodeFound(codeValue: String)
    fun onError(exception: Exception)
}