package expo.modules.mlkitcamera

import android.content.Intent
import android.provider.MediaStore
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class MlKitCameraModule : Module() {
    override fun definition() = ModuleDefinition {
        Name("MlKitCamera")


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

        View(MyCustomCameraView::class) {
            Events("onTextRecognized")
        }

        Function("hello") {
            "Hello world! 👋"
        }
    }
}
