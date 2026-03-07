package github.daisukikaffuchino.momoqr.ui.pages.scan.analyzer

interface CodeDetector {
    fun onDetected(codeValue: String)
    fun onError(exception: Exception)
}