package expo.modules.mlkitcamera

import android.content.Intent
import android.provider.MediaStore
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class MlKitCameraModule : Module() {
    override fun definition() = ModuleDefinition {
        Name("MlKitCamera")

        val ocrTextValidator = OcrTextValidator(3)


        Function("abrirCamera") {
            val activity = appContext.currentActivity
                ?: throw IllegalStateException("A Activity atual não está disponível.")
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (intent.resolveActivity(activity.packageManager) != null) {
                activity.startActivity(intent)
            } else {
                throw Exception("Nenhum aplicativo de camera encontrado no dispositivo")
            }
        }

//        Function("resetCamera"){
//            val ocrTextValidator = OcrTextValidator(3)
//            ocrTextValidator.reset()
//        }

        View(MyCustomCameraView::class) {
            Events("onTextRecognized")

            OnViewDestroys { _ ->
                ocrTextValidator.reset()
            }


            Prop("maskWidth") { view: MyCustomCameraView, maskWidth: Int? ->
                if (maskWidth != null) {
                    view.setMaskWidth(maskWidth)
                } else {
                    view.resetMaskWidth()
                }
            }
            Prop("maskHeight") { view: MyCustomCameraView, maskHeight: Int? ->
                if (maskHeight != null) {
                    view.setMaskHeight(maskHeight)
                } else {
                    view.resetMaskHeight()
                }
            }

        }

        Function("hello") {
            "Hello world! 👋"
        }
    }
}
