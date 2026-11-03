import ExpoModulesCore

public class MlKitCameraModule: Module {
  public func definition() -> ModuleDefinition {
    Name("MlKitCamera")

    Function("hello") {
      return "Hello world! 👋"
    }
  }
}
