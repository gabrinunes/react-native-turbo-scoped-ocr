import { NativeModule, requireNativeModule, requireNativeView } from "expo";
import { ViewProps } from "react-native";

declare class MlKitCameraModule extends NativeModule<{}> {
  hello(): string;
  abrirCamera(): void;
}

export const MlKitCamera =
  requireNativeModule<MlKitCameraModule>("MlKitCamera");

export interface MlKitCameraViewProps extends ViewProps {
  onTextRecognized?: (event: { nativeEvent: { text: string } }) => void;
}

const NativeView = requireNativeView<MlKitCameraViewProps>("MlKitCamera");

export function MlKitCameraView(props: MlKitCameraViewProps) {
  return <NativeView {...props} />;
}
