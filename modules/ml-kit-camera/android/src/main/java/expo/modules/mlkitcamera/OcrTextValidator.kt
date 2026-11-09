package expo.modules.mlkitcamera

class OcrTextValidator(private val minMatches: Int) {
    private var isLocked = false
    private var lastTextBuffer = ""
    private var textMatchCount = 0

    fun processNewText(rawText: String, onSuccess: (String) -> Unit) {
        if (isLocked) return

        // Limpa o texto mantendo apenas números
        val onlyNumbers = rawText.replace(Regex("[^0-9]"), "")
        if (onlyNumbers.length < 4) return

        // Validação por repetição (Filtro de ruído do OCR)
        if (onlyNumbers == lastTextBuffer) {
            textMatchCount++
            if (textMatchCount >= minMatches) {
                isLocked = true
                onSuccess(onlyNumbers)
            }
        } else {
            lastTextBuffer = onlyNumbers
            textMatchCount = 1
        }
    }

    fun reset() {
        isLocked = false
        lastTextBuffer = ""
        textMatchCount = 0
    }
}