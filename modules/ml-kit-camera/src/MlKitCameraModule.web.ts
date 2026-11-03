import { registerWebModule, NativeModule } from 'expo';

class MlKitCameraModule extends NativeModule<{}> {
  hello() {
    return 'Hello world! 👋';
  }
}

export default registerWebModule(MlKitCameraModule, 'MlKitCameraModule');
