import * as Device from "expo-device";
import { Platform, StyleSheet } from "react-native";

import { ThemedText } from "@/components/themed-text";
import { BottomTabInset, MaxContentWidth, Spacing } from "@/constants/theme";
import { Host } from "@expo/ui/jetpack-compose";
import { useIsFocused } from "expo-router/build/react-navigation/index.js";
import {
  MlKitCamera,
  MlKitCameraView,
} from "../../modules/ml-kit-camera/src/MlKitCameraModule";

function getDevMenuHint() {
  if (Platform.OS === "web") {
    return <ThemedText type="small">use browser devtools</ThemedText>;
  }
  if (Device.isDevice) {
    return (
      <ThemedText type="small">
        shake device or press <ThemedText type="code">m</ThemedText> in terminal
      </ThemedText>
    );
  }
  const shortcut = Platform.OS === "android" ? "cmd+m (or ctrl+m)" : "cmd+d";
  return (
    <ThemedText type="small">
      press <ThemedText type="code">{shortcut}</ThemedText>
    </ThemedText>
  );
}

const openCamera = () => {
  try {
    MlKitCamera.abrirCamera();
  } catch (error) {
    console.log("show error", error);
  }
};

export default function HomeScreen() {
  const isFocused = useIsFocused();
  console.log("checking", isFocused);

  if (!isFocused) {
    return null;
  }
  return (
    <Host
      style={{
        flex: 1,
        backgroundColor: "white",
      }}
    >
      <MlKitCameraView
        onTextRecognized={(event) =>
          console.log("Escutando do NATIVO", event.nativeEvent)
        }
      />
    </Host>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    flexDirection: "row",
  },
  safeArea: {
    flex: 1,
    paddingHorizontal: Spacing.four,
    alignItems: "center",
    gap: Spacing.three,
    paddingBottom: BottomTabInset + Spacing.three,
    maxWidth: MaxContentWidth,
  },
  heroSection: {
    alignItems: "center",
    justifyContent: "center",
    flex: 1,
    paddingHorizontal: Spacing.four,
    gap: Spacing.four,
  },
  title: {
    textAlign: "center",
  },
  code: {
    textTransform: "uppercase",
  },
  stepContainer: {
    gap: Spacing.three,
    alignSelf: "stretch",
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.four,
    borderRadius: Spacing.four,
  },
});
